package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.dto.front.ArticleCreateDTO;
import com.inkstage.dto.front.ArticleQueryDTO;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;

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

}
