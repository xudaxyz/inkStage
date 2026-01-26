package com.inkstage.service;

import com.inkstage.entity.model.User;

/**
 * 用户角色关联Service接口
 */
public interface UserRoleService {

    /**
     * 创建用户角色关联
     * @param user 用户
     */
    void createUserRole(User user);
}