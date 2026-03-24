package com.inkstage.service.impl;


import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.dto.front.ArticleCreateDTO;
import com.inkstage.dto.front.ArticleQueryDTO;
import com.inkstage.dto.front.MyArticleQueryDTO;
import com.inkstage.exception.BusinessException;
import com.inkstage.service.*;
import com.inkstage.service.util.ArticleServiceUtils;
import com.inkstage.utils.RedisCacheManager;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;
import com.inkstage.vo.front.MyArticleListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文章服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleCreateService articleCreateService;
    private final ArticleQueryService articleQueryService;
    private final ArticleManagementService articleManagementService;
    private final ArticleSearchService articleSearchService;
    private final ArticleStatsService articleStatsService;
    private final RedisCacheManager cacheManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createArticle(ArticleCreateDTO articleCreateDTO) {
        Long articleId = articleCreateService.createArticle(articleCreateDTO);
        // 清除相关缓存
        cacheManager.clearArticleListCache();
        return articleId;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveDraft(Long id, ArticleCreateDTO dto) {
        return articleCreateService.saveDraft(id, dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteArticle(Long id) {
        return articleManagementService.deleteArticle(id);
    }

    @Override
    public PageResult<ArticleListVO> getArticles(ArticleQueryDTO queryDTO) {
        return articleQueryService.getArticles(queryDTO);
    }

    @Override
    public ArticleDetailVO getArticleDetail(Long id) {
        ArticleServiceUtils.validateArticleId(id, "获取文章详情");
        return articleQueryService.getArticleDetail(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticle(Long articleId, ArticleCreateDTO articleCreateDTO) {
        boolean updated = articleCreateService.updateArticle(articleId, articleCreateDTO);
        if (updated) {
            // 清除文章详情缓存
            cacheManager.clearArticleDetailCache(articleId);
            // 清除文章列表缓存
            cacheManager.clearArticleListCache();
        }
        return updated;
    }

    @Override
    public void incrementArticleReadCount(Long articleId, int count) {
        articleStatsService.incrementArticleReadCount(articleId, count);
    }

    @Override
    public boolean permanentDeleteArticle(Long id) {
        return articleManagementService.permanentDeleteArticle(id);
    }

    @Override
    public List<ArticleListVO> getHotArticles(Integer limit, String timeRange) {
        limit = ArticleServiceUtils.validateLimit(limit, 10);
        if (timeRange == null || timeRange.isEmpty()) {
            timeRange = "week";
        }
        return articleQueryService.getHotArticles(limit, timeRange);
    }

    @Override
    public List<ArticleListVO> getLatestArticles(Integer limit) {
        limit = ArticleServiceUtils.validateLimit(limit, 10);
        return articleQueryService.getLatestArticles(limit);
    }

    @Override
    public List<ArticleListVO> getBannerArticles(Integer limit) {
        limit = ArticleServiceUtils.validateLimit(limit, 5);
        return articleQueryService.getBannerArticles(limit);
    }

    @Override
    public PageResult<ArticleListVO> getUserArticles(Long userId, Integer pageNum, Integer pageSize) {
        ArticleServiceUtils.validateUserId(userId, "获取用户文章列表");
        int[] validatedParams = ArticleServiceUtils.validatePageParams(pageNum, pageSize);
        return articleQueryService.getUserArticles(userId, validatedParams[0], validatedParams[1]);
    }

    @Override
    public List<ArticleListVO> getUserRelatedArticles(Long userId, Long excludeArticleId, Integer limit) {
        ArticleServiceUtils.validateUserId(userId, "获取作者相关文章");
        limit = ArticleServiceUtils.validateLimit(limit, 5);
        return articleQueryService.getUserRelatedArticles(userId, excludeArticleId, limit);
    }

    @Override
    public PageResult<MyArticleListVO> getMyArticles(MyArticleQueryDTO queryDTO) {
        if (queryDTO == null) {
            log.warn("获取我的文章列表参数为空");
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "查询参数不能为空");
        }
        Integer pageNum = queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0 ? 1 : queryDTO.getPageNum();
        Integer pageSize = queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0 ? 10 : queryDTO.getPageSize();
        return articleManagementService.getMyArticles(queryDTO.getArticleStatus(), queryDTO.getKeyword(), pageNum, pageSize);
    }

    @Override
    public PageResult<ArticleListVO> searchArticles(String keyword, String sortBy, Integer pageNum, Integer pageSize) {
        int[] validatedParams = ArticleServiceUtils.validatePageParams(pageNum, pageSize);
        return articleSearchService.searchArticles(keyword, sortBy, validatedParams[0], validatedParams[1]);
    }
}

