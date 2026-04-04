package com.inkstage.cache.service.impl;

import com.inkstage.cache.service.InteractionCacheService;
import com.inkstage.mapper.ArticleCollectionMapper;
import com.inkstage.mapper.ArticleLikeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 交互缓存服务实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class InteractionCacheServiceImpl implements InteractionCacheService {

    private final ArticleCollectionMapper articleCollectionMapper;
    private final ArticleLikeMapper articleLikeMapper;

    // ==================== 收藏相关缓存 ====================

    @Override
    @Cacheable(value = "collection:status",
            key = "#userId + ':' + #articleId",
            unless = "#result == null")
    public boolean isArticleCollected(Long articleId, Long userId) {
        log.debug("查询收藏状态, 文章ID: {}, 用户ID: {}", articleId, userId);
        // 从数据库查询收藏状态
        return articleCollectionMapper.findByArticleIdAndUserId(articleId, userId) != null;
    }

    @Override
    @CacheEvict(value = "collection:status",
            key = "#userId + ':' + #articleId")
    public void clearCollectionStatusCache(Long articleId, Long userId) {
        log.debug("清理收藏状态缓存, 文章ID: {}, 用户ID: {}", articleId, userId);
        // 缓存清理由@CacheEvict注解处理
    }

    @Override
    @CacheEvict(value = "collection:status",
            key = "#userId + ':*'")
    public void clearUserCollectionCache(Long userId) {
        log.debug("清理用户所有收藏缓存, 用户ID: {}", userId);
        // 缓存清理由@CacheEvict注解处理
    }

    // ==================== 点赞相关缓存 ====================

    @Override
    @Cacheable(value = "like:status",
            key = "#userId + ':' + #articleId",
            unless = "#result == null")
    public boolean isArticleLiked(Long articleId, Long userId) {
        log.debug("查询点赞状态, 文章ID: {}, 用户ID: {}", articleId, userId);
        // 从数据库查询点赞状态
        return articleLikeMapper.findByArticleIdAndUserId(articleId, userId) != null;
    }

    @Override
    @CacheEvict(value = "like:status",
            key = "#userId + ':' + #articleId")
    public void clearLikeStatusCache(Long articleId, Long userId) {
        log.debug("清理点赞状态缓存, 文章ID: {}, 用户ID: {}", articleId, userId);
        // 缓存清理由@CacheEvict注解处理
    }

    @Override
    @CacheEvict(value = "like:status",
            key = "#userId + ':*'")
    public void clearUserLikeCache(Long userId) {
        log.debug("清理用户所有点赞缓存, 用户ID: {}", userId);
        // 缓存清理由@CacheEvict注解处理
    }

    // ==================== 批量操作 ====================

    @Override
    public void clearUserInteractionCache(Long userId) {
        log.debug("清理用户所有交互缓存, 用户ID: {}", userId);
        // 清理用户所有收藏缓存
        clearUserCollectionCache(userId);
        // 清理用户所有点赞缓存
        clearUserLikeCache(userId);
    }
}