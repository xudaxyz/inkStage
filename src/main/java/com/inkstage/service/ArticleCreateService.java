package com.inkstage.service;

import com.inkstage.dto.front.ArticleCreateDTO;

/**
 * 文章创建服务接口
 */
public interface ArticleCreateService {

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
     * 更新文章
     *
     * @param articleId        文章ID
     * @param articleCreateDTO 文章更新DTO
     * @return 是否成功
     */
    boolean updateArticle(Long articleId, ArticleCreateDTO articleCreateDTO);
}
