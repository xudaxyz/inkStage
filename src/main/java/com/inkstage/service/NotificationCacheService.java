package com.inkstage.service;

import com.inkstage.enums.NotificationCategory;

import java.util.Map;

/**
 * 通知缓存服务接口
 */
public interface NotificationCacheService {

    /**
     * 缓存用户未读通知数量
     */
    void cacheUnreadCount(Long userId, int count);

    /**
     * 缓存用户按分类未读通知数量
     */
    void cacheUnreadCountByCategory(Long userId, Map<NotificationCategory, Integer> countMap);

    /**
     * 获取缓存的用户未读通知数量
     */
    Integer getCachedUnreadCount(Long userId);

    /**
     * 获取缓存的用户按分类未读通知数量
     */
    Map<NotificationCategory, Integer> getCachedUnreadCountByCategory(Long userId);

    /**
     * 清除用户未读通知数量缓存
     */
    void clearUnreadCountCache(Long userId);

    /**
     * 缓存用户最近的通知列表
     */
    void cacheRecentNotifications(Long userId, String notifications);

    /**
     * 获取缓存的用户最近的通知列表
     */
    String getCachedRecentNotifications(Long userId);

    /**
     * 清除用户通知列表缓存
     */
    void clearNotificationsCache(Long userId);

    /**
     * 减少用户未读通知数量
     */
    void decrementUnreadCount(Long userId);

    /**
     * 增加用户未读通知数量
     */
    void incrementUnreadCount(Long userId);

    /**
     * 重置用户未读通知数量
     */
    void resetUnreadCount(Long userId);

    /**
     * 批量更新用户未读通知数量
     */
    void batchUpdateUnreadCount(Long userId, int count);

    /**
     * 获取用户未读通知数量
     */
    Integer getUnreadCount(Long userId);
}