package com.inkstage.cache.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.constant.CacheTTL;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.cache.service.InteractionCacheService;
import com.inkstage.mapper.ArticleCollectionMapper;
import com.inkstage.mapper.ArticleLikeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 交互缓存服务实现类
 * 专门负责互动相关的缓存操作（点赞、收藏）
 * 使用 CacheManager 实现缓存管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InteractionCacheServiceImpl implements InteractionCacheService {

    private final ArticleCollectionMapper articleCollectionMapper;
    private final ArticleLikeMapper articleLikeMapper;
    private final CacheManager cacheManager;

    // ==================== 收藏相关缓存 ====================

    /**
     * 查询文章收藏状态
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return 是否已收藏
     */
    @Override
    public boolean isArticleCollected(Long articleId, Long userId) {
        String cacheKey = CacheKey.keyForArticleCollectStatus(articleId, userId);
        Boolean cached = cacheManager.get(cacheKey, Boolean.class);
        if (cached != null) {
            log.debug("从缓存获取收藏状态, 文章ID: {}, 用户ID: {}, 状态: {}", articleId, userId, cached);
            return cached;
        }

        boolean collected = articleCollectionMapper.findByArticleIdAndUserId(articleId, userId) != null;
        log.debug("从数据库查询收藏状态, 文章ID: {}, 用户ID: {}, 状态: {}", articleId, userId, collected);
        cacheManager.setWithRandomOffset(cacheKey, collected, CacheTTL.ARTICLE_COLLECT_STATUS);
        return collected;
    }

    // ==================== 点赞相关缓存 ====================

    /**
     * 查询文章点赞状态
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return 是否已点赞
     */
    @Override
    public boolean isArticleLiked(Long articleId, Long userId) {
        String cacheKey = CacheKey.keyForArticleLikeStatus(articleId, userId);
        Boolean cached = cacheManager.get(cacheKey, Boolean.class);
        if (cached != null) {
            log.debug("从缓存获取点赞状态, 文章ID: {}, 用户ID: {}, 状态: {}", articleId, userId, cached);
            return cached;
        }

        boolean liked = articleLikeMapper.findByArticleIdAndUserId(articleId, userId) != null;
        log.debug("从数据库查询点赞状态, 文章ID: {}, 用户ID: {}, 状态: {}", articleId, userId, liked);
        cacheManager.setWithRandomOffset(cacheKey, liked, CacheTTL.ARTICLE_LIKE_STATUS);
        return liked;
    }
}
