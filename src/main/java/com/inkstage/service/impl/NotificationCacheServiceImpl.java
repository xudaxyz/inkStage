package com.inkstage.service.impl;

import com.inkstage.constant.RedisKeyConstants;
import com.inkstage.service.NotificationCacheService;
import com.inkstage.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通知缓存服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCacheServiceImpl implements NotificationCacheService {

    private final RedisUtil redisUtil;

    /**
     * 获取用户未读通知数量
     */
    @Override
    public int getUnreadCount(Long userId) {
        try {
            String key = RedisKeyConstants.NOTIFICATION_UNREAD_COUNT + userId;
            Object value = redisUtil.get(key);
            if (value != null) {
                return Integer.parseInt(value.toString());
            }
        } catch (Exception e) {
            log.error("获取未读通知数量失败，用户ID: {}", userId, e);
        }
        return 0;
    }

    /**
     * 增加未读通知数量
     */
    @Override
    public void incrementUnreadCount(Long userId) {
        try {
            String key = RedisKeyConstants.NOTIFICATION_UNREAD_COUNT + userId;
            redisUtil.increment(key);
            // 设置过期时间为7天
            redisUtil.expire(key, 7 * 24 * 60 * 60);
        } catch (Exception e) {
            log.error("增加未读通知数量失败，用户ID: {}", userId, e);
        }
    }

    /**
     * 减少未读通知数量
     */
    @Override
    public void decrementUnreadCount(Long userId) {
        try {
            String key = RedisKeyConstants.NOTIFICATION_UNREAD_COUNT + userId;
            redisUtil.decrement(key);
        } catch (Exception e) {
            log.error("减少未读通知数量失败，用户ID: {}", userId, e);
        }
    }

    /**
     * 重置未读通知数量
     */
    @Override
    public void resetUnreadCount(Long userId) {
        try {
            String key = RedisKeyConstants.NOTIFICATION_UNREAD_COUNT + userId;
            redisUtil.delete(key);
        } catch (Exception e) {
            log.error("重置未读通知数量失败，用户ID: {}", userId, e);
        }
    }

    /**
     * 批量更新未读通知数量
     */
    @Override
    public void batchUpdateUnreadCount(Long userId, int count) {
        try {
            String key = RedisKeyConstants.NOTIFICATION_UNREAD_COUNT + userId;
            if (count > 0) {
                redisUtil.set(key, count);
                // 设置过期时间为7天
                redisUtil.expire(key, 7 * 24 * 60 * 60);
            } else {
                redisUtil.delete(key);
            }
        } catch (Exception e) {
            log.error("批量更新未读通知数量失败，用户ID: {}", userId, e);
        }
    }
}
