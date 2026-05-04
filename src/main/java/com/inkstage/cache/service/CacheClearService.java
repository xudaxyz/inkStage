package com.inkstage.cache.service;

/**
 * 缓存清除服务接口
 * 统一管理所有缓存清除操作
 */
public interface CacheClearService {


    // ==================== 文章相关缓存清除 ====================

    /**
     * 清除文章详情缓存
     *
     * @param articleId 文章ID
     */
    void clearArticleDetailCache(Long articleId);

    /**
     * 清除文章列表缓存
     */
    void clearArticleListCache();

    /**
     * 清除用户文章列表缓存
     *
     * @param userId 用户ID
     */
    void clearUserArticleListCache(Long userId);

    /**
     * 清除热门文章缓存
     */
    void clearHotArticleCache();

    /**
     * 清除最新文章缓存
     */
    void clearLatestArticleCache();

    /**
     * 清除轮播文章缓存
     */
    void clearBannerArticleCache();

    /**
     * 清除文章搜索缓存
     */
    void clearArticleSearchCache();

    /**
     * 清除所有文章相关缓存
     */
    void clearAllArticleCache();

    /**
     * 清除文章点赞缓存
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     */
    void clearArticleLikeCache(Long articleId, Long userId);

    /**
     * 清除文章收藏缓存
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     */
    void clearArticleCollectCache(Long articleId, Long userId);

    /**
     * 清除文章计数缓存
     *
     * @param articleId 文章ID
     */
    void clearArticleCountCache(Long articleId);

    // ==================== 用户相关缓存清除 ====================

    /**
     * 清除用户信息缓存
     *
     * @param userId 用户ID
     */
    void clearUserCache(Long userId);

    /**
     * 清除热门用户缓存
     */
    void clearHotUserCache();

    /**
     * 清除用户会话缓存
     *
     * @param userId 用户ID
     */
    void clearUserSessionCache(Long userId);

    /**
     * 清除用户登录尝试缓存
     *
     * @param account 账号
     */
    void clearLoginAttemptCache(String account);

    // ==================== 评论相关缓存清除 ====================

    /**
     * 清除文章评论列表缓存
     *
     * @param articleId 文章ID
     */
    void clearArticleCommentCache(Long articleId);

    /**
     * 清除评论回复缓存
     *
     * @param parentId 父评论ID
     */
    void clearCommentReplyCache(Long parentId);

    // ==================== 通知相关缓存清除 ====================

    /**
     * 清除通知未读数缓存
     *
     * @param userId 用户ID
     */
    void clearNotificationUnreadCache(Long userId);

    /**
     * 清除通知列表缓存
     *
     * @param userId 用户ID
     */
    void clearNotificationListCache(Long userId);

    // ==================== 分类和标签相关缓存清除 ====================

    /**
     * 清除分类缓存
     *
     * @param categoryId 分类ID
     */
    void clearCategoryCache(Long categoryId);

    /**
     * 清除所有分类缓存
     */
    void clearAllCategoryCache();

    /**
     * 清除标签缓存
     *
     * @param tagId 标签ID
     */
    void clearTagCache(Long tagId);

    /**
     * 清除所有标签缓存
     */
    void clearAllTagCache();

    // ==================== 系统相关缓存清除 ====================

    /**
     * 清除系统配置缓存
     */
    void clearSystemConfigCache();

    /**
     * 清除验证码缓存
     *
     * @param account 账号
     * @param purpose 用途
     */
    void clearVerifyCodeCache(String account, String purpose);

    /**
     * 清理收藏状态缓存
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     */
    void clearCollectionStatusCache(Long articleId, Long userId);

    /**
     * 清理用户所有收藏相关缓存
     *
     * @param userId 用户ID
     */
    void clearUserCollectionCache(Long userId);

    /**
     * 清理用户文章相关缓存
     * @param userId 用户ID
     */
    void clearUserArticleCache(Long userId);

    void cleanCacheAfterArticleCreate(Long articleId, Long userId);

    // ==================== 专栏订阅相关缓存清除 ====================

    /**
     * 清理专栏订阅状态缓存
     *
     * @param columnId 专栏ID
     * @param userId 用户ID
     */
    void clearColumnSubscriptionStatusCache(Long columnId, Long userId);

    /**
     * 清理用户的订阅列表缓存
     *
     * @param userId 用户ID
     */
    void clearUserSubscriptionListCache(Long userId);

    /**
     * 清理专栏订阅相关的所有缓存
     *
     * @param columnId 专栏ID
     * @param userId 用户ID
     */
    void clearColumnSubscriptionCache(Long columnId, Long userId);
}
