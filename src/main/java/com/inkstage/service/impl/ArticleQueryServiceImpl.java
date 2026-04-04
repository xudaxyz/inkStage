package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.dto.front.ArticleQueryDTO;
import com.inkstage.entity.model.User;
import com.inkstage.exception.BusinessException;
import com.inkstage.service.*;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 文章查询服务实现类
 * 专注于业务逻辑，缓存操作委托给ArticleCacheService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleQueryServiceImpl implements ArticleQueryService {

    private final ArticleCacheService articleCacheService;
    private final ArticleLikeService articleLikeService;
    private final ArticleCollectionService articleCollectionService;
    private final CountService countService;

    @Override
    public PageResult<ArticleListVO> getArticles(ArticleQueryDTO queryDTO) {
        try {
            return articleCacheService.getArticles(
                    queryDTO.getPageNum(),
                    queryDTO.getPageSize(),
                    queryDTO.getCategoryId(),
                    queryDTO.getTagId()
            );
        } catch (Exception e) {
            log.error("获取文章列表失败, 查询参数: {}", queryDTO, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public ArticleDetailVO getArticleDetail(Long id) {
        // 从缓存服务获取文章详情
        ArticleDetailVO articleDetailVO = articleCacheService.getArticleDetail(id);

        // 获取当前用户的点赞和收藏状态（不缓存，实时查询）
        Optional<User> currentUser = UserContext.getCurrentUserOptional();
        if (currentUser.isPresent()) {
            articleDetailVO.setIsLiked(articleLikeService.isArticleLiked(id));
            articleDetailVO.setIsCollected(articleCollectionService.isArticleCollected(id));
        } else {
            articleDetailVO.setIsLiked(false);
            articleDetailVO.setIsCollected(false);
        }

        // 更新点赞数和收藏数
        articleDetailVO.setLikeCount(Math.toIntExact(countService.getArticleLikeCount(id)));
        articleDetailVO.setCollectionCount(Math.toIntExact(countService.getArticleCollectionCount(id)));

        return articleDetailVO;
    }

    @Override
    public List<ArticleListVO> getHotArticles(Integer limit, String timeRange) {
        try {
            return articleCacheService.getHotArticles(limit, timeRange);
        } catch (Exception e) {
            log.error("获取热门文章失败, limit: {}, timeRange: {}", limit, timeRange, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public List<ArticleListVO> getLatestArticles(Integer limit) {
        try {
            return articleCacheService.getLatestArticles(limit);
        } catch (Exception e) {
            log.error("获取最新文章失败, limit: {}", limit, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public List<ArticleListVO> getBannerArticles(Integer limit) {
        try {
            return articleCacheService.getBannerArticles(limit);
        } catch (Exception e) {
            log.error("获取轮播图文章失败, limit: {}", limit, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public PageResult<ArticleListVO> getUserArticles(Long userId, Integer pageNum, Integer pageSize) {
        try {
            return articleCacheService.getUserArticles(userId, pageNum, pageSize);
        } catch (Exception e) {
            log.error("获取用户文章列表失败, 用户ID: {}, 页码: {}, 每页大小: {}", userId, pageNum, pageSize, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public List<ArticleListVO> getUserRelatedArticles(Long userId, Long excludeArticleId, Integer limit) {
        try {
            return articleCacheService.getUserRelatedArticles(userId, excludeArticleId, limit);
        } catch (Exception e) {
            log.error("获取作者相关文章失败, 用户ID: {}, 排除文章ID: {}, 限制数量: {}",
                    userId, excludeArticleId, limit, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public PageResult<ArticleListVO> searchArticles(String keyword, String sortBy, Integer pageNum, Integer pageSize) {
        try {
            return articleCacheService.searchArticles(keyword, sortBy, pageNum, pageSize);
        } catch (Exception e) {
            log.error("搜索文章失败, 关键词: {}, 排序方式: {}", keyword, sortBy, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }
}
