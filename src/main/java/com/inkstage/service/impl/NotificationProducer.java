package com.inkstage.service.impl;

import com.inkstage.config.redis.RedisStreamConstants;
import com.inkstage.entity.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * 通知消息生产者（基于Redis Streams）
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 发送通知消息到Redis Streams
     */
    public void sendNotification(Notification notification) {
        try {
            String json = objectMapper.writeValueAsString(notification);
            ObjectRecord<String, String> record = StreamRecords.newRecord()
                    .ofObject(json)
                    .withStreamKey(RedisStreamConstants.NOTIFICATION_STREAM);
            stringRedisTemplate.opsForStream().add(record);
            log.info("通知消息发送成功，用户ID: {}", notification.getUserId());
        } catch (Exception e) {
            log.error("通知消息发送失败，用户ID: {}", notification.getUserId(), e);
        }
    }

    /**
     * 批量发送通知消息
     */
    public void sendBatchNotifications(List<Notification> notifications) {
        for (Notification notification : notifications) {
            sendNotification(notification);
        }
    }
}
