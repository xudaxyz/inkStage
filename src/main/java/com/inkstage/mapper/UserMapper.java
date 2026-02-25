package com.inkstage.mapper;

import com.inkstage.entity.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    User selectByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户信息
     */
    User selectByPhone(@Param("phone") String phone);

    /**
     * 新增用户
     */
    void insert(User user);

    /**
     * 根据主键更新用户信息(选择性更新)
     * @param user 用户信息
     */
    void updateByPrimaryKeySelective(User user);

    /**
     * 根据主键查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    User selectByPrimaryKey(@Param("id") Long id);

    /**
     * 查询热门用户
     * @param limit 限制数量
     * @return 热门用户列表
     */
    List<User> selectHotUsers(@Param("limit") Integer limit);
}