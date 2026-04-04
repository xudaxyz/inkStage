package com.inkstage.service.impl;

import com.inkstage.entity.model.Follow;
import com.inkstage.entity.model.User;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.mapper.FollowMapper;
import com.inkstage.mapper.UserMapper;
import com.inkstage.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 关注服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    /**
     * 关注用户
     *
     * @param followerId  关注者ID
     * @param followingId 被关注者ID
     * @return 是否关注成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"follow:status", "follow:list"},
            key = "#followerId + ':' + #followingId",
            allEntries = true)
    public boolean followUser(Long followerId, Long followingId) {
        // 检查是否已经关注
        int followed = followMapper.checkFollowStatus(followerId, followingId);
        if (followed != 0) {
            log.info("用户 {} 已经关注了用户 {}", followerId, followingId);
            return true;
        }

        // 创建新的关注关系
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        follow.setCreateTime(LocalDateTime.now());
        follow.setDeleted(DeleteStatus.NOT_DELETED);

        int result = followMapper.insert(follow);
        if (result > 0) {
            // 更新关注者的关注数
            User follower = userMapper.findById(followerId);
            if (follower != null) {
                int followCount = follower.getFollowCount() != null ? follower.getFollowCount() : 0;
                follower.setFollowCount(followCount + 1);
                userMapper.updateByPrimaryKeySelective(follower);
            }

            // 更新被关注者的粉丝数
            User following = userMapper.findById(followingId);
            if (following != null) {
                int followerCount = following.getFollowerCount() != null ? following.getFollowerCount() : 0;
                following.setFollowerCount(followerCount + 1);
                userMapper.updateByPrimaryKeySelective(following);
            }
        }

        log.info("用户 {} 关注用户 {} 结果: {}", followerId, followingId, result > 0);
        return result > 0;
    }

    /**
     * 取消关注用户
     *
     * @param followerId  关注者ID
     * @param followingId 被关注者ID
     * @return 是否取消关注成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"follow:status", "follow:list"},
            key = "#followerId + ':' + #followingId",
            allEntries = true)
    public boolean unfollowUser(Long followerId, Long followingId) {
        int result = followMapper.delete(followerId, followingId);
        if (result > 0) {
            // 更新关注者的关注数
            User follower = userMapper.findById(followerId);
            if (follower != null) {
                int followCount = follower.getFollowCount() != null ? follower.getFollowCount() : 0;
                if (followCount > 0) {
                    follower.setFollowCount(followCount - 1);
                    userMapper.updateByPrimaryKeySelective(follower);
                }
            }

            // 更新被关注者的粉丝数
            User following = userMapper.findById(followingId);
            if (following != null) {
                int followerCount = following.getFollowerCount() != null ? following.getFollowerCount() : 0;
                if (followerCount > 0) {
                    following.setFollowerCount(followerCount - 1);
                    userMapper.updateByPrimaryKeySelective(following);
                }
            }
        }

        log.info("用户 {} 取消关注用户 {} 结果: {}", followerId, followingId, result > 0);
        return result > 0;
    }

    /**
     * 检查关注状态
     *
     * @param followerId  关注者ID
     * @param followingId 被关注者ID
     * @return 是否已关注
     */
    @Override
    @Cacheable(value = "follow:status",
            key = "#followerId + ':' + #followingId",
            unless = "#result == null")
    public boolean checkFollowStatus(Long followerId, Long followingId) {
        int result = followMapper.checkFollowStatus(followerId, followingId);
        return result > 0;
    }

    /**
     * 获取用户的关注列表
     *
     * @param followerId 关注者ID
     * @param offset     偏移量
     * @param limit      限制数量
     * @return 关注的用户ID列表
     */
    @Override
    @Cacheable(value = "follow:list",
            key = "'following:' + #followerId + ':' + #offset + ':' + #limit",
            unless = "#result == null or #result.isEmpty()")
    public List<Long> getFollowingList(Long followerId, Integer offset, Integer limit) {
        return followMapper.findFollowingIds(followerId, offset, limit);
    }

    /**
     * 获取用户的粉丝列表
     *
     * @param followingId 被关注者ID
     * @param offset      偏移量
     * @param limit       限制数量
     * @return 粉丝ID列表
     */
    @Override
    @Cacheable(value = "follow:list",
            key = "'follower:' + #followingId + ':' + #offset + ':' + #limit",
            unless = "#result == null or #result.isEmpty()")
    public List<Long> getFollowerList(Long followingId, Integer offset, Integer limit) {
        return followMapper.findFollowerIds(followingId, offset, limit);
    }

    /**
     * 获取用户的关注数
     *
     * @param followerId 关注者ID
     * @return 关注数
     */
    @Override
    @Cacheable(value = "follow:list",
            key = "'following:count:' + #followerId",
            unless = "#result == null")
    public long getFollowingCount(Long followerId) {
        return followMapper.countFollowing(followerId);
    }

    /**
     * 获取用户的粉丝数
     *
     * @param followingId 被关注者ID
     * @return 粉丝数
     */
    @Override
    @Cacheable(value = "follow:list",
            key = "'follower:count:' + #followingId",
            unless = "#result == null")
    public long getFollowerCount(Long followingId) {
        return followMapper.countFollowers(followingId);
    }
}