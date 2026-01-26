package com.inkstage.service;

import com.inkstage.entity.model.User;

/**
 * 用户Service接口
 */
public interface UserService {

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean isUsernameExists(String username);

    /**
     * 创建用户
     * @param user 用户信息
     * @return 用户信息
     */
    User createUser(User user);
    
    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
}