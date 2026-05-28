package com.inkstage.mapper;

import com.inkstage.entity.model.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
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
     * @param userId    用户ID
     * @return 点赞记录
     */
    ArticleLike findByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);


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
     * @param userId    用户ID
     * @return 影响行数
     */
    int purgeByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    // ==================== 统计（Count） ====================

    /**
     * 统计用户的点赞数
     *
     * @param userId 用户ID
     * @return 点赞数
     */
    long countByUserId(@Param("userId") Long userId);

    /**
     * 软删除用户所有点赞
     *
     * @param userId 用户ID
     */
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 恢复指定时间之后被软删除的用户点赞
     *
     * @param userId    用户ID
     * @param afterTime 时间节点，恢复此时间之后被删除的点赞
     */
    void restoreByUserIdAfterTime(@Param("userId") Long userId, @Param("afterTime") LocalDateTime afterTime);

    /**
     * 彻底删除用户所有点赞
     *
     * @param userId 用户ID
     */
    void purgeByUserId(@Param("userId") Long userId);

    /**
     * 查询指定用户被软删除的点赞记录对应的文章ID列表
     *
     * @param userId 用户ID
     * @return 被软删除的点赞记录对应的文章ID列表
     */
    List<Long> findDeletedArticleIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询指定用户在指定时间之后被软删除的点赞记录对应的文章ID列表
     *
     * @param userId    用户ID
     * @param afterTime 时间节点
     * @return 被软删除的点赞记录对应的文章ID列表
     */
    List<Long> findDeletedArticleIdsByUserIdAfterTime(@Param("userId") Long userId, @Param("afterTime") LocalDateTime afterTime);

}