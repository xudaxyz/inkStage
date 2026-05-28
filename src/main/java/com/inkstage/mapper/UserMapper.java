package com.inkstage.mapper;

import com.inkstage.dto.admin.AdminUserQueryDTO;
import com.inkstage.entity.model.User;
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
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User findById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    User findByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    User findByPhone(@Param("phone") String phone);

    /**
     * 查询热门用户
     *
     * @param limit 限制数量
     * @return 热门用户列表
     */
    List<HotUserVO> findHotUsers(@Param("limit") Integer limit);

    /**
     * 查询用户最近发布的文章
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 最近文章列表
     */
    List<AdminUserArticleVO> findRecentArticles(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 查询用户最近发布的评论
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 最近评论列表
     */
    List<AdminUserCommentVO> findRecentComments(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 管理员根据ID获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    AdminUserDetailVO findAdminUserDetailById(@Param("id") Long id);

    /**
     * 管理员分页查询用户列表
     *
     * @param query 查询条件
     * @return 用户列表
     */
    List<AdminUserListVO> findAdminUserList(@Param("query") AdminUserQueryDTO query);

    // ==================== 新增（Create） ====================

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 影响行数
     */
    int insert(User user);

    // ==================== 更新（Update） ====================

    /**
     * 根据主键更新用户信息(选择性更新)
     *
     * @param user 用户信息
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(User user);

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 状态
     * @return 受影响的行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") UserStatus status);

    /**
     * 管理员更新用户详情
     *
     * @param userDetailVO 用户详情
     * @return 影响行数
     */
    int updateAdminUserDetail(@Param("userDetail") AdminUserDetailVO userDetailVO);

    /**
     * 管理员更新用户状态
     *
     * @param id     用户ID
     * @param status 状态
     * @return 影响行数
     */
    int updateUserStatus(@Param("id") Long id, @Param("status") UserStatus status);

    // ==================== 删除（Delete） ====================

    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     * @return 影响行数
     */
    int purgeById(@Param("id") Long id);

    // ==================== 统计（Count） ====================

    /**
     * 获取用户文章数
     *
     * @param id 用户ID
     * @return 文章数
     */
    Long getArticleCount(@Param("id") Long id);

    /**
     * 更新用户文章数
     *
     * @param id    用户ID
     * @param delta 增量值（正数增加，负数减少）
     * @return 影响行数
     */
    int updateArticleCount(@Param("id") Long id, @Param("delta") int delta);

    /**
     * 获取用户关注数
     *
     * @param id 用户ID
     * @return 关注数
     */
    Long getFollowCount(@Param("id") Long id);

    /**
     * 更新用户关注数
     *
     * @param id    用户ID
     * @param delta 增量值（正数增加，负数减少）
     * @return 影响行数
     */
    int updateFollowCount(@Param("id") Long id, @Param("delta") int delta);

    /**
     * 获取用户粉丝数
     *
     * @param id 用户ID
     * @return 粉丝数
     */
    Long getFollowerCount(@Param("id") Long id);

    /**
     * 更新用户粉丝数
     *
     * @param id    用户ID
     * @param delta 增量值（正数增加，负数减少）
     * @return 影响行数
     */
    int updateFollowerCount(@Param("id") Long id, @Param("delta") int delta);

    /**
     * 获取用户评论数
     *
     * @param id 用户ID
     * @return 评论数
     */
    Long getCommentCount(@Param("id") Long id);

    /**
     * 更新用户评论数
     *
     * @param id    用户ID
     * @param delta 增量值（正数增加，负数减少）
     * @return 影响行数
     */
    int updateCommentCount(@Param("id") Long id, @Param("delta") int delta);

    /**
     * 获取用户获赞数
     *
     * @param id 用户ID
     * @return 获赞数
     */
    Long getLikeCount(@Param("id") Long id);

    /**
     * 更新用户获赞数
     *
     * @param id    用户ID
     * @param delta 增量值（正数增加，负数减少）
     * @return 影响行数
     */
    int updateLikeCount(@Param("id") Long id, @Param("delta") int delta);

    /**
     * 管理员统计用户总数
     *
     * @param query 查询条件
     * @return 总数
     */
    long countAdminUserList(@Param("query") AdminUserQueryDTO query);

    /**
     * 根据角色代码获取用户ID列表
     *
     * @param roleCode 角色代码
     * @return 用户ID列表
     */
    List<Long> findUserIdsByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 获取所有用户ID列表
     *
     * @return 所有用户ID列表
     */
    List<Long> findAllUserIds();

    /**
     * 统计所有用户总数
     *
     * @return 用户总数
     */
    long countAll();

    /**
     * 统计待审核用户数量
     *
     * @return 待审核用户数量
     */
    long countPendingReviews();

    /**
     * 统计指定日期新增用户数
     *
     * @param date 日期（格式：yyyy-MM-dd）
     * @return 新增用户数
     */
    long countNewUsersByDate(@Param("date") String date);

    /**
     * 查询冷却期已过的待删除用户ID列表
     *
     * @param now 当前时间
     * @return 已过冷却期的待删除用户ID列表
     */
    List<Long> findExpiredPendingDeleteUserIds(@Param("now") LocalDateTime now);

    /**
     * 注销用户账号（清空个人信息，状态改为已注销）
     *
     * @param id 用户ID
     * @return 影响行数
     */
    int cancelUserAccount(@Param("id") Long id);
}
