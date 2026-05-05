package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.entity.model.Notification;
import com.inkstage.enums.notification.NotificationCategory;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.notification.NotificationParam;

import java.util.List;
import java.util.Map;

/**
 * 通知服务接口
 */
public interface NotificationService {

    /**
     * 发送通知(对外开放)
     *
     * @param param 通知参数对象
     * @return 是否发送成功
     */
    boolean send(NotificationParam param);

    /**
     * 批量发送通知(对外开放)
     *
     * @param params 通知参数对象列表
     * @return 是否发送成功
     */
    boolean sendBatch(List<? extends NotificationParam> params);

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
     * 发送通知到rabbitmq(一般不对外开放)
     */
    boolean sendNotification(Notification notification);

    /**
     * 批量发送通知到rabbitmq(一般不对外开放)
     *
     * @param notifications 通知列表
     * @return 是否发送成功
     */
    boolean sendBatchNotifications(List<Notification> notifications);

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
    PageResult<Notification> getNotificationListWithPage(NotificationType notificationType, Integer pageNum, Integer pageSize);

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
