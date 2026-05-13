package com.inkstage.cache.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.constant.CacheTTL;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.cache.service.UserCacheService;
import com.inkstage.entity.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 用户缓存服务实现类
 * 用于缓存用户信息，减少数据库查询
 * 使用 CacheManager 实现缓存管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheServiceImpl implements UserCacheService {

    private final CacheManager cacheManager;

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
            String cacheKey = CacheKey.keyForUserInfo(user.getId());
            cacheManager.setWithRandomOffset(cacheKey, user, CacheTTL.USER_INFO);
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
    public Optional<User> getUserFromCache(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        try {
            String cacheKey = CacheKey.keyForUserInfo(userId);
            User user = cacheManager.get(cacheKey, User.class);
            if (user != null) {
                log.debug("从缓存中获取用户信息, 用户ID: {}", userId);
                return Optional.of(user);
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
    public void removeUserCache(Long userId) {
        if (userId == null) {
            return;
        }

        try {
            String cacheKey = CacheKey.keyForUserInfo(userId);
            cacheManager.delete(cacheKey);
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
