package com.inkstage.cache.service.impl;

import com.inkstage.cache.constant.RedisKeyConstants;
import com.inkstage.cache.service.CacheClearService;
import com.inkstage.cache.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import static com.inkstage.cache.constant.RedisKeyConstants.*;

/**
 * 缓存清除服务实现类
 * 统一管理所有缓存清除操作
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheClearServiceImpl implements CacheClearService {

    private final RedisUtil redisUtil;

    // ==================== 文章相关缓存清除 ====================

    @Override
    public void clearArticleDetailCache(Long articleId) {
        if (articleId == null) return;
        try {
            String cacheKey = RedisKeyConstants.buildArticleDetailCacheKey(articleId);
            redisUtil.delete(cacheKey);
            log.debug("清除文章详情缓存, 文章ID: {}", articleId);
        } catch (Exception e) {
            log.error("清除文章详情缓存失败, 文章ID: {}", articleId, e);
        }
    }

    @Override
    public void clearArticleListCache() {
        try {
            redisUtil.deletePattern(ARTICLES_PREFIX + "*");
            log.info("清除文章列表缓存完成");
        } catch (Exception e) {
            log.error("清除文章列表缓存失败", e);
        }
    }

    @Override
    public void clearUserArticleListCache(Long userId) {
        if (userId == null) return;
        try {
            redisUtil.deletePattern(USER_ARTICLE_LIST_PREFIX + userId + ":*");
            log.debug("清除用户文章列表缓存, 用户ID: {}", userId);
        } catch (Exception e) {
            log.error("清除用户文章列表缓存失败, 用户ID: {}", userId, e);
        }
    }

    @Override
    public void clearHotArticleCache() {
        try {
            redisUtil.deletePattern(ARTICLE_HOT_PREFIX + "*");
            log.info("清除热门文章缓存完成");
        } catch (Exception e) {
            log.error("清除热门文章缓存失败", e);
        }
    }

    @Override
    public void clearLatestArticleCache() {
        try {
            redisUtil.deletePattern(ARTICLE_LATEST_PREFIX + "*");
            log.info("清除最新文章缓存完成");
        } catch (Exception e) {
            log.error("清除最新文章缓存失败", e);
        }
    }

    @Override
    public void clearBannerArticleCache() {
        try {
            redisUtil.deletePattern(ARTICLE_BANNER_PREFIX + "*");
            log.info("清除轮播文章缓存完成");
        } catch (Exception e) {
            log.error("清除轮播文章缓存失败", e);
        }
    }

    @Override
    public void clearArticleSearchCache() {
        try {
            // 搜索缓存使用article:search:前缀
            redisUtil.deletePattern(ARTICLE_SEARCH_PREFIX + "*");
            log.info("清除文章搜索缓存完成");
        } catch (Exception e) {
            log.error("清除文章搜索缓存失败", e);
        }
    }

    @Override
    public void clearAllArticleCache() {
        try {
            // 清除所有文章相关缓存
            clearArticleListCache();
            clearHotArticleCache();
            clearLatestArticleCache();
            clearBannerArticleCache();
            clearArticleSearchCache();
            // 清除文章详情缓存（使用article:前缀的所有缓存）
            redisUtil.deletePattern(ARTICLE_PREFIX + "*");
            log.info("清除所有文章相关缓存完成");
        } catch (Exception e) {
            log.error("清除所有文章相关缓存失败", e);
        }
    }

    @Override
    public void clearArticleLikeCache(Long articleId, Long userId) {
        if (articleId == null || userId == null) return;
        try {
            String cacheKey = RedisKeyConstants.buildArticleLikeCacheKey(articleId, userId);
            redisUtil.delete(cacheKey);
            log.debug("清除文章点赞缓存, 文章ID: {}, 用户ID: {}", articleId, userId);
        } catch (Exception e) {
            log.error("清除文章点赞缓存失败, 文章ID: {}, 用户ID: {}", articleId, userId, e);
        }
    }

    @Override
    public void clearArticleCollectCache(Long articleId, Long userId) {
        if (articleId == null || userId == null) return;
        try {
            String cacheKey = RedisKeyConstants.buildArticleCollectCacheKey(articleId, userId);
            redisUtil.delete(cacheKey);
            log.debug("清除文章收藏缓存, 文章ID: {}, 用户ID: {}", articleId, userId);
        } catch (Exception e) {
            log.error("清除文章收藏缓存失败, 文章ID: {}, 用户ID: {}", articleId, userId, e);
        }
    }

    @Override
    public void clearArticleCountCache(Long articleId) {
        if (articleId == null) return;
        try {
            // 清除所有计数类型缓存
            redisUtil.deletePattern(ARTICLE_PREFIX + articleId + ":*");
            log.debug("清除文章计数缓存, 文章ID: {}", articleId);
        } catch (Exception e) {
            log.error("清除文章计数缓存失败, 文章ID: {}", articleId, e);
        }
    }

    // ==================== 用户相关缓存清除 ====================

    @Override
    public void clearUserCache(Long userId) {
        if (userId == null) return;
        try {
            String cacheKey = RedisKeyConstants.buildUserKey(userId);
            redisUtil.delete(cacheKey);
            log.debug("清除用户信息缓存, 用户ID: {}", userId);
        } catch (Exception e) {
            log.error("清除用户信息缓存失败, 用户ID: {}", userId, e);
        }
    }

    @Override
    public void clearHotUserCache() {
        try {
            redisUtil.deletePattern(USER_HOT_PREFIX + "*");
            log.info("清除热门用户缓存完成");
        } catch (Exception e) {
            log.error("清除热门用户缓存失败", e);
        }
    }

    @Override
    public void clearUserSessionCache(Long userId) {
        if (userId == null) return;
        try {
            redisUtil.deletePattern(USER_SESSION_PREFIX + userId + ":*");
            log.debug("清除用户会话缓存, 用户ID: {}", userId);
        } catch (Exception e) {
            log.error("清除用户会话缓存失败, 用户ID: {}", userId, e);
        }
    }

    @Override
    public void clearLoginAttemptCache(String account) {
        if (account == null || account.isEmpty()) return;
        try {
            String cacheKey = RedisKeyConstants.buildCacheKey(LOGIN_ATTEMPT_PREFIX, account);
            redisUtil.delete(cacheKey);
            log.debug("清除用户登录尝试缓存, 账号: {}", account);
        } catch (Exception e) {
            log.error("清除用户登录尝试缓存失败, 账号: {}", account, e);
        }
    }

    // ==================== 评论相关缓存清除 ====================

    @Override
    public void clearArticleCommentCache(Long articleId) {
        if (articleId == null) return;
        try {
            redisUtil.deletePattern(COMMENT_LIST_PREFIX + articleId + ":*");
            log.debug("清除文章评论列表缓存, 文章ID: {}", articleId);
        } catch (Exception e) {
            log.error("清除文章评论列表缓存失败, 文章ID: {}", articleId, e);
        }
    }

    @Override
    public void clearCommentReplyCache(Long parentId) {
        if (parentId == null) return;
        try {
            String pattern = RedisKeyConstants.buildCommentReplyPattern(parentId);
            redisUtil.deletePattern(pattern);
            log.debug("清除评论回复缓存, 父评论ID: {}", parentId);
        } catch (Exception e) {
            log.error("清除评论回复缓存失败, 父评论ID: {}", parentId, e);
        }
    }

    // ==================== 通知相关缓存清除 ====================

    @Override
    public void clearNotificationUnreadCache(Long userId) {
        if (userId == null) return;
        try {
            String key1 = NOTIFICATION_UNREAD_COUNT + userId;
            String key2 = NOTIFICATION_UNREAD_COUNT_BY_CATEGORY + userId;
            redisUtil.delete(key1);
            redisUtil.delete(key2);
            log.debug("清除通知未读数缓存, 用户ID: {}", userId);
        } catch (Exception e) {
            log.error("清除通知未读数缓存失败, 用户ID: {}", userId, e);
        }
    }

    @Override
    public void clearNotificationListCache(Long userId) {
        if (userId == null) return;
        try {
            String cacheKey = NOTIFICATION_RECENT_LIST + userId;
            redisUtil.delete(cacheKey);
            log.debug("清除通知列表缓存, 用户ID: {}", userId);
        } catch (Exception e) {
            log.error("清除通知列表缓存失败, 用户ID: {}", userId, e);
        }
    }

    // ==================== 分类和标签相关缓存清除 ====================

    @Override
    public void clearCategoryCache(Long categoryId) {
        if (categoryId == null) return;
        try {
            String cacheKey = RedisKeyConstants.buildCategoryVersionKey(categoryId);
            redisUtil.delete(cacheKey);
            log.debug("清除分类缓存, 分类ID: {}", categoryId);
        } catch (Exception e) {
            log.error("清除分类缓存失败, 分类ID: {}", categoryId, e);
        }
    }

    @Override
    public void clearAllCategoryCache() {
        try {
            redisUtil.deletePattern(CATEGORY_PREFIX + "*");
            log.info("清除所有分类缓存完成");
        } catch (Exception e) {
            log.error("清除所有分类缓存失败", e);
        }
    }

    @Override
    public void clearTagCache(Long tagId) {
        if (tagId == null) return;
        try {
            String cacheKey = RedisKeyConstants.buildTagVersionKey(tagId);
            redisUtil.delete(cacheKey);
            log.debug("清除标签缓存, 标签ID: {}", tagId);
        } catch (Exception e) {
            log.error("清除标签缓存失败, 标签ID: {}", tagId, e);
        }
    }

    @Override
    public void clearAllTagCache() {
        try {
            redisUtil.deletePattern(TAG_PREFIX + "*");
            log.info("清除所有标签缓存完成");
        } catch (Exception e) {
            log.error("清除所有标签缓存失败", e);
        }
    }

    /** ==================== 系统相关缓存清除 ==================== */

    @Override
    public void clearSystemConfigCache() {
        try {
            redisUtil.deletePattern(SYSTEM_CONFIG_PREFIX + "*");
            log.info("清除系统配置缓存完成");
        } catch (Exception e) {
            log.error("清除系统配置缓存失败", e);
        }
    }

    @Override
    public void clearVerifyCodeCache(String account, String purpose) {
        if (account == null || account.isEmpty()) return;
        try {
            String cacheKey = RedisKeyConstants.buildVerifyCodeKey(account, purpose);
            redisUtil.delete(cacheKey);
            log.debug("清除验证码缓存, 账号: {}, 用途: {}", account, purpose);
        } catch (Exception e) {
            log.error("清除验证码缓存失败, 账号: {}, 用途: {}", account, purpose, e);
        }
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.CACHE_COLLECTION_STATUS,
            key = "#userId + ':' + #articleId")
    public void clearCollectionStatusCache(Long articleId, Long userId) {
        log.debug("清理收藏状态缓存, 文章ID: {}, 用户ID: {}", articleId, userId);
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.CACHE_COLLECTION_STATUS,
            key = "#userId + ':*'")
    public void clearUserCollectionCache(Long userId) {
        log.debug("清理用户所有收藏缓存, 用户ID: {}", userId);
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.USER_ARTICLE_LIST_PREFIX,
            key = "#userId + ':*'")
    public void clearUserArticleCache(Long userId) {
        log.debug("清理用户文章相关缓存, 用户ID: {}", userId);
    }

    @Override
    public void cleanCacheAfterArticleCreate(Long articleId, Long userId) {
        try {
            // 创建文章后清理：最新文章、用户文章、搜索缓存
            clearLatestArticleCache();
            clearUserArticleListCache(userId);
            clearArticleSearchCache();
            clearArticleListCache();
            log.info("文章创建后清理缓存成功, 文章ID: {}", articleId);
        } catch (Exception e) {
            log.error("文章创建后清理缓存失败, 文章ID: {}", articleId, e);
        }
    }

    // ==================== 专栏订阅相关缓存清除 ====================

    @Override
    @CacheEvict(value = RedisKeyConstants.CACHE_COLUMN_SUBSCRIPTION_STATUS,
            key = "#userId + ':' + #columnId")
    public void clearColumnSubscriptionStatusCache(Long columnId, Long userId) {
        log.debug("清理专栏订阅状态缓存, 专栏ID: {}, 用户ID: {}", columnId, userId);
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.CACHE_COLUMN_SUBSCRIPTION_LIST,
            key = "#userId + ':*'")
    public void clearUserSubscriptionListCache(Long userId) {
        log.debug("清理用户订阅列表缓存, 用户ID: {}", userId);
    }

    @Override
    public void clearColumnSubscriptionCache(Long columnId, Long userId) {
        clearColumnSubscriptionStatusCache(columnId, userId);
        clearUserSubscriptionListCache(userId);
        log.debug("清理专栏订阅相关的所有缓存, 专栏ID: {}, 用户ID: {}", columnId, userId);
    }

}
