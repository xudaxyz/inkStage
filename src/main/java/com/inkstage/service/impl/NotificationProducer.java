package com.inkstage.service.impl;

import com.inkstage.config.rabbitmq.RabbitMQConfig;
import com.inkstage.dto.NotificationMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 通知消息生产者
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送通知消息到RabbitMQ
     */
    public void sendNotification(NotificationMessageDTO message) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE,
                    RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                    message
            );
            log.info("通知消息发送成功，用户ID: {}", message.getUserId());
        } catch (Exception e) {
            log.error("通知消息发送失败，用户ID: {}", message.getUserId(), e);
            // 这里可以添加重试机制或存储到数据库作为备份
        }
    }

    /**
     * 批量发送通知消息
     */
    public void sendBatchNotifications(java.util.List<NotificationMessageDTO> messages) {
        for (NotificationMessageDTO message : messages) {
            sendNotification(message);
        }
    }
}
