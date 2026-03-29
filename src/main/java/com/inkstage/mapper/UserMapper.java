package com.inkstage.mapper;

import com.inkstage.dto.admin.AdminUserQueryDTO;
import com.inkstage.entity.model.User;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.enums.user.UserStatus;
import com.inkstage.vo.admin.AdminUserArticleVO;
import com.inkstage.vo.admin.AdminUserCommentVO;
import com.inkstage.vo.admin.AdminUserDetailVO;
import com.inkstage.vo.admin.AdminUserListVO;
import com.inkstage.vo.front.HotUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {

    // ==================== 查询（Read） ====================
    
    /**
     * 根据主键查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    User findById(@Param("id") Long id);

    /**
     * 根据用户ID查询用户版本号
     * @param id 用户ID
     * @return 用户版本号
     */
    Integer findUserVersionById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    User findByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户信息
     */
    User findByPhone(@Param("phone") String phone);

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
    List<User> findByPage(
            @Param("offset") Integer offset, 
            @Param("pageSize") Integer pageSize,
            @Param("keyword") String keyword,
            @Param("role") UserRoleEnum role,
            @Param("status") UserStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 查询热门用户
     * @param limit 限制数量
     * @return 热门用户列表
     */
    List<HotUserVO> findHotUsers(@Param("limit") Integer limit);

    /**
     * 查询用户最近发布的文章
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近文章列表
     */
    List<AdminUserArticleVO> findRecentArticles(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 查询用户最近发布的评论
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近评论列表
     */
    List<AdminUserCommentVO> findRecentComments(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 批量查询用户
     * @param ids 用户ID列表
     * @return 用户列表
     */
    List<User> findByIds(@Param("ids") List<Long> ids);

    /**
     * 根据角色查询用户
     * @param role 角色
     * @param limit 限制数量
     * @return 用户列表
     */
    List<User> findByRole(@Param("role") UserRoleEnum role, @Param("limit") Integer limit);

    /**
     * 管理员根据ID获取用户详情
     * @param id 用户ID
     * @return 用户详情
     */
    AdminUserDetailVO findAdminUserDetailById(@Param("id") Long id);

    /**
     * 管理员分页查询用户列表
     * @param query 查询条件
     * @return 用户列表
     */
    List<AdminUserListVO> findAdminUserList(@Param("query") AdminUserQueryDTO query);

    // ==================== 新增（Create） ====================
    
    /**
     * 新增用户
     * @param user 用户信息
     * @return 影响行数
     */
    int insert(User user);

    // ==================== 更新（Update） ====================
    
    /**
     * 根据主键更新用户信息(选择性更新)
     * @param user 用户信息
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(User user);

    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 状态
     * @return 受影响的行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") UserStatus status);

    /**
     * 更新用户最后登录时间
     * @param id 用户ID
     * @param lastLoginTime 最后登录时间
     * @return 影响行数
     */
    int updateLastLoginTime(@Param("id") Long id, @Param("lastLoginTime") LocalDateTime lastLoginTime);

    /**
     * 管理员更新用户详情
     * @param userDetailVO 用户详情
     * @return 影响行数
     */
    int updateAdminUserDetail(@Param("userDetail") AdminUserDetailVO userDetailVO);

    /**
     * 管理员更新用户状态
     * @param id 用户ID
     * @param status 状态
     * @return 影响行数
     */
    int updateUserStatus(@Param("id") Long id, @Param("status") UserStatus status);

    // ==================== 删除（Delete） ====================
    
    /**
     * 根据ID删除用户
     * @param id 用户ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    // ==================== 统计（Count） ====================
    
    /**
     * 统计用户总数
     * @param keyword 关键词
     * @param role 角色
     * @param status 状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总数
     */
    long countByCondition(
            @Param("keyword") String keyword,
            @Param("role") UserRoleEnum role,
            @Param("status") UserStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 管理员统计用户总数
     * @param query 查询条件
     * @return 总数
     */
    long countAdminUserList(@Param("query") AdminUserQueryDTO query);

    /**
     * 根据角色代码获取用户ID列表
     * @param roleCode 角色代码
     * @return 用户ID列表
     */
    List<Long> findUserIdsByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 获取所有用户ID列表
     * @return 所有用户ID列表
     */
    List<Long> findAllUserIds();

}
