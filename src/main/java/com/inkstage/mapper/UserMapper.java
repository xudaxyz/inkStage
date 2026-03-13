package com.inkstage.mapper;

import com.inkstage.entity.model.User;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.enums.user.UserStatus;
import com.inkstage.vo.admin.AdminUserArticleVO;
import com.inkstage.vo.admin.AdminUserCommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
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

    /**
     * 分页查询用户
     * @param offset 偏移量
     * @param pageSize 每页大小
     * @param keyword 关键词
     * @param role 角色
     * @param status 状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 用户列表
     */
    List<User> selectByPage(
            @Param("offset") Integer offset, 
            @Param("pageSize") Integer pageSize,
            @Param("keyword") String keyword,
            @Param("role") UserRoleEnum role,
            @Param("status") UserStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 统计用户总数
     * @param keyword 关键词
     * @param role 角色
     * @param status 状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总数
     */
    Long countAll(
            @Param("keyword") String keyword,
            @Param("role") UserRoleEnum role,
            @Param("status") UserStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 根据ID删除用户
     * @param id 用户ID
     */
    void deleteById(@Param("id") Long id);

    /**
     * 查询用户最近发布的文章
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近文章列表
     */
    List<AdminUserArticleVO> selectRecentArticles(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 查询用户最近发布的评论
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近评论列表
     */
    List<AdminUserCommentVO> selectRecentComments(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 更新用户状态
     * @param id 用户ID
     * @param userStatus 状态
     * @return 受影响的行数
     */
    int updateUserStatus(@Param("id") Long id, @Param("userStatus") UserStatus userStatus);

}