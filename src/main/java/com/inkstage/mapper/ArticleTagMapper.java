package com.inkstage.mapper;

import com.inkstage.entity.model.ArticleTag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文章标签关联Mapper接口
 */
@Mapper
public interface ArticleTagMapper {

    /**
     * 插入文章与标签的关联
     *
     * @param articleTag 文章标签关联对象
     * @return 影响行数
     */
    int insert(ArticleTag articleTag);

    /**
     * 批量插入文章标签关联
     *
     * @param articleTags 文章标签关联列表
     * @return 影响行数
     */
    int batchInsert(List<ArticleTag> articleTags);

    /**
     * 删除文章的所有标签关联
     *
     * @param articleId 文章ID
     */
    void deleteByArticleId(Long articleId);

    /**
     * 根据文章ID查询标签关联列表
     *
     * @param articleId 文章ID
     * @return 文章标签关联列表
     */
    List<ArticleTag> findByArticleId(Long articleId);
}
