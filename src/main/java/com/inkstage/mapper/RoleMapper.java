package com.inkstage.mapper;

import com.inkstage.entity.model.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper {

    /**
     * 根据主键查询角色
     * @param id 角色ID
     * @return 角色信息
     */
    Role selectByPrimaryKey(@Param("id") Integer id);

}