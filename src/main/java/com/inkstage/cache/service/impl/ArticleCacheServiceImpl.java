package com.inkstage.cache.service.impl;

import com.inkstage.cache.constant.RedisKeyConstants;
import com.inkstage.cache.service.ArticleCacheService;
import com.inkstage.common.PageResult;
import com.inkstage.dto.front.ArticleQueryDTO;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.service.ArticleTagService;
import com.inkstage.service.FileService;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文章缓存服务实现类
 * 专门负责文章相关的缓存操作
 * 使用Spring Cache注解实现声明式缓存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleCacheServiceImpl implements ArticleCacheService {

    private final ArticleMapper articleMapper;
    private final ArticleTagService articleTagService;
    private final FileService fileService;

    // ==================== 文章详情缓存 ====================

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_ARTICLE_DETAIL, key = "#id", unless = "#result == null")
    public ArticleDetailVO getArticleDetail(Long id) {
        ArticleDetailVO articleDetailVO = articleMapper.findDetailById(id);
        if (articleDetailVO == null) {
            log.warn("文章详情: {} 不存在", id);
            throw new BusinessException("文章不存在");
        }

        // 查询文章标签
        articleDetailVO.setTags(articleTagService.getTagsByArticleId(id));
        fileService.ensureImageFullUrl(articleDetailVO);

        log.debug("从数据库获取文章详情, id: {}", id);
        return articleDetailVO;
    }

    // ==================== 文章列表缓存 ====================

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_ARTICLES,
            key = "#pageNum + ':' + #pageSize + ':' + (#categoryId ?: 0) + ':' + (#tagId ?: 0)",
            unless = "#result == null")
    public PageResult<ArticleListVO> getArticles(int pageNum, int pageSize, Long categoryId, Long tagId) {
        ArticleQueryDTO queryDTO = new ArticleQueryDTO();
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        queryDTO.setCategoryId(categoryId);
        queryDTO.setTagId(tagId);
        queryDTO.setOffset((pageNum - 1) * pageSize);

        List<ArticleListVO> articleList = articleMapper.findArticleList(queryDTO);
        long total = articleMapper.countArticleList(queryDTO);

        fileService.ensureImageFullUrl(articleList);

        log.info("获取文章列表成功, 总数: {}, 页码: {}, 每页大小: {}",
                total, pageNum, pageSize);

        return PageResult.build(articleList, total, pageNum, pageSize);
    }

    // ==================== 热门文章缓存 ====================

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_ARTICLE_HOT,
            key = "#limit + ':' + (#timeRange ?: 'week')",
            unless = "#result == null")
    public List<ArticleListVO> getHotArticles(Integer limit, String timeRange) {
        List<ArticleListVO> hotArticles = articleMapper.findHotArticles(limit);

        fileService.ensureImageFullUrl(hotArticles);

        log.info("获取热门文章成功, limit: {}, timeRange: {}", limit, timeRange);
        return hotArticles;
    }

    // ==================== 最新文章缓存 ====================

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_ARTICLE_LATEST, key = "#limit", unless = "#result == null")
    public List<ArticleListVO> getLatestArticles(Integer limit) {
        List<ArticleListVO> latestArticles = articleMapper.findLatestArticles(limit);

        fileService.ensureImageFullUrl(latestArticles);

        log.info("获取最新文章成功, limit: {}", limit);
        return latestArticles;
    }

    // ==================== 轮播图文章缓存 ====================

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_ARTICLE_BANNER, key = "#limit", unless = "#result == null")
    public List<ArticleListVO> getBannerArticles(Integer limit) {
        List<ArticleListVO> bannerArticles = articleMapper.findBannerArticles(limit);

        fileService.ensureImageFullUrl(bannerArticles);

        log.info("获取轮播图文章成功, limit: {}", limit);
        return bannerArticles;
    }

    // ==================== 用户文章缓存 ====================

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_ARTICLE_USER,
            key = "#userId + ':' + #pageNum + ':' + #pageSize",
            unless = "#result == null")
    public PageResult<ArticleListVO> getUserArticles(Long userId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;

        List<ArticleListVO> articleList = articleMapper.findUserArticles(userId, offset, pageSize);
        long total = articleMapper.countUserArticles(userId);

        fileService.ensureImageFullUrl(articleList);

        log.debug("从数据库获取用户文章列表, userId: {}, pageNum: {}", userId, pageNum);
        return PageResult.build(articleList, total, pageNum, pageSize);
    }

    // ==================== 作者相关文章缓存 ====================

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_ARTICLE_USER_RELATED,
            key = "#userId + ':' + #excludeArticleId + ':' + #limit",
            unless = "#result == null")
    public List<ArticleListVO> getUserRelatedArticles(Long userId, Long excludeArticleId, Integer limit) {
        List<ArticleListVO> relatedArticles = articleMapper.findUserRelatedArticles(userId, excludeArticleId, limit);

        fileService.ensureImageFullUrl(relatedArticles);

        log.debug("从数据库获取作者相关文章, userId: {}, limit: {}", userId, limit);
        return relatedArticles;
    }

    // ==================== 搜索缓存 ====================

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_ARTICLE_SEARCH,
            key = "#keyword + ':' + (#sortBy ?: 'default') + ':' + #pageNum + ':' + #pageSize",
            unless = "#result == null or #result.total == 0")
    public PageResult<ArticleListVO> searchArticles(String keyword, String sortBy, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;

        List<ArticleListVO> articleList = articleMapper.searchArticles(keyword, sortBy, offset, pageSize);
        long total = articleMapper.countSearchArticles(keyword);

        fileService.ensureImageFullUrl(articleList);

        log.info("搜索文章成功, 关键词: {}, 总数: {}", keyword, total);
        return PageResult.build(articleList, total, pageNum, pageSize);
    }
}