package com.inkstage.service.impl;

import com.inkstage.cache.constant.RedisKeyConstants;
import com.inkstage.enums.CountType;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.service.CountService;
import com.inkstage.cache.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 文章计数服务实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CountServiceImpl implements CountService {

    private final RedisUtil redisUtil;
    private final ArticleMapper articleMapper;

    @Override
    public void updateArticleReadCount(Long articleId, int count) {
        updateCount(articleId, count, CountType.ARTICLE_READ_COUNT);
    }

    @Override
    public void updateArticleLikeCount(Long articleId, int count) {
        updateCount(articleId, count, CountType.ARTICLE_LIKE_COUNT);
    }


    @Override
    public void updateArticleCommentCount(Long articleId, int count) {
        updateCount(articleId, count, CountType.ARTICLE_COMMENT_COUNT);
    }

    @Override
    public void updateArticleCollectionCount(Long articleId, int count) {
        updateCount(articleId, count, CountType.ARTICLE_COLLECTION_COUNT);
    }

    @Override
    public void updateArticleShareCount(Long articleId, int count) {
        updateCount(articleId, count, CountType.ARTICLE_SHARE_COUNT);
    }

    @Override
    public Long getArticleReadCount(Long articleId) {
        return getCount(articleId, CountType.ARTICLE_READ_COUNT);
    }

    @Override
    public Long getArticleLikeCount(Long articleId) {
        return getCount(articleId, CountType.ARTICLE_LIKE_COUNT);
    }

    @Override
    public Long getArticleCommentCount(Long articleId) {
        return getCount(articleId, CountType.ARTICLE_COMMENT_COUNT);
    }

    @Override
    public Long getArticleCollectionCount(Long articleId) {
        return getCount(articleId, CountType.ARTICLE_COLLECTION_COUNT);
    }

    @Override
    public Long getArticleShareCount(Long articleId) {
        return getCount(articleId, CountType.ARTICLE_SHARE_COUNT);
    }

    @Async
    @Override
    public void syncArticleCount(Long articleId, CountType countType, int count) {
        try {
            switch (countType) {
                case ARTICLE_READ_COUNT -> articleMapper.updateReadCount(articleId, count);
                case ARTICLE_LIKE_COUNT -> articleMapper.updateLikeCount(articleId, count);
                case ARTICLE_COMMENT_COUNT -> articleMapper.updateCommentCount(articleId, count);
                case ARTICLE_COLLECTION_COUNT -> articleMapper.updateCollectionCount(articleId, count);
                case ARTICLE_SHARE_COUNT -> articleMapper.updateShareCount(articleId, count);
            }
            log.info("同步文章计数到数据库成功, 文章ID: {}", articleId);
        } catch (Exception e) {
            log.error("同步文章计数到数据库失败, 文章ID: {}, 更新类型: {}", articleId, countType.getType(), e);
        }
    }

    /**
     * 增加计数
     */
    private void updateCount(Long articleId, int count, CountType countType) {
        log.info("更新文章: {} - {} 计数 [{}]", articleId, countType, count);
        String key = buildCountCacheKey(articleId, countType);
        // 由于count有正负之分, 此直接使用increment
        redisUtil.increment(key, count);

        // 设置过期时间为1小时
        redisUtil.expire(key, 1, TimeUnit.HOURS);

        // 异步更新到数据库
        syncArticleCount(articleId, countType, count);
    }

    /**
     * 获取计数
     */
    private Long getCount(Long articleId, CountType countType) {
        log.info("获取文章计数, 文章ID: {}, 计数类型: {}", articleId, countType);
        String key = buildCountCacheKey(articleId, countType);
        Long count = redisUtil.get(key, Long.class);
        if (count == null) {
            // 从数据库获取初始值
            count = switch (countType) {
                case ARTICLE_READ_COUNT -> articleMapper.getReadCount(articleId);
                case ARTICLE_LIKE_COUNT -> articleMapper.getLikeCount(articleId);
                case ARTICLE_COMMENT_COUNT -> articleMapper.getCommentCount(articleId);
                case ARTICLE_COLLECTION_COUNT -> articleMapper.getCollectionCount(articleId);
                case ARTICLE_SHARE_COUNT -> articleMapper.getShareCount(articleId);
                default -> throw new IllegalArgumentException("未知的计数类型: " + countType);
            };
            // 缓存到Redis
            redisUtil.set(key, count, 30, TimeUnit.MINUTES);
        }
        return count;
    }

    /**
     * 构建计数缓存键
     */
    private String buildCountCacheKey(Long articleId, CountType countType) {
        String countTypeSuffix = switch (countType) {
            case ARTICLE_READ_COUNT -> "read";
            case ARTICLE_LIKE_COUNT -> "like";
            case ARTICLE_COMMENT_COUNT -> "comment";
            case ARTICLE_COLLECTION_COUNT -> "collect";
            case ARTICLE_SHARE_COUNT -> "share";
            default -> countType.getType();
        };
        return RedisKeyConstants.buildArticleCountCacheKey(articleId, countTypeSuffix);
    }

}
