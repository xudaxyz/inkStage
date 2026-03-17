package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.dto.admin.AdminArticleQueryDTO;
import com.inkstage.dto.front.ArticleCreateDTO;
import com.inkstage.dto.front.ArticleQueryDTO;
import com.inkstage.dto.front.MyArticleQueryDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.vo.admin.AdminArticleVO;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;
import com.inkstage.vo.front.MyArticleListVO;

import java.util.List;

/**
 * 文章服务接口
 * 提供文章的创建、查询、更新、删除等核心功能
 */
public interface ArticleService {

    /**
     * 创建文章
     *
     * @param articleCreateDTO 文章创建DTO，包含文章标题、内容、分类等信息
     * @return 文章ID
     */
    Long createArticle(ArticleCreateDTO articleCreateDTO);

    /**
     * 保存草稿
     *
     * @param articleId        文章ID(如果为null则创建新草稿)
     * @param articleCreateDTO 文章DTO，包含草稿内容
     * @return 文章ID
     */
    Long saveDraft(Long articleId, ArticleCreateDTO articleCreateDTO);

    /**
     * 删除文章（软删除）
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean deleteArticle(Long id);

    /**
     * 获取文章列表
     *
     * @param queryDTO 查询参数，包含分类、标签、排序等条件
     * @return 分页结果
     */
    PageResult<ArticleListVO> getArticles(ArticleQueryDTO queryDTO);

    /**
     * 获取文章详情
     *
     * @param articleId 文章ID
     * @return 文章详情，包含完整的文章内容和相关信息
     */
    ArticleDetailVO getArticleDetail(Long articleId);

    /**
     * 更新文章
     *
     * @param articleId        文章ID
     * @param articleCreateDTO 文章更新DTO，包含更新后的文章信息
     * @return 是否成功
     */
    boolean updateArticle(Long articleId, ArticleCreateDTO articleCreateDTO);

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
     * 获取当前用户的文章列表，支持按状态过滤和搜索
     *
     * @param queryDTO 我的文章查询DTO，包含状态、关键词、分页等参数
     * @return 分页结果
     */
    PageResult<MyArticleListVO> getMyArticles(MyArticleQueryDTO queryDTO);

    /**
     * 增加文章阅读数
     *
     * @param articleId 文章ID
     * @param count     阅读数增量
     */
    void incrementArticleReadCount(Long articleId, int count);

    /**
     * 彻底删除文章
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean permanentDeleteArticle(Long id);

    /**
     * 搜索文章
     *
     * @param keyword   搜索关键词
     * @param sortBy    排序方式：relevance, publishTime, readCount
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @return 分页结果
     */
    PageResult<ArticleListVO> searchArticles(String keyword, String sortBy, Integer pageNum, Integer pageSize);

    /**
     * 分页获取所有文章（管理员）
     *
     * @param queryDTO 文章查询DTO，包含状态、关键词、分页等参数
     * @return 分页结果
     */
    PageResult<AdminArticleVO> getAdminArticlesByPage(AdminArticleQueryDTO queryDTO);

    /**
     * 根据ID获取文章（管理员）
     *
     * @param id 文章ID
     * @return 文章信息
     */
    Article getArticleById(Long id);

    /**
     * 更新文章状态（管理员）
     *
     * @param id     文章ID
     * @param status 文章状态
     * @return 更新后的文章
     */
    Article updateArticleStatus(Long id, ArticleStatus status);

}
