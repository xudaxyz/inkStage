package com.inkstage.service.impl;


import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.dto.admin.AdminArticleQueryDTO;
import com.inkstage.dto.front.ArticleCreateDTO;
import com.inkstage.dto.front.ArticleQueryDTO;
import com.inkstage.dto.front.MyArticleQueryDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.service.*;
import com.inkstage.utils.RedisCacheManager;
import com.inkstage.vo.admin.AdminArticleDetailVO;
import com.inkstage.vo.admin.AdminArticleVO;
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
        if (id == null || id <= 0) {
            log.warn("获取文章详情参数无效, 文章ID: {}", id);
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "文章ID无效");
        }
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
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        if (timeRange == null || timeRange.isEmpty()) {
            timeRange = "week";
        }
        return articleQueryService.getHotArticles(limit, timeRange);
    }

    @Override
    public List<ArticleListVO> getLatestArticles(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return articleQueryService.getLatestArticles(limit);
    }

    @Override
    public List<ArticleListVO> getBannerArticles(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 5;
        }
        return articleQueryService.getBannerArticles(limit);
    }

    @Override
    public PageResult<ArticleListVO> getUserArticles(Long userId, Integer pageNum, Integer pageSize) {
        if (userId == null || userId <= 0) {
            log.warn("获取用户文章列表参数无效, 用户ID: {}", userId);
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "用户ID无效");
        }
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        return articleQueryService.getUserArticles(userId, pageNum, pageSize);
    }

    @Override
    public List<ArticleListVO> getUserRelatedArticles(Long userId, Long excludeArticleId, Integer limit) {
        if (userId == null || userId <= 0) {
            log.warn("获取作者相关文章参数无效, 用户ID: {}", userId);
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "用户ID无效");
        }
        if (limit == null || limit <= 0) {
            limit = 5;
        }
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
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        return articleSearchService.searchArticles(keyword, sortBy, pageNum, pageSize);
    }

    @Override
    public Article getArticleById(Long id) {
        if (id == null || id <= 0) {
            log.warn("根据ID获取文章参数无效, 文章ID: {}", id);
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "文章ID无效");
        }
        return articleManagementService.getArticleById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Article updateArticleStatus(Long id, ArticleStatus status) {
        if (id == null || id <= 0 || status == null) {
            log.warn("更新文章状态参数无效, 文章ID: {}, 状态: {}", id, status);
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "文章ID或状态无效");
        }
        return articleManagementService.updateArticleStatus(id, status);
    }

    @Override
    public PageResult<AdminArticleVO> getAdminArticlesByPage(AdminArticleQueryDTO queryDTO) {
        if (queryDTO == null) {
            log.warn("管理员分页获取文章列表参数为空");
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "查询参数不能为空");
        }
        return articleManagementService.getAdminArticlesByPage(queryDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveArticle(Long id) {
        if (id == null || id <= 0) {
            log.warn("审核通过文章参数无效, 文章ID: {}", id);
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "文章ID无效");
        }
        return articleManagementService.approveArticle(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectArticle(Long id, String reason) {
        if (id == null || id <= 0) {
            log.warn("审核拒绝文章参数无效, 文章ID: {}", id);
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "文章ID无效");
        }
        if (reason == null || reason.trim().isEmpty()) {
            log.warn("审核拒绝文章缺少拒绝原因, 文章ID: {}", id);
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "拒绝原因不能为空");
        }
        return articleManagementService.rejectArticle(id, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reprocessArticle(Long id) {
        if (id == null || id <= 0) {
            log.warn("重新审核文章参数无效, 文章ID: {}", id);
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "文章ID无效");
        }
        return articleManagementService.reprocessArticle(id);
    }

    @Override
    public AdminArticleDetailVO getAdminArticleDetail(Long id) {
        if (id == null || id <= 0) {
            log.warn("获取管理员文章详情参数无效, 文章ID: {}", id);
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "文章ID无效");
        }
        return articleManagementService.getAdminArticleDetail(id);
    }
}
