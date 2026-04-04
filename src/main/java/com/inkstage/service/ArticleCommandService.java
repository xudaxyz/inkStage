package com.inkstage.service;

import com.inkstage.dto.front.ArticleCreateDTO;

/**
 * 文章命令服务接口
 * 负责文章的写操作（创建、更新、删除）
 * 专注于业务逻辑，缓存清理委托给 ArticleCacheService
 */
public interface ArticleCommandService {

    /**
     * 创建文章
     *
     * @param articleCreateDTO 文章创建DTO
     * @return 创建的文章ID
     */
    Long createArticle(ArticleCreateDTO articleCreateDTO);

    /**
     * 更新文章
     *
     * @param articleId 文章ID
     * @param articleCreateDTO 文章更新DTO
     * @return 是否更新成功
     */
    boolean updateArticle(Long articleId, ArticleCreateDTO articleCreateDTO);

    /**
     * 删除文章（逻辑删除）
     *
     * @param articleId 文章ID
     * @return 是否删除成功
     */
    boolean deleteArticle(Long articleId);

    /**
     * 彻底删除文章（物理删除）
     *
     * @param articleId 文章ID
     * @return 是否删除成功
     */
    boolean permanentDeleteArticle(Long articleId);

    /**
     * 保存草稿
     *
     * @param id 草稿ID（新建时为null）
     * @param dto 草稿DTO
     * @return 保存的草稿ID
     */
    Long saveDraft(Long id, ArticleCreateDTO dto);
}
