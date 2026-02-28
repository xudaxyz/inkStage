package com.inkstage.service;

/**
 * 文章收藏服务接口
 */
public interface ArticleCollectionService {

    /**
     * 收藏文章
     *
     * @param articleId 文章ID
     * @param folderId  文件夹ID
     * @return 是否收藏成功
     */
    boolean collectArticle(Long articleId, Long folderId);

    /**
     * 取消收藏
     *
     * @param articleId 文章ID
     * @return 是否取消成功
     */
    boolean unCollectArticle(Long articleId);

    /**
     * 检查用户是否已收藏文章
     *
     * @param articleId 文章ID
     * @return 是否已收藏
     */
    boolean isArticleCollected(Long articleId);

}