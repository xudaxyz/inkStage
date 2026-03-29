package com.inkstage.cache.service.impl;

import com.inkstage.cache.constant.RedisKeyConstants;
import com.inkstage.entity.model.User;
import com.inkstage.cache.service.UserCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * 用户缓存服务实现类
 * 用于缓存用户信息，减少数据库查询
 */
@Slf4j
@Service
public class UserCacheServiceImpl implements UserCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户信息缓存过期时间：30分钟
     */
    private static final Duration USER_CACHE_EXPIRY = Duration.ofMinutes(30);

    public UserCacheServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 缓存用户信息
     *
     * @param user 用户对象
     */
    @Override
    public void cacheUser(User user) {
        if (user == null || user.getId() == null) {
            return;
        }

        try {
            String key = RedisKeyConstants.USER_PREFIX + user.getId();
            redisTemplate.opsForValue().set(key, user, USER_CACHE_EXPIRY);
            log.debug("缓存用户信息, 用户ID: {}", user.getId());
        } catch (Exception e) {
            log.error("缓存用户信息失败", e);
        }
    }

    /**
     * 获取缓存的用户信息
     *
     * @param userId 用户ID
     * @return 可选的用户对象
     */
    @Override
    public Optional<User> getUserFromCache(String userId) {
        if (userId == null) {
            return Optional.empty();
        }

        try {
            String key = RedisKeyConstants.USER_PREFIX + userId;
            Object value = redisTemplate.opsForValue().get(key);
            if (value instanceof User) {
                log.debug("从缓存中获取用户信息, 用户ID: {}", userId);
                return Optional.of((User) value);
            }
        } catch (Exception e) {
            log.error("从缓存中获取用户信息失败", e);
        }

        return Optional.empty();
    }

    /**
     * 删除用户缓存
     *
     * @param userId 用户ID
     */
    @Override
    public void removeUserCache(String userId) {
        if (userId == null) {
            return;
        }

        try {
            String key = RedisKeyConstants.USER_PREFIX + userId;
            redisTemplate.delete(key);
            log.debug("删除用户缓存, 用户ID: {}", userId);
        } catch (Exception e) {
            log.error("删除用户缓存失败", e);
        }
    }

    /**
     * 更新用户缓存
     *
     * @param user 用户对象
     */
    @Override
    public void updateUserCache(User user) {
        cacheUser(user);
    }
}
