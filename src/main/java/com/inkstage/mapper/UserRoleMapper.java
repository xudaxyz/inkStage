package com.inkstage.mapper;

import com.inkstage.entity.model.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<UserRole> selectByUserId(@Param("userId") Long userId);
}