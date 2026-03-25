package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.enums.user.UserStatus;
import com.inkstage.vo.admin.AdminUserDetailVO;
import com.inkstage.vo.admin.AdminUserListVO;
import com.inkstage.dto.admin.AdminUserQueryDTO;

import java.util.List;

/**
 * 用户管理服务接口（管理员）
 */
public interface UserAdminService {

    /**
     * 根据ID获取用户详情VO
     * @param id 用户ID
     * @return 用户详情VO
     */
    AdminUserDetailVO getUserDetailById(Long id);

    /**
     * 分页获取用户
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<AdminUserListVO> getUsersByPage(AdminUserQueryDTO pageRequest);

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
    void updateUserDetail(Long id, AdminUserDetailVO userDetailVO);

    /**
     * 更新用户状态
     * @param id 用户ID
     * @param userStatus 用户状态
     * @return 是否更新成功，true表示更新成功，false表示更新失败
     */
    Boolean updateUserStatus(Long id, UserStatus userStatus);
    
    /**
     * 更新用户状态并发送通知
     * @param id 用户ID
     * @param userStatus 用户状态
     * @param reason 状态变更原因
     * @return 是否更新成功，true表示更新成功，false表示更新失败
     */
    Boolean updateUserStatusWithNotification(Long id, UserStatus userStatus, String reason);

    /**
     * 根据角色代码获取用户ID列表
     * @param roleCode 角色代码
     * @return 用户ID列表
     */
    List<Long> getUserIdsByRoleCode(String roleCode);

    /**
     * 获取所有用户ID
     * @return 所有用户ID列表
     */
    List<Long> getAllUserIds();

}