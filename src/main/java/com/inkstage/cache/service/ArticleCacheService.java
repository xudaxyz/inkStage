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

    /**
     * 获取文章详情（带缓存）
     */
    ArticleDetailVO getArticleDetail(Long id);

    /**
     * 获取文章列表（带缓存）
     */
    PageResult<ArticleListVO> getArticles(int pageNum, int pageSize, Long categoryId, Long tagId);


    /**
     * 获取热门文章（带缓存）
     */
    List<ArticleListVO> getHotArticles(Integer limit, String timeRange);

    /**
     * 获取最新文章（带缓存）
     */
    List<ArticleListVO> getLatestArticles(Integer limit);

    /**
     * 获取轮播图文章（带缓存）
     */
    List<ArticleListVO> getBannerArticles(Integer limit);

    /**
     * 获取用户文章列表（带缓存）
     */
    PageResult<ArticleListVO> getUserArticles(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取作者相关文章（带缓存）
     */
    List<ArticleListVO> getUserRelatedArticles(Long userId, Long excludeArticleId, Integer limit);

    /**
     * 搜索文章（带缓存）
     */
    PageResult<ArticleListVO> searchArticles(String keyword, String sortBy, Integer pageNum, Integer pageSize);
}