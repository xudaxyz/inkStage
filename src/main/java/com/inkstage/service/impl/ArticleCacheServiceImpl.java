package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.dto.front.ArticleQueryDTO;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.service.ArticleCacheService;
import com.inkstage.service.ArticleTagService;
import com.inkstage.service.FileService;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
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
    @Cacheable(value = "article:detail", key = "#id", unless = "#result == null")
    public ArticleDetailVO getArticleDetail(Long id) {
        ArticleDetailVO articleDetailVO = articleMapper.findDetailById(id);
        if (articleDetailVO == null) {
            log.warn("文章详情: {} 不存在", id);
            throw new BusinessException("文章不存在");
        }

        // 查询文章标签
        articleDetailVO.setTags(articleTagService.getTagsByArticleId(id));
        fileService.ensureArticleDetailIsFullUrl(articleDetailVO);

        log.debug("从数据库获取文章详情, id: {}", id);
        return articleDetailVO;
    }

    @Override
    @CacheEvict(value = "article:detail", key = "#id")
    public void clearArticleDetailCache(Long id) {
        log.info("清理文章详情缓存成功, id: {}", id);
    }

    // ==================== 文章列表缓存 ====================

    @Override
    @Cacheable(value = "article:list",
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
        fileService.ensureArticleImageAreFullUrl(articleList);
        long total = articleMapper.countArticleList(queryDTO);

        log.info("获取文章列表成功, 总数: {}, 页码: {}, 每页大小: {}",
                total, pageNum, pageSize);

        return PageResult.build(articleList, total, pageNum, pageSize);
    }

    @Override
    @CacheEvict(value = "article:list", allEntries = true)
    public void clearArticleListCache() {
        log.info("清理文章列表缓存成功");
    }

    // ==================== 热门文章缓存 ====================

    @Override
    @Cacheable(value = "article:hot",
            key = "#limit + ':' + (#timeRange ?: 'week')",
            unless = "#result == null")
    public List<ArticleListVO> getHotArticles(Integer limit, String timeRange) {
        List<ArticleListVO> hotArticles = articleMapper.findHotArticles(limit);
        fileService.ensureArticleImageAreFullUrl(hotArticles);

        log.info("获取热门文章成功, limit: {}, timeRange: {}", limit, timeRange);
        return hotArticles;
    }

    @Override
    @CacheEvict(value = "article:hot", allEntries = true)
    public void clearHotArticlesCache() {
        log.info("清理热门文章缓存成功");
    }

    // ==================== 最新文章缓存 ====================

    @Override
    @Cacheable(value = "article:latest", key = "#limit", unless = "#result == null")
    public List<ArticleListVO> getLatestArticles(Integer limit) {
        List<ArticleListVO> latestArticles = articleMapper.findLatestArticles(limit);
        fileService.ensureArticleImageAreFullUrl(latestArticles);

        log.info("获取最新文章成功, limit: {}", limit);
        return latestArticles;
    }

    @Override
    @CacheEvict(value = "article:latest", allEntries = true)
    public void clearLatestArticlesCache() {
        log.info("清理最新文章缓存成功");
    }

    // ==================== 轮播图文章缓存 ====================

    @Override
    @Cacheable(value = "article:banner", key = "#limit", unless = "#result == null")
    public List<ArticleListVO> getBannerArticles(Integer limit) {
        List<ArticleListVO> bannerArticles = articleMapper.findBannerArticles(limit);
        fileService.ensureArticleImageAreFullUrl(bannerArticles);

        log.info("获取轮播图文章成功, limit: {}", limit);
        return bannerArticles;
    }

    @Override
    @CacheEvict(value = "article:banner", allEntries = true)
    public void clearBannerArticlesCache() {
        log.info("清理轮播图文章缓存成功");
    }

    // ==================== 用户文章缓存 ====================

    @Override
    @Cacheable(value = "article:user",
            key = "#userId + ':' + #pageNum + ':' + #pageSize",
            unless = "#result == null")
    public PageResult<ArticleListVO> getUserArticles(Long userId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;

        List<ArticleListVO> articleList = articleMapper.findUserArticles(userId, offset, pageSize);
        fileService.ensureArticleImageAreFullUrl(articleList);
        long total = articleMapper.countUserArticles(userId);

        log.debug("从数据库获取用户文章列表, userId: {}, pageNum: {}", userId, pageNum);
        return PageResult.build(articleList, total, pageNum, pageSize);
    }

    @Override
    @CacheEvict(value = "article:user", allEntries = true)
    public void clearUserArticlesCache() {
        log.info("清理用户文章缓存成功");
    }

    // ==================== 作者相关文章缓存 ====================

    @Override
    @Cacheable(value = "article:user:related",
            key = "#userId + ':' + #excludeArticleId + ':' + #limit",
            unless = "#result == null")
    public List<ArticleListVO> getUserRelatedArticles(Long userId, Long excludeArticleId, Integer limit) {
        List<ArticleListVO> relatedArticles = articleMapper.findUserRelatedArticles(userId, excludeArticleId, limit);
        fileService.ensureArticleImageAreFullUrl(relatedArticles);

        log.debug("从数据库获取作者相关文章, userId: {}, limit: {}", userId, limit);
        return relatedArticles;
    }

    @Override
    @CacheEvict(value = "article:user:related", allEntries = true)
    public void clearUserRelatedArticlesCache() {
        log.info("清理作者相关文章缓存成功");
    }

    // ==================== 搜索缓存 ====================

    @Override
    @Cacheable(value = "article:search",
            key = "#keyword + ':' + (#sortBy ?: 'default') + ':' + #pageNum + ':' + #pageSize",
            unless = "#result == null or #result.total == 0")
    public PageResult<ArticleListVO> searchArticles(String keyword, String sortBy, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;

        List<ArticleListVO> articleList = articleMapper.searchArticles(keyword, sortBy, offset, pageSize);
        fileService.ensureArticleImageAreFullUrl(articleList);
        long total = articleMapper.countSearchArticles(keyword);

        log.info("搜索文章成功, 关键词: {}, 总数: {}", keyword, total);
        return PageResult.build(articleList, total, pageNum, pageSize);
    }

    @Override
    @CacheEvict(value = "article:search", allEntries = true)
    public void clearSearchArticlesCache() {
        log.info("清理搜索文章缓存成功");
    }

    // ==================== 全局缓存操作 ====================

    @Override
    @CacheEvict(value = {
            "article:list",
            "article:detail",
            "article:hot",
            "article:latest",
            "article:banner",
            "article:user",
            "article:user:related",
            "article:my",
            "article:search"
    }, allEntries = true)
    public void clearAllArticleCache() {
        log.info("清理所有文章缓存成功");
    }

    // ==================== 场景化缓存清理实现 ====================

    @Override
    public void cleanCacheAfterArticleCreate(Long articleId, Long userId) {
        try {
            // 创建文章后清理：最新文章、用户文章、搜索缓存
            clearLatestArticlesCache();
            clearUserArticlesCache();
            clearSearchArticlesCache();
            log.info("文章创建后清理缓存成功, 文章ID: {}", articleId);
        } catch (Exception e) {
            log.error("文章创建后清理缓存失败, 文章ID: {}", articleId, e);
        }
    }

    @Override
    public void cleanCacheAfterArticleUpdate(Long articleId, Long userId) {
        try {
            // 更新文章后清理：文章详情、列表、搜索缓存
            clearArticleDetailCache(articleId);
            clearArticleListCache();
            clearSearchArticlesCache();
            log.info("文章更新后清理缓存成功, 文章ID: {}", articleId);
        } catch (Exception e) {
            log.error("文章更新后清理缓存失败, 文章ID: {}", articleId, e);
        }
    }

    @Override
    public void cleanCacheAfterArticleDelete(Long articleId, Long userId) {
        try {
            // 删除文章后清理：所有相关缓存
            clearArticleDetailCache(articleId);
            clearArticleListCache();
            clearHotArticlesCache();
            clearLatestArticlesCache();
            clearUserArticlesCache();
            clearSearchArticlesCache();
            log.info("文章删除后清理缓存成功, 文章ID: {}", articleId);
        } catch (Exception e) {
            log.error("文章删除后清理缓存失败, 文章ID: {}", articleId, e);
        }
    }

    @Override
    public void cleanCacheAfterAdminOperation(Long articleId) {
        try {
            // 管理员操作后清理：文章详情、列表、热门、搜索缓存
            clearArticleDetailCache(articleId);
            clearArticleListCache();
            clearHotArticlesCache();
            clearSearchArticlesCache();
            log.info("管理员操作后清理缓存成功, 文章ID: {}", articleId);
        } catch (Exception e) {
            log.error("管理员操作后清理缓存失败, 文章ID: {}", articleId, e);
        }
    }
}
