package com.inkstage.service;

import com.inkstage.entity.model.User;

import java.util.Optional;

/**
 * 用户缓存服务接口
 * 用于缓存用户信息，减少数据库查询
 */
public interface UserCacheService {

    /**
     * 缓存用户信息
     *
     * @param user 用户对象
     */
    void cacheUser(User user);

    /**
     * 获取缓存的用户信息
     *
     * @param userId 用户ID
     * @return 可选的用户对象
     */
    Optional<User> getUserFromCache(String userId);

    /**
     * 删除用户缓存
     *
     * @param userId 用户ID
     */
    void removeUserCache(String userId);

    /**
     * 更新用户缓存
     *
     * @param user 用户对象
     */
    void updateUserCache(User user);
}
