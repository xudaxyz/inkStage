package com.inkstage.utils;

import com.inkstage.constant.RedisKeyConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 缓存管理工具类
 * 提供统一的缓存清除和管理方法
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheManager {

    private final RedisUtil redisUtil;

    // ==================== 文章相关缓存 ====================

    /**
     * 清除文章详情缓存
     *
     * @param articleId 文章ID
     */
    public void clearArticleDetailCache(Long articleId) {
        try {
            String articleDetailKey = RedisKeyConstants.buildCacheKey("article:detail:", articleId.toString());
            redisUtil.delete(articleDetailKey);
            log.info("清除文章详情缓存, 文章ID: {}", articleId);
        } catch (Exception e) {
            log.error("清除文章详情缓存失败, 文章ID: {}", articleId, e);
        }
    }

    /**
     * 清除文章列表缓存
     */
    public void clearArticleListCache() {
        String articleListKey = "cache:article:list:*";
        redisUtil.deletePattern(articleListKey);
        log.info("清除文章列表缓存完成");
    }

    /**
     * 清除用户文章列表缓存
     *
     * @param userId 用户ID
     */
    public void clearMyArticleListCache(Long userId) {
        if (userId != null) {
            String myArticleListKey = "cache:article:my:*";
            redisUtil.deletePattern(myArticleListKey);
            log.info("清除我的文章列表缓存完成");
        }
    }

    /**
     * 清除热门文章缓存
     */
    public void clearHotArticleCache() {
        String hotArticleKey = "cache:article:hot:*";
        redisUtil.deletePattern(hotArticleKey);
        log.info("清除热门文章缓存完成");
    }

    /**
     * 清除最新文章缓存
     */
    public void clearLatestArticleCache() {
        String latestArticleKey = "cache:article:latest:*";
        redisUtil.deletePattern(latestArticleKey);
        log.info("清除最新文章缓存完成");
    }

    /**
     * 清除轮播图文章缓存
     */
    public void clearBannerArticleCache() {
        String bannerArticleKey = "cache:article:banner:*";
        redisUtil.deletePattern(bannerArticleKey);
        log.info("清除轮播图文章缓存完成");
    }

    /**
     * 清除用户文章列表缓存
     *
     * @param userId 用户ID
     */
    public void clearUserArticleCache(Long userId) {
        if (userId != null) {
            String userArticleKey = RedisKeyConstants.buildCacheKey("article:user:", userId + ":*");
            redisUtil.deletePattern(userArticleKey);
            log.info("清除用户文章列表缓存, 用户ID: {}", userId);
        }
    }

    /**
     * 清除搜索结果缓存
     */
    public void clearSearchCache() {
        String searchKey = "cache:article:search:*";
        redisUtil.deletePattern(searchKey);
        log.info("清除搜索结果缓存完成");
    }

    // ==================== 评论相关缓存 ====================

    /**
     * 清除文章评论列表缓存
     *
     * @param articleId 文章ID
     */
    public void clearArticleCommentCache(Long articleId) {
        if (articleId != null) {
            String commentListPattern = RedisKeyConstants.buildCacheKey("comment:list:", articleId + ":*");
            redisUtil.deletePattern(commentListPattern);
            log.info("清除文章评论列表缓存, 文章ID: {}", articleId);
        }
    }

    /**
     * 清除文章回复评论列表缓存
     *
      * @param parentId 父评论ID
     */
    public void clearArticleCommentRepliesCache(Long parentId) {
        if (parentId != null) {
            String commentReplyPattern = RedisKeyConstants.buildCacheKey("comment:replies:", parentId + ":*");
            redisUtil.deletePattern(commentReplyPattern);
            log.info("清除文章回复评论列表缓存, 父评论ID: {}", parentId);
        }
    }

    // ==================== 用户相关缓存 ====================

    /**
     * 清除用户信息缓存
     *
     * @param userId 用户ID
     */
    public void clearUserCache(Long userId) {
        if (userId != null) {
            String userKey = RedisKeyConstants.buildUserKey(userId);
            redisUtil.delete(userKey);
            log.info("清除用户信息缓存, 用户ID: {}", userId);
        }
    }

    /**
     * 清除热门用户缓存
     */
    public void clearHotUserCache() {
        String hotUserKey = "cache:user:hot:*";
        redisUtil.deletePattern(hotUserKey);
        log.info("清除热门用户缓存完成");
    }

    // ==================== 通用方法 ====================

    /**
     * 清除指定模式的缓存
     *
     * @param pattern 缓存键模式
     */
    public void clearCacheByPattern(String pattern) {
        redisUtil.deletePattern(pattern);
        log.info("清除缓存, 模式: {}", pattern);
    }

    /**
     * 清除指定键的缓存
     *
     * @param key 缓存键
     */
    public void clearCacheByKey(String key) {
        redisUtil.delete(key);
        log.info("清除缓存, 键: {}", key);
    }
}
