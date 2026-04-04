package com.inkstage.cache.service.impl;

import com.inkstage.cache.constant.RedisKeyConstants;
import com.inkstage.cache.service.InteractionCacheService;
import com.inkstage.mapper.ArticleCollectionMapper;
import com.inkstage.mapper.ArticleLikeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @Cacheable(value = RedisKeyConstants.CACHE_COLLECTION_STATUS,
            key = "#userId + ':' + #articleId",
            unless = "#result == null")
    public boolean isArticleCollected(Long articleId, Long userId) {
        log.debug("查询收藏状态, 文章ID: {}, 用户ID: {}", articleId, userId);
        // 从数据库查询收藏状态
        return articleCollectionMapper.findByArticleIdAndUserId(articleId, userId) != null;
    }

    // ==================== 点赞相关缓存 ====================

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_LIKE_STATUS,
            key = "#userId + ':' + #articleId",
            unless = "#result == null")
    public boolean isArticleLiked(Long articleId, Long userId) {
        log.debug("查询点赞状态, 文章ID: {}, 用户ID: {}", articleId, userId);
        // 从数据库查询点赞状态
        return articleLikeMapper.findByArticleIdAndUserId(articleId, userId) != null;
    }

}