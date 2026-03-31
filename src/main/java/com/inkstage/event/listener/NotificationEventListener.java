package com.inkstage.event.listener;

import com.inkstage.entity.model.Notification;
import com.inkstage.enums.PushedStatus;
import com.inkstage.enums.ReadStatus;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.event.NotificationEvent;
import com.inkstage.mapper.NotificationMapper;
import com.inkstage.service.NotificationCacheService;
import com.inkstage.service.NotificationSettingService;
import com.inkstage.service.WebSocketService;
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
            boolean enabled = notificationSettingService.isNotificationEnabled(event.getUserId(), event.getNotificationType());
            if (!enabled) {
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
                webSocketService.sendNotificationToUser(event.getUserId(), notification);

                // 发送未读数量更新
                int unreadCount = notificationCacheService.getUnreadCount(event.getUserId());
                webSocketService.sendUnreadCountToUser(event.getUserId(), unreadCount);
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
        notification.setPushedStatus(PushedStatus.PUSHED);
        LocalDateTime now = LocalDateTime.now();
        notification.setPushTime(now);
        notification.setRelatedId(event.getRelatedId());
        notification.setRelatedType(event.getRelatedType());
        notification.setSenderId(event.getSenderId());
        notification.setActionUrl(event.getActionUrl());
        notification.setExtraData(event.getExtraData());
        notification.setCreateTime(now);
        notification.setUpdateTime(now);
        notification.setDeleted(DeleteStatus.NOT_DELETED);

        return notification;
    }

}
