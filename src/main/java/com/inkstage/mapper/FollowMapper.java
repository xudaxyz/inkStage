package com.inkstage.mapper;

import com.inkstage.entity.model.Follow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 关注关系Mapper接口
 */
@Mapper
public interface FollowMapper {

    // ==================== 查询（Read） ====================

    /**
     * 检查关注关系是否存在
     *
     * @param followerId  关注者ID
     * @param followingId 被关注者ID
     * @return 关注关系
     */
    int checkFollowStatus(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    /**
     * 查询用户的关注列表
     *
     * @param followerId 关注者ID
     * @param offset     偏移量
     * @param limit      限制数量
     * @return 关注列表
     */
    List<Long> findFollowingIds(@Param("followerId") Long followerId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 查询用户的粉丝列表
     *
     * @param followingId 被关注者ID
     * @param offset      偏移量
     * @param limit       限制数量
     * @return 粉丝列表
     */
    List<Long> findFollowerIds(@Param("followingId") Long followingId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    // ==================== 新增（Create） ====================

    /**
     * 插入关注关系
     *
     * @param follow 关注关系
     * @return 影响行数
     */
    int insert(Follow follow);

    // ==================== 删除（Delete） ====================

    /**
     * 删除关注关系
     *
     * @param followerId  关注者ID
     * @param followingId 被关注者ID
     * @return 影响行数
     */
    int purge(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    // ==================== 统计（Count） ====================

    /**
     * 统计用户的关注数
     *
     * @param followerId 关注者ID
     * @return 关注数
     */
    long countFollowing(@Param("followerId") Long followerId);

    /**
     * 统计用户的粉丝数
     *
     * @param followingId 被关注者ID
     * @return 粉丝数
     */
    long countFollowers(@Param("followingId") Long followingId);

    /**
     * 软删除用户作为关注者的关注关系
     *
     * @param followerId 关注者ID
     */
    void deleteByFollowerId(@Param("followerId") Long followerId);

    /**
     * 软删除用户作为被关注者的关注关系
     *
     * @param followingId 被关注者ID
     */
    void deleteByFollowingId(@Param("followingId") Long followingId);

    /**
     * 恢复指定时间之后被软删除的、用户作为关注者的关注关系
     *
     * @param followerId 关注者ID
     * @param afterTime  时间节点，恢复此时间之后被删除的关注关系
     */
    void restoreByFollowerIdAfterTime(@Param("followerId") Long followerId, @Param("afterTime") LocalDateTime afterTime);

    /**
     * 恢复指定时间之后被软删除的、用户作为被关注者的关注关系
     *
     * @param followingId 被关注者ID
     * @param afterTime   时间节点，恢复此时间之后被删除的关注关系
     */
    void restoreByFollowingIdAfterTime(@Param("followingId") Long followingId, @Param("afterTime") LocalDateTime afterTime);

    /**
     * 彻底删除用户作为关注者的关注关系
     *
     * @param followerId 关注者ID
     */
    void purgeByFollowerId(@Param("followerId") Long followerId);

    /**
     * 彻底删除用户作为被关注者的关注关系
     *
     * @param followingId 被关注者ID
     */
    void purgeByFollowingId(@Param("followingId") Long followingId);

}
