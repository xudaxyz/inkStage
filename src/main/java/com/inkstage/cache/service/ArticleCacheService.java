package com.inkstage.cache.service;

import com.inkstage.common.PageResult;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;

import java.util.List;

/**
 * 文章缓存服务接口
 * 专门负责文章相关的缓存操作
 */
public interface ArticleCacheService {

    // ==================== 文章详情缓存 ====================

    /**
     * 获取文章详情（带缓存）
     */
    ArticleDetailVO getArticleDetail(Long id);

    /**
     * 清理文章详情缓存
     */
    void clearArticleDetailCache(Long id);

    // ==================== 文章列表缓存 ====================

    /**
     * 获取文章列表（带缓存）
     */
    PageResult<ArticleListVO> getArticles(int pageNum, int pageSize, Long categoryId, Long tagId);

    /**
     * 清理文章列表缓存
     */
    void clearArticleListCache();

    // ==================== 热门文章缓存 ====================

    /**
     * 获取热门文章（带缓存）
     */
    List<ArticleListVO> getHotArticles(Integer limit, String timeRange);

    /**
     * 清理热门文章缓存
     */
    void clearHotArticlesCache();

    // ==================== 最新文章缓存 ====================

    /**
     * 获取最新文章（带缓存）
     */
    List<ArticleListVO> getLatestArticles(Integer limit);

    /**
     * 清理最新文章缓存
     */
    void clearLatestArticlesCache();

    // ==================== 轮播图文章缓存 ====================

    /**
     * 获取轮播图文章（带缓存）
     */
    List<ArticleListVO> getBannerArticles(Integer limit);

    /**
     * 清理轮播图文章缓存
     */
    void clearBannerArticlesCache();

    // ==================== 用户文章缓存 ====================

    /**
     * 获取用户文章列表（带缓存）
     */
    PageResult<ArticleListVO> getUserArticles(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 清理用户文章缓存
     */
    void clearUserArticlesCache();

    // ==================== 作者相关文章缓存 ====================

    /**
     * 获取作者相关文章（带缓存）
     */
    List<ArticleListVO> getUserRelatedArticles(Long userId, Long excludeArticleId, Integer limit);

    /**
     * 清理作者相关文章缓存
     */
    void clearUserRelatedArticlesCache();

    // ==================== 搜索缓存 ====================

    /**
     * 搜索文章（带缓存）
     */
    PageResult<ArticleListVO> searchArticles(String keyword, String sortBy, Integer pageNum, Integer pageSize);

    /**
     * 清理搜索缓存
     */
    void clearSearchArticlesCache();

    // ==================== 全局缓存操作 ====================

    /**
     * 清理所有文章缓存
     */
    void clearAllArticleCache();

    // ==================== 场景化缓存清理 ====================

    /**
     * 文章创建后清理缓存
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     */
    void cleanCacheAfterArticleCreate(Long articleId, Long userId);

    /**
     * 文章更新后清理缓存
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     */
    void cleanCacheAfterArticleUpdate(Long articleId, Long userId);

    /**
     * 文章删除后清理缓存
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     */
    void cleanCacheAfterArticleDelete(Long articleId, Long userId);

    /**
     * 管理员操作后清理缓存
     *
     * @param articleId 文章ID
     */
    void cleanCacheAfterAdminOperation(Long articleId);
}