package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.dto.front.ArticleCreateDTO;
import com.inkstage.dto.front.ArticleQueryDTO;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;
import com.inkstage.vo.front.MyArticleListVO;

import java.util.List;

/**
 * 文章服务接口
 */
public interface ArticleService {

    /**
     * 创建文章
     *
     * @param articleCreateDTO 文章创建DTO
     * @return 文章ID
     */
    Long createArticle(ArticleCreateDTO articleCreateDTO);


    /**
     * 保存草稿
     *
     * @param articleId        文章ID(如果为null则创建新草稿)
     * @param articleCreateDTO 文章DTO
     * @return 文章ID
     */
    Long saveDraft(Long articleId, ArticleCreateDTO articleCreateDTO);

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean deleteArticle(Long id);

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
     * 更新文章
     *
     * @param articleId        文章ID
     * @param articleCreateDTO 文章更新DTO
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
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页数量
     * @return 分页结果
     */
    PageResult<ArticleListVO> getUserArticles(Long userId, Integer page, Integer size);

    /**
     * 获取作者相关文章（排除当前文章）
     *
     * @param userId           用户ID
     * @param excludeArticleId 排除的文章ID
     * @param limit            限制数量
     * @return 相关文章列表
     */
    List<ArticleListVO> getAuthorRelatedArticles(Long userId, Long excludeArticleId, Integer limit);

    /**
     * 获取当前用户的文章列表，支持按状态过滤和搜索
     *
     * @param articleStatus    文章状态
     * @param keyword   搜索关键词
     * @param page      页码
     * @param size      每页数量
     * @return 分页结果
     */
    PageResult<MyArticleListVO> getMyArticles(ArticleStatus articleStatus, String keyword, Integer page, Integer size);

    /**
     * 增加文章阅读数
     *
     * @param articleId 文章ID
     * @param count     阅读数增量
     */
    void incrementArticleReadCount(Long articleId, int count);

    /**
     * 彻底删除文章
     * @param id 文章ID
     * @return 是否成功
     */
    boolean permanentDeleteArticle(Long id);
}
