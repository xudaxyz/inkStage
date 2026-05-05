package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.entity.model.Notification;
import com.inkstage.enums.PushedStatus;
import com.inkstage.enums.ReadStatus;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.enums.common.Priority;
import com.inkstage.enums.notification.NotificationCategory;
import com.inkstage.enums.notification.NotificationTemplateVariable;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.mapper.NotificationMapper;
import com.inkstage.notification.NotificationParam;
import com.inkstage.service.*;
import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 通知服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationProducer notificationProducer;
    private final NotificationCacheService notificationCacheService;
    private final NotificationTemplateService notificationTemplateService;
    private final NotificationSettingService notificationSettingService;
    private final WebSocketService webSocketService;

    @Override
    public boolean send(NotificationParam param) {
        Long userId = param.getUserId();
        NotificationType notificationType = param.getNotificationType();

        // 检查用户是否开启了该类型的通知
        if (isNotificationDisabled(userId, notificationType)) {
            return false;
        }
        Notification notification = buildNotificationFromParam(param);

        if (notification != null) {
            // 发送通知
            return sendNotification(notification);
        }
        // 通知内容为空, 返回false
        return false;
    }

    @Override
    public boolean sendBatch(List<? extends NotificationParam> params) {
        if (params == null || params.isEmpty()) {
            return true;
        }

        List<Notification> notifications = params.stream()
                .map(this::buildNotificationFromParam)
                .filter(notification -> notification != null && !isNotificationDisabled(notification.getUserId(), notification.getNotificationType()))
                .toList();

        if (notifications.isEmpty()) {
            return true;
        }

        return sendBatchNotifications(notifications);
    }

    @Override
    public boolean markAsRead(Long notificationId) {
        int result = notificationMapper.updateReadStatus(notificationId, ReadStatus.READ);
        if (result > 0) {
            Notification notification = notificationMapper.selectById(notificationId);
            if (notification != null) {
                notificationCacheService.decrementUnreadCount(notification.getUserId());
                int unreadCount = notificationCacheService.getUnreadCount(notification.getUserId());
                webSocketService.sendUnreadCountToUser(notification.getUserId(), unreadCount);
            }
        }
        return result > 0;
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        int result = notificationMapper.updateAllReadStatus(userId, ReadStatus.READ);
        if (result > 0) {
            notificationCacheService.resetUnreadCount(userId);
            webSocketService.sendUnreadCountToUser(userId, 0);
        }
        return result > 0;
    }

    @Override
    public boolean deleteNotification(Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        int result = notificationMapper.deleteById(notificationId);
        if (result > 0 && notification != null && ReadStatus.UNREAD.equals(notification.getReadStatus())) {
            notificationCacheService.decrementUnreadCount(notification.getUserId());
            int unreadCount = notificationCacheService.getUnreadCount(notification.getUserId());
            webSocketService.sendUnreadCountToUser(notification.getUserId(), unreadCount);
        }
        return result > 0;
    }

    @Override
    public boolean sendNotification(Notification notification) {
        // 发送到RabbitMQ
        notificationProducer.sendNotification(notification);
        return true;
    }

    /**
     * 检查用户是否开启了该类型的通知
     *
     * @param userId           用户ID
     * @param notificationType 通知类型
     * @return 是否开启
     */
    private boolean isNotificationDisabled(Long userId, NotificationType notificationType) {
        boolean enabled = notificationSettingService.isNotificationEnabled(userId, notificationType);
        if (!enabled) {
            log.info("用户 ID {} 已关闭 {} 类型的通知", userId, notificationType.getDesc());
        }
        return !enabled;
    }

    @Override
    public boolean sendBatchNotifications(List<Notification> notifications) {
        try {
            // 批量发送到RabbitMQ
            if (!notifications.isEmpty()) {
                notificationProducer.sendBatchNotifications(notifications);
            }
            return true;
        } catch (Exception e) {
            log.error("批量发送通知失败", e);
            return false;
        }
    }

    @Override
    public int getUnreadCount(Long userId) {
        // 尝试从缓存获取
        Integer cachedCount = notificationCacheService.getCachedUnreadCount(userId);
        if (cachedCount != null) {
            return cachedCount;
        }
        // 从数据库查询
        int unreadCount = notificationMapper.countUnreadByUserId(userId);
        // 更新缓存
        notificationCacheService.cacheUnreadCount(userId, unreadCount);
        return unreadCount;
    }

    @Override
    public void syncUnreadCount(Long userId) {
        // 从数据库查询实际未读数量
        int unreadCount = notificationMapper.countUnreadByUserId(userId);
        // 更新缓存
        notificationCacheService.batchUpdateUnreadCount(userId, unreadCount);
    }

    @Override
    public PageResult<Notification> getNotificationListWithPage(NotificationType type, Integer pageNum, Integer pageSize) {
        List<Notification> notifications;
        int total;
        Long userId = UserContext.getCurrentUserId();

        int offset = (pageNum - 1) * pageSize;

        if (type == null) {
            notifications = notificationMapper.selectByUserIdWithPage(userId, offset, pageSize);
            total = notificationMapper.countByUserId(userId);
        } else {
            notifications = notificationMapper.selectByUserIdAndTypeWithPage(userId, type, offset, pageSize);
            total = notificationMapper.countByUserIdAndType(userId, type);
        }

        return PageResult.build(notifications, (long) total, pageNum, pageSize);
    }

    @Override
    public PageResult<Notification> getNotificationsByCategory(Long userId, NotificationCategory category, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Notification> notifications = notificationMapper.selectByUserIdAndCategoryWithPage(userId, category, offset, pageSize);
        int total = notificationMapper.countByUserIdAndCategory(userId, category);
        return PageResult.build(notifications, (long) total, pageNum, pageSize);
    }

    @Override
    public Map<NotificationCategory, Integer> getUnreadCountByCategory(Long userId) {
        // 尝试从缓存获取
        Map<NotificationCategory, Integer> cachedCountMap = notificationCacheService.getCachedUnreadCountByCategory(userId);
        if (cachedCountMap != null) {
            return cachedCountMap;
        }

        // 从数据库查询
        List<Map<String, Object>> countList = notificationMapper.countUnreadByUserIdGroupByCategory(userId);
        Map<NotificationCategory, Integer> countMap = new java.util.HashMap<>();

        // 初始化所有分类的未读数量为0
        for (NotificationCategory cat : NotificationCategory.values()) {
            countMap.put(cat, 0);
        }

        // 填充查询结果
        for (Map<String, Object> map : countList) {
            String categoryName = (String) map.get("category");
            Integer count = (Integer) map.get("count");
            try {
                NotificationCategory category = NotificationCategory.valueOf(categoryName);
                countMap.put(category, count);
            } catch (IllegalArgumentException e) {
                log.warn("Unknown notification category: {}", categoryName);
            }
        }

        // 缓存结果
        notificationCacheService.cacheUnreadCountByCategory(userId, countMap);
        return countMap;
    }

    @Override
    public boolean markAsReadByCategory(Long userId, NotificationCategory category) {
        int result = notificationMapper.updateByUserIdAndCategoryReadStatus(userId, category, ReadStatus.READ);
        if (result > 0) {
            // 清除缓存
            notificationCacheService.clearUnreadCountCache(userId);
        }
        return result > 0;
    }

    @Override
    public boolean clearNotifications(Long userId) {
        int result = notificationMapper.deleteByUserId(userId);
        if (result > 0) {
            // 清除缓存
            notificationCacheService.clearUnreadCountCache(userId);
            notificationCacheService.clearNotificationsCache(userId);
        }
        return result > 0;
    }

    @Override
    public List<Notification> getAggregatedNotifications(Long userId) {
        return notificationMapper.selectByUserId(userId);
    }

    /**
     * 构建通知对象
     *
     * @param userId           用户ID
     * @param notificationType 通知类型
     * @param content          通知内容
     * @return 通知对象
     */
    private Notification buildNotification(Long userId, NotificationType notificationType, Map<String, Object> content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setNotificationType(notificationType);
        notification.setPriority(Priority.NORMAL);
        notification.setPushedStatus(PushedStatus.PUSHED);
        notification.setPushTime(LocalDateTime.now());
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setTitle((String) content.get("title"));
        notification.setContent((String) content.get("content"));
        notification.setRelatedId((Long) content.get("relatedId"));
        notification.setSenderId((Long) content.get("senderId"));
        notification.setActionUrl((String) content.get("actionUrl"));
        notification.setExtraData((String) content.get("extraData"));
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        notification.setDeleted(DeleteStatus.NOT_DELETED);
        return notification;
    }

    /**
     * 从通知参数中获取对应的值, 并转换成对应的通知
     *
     * @param notificationParam 通知参数
     * @return Notification
     */
    private Notification buildNotificationFromParam(NotificationParam notificationParam) {
        Long userId = notificationParam.getUserId();
        NotificationType notificationType = notificationParam.getNotificationType();

        // 将参数转换为 Map
        Map<String, Object> params = notificationParam.toMap();
        params.put(NotificationTemplateVariable.NOTIFICATION_TIME.getKey(), LocalDateTime.now().toString());
        if (notificationParam.getSenderId() != null) {
            params.put(NotificationTemplateVariable.SENDER_ID.getKey(), notificationParam.getSenderId());
        } else {
            params.put(NotificationTemplateVariable.SENDER_ID.getKey(), 0L);
        }

        // 使用模板生成通知内容
        Map<String, Object> notificationContent = notificationTemplateService.generateNotificationContent(notificationType, params);

        if (notificationContent != null) {
            return buildNotification(userId, notificationType, notificationContent);
        }
        return null;
    }
}
