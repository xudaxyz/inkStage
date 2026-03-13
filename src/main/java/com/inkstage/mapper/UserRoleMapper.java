package com.inkstage.mapper;

import com.inkstage.entity.model.UserRole;
import com.inkstage.enums.user.UserRoleEnum;
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

    /**
     * 根据用户ID删除角色关联
     *
     * @param userId 用户ID
     */
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 更新用户角色
     * @param userId 用户ID
     * @param userRole 角色
     * @return 影响行数
     */
    int updateUserRole(@Param("userId") Long userId, @Param("userRole") UserRoleEnum userRole);
}