package com.inkstage.mapper;

import com.inkstage.entity.model.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章点赞Mapper接口
 */
@Mapper
public interface ArticleLikeMapper {

    /**
     * 插入点赞记录
     *
     * @param articleLike 点赞实体
     * @return 影响行数
     */
    int insert(ArticleLike articleLike);

    /**
     * 根据文章ID和用户ID删除点赞记录
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 根据文章ID和用户ID查询点赞记录数
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 记录数
     */
    int countByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

}