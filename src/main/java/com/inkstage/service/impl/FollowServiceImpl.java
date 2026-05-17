package com.inkstage.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.constant.CacheTTL;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.entity.model.Follow;
import com.inkstage.entity.model.User;
import com.inkstage.enums.CountType;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.mapper.FollowMapper;
import com.inkstage.mapper.UserMapper;
import com.inkstage.notification.param.FollowParam;
import com.inkstage.service.FollowService;
import com.inkstage.service.NotificationService;
import com.inkstage.utils.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;

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
    private final NotificationService notificationService;
    private final CacheManager cacheManager;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final CountProducer countProducer;

    /**
     * 关注用户
     *
     * @param followerId  关注者ID
     * @param followingId 被关注者ID
     * @return 是否关注成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
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
        follow.setId(snowflakeIdGenerator.nextId());

        int result = followMapper.insert(follow);
        if (result > 0) {
            countProducer.sendCountMessage(CountType.USER_FOLLOW, followerId, 1);
            countProducer.sendCountMessage(CountType.USER_FOLLOWER, followingId, 1);

            User follower = userMapper.findById(followerId);
            FollowParam param = FollowParam.builder()
                    .userId(followingId)
                    .followerId(followerId)
                    .username(follower != null ? follower.getNickname() : "未知用户")
                    .senderId(followerId)
                    .notificationType(NotificationType.FOLLOW)
                    .build();
            notificationService.send(param);
        }

        cacheManager.deletePattern(CacheKey.FOLLOW);
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
    public boolean unfollowUser(Long followerId, Long followingId) {
        int result = followMapper.delete(followerId, followingId);
        if (result > 0) {
            countProducer.sendCountMessage(CountType.USER_FOLLOW, followerId, -1);
            countProducer.sendCountMessage(CountType.USER_FOLLOWER, followingId, -1);
        }

        cacheManager.deletePattern(CacheKey.FOLLOW);

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
    public boolean checkFollowStatus(Long followerId, Long followingId) {
        String cacheKey = CacheKey.keyForFollowStatus(followerId, followingId);
        Boolean result = cacheManager.get(cacheKey, Boolean.class);
        if (result != null) {
            return result;
        }
        int count = followMapper.checkFollowStatus(followerId, followingId);
        result = count > 0;
        cacheManager.set(cacheKey, result, CacheTTL.DEFAULT);
        return result;
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
    public List<Long> getFollowingList(Long followerId, Integer offset, Integer limit) {
        String cacheKey = CacheKey.keyForFollowingList(followerId, offset, limit);
        List<Long> result = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (result != null && !result.isEmpty()) {
            return result;
        }
        result = followMapper.findFollowingIds(followerId, offset, limit);
        if (result != null && !result.isEmpty()) {
            cacheManager.set(cacheKey, result, CacheTTL.DEFAULT);
        }
        return result;
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
    public List<Long> getFollowerList(Long followingId, Integer offset, Integer limit) {
        String cacheKey = CacheKey.keyForFollowerList(followingId, offset, limit);
        List<Long> result = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (result != null && !result.isEmpty()) {
            return result;
        }
        result = followMapper.findFollowerIds(followingId, offset, limit);
        if (result != null && !result.isEmpty()) {
            cacheManager.set(cacheKey, result, CacheTTL.DEFAULT);
        }
        return result;
    }

    /**
     * 获取用户的关注数
     *
     * @param followerId 关注者ID
     * @return 关注数
     */
    @Override
    public long getFollowingCount(Long followerId) {
        String cacheKey = CacheKey.keyForUserFollowingCount(followerId);
        Long result = cacheManager.get(cacheKey, Long.class);
        if (result != null) {
            return result;
        }
        result = followMapper.countFollowing(followerId);
        cacheManager.set(cacheKey, result, CacheTTL.DEFAULT);
        return result;
    }

    /**
     * 获取用户的粉丝数
     *
     * @param followingId 被关注者ID
     * @return 粉丝数
     */
    @Override
    public long getFollowerCount(Long followingId) {
        String cacheKey = CacheKey.keyForUserFollowerCount(followingId);
        Long result = cacheManager.get(cacheKey, Long.class);
        if (result != null) {
            return result;
        }
        result = followMapper.countFollowers(followingId);
        cacheManager.set(cacheKey, result, CacheTTL.DEFAULT);
        return result;
    }
}