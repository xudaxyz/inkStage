package com.inkstage.service;

import com.inkstage.entity.model.Role;

/**
 * 角色Service接口
 */
public interface RoleService {

    /**
     * 根据ID获取角色
     * @param id 角色ID
     * @return 角色信息
     */
    Role getRoleById(Long id);

}