package com.inkstage.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {

    // 通知交换机名称
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    // 通知队列名称
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    // 通知路由键
    public static final String NOTIFICATION_ROUTING_KEY = "notification.key";

    /**
     * 创建通知交换机
     */
    @Bean
    public Exchange notificationExchange() {
        return ExchangeBuilder.directExchange(NOTIFICATION_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 创建通知队列
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", NOTIFICATION_EXCHANGE + ".dlx")
                .withArgument("x-dead-letter-routing-key", NOTIFICATION_ROUTING_KEY + ".dlx")
                .withArgument("x-message-ttl", 600000) // 10分钟过期
                .build();
    }

    /**
     * 创建死信交换机
     */
    @Bean
    public Exchange notificationDlxExchange() {
        return ExchangeBuilder.directExchange(NOTIFICATION_EXCHANGE + ".dlx")
                .durable(true)
                .build();
    }

    /**
     * 创建死信队列
     */
    @Bean
    public Queue notificationDlxQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE + ".dlx")
                .build();
    }

    /**
     * 绑定通知队列到交换机
     */
    @Bean
    public Binding notificationBinding(Queue notificationQueue, Exchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(notificationExchange)
                .with(NOTIFICATION_ROUTING_KEY)
                .noargs();
    }

    /**
     * 绑定死信队列到死信交换机
     */
    @Bean
    public Binding notificationDlxBinding(Queue notificationDlxQueue, Exchange notificationDlxExchange) {
        return BindingBuilder.bind(notificationDlxQueue)
                .to(notificationDlxExchange)
                .with(NOTIFICATION_ROUTING_KEY + ".dlx")
                .noargs();
    }

    /**
     * 配置消息转换器，允许反序列化指定的类
     */
    @Bean
    public SimpleMessageConverter messageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        // 添加允许反序列化的类
        List<String> allowedList = new ArrayList<>();
        allowedList.add("com.inkstage.dto.NotificationMessageDTO");
        allowedList.add("com.inkstage.enums.notification.NotificationType");
        allowedList.add("com.inkstage.enums.ReportTargetType");
        allowedList.add("java.lang.Enum");
        converter.setAllowedListPatterns(allowedList);
        return converter;
    }
}
