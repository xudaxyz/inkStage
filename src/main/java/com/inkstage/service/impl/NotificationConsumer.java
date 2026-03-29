package com.inkstage.service.impl;

import com.inkstage.config.rabbitmq.RabbitMQConfig;
import com.inkstage.dto.NotificationMessageDTO;
import com.inkstage.entity.model.Notification;
import com.inkstage.entity.model.NotificationSetting;
import com.inkstage.enums.PushedStatus;
import com.inkstage.enums.ReadStatus;
import com.inkstage.mapper.NotificationMapper;
import com.inkstage.service.EmailService;
import com.inkstage.service.NotificationCacheService;
import com.inkstage.service.NotificationSettingService;
import com.inkstage.service.UserService;
import com.inkstage.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 通知消息消费者
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

    /**
     * 从RabbitMQ队列接收通知消息
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(NotificationMessageDTO message) {
        log.info("开始处理通知消息，用户ID: {}", message.getUserId());
        
        try {
            // 构建通知实体
            Notification notification = buildNotification(message);
            
            // 保存到数据库
            int result = notificationMapper.insert(notification);
            if (result > 0) {
                log.info("通知保存成功，用户ID: {}, 通知ID: {}", message.getUserId(), notification.getId());
                
                try {
                    // 更新Redis缓存中的未读通知数量
                    notificationCacheService.incrementUnreadCount(message.getUserId());
                    
                    // 通过WebSocket发送实时通知
                    webSocketService.sendNotificationToUser(message.getUserId(), notification);
                    
                    // 发送未读通知数量
                    int unreadCount = notificationCacheService.getUnreadCount(message.getUserId());
                    webSocketService.sendUnreadCountToUser(message.getUserId(), unreadCount);
                    
                    // 发送邮件通知（如果用户开启了邮件通知）
                    try {
                        NotificationSetting setting = notificationSettingService.getNotificationSetting(message.getUserId());
                        if (setting != null && setting.getEmailNotification()) {
                            // 获取用户信息
                            com.inkstage.entity.model.User user = userService.getUserById(message.getUserId());
                            if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
                                // 构建邮件内容
                                String subject = notification.getTitle();
                                String content = notification.getContent() + "\n\n点击查看详情: " + notification.getActionUrl();
                                
                                // 发送邮件
                                emailService.sendNotificationEmail(user.getEmail(), subject, content);
                            }
                        }
                    } catch (Exception e) {
                        // 邮件发送失败不影响通知的保存
                        log.warn("发送邮件通知失败，用户ID: {}", message.getUserId(), e);
                    }
                } catch (Exception e) {
                    // WebSocket或缓存操作失败不影响通知的保存
                    log.warn("发送实时通知或更新缓存失败，用户ID: {}", message.getUserId(), e);
                }
            } else {
                log.error("通知保存失败，用户ID: {}", message.getUserId());
            }
        } catch (Exception e) {
            log.error("处理通知消息失败，用户ID: {}", message.getUserId(), e);
            // 可以考虑添加重试机制或死信队列处理
        }
    }

    /**
     * 构建通知实体
     */
    private Notification buildNotification(NotificationMessageDTO message) {
        Notification notification = new Notification();
        notification.setUserId(message.getUserId());
        notification.setNotificationType(message.getNotificationType());
        notification.setContent(message.getContent());
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setRelatedId(message.getRelatedId());
        notification.setRelatedType(message.getRelatedType());
        notification.setSenderId(message.getSenderId());
        notification.setPushedStatus(PushedStatus.NOT_PUSHED);
        notification.setActionUrl(message.getActionUrl());
        notification.setExtraData(message.getExtraData());
        notification.setTitle(message.getTitle());
        
        // 设置创建时间
        notification.setCreateTime(LocalDateTime.now());
        
        return notification;
    }
}
