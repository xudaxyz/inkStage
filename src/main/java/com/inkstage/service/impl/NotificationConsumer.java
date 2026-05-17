package com.inkstage.service.impl;

import com.inkstage.config.redis.RedisStreamConstants;
import com.inkstage.entity.model.Notification;
import com.inkstage.entity.model.NotificationSetting;
import com.inkstage.entity.model.User;
import com.inkstage.mapper.NotificationMapper;
import com.inkstage.service.*;
import com.inkstage.utils.SnowflakeIdGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * 通知消息消费者（基于Redis Streams）
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationMapper notificationMapper;
    private final NotificationCacheService notificationCacheService;
    private final WebSocketService webSocketService;
    private final NotificationSettingService notificationSettingService;
    private final EmailService emailService;
    private final UserService userService;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        createConsumerGroup(RedisStreamConstants.NOTIFICATION_STREAM,
                RedisStreamConstants.NOTIFICATION_CONSUMER_GROUP);
        createConsumerGroup(RedisStreamConstants.NOTIFICATION_DLQ_STREAM,
                RedisStreamConstants.NOTIFICATION_DLQ_CONSUMER_GROUP);
    }

    private void createConsumerGroup(String streamKey, String groupName) {
        try {
            stringRedisTemplate.opsForStream().createGroup(streamKey, ReadOffset.from("0"), groupName);
            log.info("创建消费者组成功: {}", groupName);
        } catch (Exception e) {
            log.info("消费者组已存在: {}", groupName);
        }
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 5000)
    public void consumeNotification() {
        try {
            List<MapRecord<String, Object, Object>> records = readStream(
                    Consumer.from(RedisStreamConstants.NOTIFICATION_CONSUMER_GROUP,
                            RedisStreamConstants.NOTIFICATION_CONSUMER_NAME),
                    StreamReadOptions.empty().count(10),
                    StreamOffset.create(RedisStreamConstants.NOTIFICATION_STREAM, ReadOffset.lastConsumed())
            );

            if (records == null || records.isEmpty()) {
                return;
            }

            for (MapRecord<String, Object, Object> record : records) {
                processRecord(record);
            }
        } catch (Exception e) {
            log.error("消费通知消息异常", e);
        }
    }

    @Scheduled(fixedDelay = 30_000, initialDelay = 10_000)
    public void retryPendingMessages() {
        try {
            PendingMessagesSummary summary = stringRedisTemplate.opsForStream()
                    .pending(RedisStreamConstants.NOTIFICATION_STREAM,
                            RedisStreamConstants.NOTIFICATION_CONSUMER_GROUP);
            if (summary == null || summary.getTotalPendingMessages() == 0) {
                return;
            }

            PendingMessages pendingMessages = stringRedisTemplate.opsForStream().pending(
                    RedisStreamConstants.NOTIFICATION_STREAM,
                    Consumer.from(RedisStreamConstants.NOTIFICATION_CONSUMER_GROUP,
                            RedisStreamConstants.NOTIFICATION_CONSUMER_NAME),
                    Range.unbounded(),
                    20
            );

            if (pendingMessages == null || pendingMessages.isEmpty()) {
                return;
            }

            for (PendingMessage pending : pendingMessages) {
                if (pending.getElapsedTimeSinceLastDelivery().toMillis()
                        < RedisStreamConstants.PENDING_MESSAGE_MIN_IDLE_TIME_MS) {
                    continue;
                }
                retryOrMoveToDlq(pending);
            }
        } catch (Exception e) {
            log.error("重试待处理消息异常", e);
        }
    }

    private void retryOrMoveToDlq(PendingMessage pending) {
        try {
            List<MapRecord<String, Object, Object>> records = readStream(
                    Consumer.from(RedisStreamConstants.NOTIFICATION_CONSUMER_GROUP,
                            RedisStreamConstants.NOTIFICATION_CONSUMER_NAME),
                    StreamReadOptions.empty().count(1),
                    StreamOffset.create(RedisStreamConstants.NOTIFICATION_STREAM,
                            ReadOffset.from(pending.getIdAsString()))
            );

            if (records == null || records.isEmpty()) {
                return;
            }

            MapRecord<String, Object, Object> record = records.getFirst();
            if (pending.getTotalDeliveryCount() >= RedisStreamConstants.MAX_RETRY_COUNT) {
                moveToDlq(record);
                acknowledge(record);
                log.warn("消息超过最大重试次数，已转入死信: {}", record.getId());
            } else {
                processRecord(record);
            }
        } catch (Exception e) {
            log.error("重试消息处理异常，messageId: {}", pending.getIdAsString(), e);
        }
    }

    private void processRecord(MapRecord<String, Object, Object> record) {
        try {
            Object payload = record.getValue().get("payload");
            if (payload == null) {
                acknowledge(record);
                return;
            }

            Notification notification = objectMapper.readValue(payload.toString(), Notification.class);
            handleNotification(notification);
            acknowledge(record);
        } catch (Exception e) {
            log.error("处理通知消息失败，recordId: {}", record.getId(), e);
        }
    }

    private void acknowledge(MapRecord<String, Object, Object> record) {
        stringRedisTemplate.opsForStream().acknowledge(
                RedisStreamConstants.NOTIFICATION_STREAM,
                RedisStreamConstants.NOTIFICATION_CONSUMER_GROUP,
                record.getId()
        );
    }

    private void moveToDlq(MapRecord<String, Object, Object> record) {
        try {
            var dlqRecord = StreamRecords.newRecord()
                    .ofObject(record.getValue())
                    .withStreamKey(RedisStreamConstants.NOTIFICATION_DLQ_STREAM);
            stringRedisTemplate.opsForStream().add(dlqRecord);
        } catch (Exception e) {
            log.error("转入死信队列失败，recordId: {}", record.getId(), e);
        }
    }

    /**
     * 处理通知消息
     */
    public void handleNotification(Notification notification) {
        log.info("开始处理通知消息: {}", notification);

        notification.setId(snowflakeIdGenerator.nextId());
        int result = notificationMapper.insert(notification);
        if (result <= 0) {
            log.error("通知保存失败，用户ID: {}", notification.getUserId());
            throw new RuntimeException("通知保存失败");
        }

        log.info("通知保存成功，用户ID: {}, 通知ID: {}", notification.getUserId(), notification.getId());
        updateCacheAndPush(notification);
        sendEmailIfNeeded(notification);
    }

    private void updateCacheAndPush(Notification notification) {
        try {
            notificationCacheService.incrementUnreadCount(notification.getUserId());
            webSocketService.sendNotificationToUser(notification.getUserId(), notification);

            int unreadCount = notificationCacheService.getUnreadCount(notification.getUserId());
            webSocketService.sendUnreadCountToUser(notification.getUserId(), unreadCount);
        } catch (Exception e) {
            log.warn("发送实时通知或更新缓存失败，用户ID: {}", notification.getUserId(), e);
        }
    }

    private void sendEmailIfNeeded(Notification notification) {
        try {
            NotificationSetting setting = notificationSettingService
                    .getNotificationSetting(notification.getUserId());
            if (setting == null || !setting.getEmailNotification()) {
                return;
            }

            User user = userService.getUserById(notification.getUserId());
            if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
                return;
            }

            String subject = notification.getTitle();
            String content = notification.getContent() + "\n\n点击查看详情: " + notification.getActionUrl();
            emailService.sendNotificationEmail(user.getEmail(), subject, content);
        } catch (Exception e) {
            log.warn("发送邮件通知失败，用户ID: {}", notification.getUserId(), e);
        }
    }

    @SafeVarargs
    private List<MapRecord<String, Object, Object>> readStream(
            Consumer consumer,
            StreamReadOptions options,
            StreamOffset<String>... streams) {
        return stringRedisTemplate.opsForStream().read(consumer, options, streams);
    }
}
