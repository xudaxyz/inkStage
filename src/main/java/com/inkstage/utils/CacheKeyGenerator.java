package com.inkstage.utils;

import com.inkstage.constant.RedisKeyConstants;

/**
 * 缓存键生成工具类
 * 集中管理所有缓存键的生成逻辑，减少代码冗余
 */
public class CacheKeyGenerator {

    /**
     * 生成文章详情缓存键
     * @param articleId 文章ID
     * @return 缓存键
     */
    public static String generateArticleDetailKey(Long articleId) {
        return RedisKeyConstants.buildCacheKey("article:detail:", articleId.toString());
    }

    /**
     * 生成文章列表缓存键
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param categoryId 分类ID（可选）
     * @return 缓存键
     */
    public static String generateArticleListKey(Integer pageNum, Integer pageSize, Long categoryId) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(pageNum).append(":").append(pageSize);
        if (categoryId != null) {
            keyBuilder.append(":").append(categoryId);
        }
        return RedisKeyConstants.buildCacheKey("article:list:", keyBuilder.toString());
    }

    /**
     * 生成评论列表缓存键
     * @param articleId 文章ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 缓存键
     */
    public static String generateCommentListKey(Long articleId, Integer pageNum, Integer pageSize) {
        return RedisKeyConstants.buildCacheKey(
                "comment:list:",
                articleId + ":" + pageNum + ":" + pageSize
        );
    }

    /**
     * 生成评论列表缓存键（包含排序和子评论参数）
     * @param articleId 文章ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param sortBy 排序方式
     * @param maxReplies 子评论最大数量
     * @param replySortBy 子评论排序方式
     * @return 缓存键
     */
    public static String generateCommentListKey(Long articleId, Integer pageNum, Integer pageSize, 
                                               String sortBy, Integer maxReplies, String replySortBy) {
        return RedisKeyConstants.buildCacheKey(
                "comment:list:",
                articleId + ":" + pageNum + ":" + pageSize + ":" + sortBy + ":" + maxReplies + ":" + replySortBy
        );
    }

    /**
     * 生成用户信息缓存键
     * @param userId 用户ID
     * @return 缓存键
     */
    public static String generateUserKey(Long userId) {
        return RedisKeyConstants.buildUserKey(userId);
    }

    /**
     * 生成热门用户缓存键
     * @param limit 数量限制
     * @return 缓存键
     */
    public static String generateHotUserKey(Integer limit) {
        return RedisKeyConstants.buildCacheKey("user:hot:", limit.toString());
    }

    /**
     * 生成评论计数缓存键
     * @param commentId 评论ID
     * @param countType 计数类型
     * @return 缓存键
     */
    public static String generateCommentCountKey(Long commentId, String countType) {
        return RedisKeyConstants.buildHotDataKey("comment:", commentId + ":" + countType);
    }

    /**
     * 生成文章阅读数缓存键
     * @param articleId 文章ID
     * @return 缓存键
     */
    public static String generateArticleReadCountKey(Long articleId) {
        return RedisKeyConstants.buildHotDataKey("article:", articleId + ":read");
    }

    /**
     * 生成文章点赞数缓存键
     * @param articleId 文章ID
     * @return 缓存键
     */
    public static String generateArticleLikeCountKey(Long articleId) {
        return RedisKeyConstants.buildHotDataKey("article:", articleId + ":like");
    }

    /**
     * 生成用户文章列表缓存键
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 缓存键
     */
    public static String generateUserArticleListKey(Long userId, Integer pageNum, Integer pageSize) {
        return RedisKeyConstants.buildCacheKey(
                "user:article:list:",
                userId + ":" + pageNum + ":" + pageSize
        );
    }

    /**
     * 生成热门文章缓存键
     * @param limit 数量限制
     * @param timeRange 时间范围
     * @return 缓存键
     */
    public static String generateHotArticleKey(Integer limit, String timeRange) {
        return RedisKeyConstants.buildCacheKey("article:hot:", limit + ":" + timeRange);
    }

    /**
     * 生成最新文章缓存键
     * @param limit 数量限制
     * @return 缓存键
     */
    public static String generateLatestArticleKey(Integer limit) {
        return RedisKeyConstants.buildCacheKey("article:latest:", limit.toString());
    }

    /**
     * 生成轮播文章缓存键
     * @param limit 数量限制
     * @return 缓存键
     */
    public static String generateBannerArticleKey(Integer limit) {
        return RedisKeyConstants.buildCacheKey("article:banner:", limit.toString());
    }
}
