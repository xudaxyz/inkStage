package com.inkstage.service.impl;

import com.inkstage.config.redis.RedisStreamConstants;
import com.inkstage.enums.CountType;
import com.inkstage.event.CountMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * 计数消息生产者（基于Redis Streams）
 * <p>
 * 将计数更新消息发送到Redis Stream，由CountConsumer异步消费处理。
 * 替代原有的Spring ApplicationEvent机制，提供消息持久化和重试能力。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CountProducer {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 发送计数消息到Redis Streams
     *
     * @param countType 计数类型
     * @param targetId  目标记录ID
     * @param delta     增量（正数增加，负数减少）
     */
    public void sendCountMessage(CountType countType, Long targetId, int delta) {
        try {
            CountMessage message = new CountMessage(countType, targetId, delta);
            String json = objectMapper.writeValueAsString(message);
            ObjectRecord<String, String> record = StreamRecords.newRecord()
                    .ofObject(json)
                    .withStreamKey(RedisStreamConstants.COUNT_STREAM);
            stringRedisTemplate.opsForStream().add(record);
            log.info("计数消息发送成功, 计数类型: {}, 目标ID: {}, 增量: {}", countType, targetId, delta);
        } catch (Exception e) {
            log.error("计数消息发送失败, 计数类型: {}, 目标ID: {}, 增量: {}", countType, targetId, delta, e);
        }
    }
}
