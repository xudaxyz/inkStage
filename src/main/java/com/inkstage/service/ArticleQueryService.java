package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.dto.front.ArticleQueryDTO;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;

import java.util.List;

/**
 * 文章查询服务接口
 */
public interface ArticleQueryService {

    /**
     * 获取文章列表
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    PageResult<ArticleListVO> getArticles(ArticleQueryDTO queryDTO);

    /**
     * 获取文章详情
     *
     * @param articleId 文章ID
     * @return 文章详情
     */
    ArticleDetailVO getArticleDetail(Long articleId);

    /**
     * 获取热门文章
     *
     * @param limit     限制数量
     * @param timeRange 时间范围：day, week, month
     * @return 热门文章列表
     */
    List<ArticleListVO> getHotArticles(Integer limit, String timeRange);

    /**
     * 获取最新文章
     *
     * @param limit 限制数量
     * @return 最新文章列表
     */
    List<ArticleListVO> getLatestArticles(Integer limit);

    /**
     * 获取轮播图文章
     *
     * @param limit 限制数量
     * @return 轮播图文章列表
     */
    List<ArticleListVO> getBannerArticles(Integer limit);

    /**
     * 获取指定用户的文章列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<ArticleListVO> getUserArticles(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取作者相关文章（排除当前文章）
     *
     * @param userId           用户ID
     * @param excludeArticleId 排除的文章ID
     * @param limit            限制数量
     * @return 相关文章列表
     */
    List<ArticleListVO> getUserRelatedArticles(Long userId, Long excludeArticleId, Integer limit);

    /**
     * 搜索文章
     *
     * @param keyword  搜索关键词
     * @param sortBy   排序方式：relevance, publishTime, readCount
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<ArticleListVO> searchArticles(String keyword, String sortBy, Integer pageNum, Integer pageSize);
}
