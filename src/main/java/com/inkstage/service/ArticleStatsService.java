package com.inkstage.service;

/**
 * 文章统计服务接口
 */
public interface ArticleStatsService {

    /**
     * 增加文章阅读数
     *
     * @param articleId 文章ID
     * @param count     阅读数增量
     */
    void incrementArticleReadCount(Long articleId, int count);
}
