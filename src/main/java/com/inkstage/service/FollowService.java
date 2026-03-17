package com.inkstage.service;

import java.util.List;

/**
 * 关注服务接口
 */
public interface FollowService {

    /**
     * 关注用户
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     * @return 是否关注成功
     */
    boolean followUser(Long followerId, Long followingId);

    /**
     * 取消关注用户
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     * @return 是否取消关注成功
     */
    boolean unfollowUser(Long followerId, Long followingId);

    /**
     * 检查关注状态
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     * @return 是否已关注
     */
    boolean checkFollowStatus(Long followerId, Long followingId);

    /**
     * 获取用户的关注列表
     * @param followerId 关注者ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 关注的用户ID列表
     */
    List<Long> getFollowingList(Long followerId, Integer offset, Integer limit);

    /**
     * 获取用户的粉丝列表
     * @param followingId 被关注者ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 粉丝ID列表
     */
    List<Long> getFollowerList(Long followingId, Integer offset, Integer limit);

    /**
     * 获取用户的关注数
     * @param followerId 关注者ID
     * @return 关注数
     */
    long getFollowingCount(Long followerId);

    /**
     * 获取用户的粉丝数
     * @param followingId 被关注者ID
     * @return 粉丝数
     */
    long getFollowerCount(Long followingId);
}
