package com.inkstage.cache.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.service.CacheClearService;
import com.inkstage.cache.service.CacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 缓存清除服务实现类
 * 统一管理所有缓存清除操作
 * 使用 CacheManager 实现缓存管理
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheClearServiceImpl implements CacheClearService {

    private final CacheManager cacheManager;

    // ==================== 文章相关缓存清除 ====================

    @Override
    public void clearArticleDetailCache(Long articleId) {
        if (articleId == null) return;
        try {
            String cacheKey = CacheKey.keyForArticleDetail(articleId);
            cacheManager.delete(cacheKey);
            log.debug("清除文章详情缓存, 文章ID: {}", articleId);
        } catch (Exception e) {
            log.error("清除文章详情缓存失败, 文章ID: {}", articleId, e);
        }
    }

    @Override
    public void clearArticleListCache() {
        try {
            cacheManager.deletePattern(CacheKey.ARTICLES);
            log.info("清除文章列表缓存完成");
        } catch (Exception e) {
            log.error("清除文章列表缓存失败", e);
        }
    }

    @Override
    public void clearUserArticleListCache(Long userId) {
        if (userId == null) return;
        try {
            cacheManager.deletePattern(CacheKey.USER_ARTICLES + userId);
            log.debug("清除用户文章列表缓存, 用户ID: {}", userId);
        } catch (Exception e) {
            log.error("清除用户文章列表缓存失败, 用户ID: {}", userId, e);
        }
    }

    @Override
    public void clearHotArticleCache() {
        try {
            cacheManager.deletePattern(CacheKey.ARTICLE_HOT);
            log.info("清除热门文章缓存完成");
        } catch (Exception e) {
            log.error("清除热门文章缓存失败", e);
        }
    }

    @Override
    public void clearLatestArticleCache() {
        try {
            cacheManager.deletePattern(CacheKey.ARTICLE_LATEST);
            log.info("清除最新文章缓存完成");
        } catch (Exception e) {
            log.error("清除最新文章缓存失败", e);
        }
    }

    @Override
    public void clearBannerArticleCache() {
        try {
            cacheManager.deletePattern(CacheKey.ARTICLE_BANNER);
            log.info("清除轮播文章缓存完成");
        } catch (Exception e) {
            log.error("清除轮播文章缓存失败", e);
        }
    }

    @Override
    public void clearArticleSearchCache() {
        try {
            cacheManager.deletePattern(CacheKey.ARTICLE_SEARCH);
            log.info("清除文章搜索缓存完成");
        } catch (Exception e) {
            log.error("清除文章搜索缓存失败", e);
        }
    }

    @Override
    public void clearAllArticleCache() {
        try {
            clearArticleListCache();
            clearHotArticleCache();
            clearLatestArticleCache();
            clearBannerArticleCache();
            clearArticleSearchCache();
            cacheManager.deletePattern(CacheKey.ARTICLE);
            log.info("清除所有文章相关缓存完成");
        } catch (Exception e) {
            log.error("清除所有文章相关缓存失败", e);
        }
    }

    @Override
    public void clearArticleLikeCache(Long articleId, Long userId) {
        if (articleId == null || userId == null) return;
        try {
            String cacheKey = CacheKey.keyForArticleLikeStatus(articleId, userId);
            cacheManager.delete(cacheKey);
            log.debug("清除文章点赞缓存, 文章ID: {}, 用户ID: {}", articleId, userId);
        } catch (Exception e) {
            log.error("清除文章点赞缓存失败, 文章ID: {}, 用户ID: {}", articleId, userId, e);
        }
    }

    @Override
    public void clearArticleCollectCache(Long articleId, Long userId) {
        if (articleId == null || userId == null) return;
        try {
            String cacheKey = CacheKey.keyForArticleCollectionStatus(articleId, userId);
            cacheManager.delete(cacheKey);
            log.debug("清除文章收藏缓存, 文章ID: {}, 用户ID: {}", articleId, userId);
        } catch (Exception e) {
            log.error("清除文章收藏缓存失败, 文章ID: {}, 用户ID: {}", articleId, userId, e);
        }
    }

    @Override
    public void clearArticleCountCache(Long articleId) {
        if (articleId == null) return;
        try {
            cacheManager.deletePattern(CacheKey.ARTICLE + articleId);
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
            String cacheKey = CacheKey.keyForUserInfo(userId);
            cacheManager.delete(cacheKey);
            log.debug("清除用户信息缓存, 用户ID: {}", userId);
        } catch (Exception e) {
            log.error("清除用户信息缓存失败, 用户ID: {}", userId, e);
        }
    }

    @Override
    public void clearHotUserCache() {
        try {
            cacheManager.deletePattern(CacheKey.USER_HOT);
            log.info("清除热门用户缓存完成");
        } catch (Exception e) {
            log.error("清除热门用户缓存失败", e);
        }
    }

    @Override
    public void clearUserSessionCache(Long userId) {
        if (userId == null) return;
        try {
            cacheManager.deletePattern(CacheKey.USER_SESSION + userId);
            log.debug("清除用户会话缓存, 用户ID: {}", userId);
        } catch (Exception e) {
            log.error("清除用户会话缓存失败, 用户ID: {}", userId, e);
        }
    }

    @Override
    public void clearLoginAttemptCache(String account) {
        if (account == null || account.isEmpty()) return;
        try {
            String cacheKey = CacheKey.keyForLoginAttempt(account);
            cacheManager.delete(cacheKey);
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
            cacheManager.deletePattern(CacheKey.COMMENT_LIST + articleId);
            log.debug("清除文章评论列表缓存, 文章ID: {}", articleId);
        } catch (Exception e) {
            log.error("清除文章评论列表缓存失败, 文章ID: {}", articleId, e);
        }
    }

    @Override
    public void clearCommentReplyCache(Long parentId) {
        if (parentId == null) return;
        try {
            cacheManager.deletePattern(CacheKey.COMMENT_REPLY + parentId);
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
            String key = CacheKey.keyForNotificationUnreadCount(userId);
            cacheManager.delete(key);
            log.debug("清除通知未读数缓存, 用户ID: {}", userId);
        } catch (Exception e) {
            log.error("清除通知未读数缓存失败, 用户ID: {}", userId, e);
        }
    }

    @Override
    public void clearNotificationSettingCache(Long userId) {
        if (userId == null) return;
        try {
            String cacheKey = CacheKey.keyForNotificationSetting(userId);
            cacheManager.delete(cacheKey);
            log.debug("清除通知设置缓存, 用户ID: {}", userId);
        } catch (Exception e) {
            log.error("清除通知设置缓存失败, 用户ID: {}", userId, e);
        }
    }

    // ==================== 分类和标签相关缓存清除 ====================

    @Override
    public void clearCategoryCache(Long categoryId) {
        if (categoryId == null) return;
        try {
            cacheManager.deletePattern(CacheKey.CATEGORY + categoryId);
            log.debug("清除分类缓存, 分类ID: {}", categoryId);
        } catch (Exception e) {
            log.error("清除分类缓存失败, 分类ID: {}", categoryId, e);
        }
    }

    @Override
    public void clearAllCategoryCache() {
        try {
            cacheManager.deletePattern(CacheKey.CATEGORY);
            log.info("清除所有分类缓存完成");
        } catch (Exception e) {
            log.error("清除所有分类缓存失败", e);
        }
    }

    @Override
    public void clearTagCache(Long tagId) {
        if (tagId == null) return;
        try {
            cacheManager.deletePattern(CacheKey.TAG + tagId);
            log.debug("清除标签缓存, 标签ID: {}", tagId);
        } catch (Exception e) {
            log.error("清除标签缓存失败, 标签ID: {}", tagId, e);
        }
    }

    @Override
    public void clearAllTagCache() {
        try {
            cacheManager.deletePattern(CacheKey.TAG);
            log.info("清除所有标签缓存完成");
        } catch (Exception e) {
            log.error("清除所有标签缓存失败", e);
        }
    }

    // ==================== 系统相关缓存清除 ====================

    @Override
    public void clearSystemConfigCache() {
        try {
            cacheManager.deletePattern(CacheKey.SYSTEM_CONFIG);
            log.info("清除系统配置缓存完成");
        } catch (Exception e) {
            log.error("清除系统配置缓存失败", e);
        }
    }

    @Override
    public void clearVerifyCodeCache(String account, String purpose) {
        if (account == null || account.isEmpty()) return;
        try {
            String cacheKey = CacheKey.keyForVerifyCode(account, purpose);
            cacheManager.delete(cacheKey);
            log.debug("清除验证码缓存, 账号: {}, 用途: {}", account, purpose);
        } catch (Exception e) {
            log.error("清除验证码缓存失败, 账号: {}, 用途: {}", account, purpose, e);
        }
    }

    @Override
    public void clearCollectionStatusCache(Long articleId, Long userId) {
        if (articleId == null || userId == null) return;
        try {
            String cacheKey = CacheKey.keyForArticleCollectionStatus(articleId, userId);
            cacheManager.delete(cacheKey);
            log.debug("清理收藏状态缓存, 文章ID: {}, 用户ID: {}", articleId, userId);
        } catch (Exception e) {
            log.error("清理收藏状态缓存失败, 文章ID: {}, 用户ID: {}", articleId, userId, e);
        }
    }

    @Override
    public void clearUserCollectionCache(Long userId) {
        if (userId == null) return;
        try {
            cacheManager.deletePattern(CacheKey.ARTICLE_COLLECT_STATUS + "*:" + userId);
            log.debug("清理用户所有收藏缓存, 用户ID: {}", userId);
        } catch (Exception e) {
            log.error("清理用户所有收藏缓存失败, 用户ID: {}", userId, e);
        }
    }

    @Override
    public void clearUserArticleCache(Long userId) {
        if (userId == null) return;
        try {
            cacheManager.deletePattern(CacheKey.USER_ARTICLES + userId);
            log.debug("清理用户文章相关缓存, 用户ID: {}", userId);
        } catch (Exception e) {
            log.error("清理用户文章相关缓存失败, 用户ID: {}", userId, e);
        }
    }

    @Override
    public void cleanCacheAfterArticleCreate(Long articleId, Long userId) {
        try {
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
    public void clearColumnSubscriptionStatusCache(Long columnId, Long userId) {
        if (userId == null) return;
        try {
            cacheManager.deletePattern(CacheKey.COLUMN_SUBSCRIPTION_STATUS + userId);
            log.debug("清理用户专栏订阅状态缓存, 专栏ID: {}, 用户ID: {}", columnId, userId);
        } catch (Exception e) {
            log.error("清理用户专栏订阅状态失败, 专栏ID: {}, 用户ID: {}", columnId, userId, e);
        }
    }

    @Override
    public void clearUserSubscriptionListCache(Long userId) {
        if (userId == null) return;
        try {
            String cacheKey = CacheKey.COLUMN_SUBSCRIPTION_LIST + userId + ":*";
            log.info("清理用户订阅列表缓存key: {}", cacheKey);
            cacheManager.deletePattern(cacheKey);
            log.debug("清理用户订阅列表缓存, 用户ID: {}", userId);
        } catch (Exception e) {
            log.error("清理用户订阅列表缓存失败, 用户ID: {}", userId, e);
        }
    }

    @Override
    public void clearColumnSubscriptionCache(Long columnId, Long userId) {
        clearColumnSubscriptionStatusCache(columnId, userId);
        clearUserSubscriptionListCache(userId);
        log.debug("清理专栏订阅相关的所有缓存, 专栏ID: {}, 用户ID: {}", columnId, userId);
    }

    // ==================== 专栏相关缓存清除 ====================

    @Override
    public void clearColumnDetailCache(Long columnId) {
        if (columnId == null) return;
        try {
            String cacheKey = CacheKey.keyForColumnDetail(columnId);
            cacheManager.delete(cacheKey);
            log.debug("清除专栏详情缓存, 专栏ID: {}", columnId);
        } catch (Exception e) {
            log.error("清除专栏详情缓存失败, 专栏ID: {}", columnId, e);
        }
    }

    @Override
    public void clearColumnListCache() {
        try {
            cacheManager.deletePattern(CacheKey.COLUMN);
            log.info("清除专栏列表缓存完成");
        } catch (Exception e) {
            log.error("清除专栏列表缓存失败", e);
        }
    }

    @Override
    public void clearColumnArticlesCache(Long columnId) {
        if (columnId == null) return;
        try {
            cacheManager.deletePattern(CacheKey.COLUMN_ARTICLES + columnId);
            log.debug("清除专栏文章列表缓存, 专栏ID: {}", columnId);
        } catch (Exception e) {
            log.error("清除专栏文章列表缓存失败, 专栏ID: {}", columnId, e);
        }
    }

    @Override
    public void clearHotColumnCache() {
        try {
            cacheManager.deletePattern(CacheKey.COLUMN_HOT);
            log.info("清除热门专栏缓存完成");
        } catch (Exception e) {
            log.error("清除热门专栏缓存失败", e);
        }
    }
}
