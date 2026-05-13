package com.inkstage.cache.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.constant.CacheTTL;
import com.inkstage.cache.service.ArticleCacheService;
import com.inkstage.cache.service.CacheManager;
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
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.util.List;

/**
 * 文章缓存服务实现类
 * 专门负责文章相关的缓存操作
 * 使用 CacheManager 实现缓存管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleCacheServiceImpl implements ArticleCacheService {

    private final ArticleMapper articleMapper;
    private final ArticleTagService articleTagService;
    private final FileService fileService;
    private final CacheManager cacheManager;

    // ==================== 文章详情缓存 ====================

    @Override
    public ArticleDetailVO getArticleDetail(Long id) {
        String cacheKey = CacheKey.keyForArticleDetail(id);
        ArticleDetailVO articleDetailVO = cacheManager.get(cacheKey, ArticleDetailVO.class);
        if (articleDetailVO != null) {
            log.debug("从缓存获取文章详情, id: {}", id);
            return articleDetailVO;
        }

        articleDetailVO = articleMapper.findDetailById(id);
        if (articleDetailVO == null) {
            log.warn("文章详情: {} 不存在", id);
            throw new BusinessException("文章不存在");
        }

        // 查询文章标签
        articleDetailVO.setTags(articleTagService.getTagsByArticleId(id));
        fileService.ensureImageFullUrl(articleDetailVO);

        log.debug("从数据库获取文章详情, id: {}", id);
        cacheManager.set(cacheKey, articleDetailVO, CacheTTL.ARTICLE_DETAIL);
        return articleDetailVO;
    }

    // ==================== 文章列表缓存 ====================

    @Override
    public PageResult<ArticleListVO> getArticles(int pageNum, int pageSize, Long categoryId, Long tagId) {
        String cacheKey = CacheKey.keyForArticleList(pageNum, pageSize, categoryId, tagId);
        PageResult<ArticleListVO> result = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (result != null) {
            log.debug("从缓存获取文章列表, pageNum: {}, pageSize: {}", pageNum, pageSize);
            return result;
        }

        ArticleQueryDTO queryDTO = new ArticleQueryDTO();
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        queryDTO.setCategoryId(categoryId);
        queryDTO.setTagId(tagId);
        queryDTO.setOffset((pageNum - 1) * pageSize);

        List<ArticleListVO> articleList = articleMapper.findArticleList(queryDTO);
        long total = articleMapper.countArticleList(queryDTO);

        fileService.ensureImageFullUrl(articleList);

        result = PageResult.build(articleList, total, pageNum, pageSize);
        log.info("从数据库获取文章列表成功, 总数: {}, 页码: {}, 每页大小: {}", total, pageNum, pageSize);
        cacheManager.set(cacheKey, result, CacheTTL.ARTICLE_LIST);
        return result;
    }

    // ==================== 热门文章缓存 ====================

    @Override
    public List<ArticleListVO> getHotArticles(Integer limit, String timeRange) {
        String cacheKey = CacheKey.keyForArticleHot(limit, timeRange);
        List<ArticleListVO> hotArticles = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (hotArticles != null) {
            log.debug("从缓存获取热门文章, limit: {}, timeRange: {}", limit, timeRange);
            return hotArticles;
        }

        hotArticles = articleMapper.findHotArticles(limit);
        fileService.ensureImageFullUrl(hotArticles);

        log.info("从数据库获取热门文章成功, limit: {}, timeRange: {}", limit, timeRange);
        cacheManager.set(cacheKey, hotArticles, CacheTTL.ARTICLE_HOT);
        return hotArticles;
    }

    // ==================== 最新文章缓存 ====================

    @Override
    public List<ArticleListVO> getLatestArticles(Integer limit) {
        String cacheKey = CacheKey.keyForArticleLatest(limit);
        List<ArticleListVO> latestArticles = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (latestArticles != null) {
            log.debug("从缓存获取最新文章, limit: {}", limit);
            return latestArticles;
        }

        latestArticles = articleMapper.findLatestArticles(limit);
        fileService.ensureImageFullUrl(latestArticles);

        log.info("从数据库获取最新文章成功, limit: {}", limit);
        cacheManager.set(cacheKey, latestArticles, CacheTTL.ARTICLE_LATEST);
        return latestArticles;
    }

    // ==================== 轮播图文章缓存 ====================

    @Override
    public List<ArticleListVO> getBannerArticles(Integer limit) {
        String cacheKey = CacheKey.keyForArticleBanner(limit);
        List<ArticleListVO> bannerArticles = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (bannerArticles != null) {
            log.debug("从缓存获取轮播图文章, limit: {}", limit);
            return bannerArticles;
        }

        bannerArticles = articleMapper.findBannerArticles(limit);
        fileService.ensureImageFullUrl(bannerArticles);

        log.info("从数据库获取轮播图文章成功, limit: {}", limit);
        cacheManager.set(cacheKey, bannerArticles, CacheTTL.ARTICLE_BANNER);
        return bannerArticles;
    }

    // ==================== 用户文章缓存 ====================

    @Override
    public PageResult<ArticleListVO> getUserArticles(Long userId, Integer pageNum, Integer pageSize) {
        String cacheKey = CacheKey.keyForUserArticles(userId, pageNum, pageSize);
        PageResult<ArticleListVO> result = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (result != null) {
            log.debug("从缓存获取用户文章列表, userId: {}, pageNum: {}", userId, pageNum);
            return result;
        }

        int offset = (pageNum - 1) * pageSize;
        List<ArticleListVO> articleList = articleMapper.findUserArticles(userId, offset, pageSize);
        long total = articleMapper.countUserArticles(userId);

        fileService.ensureImageFullUrl(articleList);

        result = PageResult.build(articleList, total, pageNum, pageSize);
        log.debug("从数据库获取用户文章列表, userId: {}, pageNum: {}", userId, pageNum);
        cacheManager.set(cacheKey, result, CacheTTL.USER_ARTICLES);
        return result;
    }

    // ==================== 作者相关文章缓存 ====================

    @Override
    public List<ArticleListVO> getUserRelatedArticles(Long userId, Long excludeArticleId, Integer limit) {
        String cacheKey = CacheKey.keyForUserRelatedArticles(userId, excludeArticleId, limit);
        List<ArticleListVO> relatedArticles = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (relatedArticles != null) {
            log.debug("从缓存获取作者相关文章, userId: {}, limit: {}", userId, limit);
            return relatedArticles;
        }

        relatedArticles = articleMapper.findUserRelatedArticles(userId, excludeArticleId, limit);
        fileService.ensureImageFullUrl(relatedArticles);

        log.debug("从数据库获取作者相关文章, userId: {}, limit: {}", userId, limit);
        cacheManager.set(cacheKey, relatedArticles, CacheTTL.USER_HOT);
        return relatedArticles;
    }

    // ==================== 搜索缓存 ====================

    @Override
    public PageResult<ArticleListVO> searchArticles(String keyword, String sortBy, Integer pageNum, Integer pageSize) {
        String cacheKey = CacheKey.keyForArticleSearch(keyword, sortBy, pageNum, pageSize);
        PageResult<ArticleListVO> result = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (result != null) {
            log.debug("从缓存获取搜索结果, 关键词: {}, 页码: {}", keyword, pageNum);
            return result;
        }

        int offset = (pageNum - 1) * pageSize;
        List<ArticleListVO> articleList = articleMapper.searchArticles(keyword, sortBy, offset, pageSize);
        long total = articleMapper.countSearchArticles(keyword);

        fileService.ensureImageFullUrl(articleList);

        result = PageResult.build(articleList, total, pageNum, pageSize);
        log.info("从数据库搜索文章成功, 关键词: {}, 总数: {}", keyword, total);
        if (total > 0) {
            cacheManager.set(cacheKey, result, CacheTTL.ARTICLE_SEARCH);
        } else {
            cacheManager.set(cacheKey, result, CacheTTL.FIVE_MINUTES);
        }
        return result;
    }
}
