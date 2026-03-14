package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.entity.model.User;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.enums.user.UserStatus;
import com.inkstage.vo.front.HotUserVO;

import java.util.List;

/**
 * 用户服务接口
 * 提供用户的创建、查询、更新、删除等核心功能
 */
public interface UserService {

    /**
     * 检查用户名是否已存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean isUsernameExists(String username);

    /**
     * 检查邮箱是否已存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean isEmailExists(String email);

    /**
     * 检查手机号是否已存在
     *
     * @param phone 手机号
     * @return 是否存在
     */
    boolean isPhoneExists(String phone);

    /**
     * 创建用户
     *
     * @param user 用户信息，包含用户名、密码、邮箱等
     * @return 创建后的用户信息
     */
    User createUser(User user);
    
    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 根据邮箱获取用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    User getUserByEmail(String email);

    /**
     * 根据手机号获取用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    User getUserByPhone(String phone);

    /**
     * 更新用户信息
     *
     * @param user 用户信息，包含需要更新的字段
     * @return 更新后的用户信息
     */
    User updateUser(User user);

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Long id);

    /**
     * 根据ID获取用户详情VO（管理员）
     *
     * @param id 用户ID
     * @return 用户详情VO，包含完整的用户信息
     */
    com.inkstage.vo.admin.AdminUserDetailVO getUserDetailById(Long id);

    /**
     * 获取热门用户
     *
     * @param limit 限制数量
     * @return 热门用户列表
     */
    List<HotUserVO> getHotUsers(Integer limit);

    /**
     * 分页获取用户（管理员）
     *
     * @param pageRequest 分页请求，包含关键词、状态等过滤条件
     * @return 分页结果
     */
    PageResult<com.inkstage.vo.admin.AdminUserListVO> getUsersByPage(com.inkstage.dto.admin.AdminUserQueryDTO pageRequest);

    /**
     * 删除用户（软删除）
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 更新用户详情（管理员）
     *
     * @param id           用户ID
     * @param userDetailVO 用户详情VO，包含需要更新的详细信息
     */
    void updateUserDetail(Long id, com.inkstage.vo.admin.AdminUserDetailVO userDetailVO);

    /**
     * 更新用户状态
     *
     * @param id         用户ID
     * @param userStatus 用户状态
     * @return 是否更新成功，true表示更新成功，false表示更新失败
     */
    Boolean updateUserStatus(Long id, UserStatus userStatus);

    /**
     * 获取用户资料（包含完整的图片URL）
     *
     * @param id 用户ID
     * @return 用户信息，包含完整的图片URL
     */
    User getUserProfile(Long id);
}