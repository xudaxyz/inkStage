package com.inkstage.service.impl;

import com.inkstage.cache.service.CacheManager;
import com.inkstage.enums.notification.NotificationCategory;
import com.inkstage.service.NotificationCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static com.inkstage.cache.constant.CacheKey.*;

/**
 * 通知缓存服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCacheServiceImpl implements NotificationCacheService {

    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager;

    @Override
    public void cacheUnreadCount(Long userId, int count) {
        String key = NOTIFICATION_UNREAD_COUNT + userId;
        cacheManager.set(key, count);
    }

    @Override
    public void cacheUnreadCountByCategory(Long userId, Map<NotificationCategory, Integer> countMap) {
        String key = NOTIFICATION_UNREAD_COUNT_BY_CATEGORY + userId;
        try {
            String json = objectMapper.writeValueAsString(countMap);
            cacheManager.set(key, json);
        } catch (Exception e) {
            log.error("缓存未读数量失败", e);
        }
    }

    @Override
    public Integer getCachedUnreadCount(Long userId) {
        String key = NOTIFICATION_UNREAD_COUNT + userId;
        return cacheManager.get(key, Integer.class);
    }

    @Override
    public Map<NotificationCategory, Integer> getCachedUnreadCountByCategory(Long userId) {
        String key = NOTIFICATION_UNREAD_COUNT_BY_CATEGORY + userId;
        String json = cacheManager.get(key, String.class);
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
        String unreadCountKey = NOTIFICATION_UNREAD_COUNT + userId;
        String unreadCountByCateGoryKey = NOTIFICATION_UNREAD_COUNT_BY_CATEGORY + userId;
        cacheManager.delete(unreadCountKey);
        cacheManager.delete(unreadCountByCateGoryKey);
    }

    @Override
    public void cacheRecentNotifications(Long userId, String notifications) {
        String key = NOTIFICATION_RECENT_LIST + userId;
        cacheManager.set(key, notifications);
    }

    @Override
    public String getCachedRecentNotifications(Long userId) {
        String key = NOTIFICATION_RECENT_LIST + userId;
        return cacheManager.get(key, String.class);
    }

    @Override
    public void clearNotificationsCache(Long userId) {
        String key = NOTIFICATION_RECENT_LIST + userId;
        cacheManager.delete(key);
    }

    @Override
    public void decrementUnreadCount(Long userId) {
        String key = NOTIFICATION_UNREAD_COUNT + userId;
        cacheManager.decrement(key);
    }

    @Override
    public void incrementUnreadCount(Long userId) {
        String key = NOTIFICATION_UNREAD_COUNT + userId;
        Long increment = cacheManager.increment(key);
        log.info("增加用户[{}] 通知未读数量: {}", userId, increment);
    }

    @Override
    public void resetUnreadCount(Long userId) {
        String key = NOTIFICATION_UNREAD_COUNT + userId;
        cacheManager.set(key, 0);
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