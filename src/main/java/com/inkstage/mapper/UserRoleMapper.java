package com.inkstage.mapper;

import com.inkstage.entity.model.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper接口
 */
@Mapper
public interface UserRoleMapper {

    /**
     * 新增用户角色关联
     *
     * @param userRole 用户角色关联对象
     */
    void insert(UserRole userRole);
}