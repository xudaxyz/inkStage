package com.inkstage.service.impl;

import com.inkstage.config.redis.RedisStreamConstants;
import com.inkstage.event.CountMessage;
import com.inkstage.service.CountService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;

/**
 * 计数消息消费者（基于Redis Streams）
 * <p>
 * 消费Redis Stream中的计数消息，执行计数更新逻辑。
 * 支持幂等消费（防止重复递增）、消息重试和死信队列。
 * <p>
 * 消费流程：
 * 1. 首次消费：幂等Key不存在 → Redis递增 + DB同步 → ACK
 * 2. 重试消费：幂等Key已存在 → 仅重试DB同步 → ACK
 * 3. DB同步失败：不ACK，等待重试机制重新投递
 * 4. 超过最大重试次数：转入死信队列
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CountConsumer {

    private final CountService countService;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        createConsumerGroup(RedisStreamConstants.COUNT_STREAM,
                RedisStreamConstants.COUNT_CONSUMER_GROUP);
        createConsumerGroup(RedisStreamConstants.COUNT_DLQ_STREAM,
                RedisStreamConstants.COUNT_DLQ_CONSUMER_GROUP);
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
    public void consumeCount() {
        try {
            List<MapRecord<String, Object, Object>> records = readStream(
                    Consumer.from(RedisStreamConstants.COUNT_CONSUMER_GROUP,
                            RedisStreamConstants.COUNT_CONSUMER_NAME),
                    StreamReadOptions.empty().count(10),
                    StreamOffset.create(RedisStreamConstants.COUNT_STREAM, ReadOffset.lastConsumed())
            );

            if (records == null || records.isEmpty()) {
                return;
            }

            for (MapRecord<String, Object, Object> record : records) {
                processRecord(record);
            }
        } catch (Exception e) {
            log.error("消费计数消息异常", e);
        }
    }

    @Scheduled(fixedDelay = 30_000, initialDelay = 10_000)
    public void retryPendingMessages() {
        try {
            PendingMessagesSummary summary = stringRedisTemplate.opsForStream()
                    .pending(RedisStreamConstants.COUNT_STREAM,
                            RedisStreamConstants.COUNT_CONSUMER_GROUP);
            if (summary == null || summary.getTotalPendingMessages() == 0) {
                return;
            }

            PendingMessages pendingMessages = stringRedisTemplate.opsForStream().pending(
                    RedisStreamConstants.COUNT_STREAM,
                    Consumer.from(RedisStreamConstants.COUNT_CONSUMER_GROUP,
                            RedisStreamConstants.COUNT_CONSUMER_NAME),
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
            log.error("重试待处理计数消息异常", e);
        }
    }

    private void retryOrMoveToDlq(PendingMessage pending) {
        try {
            List<MapRecord<String, Object, Object>> records = readStream(
                    Consumer.from(RedisStreamConstants.COUNT_CONSUMER_GROUP,
                            RedisStreamConstants.COUNT_CONSUMER_NAME),
                    StreamReadOptions.empty().count(1),
                    StreamOffset.create(RedisStreamConstants.COUNT_STREAM,
                            ReadOffset.from(pending.getIdAsString()))
            );

            if (records == null || records.isEmpty()) {
                return;
            }

            MapRecord<String, Object, Object> record = records.getFirst();
            if (pending.getTotalDeliveryCount() >= RedisStreamConstants.MAX_RETRY_COUNT) {
                moveToDlq(record);
                acknowledge(record);
                log.warn("计数消息超过最大重试次数，已转入死信: {}", record.getId());
            } else {
                processRecord(record);
            }
        } catch (Exception e) {
            log.error("重试计数消息处理异常，messageId: {}", pending.getIdAsString(), e);
        }
    }

    private void processRecord(MapRecord<String, Object, Object> record) {
        try {
            Object payload = record.getValue().get("payload");
            if (payload == null) {
                acknowledge(record);
                return;
            }

            CountMessage message = objectMapper.readValue(payload.toString(), CountMessage.class);

            String idempotentKey = RedisStreamConstants.COUNT_IDEMPOTENT_KEY_PREFIX + record.getId();
            Boolean isFirst = stringRedisTemplate.opsForValue()
                    .setIfAbsent(idempotentKey, "1",
                            Duration.ofMinutes(RedisStreamConstants.IDEMPOTENT_KEY_TTL_MINUTES));

            if (Boolean.FALSE.equals(isFirst)) {
                countService.syncToDatabase(message.getCountType(), message.getTargetId(), message.getDelta());
                acknowledge(record);
                return;
            }

            countService.updateCount(message.getCountType(), message.getTargetId(), message.getDelta());
            acknowledge(record);
        } catch (Exception e) {
            log.error("处理计数消息失败，recordId: {}", record.getId(), e);
        }
    }

    private void acknowledge(MapRecord<String, Object, Object> record) {
        stringRedisTemplate.opsForStream().acknowledge(
                RedisStreamConstants.COUNT_STREAM,
                RedisStreamConstants.COUNT_CONSUMER_GROUP,
                record.getId()
        );
    }

    private void moveToDlq(MapRecord<String, Object, Object> record) {
        try {
            var dlqRecord = StreamRecords.newRecord()
                    .ofObject(record.getValue())
                    .withStreamKey(RedisStreamConstants.COUNT_DLQ_STREAM);
            stringRedisTemplate.opsForStream().add(dlqRecord);
        } catch (Exception e) {
            log.error("转入计数死信队列失败，recordId: {}", record.getId(), e);
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
