package com.inkstage.service;

import com.inkstage.entity.model.User;

/**
 * 用户注册服务接口
 */
public interface UserRegistrationService {

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean isUsernameExists(String username);

    /**
     * 检查邮箱是否已存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean isEmailExists(String email);

    /**
     * 检查手机号是否已存在
     * @param phone 手机号
     * @return 是否存在
     */
    boolean isPhoneExists(String phone);

    /**
     * 创建用户
     * @param user 用户信息
     * @return 用户信息
     */
    User createUser(User user);
}