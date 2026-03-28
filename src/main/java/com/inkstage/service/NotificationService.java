package com.inkstage.service;

import com.inkstage.common.PageRequest;
import com.inkstage.common.PageResult;
import com.inkstage.entity.model.Notification;
import com.inkstage.enums.NotificationCategory;
import com.inkstage.enums.NotificationType;

import java.util.List;
import java.util.Map;

/**
 * 通知服务接口
 */
public interface NotificationService {
    
    /**
     * 获取用户的通知列表
     */
    List<Notification> getNotificationList(Long userId, NotificationType type);
    
    /**
     * 标记通知为已读
     */
    boolean markAsRead(Long notificationId);
    
    /**
     * 标记所有通知为已读
     */
    boolean markAllAsRead(Long userId);
    
    /**
     * 删除通知
     */
    boolean deleteNotification(Long notificationId);
    
    /**
     * 发送通知
     */
    boolean sendNotification(Notification notification);

    /**
     * 发送带模板的通知
     */
    boolean sendNotificationWithTemplate(Long userId, NotificationType type, Long relatedId, Long senderId, Object... params);

    /**
     * 批量发送通知
     */
    boolean sendBatchNotifications(List<Notification> notifications);

    /**
     * 批量发送带模板的通知
     */
    boolean sendBatchNotificationsWithTemplate(List<Map<String, Object>> notificationDataList);

    /**
     * 获取用户未读通知数量
     */
    int getUnreadCount(Long userId);
    
    /**
     * 同步用户未读通知数量到缓存
     */
    void syncUnreadCount(Long userId);
    
    /**
     * 分页获取用户的通知列表
     */
    PageResult<Notification> getNotificationListWithPage( NotificationType type, Integer pageNum, Integer pageSize);

    /**
     * 按分类获取用户的通知列表
     */
    PageResult<Notification> getNotificationsByCategory(Long userId, NotificationCategory category, Integer pageNum, Integer pageSize);

    /**
     * 按分类获取用户未读通知数量
     */
    Map<NotificationCategory, Integer> getUnreadCountByCategory(Long userId);

    /**
     * 按分类标记通知为已读
     */
    boolean markAsReadByCategory(Long userId, NotificationCategory category);

    /**
     * 清空用户通知
     */
    boolean clearNotifications(Long userId);

    /**
     * 获取聚合后的通知
     */
    List<Notification> getAggregatedNotifications(Long userId);
}
