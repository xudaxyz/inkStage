package com.inkstage.cache.service;

/**
 * 交互缓存服务接口
 * 负责管理用户与文章之间的交互缓存，如收藏、点赞等
 */
public interface InteractionCacheService {
    
    // ==================== 收藏相关缓存 ====================

    /**
     * 检查文章是否被收藏（带缓存）
     * 
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 是否被收藏
     */
    boolean isArticleCollected(Long articleId, Long userId);
    
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
    
    // ==================== 点赞相关缓存 ====================

    /**
     * 检查文章是否被点赞（带缓存）
     * 
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 是否被点赞
     */
    boolean isArticleLiked(Long articleId, Long userId);
    
    /**
     * 清理点赞状态缓存
     * 
     * @param articleId 文章ID
     * @param userId 用户ID
     */
    void clearLikeStatusCache(Long articleId, Long userId);
    
    /**
     * 清理用户所有点赞相关缓存
     * 
     * @param userId 用户ID
     */
    void clearUserLikeCache(Long userId);
    
    // ==================== 批量操作 ====================

    /**
     * 清理用户所有交互相关缓存
     * 
     * @param userId 用户ID
     */
    void clearUserInteractionCache(Long userId);
}