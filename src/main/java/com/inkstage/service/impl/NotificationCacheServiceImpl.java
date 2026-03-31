package com.inkstage.service.impl;

import com.inkstage.enums.notification.NotificationCategory;
import com.inkstage.service.NotificationCacheService;
import com.inkstage.cache.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.inkstage.cache.constant.RedisKeyConstants.*;

/**
 * 通知缓存服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCacheServiceImpl implements NotificationCacheService {

    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void cacheUnreadCount(Long userId, int count) {
        String key = NOTIFICATION_UNREAD_COUNT + userId;
        redisUtil.set(key, count, 30, TimeUnit.MINUTES);
    }

    @Override
    public void cacheUnreadCountByCategory(Long userId, Map<NotificationCategory, Integer> countMap) {
        String key = NOTIFICATION_UNREAD_COUNT_BY_CATEGORY + userId;
        try {
            String json = objectMapper.writeValueAsString(countMap);
            redisUtil.set(key, json, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("缓存未读数量失败", e);
        }
    }

    @Override
    public Integer getCachedUnreadCount(Long userId) {
        String key = NOTIFICATION_UNREAD_COUNT + userId;
        return redisUtil.get(key, Integer.class);
    }

    @Override
    public Map<NotificationCategory, Integer> getCachedUnreadCountByCategory(Long userId) {
        String key = NOTIFICATION_UNREAD_COUNT_BY_CATEGORY + userId;
        String json = redisUtil.get(key, String.class);
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("获取缓存未读数量失败", e);
            return null;
        }
    }

    @Override
    public void clearUnreadCountCache(Long userId) {
        String key1 = NOTIFICATION_UNREAD_COUNT + userId;
        String key2 = NOTIFICATION_UNREAD_COUNT_BY_CATEGORY + userId;
        redisUtil.delete(key1);
        redisUtil.delete(key2);
    }

    @Override
    public void cacheRecentNotifications(Long userId, String notifications) {
        String key = NOTIFICATION_RECENT_LIST + userId;
        redisUtil.set(key, notifications, 5, TimeUnit.MINUTES);
    }

    @Override
    public String getCachedRecentNotifications(Long userId) {
        String key = NOTIFICATION_RECENT_LIST + userId;
        return redisUtil.get(key, String.class);
    }

    @Override
    public void clearNotificationsCache(Long userId) {
        String key = NOTIFICATION_RECENT_LIST + userId;
        redisUtil.delete(key);
    }

    @Override
    public void decrementUnreadCount(Long userId) {
        String key = NOTIFICATION_UNREAD_COUNT + userId;
        redisUtil.decrement(key);
    }

    @Override
    public void incrementUnreadCount(Long userId) {
        String key = NOTIFICATION_UNREAD_COUNT + userId;
        redisUtil.increment(key);
    }

    @Override
    public void resetUnreadCount(Long userId) {
        String key = NOTIFICATION_UNREAD_COUNT + userId;
        redisUtil.set(key, 0);
    }

    @Override
    public void batchUpdateUnreadCount(Long userId, int count) {
        cacheUnreadCount(userId, count);
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        return getCachedUnreadCount(userId);
    }
}