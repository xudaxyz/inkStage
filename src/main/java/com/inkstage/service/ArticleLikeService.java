package com.inkstage.service;

/**
 * 文章点赞服务接口
 */
public interface ArticleLikeService {

    /**
     * 点赞文章
     *
     * @param articleId 文章ID
     * @return 是否点赞成功
     */
    boolean likeArticle(Long articleId);

    /**
     * 取消点赞
     *
     * @param articleId 文章ID
     * @return 是否取消成功
     */
    boolean unlikeArticle(Long articleId);

    /**
     * 检查用户是否已点赞文章
     *
     * @param articleId 文章ID
     * @return 是否已点赞
     */
    boolean isArticleLiked(Long articleId);
}