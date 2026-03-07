package com.inkstage.service;

/**
 * 通知缓存服务接口
 */
public interface NotificationCacheService {

    /**
     * 获取用户未读通知数量
     */
    int getUnreadCount(Long userId);

    /**
     * 增加未读通知数量
     */
    void incrementUnreadCount(Long userId);

    /**
     * 减少未读通知数量
     */
    void decrementUnreadCount(Long userId);

    /**
     * 重置未读通知数量
     */
    void resetUnreadCount(Long userId);

    /**
     * 批量更新未读通知数量
     */
    void batchUpdateUnreadCount(Long userId, int count);
}
