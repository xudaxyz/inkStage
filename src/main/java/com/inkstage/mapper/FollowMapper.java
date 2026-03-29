package com.inkstage.mapper;

import com.inkstage.entity.model.Follow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 关注关系Mapper接口
 */
@Mapper
public interface FollowMapper {

    // ==================== 查询（Read） ====================
    
    /**
     * 检查关注关系是否存在
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     * @return 关注关系
     */
    int checkFollowStatus(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    /**
     * 查询用户的关注列表
     * @param followerId 关注者ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 关注列表
     */
    List<Long> findFollowingIds(@Param("followerId") Long followerId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 查询用户的粉丝列表
     * @param followingId 被关注者ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 粉丝列表
     */
    List<Long> findFollowerIds(@Param("followingId") Long followingId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 批量检查关注关系
     * @param followerId 关注者ID
     * @param followingIds 被关注者ID列表
     * @return 关注关系列表
     */
    List<Follow> findByFollowerAndFollowingIds(@Param("followerId") Long followerId, @Param("followingIds") List<Long> followingIds);

    // ==================== 新增（Create） ====================
    
    /**
     * 插入关注关系
     * @param follow 关注关系
     * @return 影响行数
     */
    int insert(Follow follow);

    // ==================== 删除（Delete） ====================
    
    /**
     * 删除关注关系
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     * @return 影响行数
     */
    int delete(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    // ==================== 统计（Count） ====================
    
    /**
     * 统计用户的关注数
     * @param followerId 关注者ID
     * @return 关注数
     */
    long countFollowing(@Param("followerId") Long followerId);

    /**
     * 统计用户的粉丝数
     * @param followingId 被关注者ID
     * @return 粉丝数
     */
    long countFollowers(@Param("followingId") Long followingId);

}
