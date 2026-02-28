package com.inkstage.service;

import com.inkstage.enums.CountType;

/**
 * 文章计数服务接口
 */
public interface CountService {

    /**
     * 更新文章阅读数
     *
     * @param articleId 文章ID
     * @param count     增加/减少的数量
     */
    void updateArticleReadCount(Long articleId, int count);

    /**
     * 增加/减少文章点赞数
     *
     * @param articleId 文章ID
     * @param count     增加/减少的数量
     */
    void updateArticleLikeCount(Long articleId, int count);

    /**
     * 增加/减少文章收藏数
     *
     * @param articleId 文章ID
     * @param count     增加/减少的数量
     */
    void updateArticleCollectionCount(Long articleId, int count);

    /**
     * 增加/减少文章评论数
     *
     * @param articleId 文章ID
     * @param count     增加/减少的数量
     */
    void updateArticleCommentCount(Long articleId, int count);

    /**
     * 增加/减少文章分享数
     *
     * @param articleId 文章ID
     * @param count     增加/减少的数量
     */
    void updateArticleShareCount(Long articleId, int count);

    /**
     * 获取文章阅读数
     *
     * @param articleId 文章ID
     * @return 阅读数
     */
    Long getArticleReadCount(Long articleId);

    /**
     * 获取文章点赞数
     *
     * @param articleId 文章ID
     * @return 点赞数
     */
    Long getArticleLikeCount(Long articleId);

    /**
     * 获取文章评论数
     *
     * @param articleId 文章ID
     * @return 评论数
     */
    Long getArticleCommentCount(Long articleId);

    /**
     * 获取文章收藏数
     *
     * @param articleId 文章ID
     * @return 收藏数
     */
    Long getArticleCollectionCount(Long articleId);

    /**
     * 获取文章分享数
     *
     * @param articleId 文章ID
     * @return 分享数
     */
    Long getArticleShareCount(Long articleId);

    /**
     * 异步缓存计数到数据库
     *
     * @param articleId 文章ID
     * @param countType 计数类型: read, like, comment, collection, share
     */
    void syncArticleCount(Long articleId, CountType countType, int count);
}
