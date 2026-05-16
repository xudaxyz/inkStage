package com.inkstage.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.enums.CountType;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.mapper.CategoryMapper;
import com.inkstage.mapper.CollectionFolderMapper;
import com.inkstage.mapper.ColumnMapper;
import com.inkstage.mapper.CommentMapper;
import com.inkstage.mapper.SearchHotWordMapper;
import com.inkstage.mapper.SystemAnnouncementMapper;
import com.inkstage.mapper.TagMapper;
import com.inkstage.mapper.UserMapper;
import com.inkstage.service.CountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通用计数服务实现类
 * <p>
 * 采用 Redis 缓存 + 异步同步数据库的模式：
 * 1. 先通过 Redis 原子操作更新缓存计数
 * 2. 再异步将增量同步到数据库
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

    @Async("countTaskExecutor")
    @Override
    public void syncToDatabase(CountType countType, Long targetId, int delta) {
        int result = 0;
        try {
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
            }
            log.info("同步计数到数据库成功, 更新条数: [{}], 计数类型: [{}], 目标ID: [{}], 增量: [{}]", result, countType, targetId, delta);
        } catch (Exception e) {
            log.error("同步计数到数据库失败, 计数类型: {}, 目标ID: {}, 增量: {}", countType, targetId, delta, e);
        }
    }

    /**
     * 从数据库获取计数初始值
     */
    private Long getCountFromDatabase(CountType countType, Long targetId) {
        return switch (countType) {
            case ARTICLE_READ -> articleMapper.getReadCount(targetId);
            case ARTICLE_LIKE -> articleMapper.getLikeCount(targetId);
            case ARTICLE_COMMENT -> articleMapper.getCommentCount(targetId);
            case ARTICLE_COLLECTION -> articleMapper.getCollectionCount(targetId);
            case ARTICLE_SHARE -> articleMapper.getShareCount(targetId);
            default -> null;
        };
    }
}
