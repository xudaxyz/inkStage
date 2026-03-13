package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.entity.model.User;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.enums.user.UserStatus;
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
     * 根据ID获取用户详情VO
     * @param id 用户ID
     * @return 用户详情VO
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
     * 分页获取用户
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<com.inkstage.vo.admin.AdminUserListVO> getUsersByPage(com.inkstage.dto.admin.AdminUserQueryDTO pageRequest);

    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 更新用户详情
     * @param id 用户ID
     * @param userDetailVO 用户详情VO
     */
    void updateUserDetail(Long id, com.inkstage.vo.admin.AdminUserDetailVO userDetailVO);

    /**
     * 更新用户状态
     * @param id 用户ID
     * @param userStatus 用户状态
     * @return 是否更新成功，true表示更新成功，false表示更新失败
     */
    Boolean updateUserStatus(Long id, UserStatus userStatus);

}