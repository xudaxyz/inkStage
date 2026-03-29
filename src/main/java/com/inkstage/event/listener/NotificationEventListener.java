package com.inkstage.event.listener;

import com.inkstage.dto.NotificationMessageDTO;
import com.inkstage.entity.model.Notification;
import com.inkstage.entity.model.NotificationSetting;
import com.inkstage.enums.ReadStatus;
import com.inkstage.event.NotificationEvent;
import com.inkstage.mapper.NotificationMapper;
import com.inkstage.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 通知事件监听器
 * <p>
 * 监听通知事件并处理通知发送逻辑
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationMapper notificationMapper;
    private final NotificationCacheService notificationCacheService;
    private final WebSocketService webSocketService;
    private final NotificationSettingService notificationSettingService;
    private final EmailService emailService;
    private final UserService userService;

    /**
     * 处理通知事件
     * <p>
     * 使用异步处理，不阻塞主业务流程
     */
    @EventListener
    @Async("notificationTaskExecutor")
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("接收到通知事件，用户ID: {}, 类型: {}", event.getUserId(), event.getNotificationType());

        try {
            // 检查用户是否开启了该类型的通知
            if (!isNotificationEnabled(event.getUserId(), event.getNotificationType())) {
                log.info("用户 {} 已关闭 {} 类型的通知", event.getUserId(), event.getNotificationType());
                return;
            }

            // 构建并保存通知
            Notification notification = buildNotification(event);
            int result = notificationMapper.insert(notification);

            if (result > 0) {
                log.info("通知保存成功，用户ID: {}, 通知ID: {}", event.getUserId(), notification.getId());

                // 更新缓存
                notificationCacheService.incrementUnreadCount(event.getUserId());

                // 发送WebSocket实时通知
                sendWebSocketNotification(event.getUserId(), notification);

                // 发送邮件通知（如果开启）
                sendEmailNotification(event.getUserId(), event.getTitle(), event.getContent(), event.getActionUrl());
            } else {
                log.error("通知保存失败，用户ID: {}", event.getUserId());
            }
        } catch (Exception e) {
            log.error("处理通知事件失败，用户ID: {}", event.getUserId(), e);
        }
    }

    /**
     * 构建通知实体
     */
    private Notification buildNotification(NotificationEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setNotificationType(event.getNotificationType());
        notification.setTitle(event.getTitle());
        notification.setContent(event.getContent());
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setRelatedId(event.getRelatedId());
        notification.setRelatedType(event.getRelatedType());
        notification.setSenderId(event.getSenderId());
        notification.setActionUrl(event.getActionUrl());
        notification.setExtraData(event.getExtraData());
        notification.setCreateTime(LocalDateTime.now());
        return notification;
    }

    /**
     * 检查用户是否开启了该类型的通知
     */
    private boolean isNotificationEnabled(Long userId, com.inkstage.enums.NotificationType type) {
        String settingKey = switch (type) {
            case ARTICLE_PUBLISH -> "articlePublish";
            case ARTICLE_LIKE -> "articleLike";
            case ARTICLE_COLLECTION -> "articleCollection";
            case ARTICLE_COMMENT -> "articleComment";
            case COMMENT_REPLY -> "commentReply";
            case COMMENT_LIKE -> "commentLike";
            case FOLLOW -> "follow";
            case MESSAGE -> "message";
            case REPORT -> "report";
            case FEEDBACK -> "feedback";
            case SYSTEM, USER_STATUS_CHANGE, ARTICLE_REVIEW_REPROCESS, ARTICLE_TOP, ARTICLE_RECOMMEND,
                 ARTICLE_DELETE, ARTICLE_ONLINE, ARTICLE_OFFLINE, TAG_DELETE, ARTICLE_REVIEW_REJECT,
                 COMMENT_REVIEW_REJECT, COMMENT_TOP -> "system";
        };

        return notificationSettingService.isNotificationEnabled(userId, settingKey);
    }

    /**
     * 发送WebSocket实时通知
     */
    private void sendWebSocketNotification(Long userId, Notification notification) {
        try {
            NotificationMessageDTO message = new NotificationMessageDTO();
            message.setUserId(notification.getUserId());
            message.setTitle(notification.getTitle());
            message.setContent(notification.getContent());
            message.setNotificationType(notification.getNotificationType());
            message.setRelatedId(notification.getRelatedId());
            message.setRelatedType(notification.getRelatedType());
            message.setSenderId(notification.getSenderId());
            message.setActionUrl(notification.getActionUrl());
            message.setExtraData(notification.getExtraData());

            webSocketService.sendNotificationToUser(userId, notification);

            // 发送未读数量更新
            int unreadCount = notificationCacheService.getUnreadCount(userId);
            webSocketService.sendUnreadCountToUser(userId, unreadCount);
        } catch (Exception e) {
            log.warn("发送WebSocket通知失败，用户ID: {}", userId, e);
        }
    }

    /**
     * 发送邮件通知
     */
    private void sendEmailNotification(Long userId, String title, String content, String actionUrl) {
        try {
            NotificationSetting setting = notificationSettingService.getNotificationSetting(userId);
            if (setting == null || !setting.getEmailNotification()) {
                return;
            }

            com.inkstage.entity.model.User user = userService.getUserById(userId);
            if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
                return;
            }

            String emailContent = content + "\n\n点击查看详情: " + actionUrl;
            emailService.sendNotificationEmail(user.getEmail(), title, emailContent);
        } catch (Exception e) {
            log.warn("发送邮件通知失败，用户ID: {}", userId, e);
        }
    }
}
