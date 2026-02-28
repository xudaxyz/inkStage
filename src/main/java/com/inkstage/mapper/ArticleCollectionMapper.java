package com.inkstage.mapper;

import com.inkstage.entity.model.ArticleCollection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章收藏Mapper接口
 */
@Mapper
public interface ArticleCollectionMapper {

    /**
     * 插入收藏记录
     *
     * @param articleCollection 收藏实体
     * @return 影响行数
     */
    int insert(ArticleCollection articleCollection);

    /**
     * 根据文章ID和用户ID删除收藏记录
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 根据文章ID和用户ID查询收藏记录数
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 记录数
     */
    int countByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

}