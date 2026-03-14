package com.inkstage.mapper;

import com.inkstage.entity.model.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章点赞Mapper接口
 */
@Mapper
public interface ArticleLikeMapper {

    // ==================== 查询（Read） ====================
    
    /**
     * 根据文章ID和用户ID查询点赞记录
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 点赞记录
     */
    ArticleLike findByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 查询用户点赞的文章ID列表
     *
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 文章ID列表
     */
    List<Long> findArticleIdsByUserId(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 批量检查用户对文章的点赞状态
     *
     * @param userId 用户ID
     * @param articleIds 文章ID列表
     * @return 点赞记录列表
     */
    List<ArticleLike> findByUserIdAndArticleIds(@Param("userId") Long userId, @Param("articleIds") List<Long> articleIds);

    // ==================== 新增（Create） ====================
    
    /**
     * 插入点赞记录
     *
     * @param articleLike 点赞实体
     * @return 影响行数
     */
    int insert(ArticleLike articleLike);

    // ==================== 删除（Delete） ====================
    
    /**
     * 根据文章ID和用户ID删除点赞记录
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    // ==================== 统计（Count） ====================
    
    /**
     * 统计文章的点赞数
     *
     * @param articleId 文章ID
     * @return 点赞数
     */
    long countByArticleId(@Param("articleId") Long articleId);

    /**
     * 统计用户的点赞数
     *
     * @param userId 用户ID
     * @return 点赞数
     */
    long countByUserId(@Param("userId") Long userId);

}