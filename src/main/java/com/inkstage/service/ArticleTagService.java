package com.inkstage.service;

import com.inkstage.entity.model.Tag;

import java.util.List;

/**
 * 文章标签关联服务接口
 */
public interface ArticleTagService {

    /**
     * 根据文章ID获取关联的标签
     *
     * @param articleId 文章ID
     * @return 标签列表
     */
    List<Tag> getTagsByArticleId(Long articleId);

    /**
     * 保存文章与标签的关联(通过标签ID)
     *
     * @param articleId 文章ID
     * @param tagIds    标签ID列表
     */
    void saveArticleTags(Long articleId, List<Long> tagIds);

    /**
     * 删除文章的所有标签关联
     *
     * @param articleId 文章ID
     */
    void deleteArticleTagsByArticleId(Long articleId);

    /**
     * 处理文章标签关联（包括标签创建）
     * @param articleId 文章ID
     * @param tags 标签列表
     */
    void handleArticleTags(Long articleId, List<Tag> tags);
}
