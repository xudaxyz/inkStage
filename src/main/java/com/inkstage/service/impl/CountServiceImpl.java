package com.inkstage.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.enums.CountType;
import com.inkstage.mapper.*;
import com.inkstage.service.CountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通用计数服务实现类
 * <p>
 * 采用 Redis 缓存 + 同步数据库的模式：
 * 1. 先通过 Redis 原子操作更新缓存计数
 * 2. 再同步将增量写入数据库
 * <p>
 * 新增计数类型只需：
 * 1. 在 CountType 枚举中添加一行
 * 2. 在 syncToDatabase 方法中添加对应的 Mapper 调用
 * 3. 在 getCountFromDatabase 方法中添加对应的查询逻辑
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CountServiceImpl implements CountService {

    private final CacheManager cacheManager;
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ColumnMapper columnMapper;
    private final CommentMapper commentMapper;
    private final CollectionFolderMapper collectionFolderMapper;
    private final SearchHotWordMapper searchHotWordMapper;
    private final SystemAnnouncementMapper systemAnnouncementMapper;

    @Override
    public void updateCount(CountType countType, Long targetId, int delta) {
        if (targetId == null) {
            log.warn("计数更新失败, 目标ID为空, 计数类型: {}", countType);
            return;
        }
        if (delta == 0) {
            return;
        }
        String key = CacheKey.keyForCount(countType, targetId);
        cacheManager.increment(key, delta);
        syncToDatabase(countType, targetId, delta);
        log.info("更新计数: {} - 目标ID: {}, 增量: {}", countType.getDescription(), targetId, delta);
    }

    @Override
    public void batchUpdate(CountType countType, List<Long> targetIds, int delta) {
        if (targetIds == null || targetIds.isEmpty()) {
            return;
        }
        if (delta == 0) {
            return;
        }
        for (Long targetId : targetIds) {
            if (targetId != null) {
                String key = CacheKey.keyForCount(countType, targetId);
                cacheManager.increment(key, delta);
                syncToDatabase(countType, targetId, delta);
            }
        }
        log.info("批量更新计数: {} - 目标数量: {}, 增量: {}", countType.getDescription(), targetIds.size(), delta);
    }

    @Override
    public long getCount(CountType countType, Long targetId) {
        if (targetId == null) {
            return 0;
        }
        String key = CacheKey.keyForCount(countType, targetId);
        Long count = cacheManager.get(key, Long.class);
        if (count != null) {
            return count;
        }
        count = getCountFromDatabase(countType, targetId);
        if (count != null) {
            cacheManager.set(key, count);
        }
        return count != null ? count : 0;
    }

    @Override
    public void syncToDatabase(CountType countType, Long targetId, int delta) {
        int result;
        switch (countType) {
            case ARTICLE_READ -> result = articleMapper.updateReadCount(targetId, delta);
            case ARTICLE_LIKE -> result = articleMapper.updateLikeCount(targetId, delta);
            case ARTICLE_COMMENT -> result = articleMapper.updateCommentCount(targetId, delta);
            case ARTICLE_COLLECTION -> result = articleMapper.updateCollectionCount(targetId, delta);
            case ARTICLE_SHARE -> result = articleMapper.updateShareCount(targetId, delta);
            case USER_ARTICLE -> result = userMapper.updateArticleCount(targetId, delta);
            case USER_FOLLOW -> result = userMapper.updateFollowCount(targetId, delta);
            case USER_FOLLOWER -> result = userMapper.updateFollowerCount(targetId, delta);
            case USER_COMMENT -> result = userMapper.updateCommentCount(targetId, delta);
            case USER_LIKE -> result = userMapper.updateLikeCount(targetId, delta);
            case COMMENT_LIKE -> result = commentMapper.updateLikeCount(targetId, delta);
            case COMMENT_REPLY -> result = commentMapper.updateReplyCount(targetId, delta);
            case COMMENT_REPORT -> result = commentMapper.updateReportCount(targetId, delta);
            case COLUMN_ARTICLE -> result = columnMapper.updateArticleCount(targetId, delta);
            case COLUMN_READ -> result = columnMapper.updateReadCount(targetId, delta);
            case COLUMN_SUBSCRIPTION -> result = columnMapper.updateSubscriptionCount(targetId, delta);
            case TAG_ARTICLE -> result = tagMapper.updateArticleCount(targetId, delta);
            case TAG_USAGE -> result = tagMapper.updateUsageCount(targetId, delta);
            case CATEGORY_ARTICLE -> result = categoryMapper.updateArticleCount(targetId, delta);
            case FOLDER_ARTICLE -> result = collectionFolderMapper.updateArticleCount(targetId, delta);
            case HOT_WORD_SEARCH -> result = searchHotWordMapper.updateSearchCount(targetId, delta);
            case ANNOUNCEMENT_READ -> result = systemAnnouncementMapper.updateReadCount(targetId, delta);
            default -> throw new IllegalArgumentException("未知的计数类型: " + countType);
        }
        log.info("同步计数到数据库成功, 更新条数: [{}], 计数类型: [{}], 目标ID: [{}], 增量: [{}]", result, countType, targetId, delta);
    }

    /**
     * 从数据库获取计数初始值
     */
    private Long getCountFromDatabase(CountType countType, Long targetId) {
        Long result = null;
        switch (countType) {
            case ARTICLE_READ -> result = articleMapper.getReadCount(targetId);
            case ARTICLE_LIKE -> result = articleMapper.getLikeCount(targetId);
            case ARTICLE_COMMENT -> result = articleMapper.getCommentCount(targetId);
            case ARTICLE_COLLECTION -> result = articleMapper.getCollectionCount(targetId);
            case ARTICLE_SHARE -> result = articleMapper.getShareCount(targetId);
            case USER_ARTICLE -> result = userMapper.getArticleCount(targetId);
            case USER_FOLLOW -> result = userMapper.getFollowCount(targetId);
            case USER_FOLLOWER -> result = userMapper.getFollowerCount(targetId);
            case USER_COMMENT -> result = userMapper.getCommentCount(targetId);
            case USER_LIKE -> result = userMapper.getLikeCount(targetId);
            case COMMENT_LIKE -> result = commentMapper.getLikeCount(targetId);
            case COMMENT_REPLY -> result = commentMapper.getReplyCount(targetId);
            case COMMENT_REPORT -> result = commentMapper.getReportCount(targetId);
            case COLUMN_ARTICLE -> result = columnMapper.getArticleCount(targetId);
            case COLUMN_READ -> result = columnMapper.getReadCount(targetId);
            case COLUMN_SUBSCRIPTION -> result = columnMapper.getSubscriptionCount(targetId);
            case TAG_ARTICLE -> result = tagMapper.getArticleCount(targetId);
            case TAG_USAGE -> result = tagMapper.getUsageCount(targetId);
            case CATEGORY_ARTICLE -> result = categoryMapper.getArticleCount(targetId);
            case FOLDER_ARTICLE -> result = collectionFolderMapper.getArticleCount(targetId);
            case HOT_WORD_SEARCH -> result = searchHotWordMapper.getSearchCount(targetId);
            case ANNOUNCEMENT_READ -> result = systemAnnouncementMapper.getReadCount(targetId);
        }
        log.info("从数据库获取计数成功, 计数类型: [{}], 目标ID: [{}], 结果(条数): [{}]", countType, targetId, result);
        return result;
    }
}
