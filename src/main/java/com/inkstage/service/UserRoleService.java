package com.inkstage.service;

import com.inkstage.entity.model.User;
import com.inkstage.entity.model.UserRole;

import java.util.List;

/**
 * 用户角色关联Service接口
 */
public interface UserRoleService {

    /**
     * 创建用户角色关联
     * @param user 用户
     */
    void createUserRole(User user);

    /**
     * 根据用户ID获取角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<UserRole> getUserRoles(Long userId);
}