package com.inkstage.service;

import com.inkstage.entity.model.User;
import com.inkstage.vo.front.HotUserVO;

import java.util.List;

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
    
    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 根据邮箱获取用户
     * @param email 邮箱
     * @return 用户信息
     */
    User getUserByEmail(String email);

    /**
     * 根据手机号获取用户
     * @param phone 手机号
     * @return 用户信息
     */
    User getUserByPhone(String phone);

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    User updateUser(User user);

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Long id);

    /**
     * 获取热门用户
     *
     * @param limit 限制数量
     * @return 热门用户列表
     */
    List<HotUserVO> getHotUsers(Integer limit);

}