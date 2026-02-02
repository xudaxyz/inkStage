package com.inkstage.mapper;

import com.inkstage.dto.front.CommentQueryDTO;
import com.inkstage.entity.model.Comment;
import com.inkstage.vo.front.ArticleCommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论Mapper接口
 */
@Mapper
public interface CommentMapper {

    /**
     * 根据文章ID查询评论列表
     *
     * @param queryDTO 查询条件
     * @return 评论列表
     */
    List<ArticleCommentVO> selectCommentsByArticleId(@Param("query") CommentQueryDTO queryDTO);

    /**
     * 根据文章ID查询评论总数
     *
     * @param queryDTO 查询条件
     * @return 评论总数
     */
    Long countCommentsByArticleId(@Param("query") CommentQueryDTO queryDTO);

    /**
     * 插入评论
     *
     * @param comment 评论实体
     * @return 影响行数
     */
    int insert(Comment comment);

    /**
     * 根据ID更新评论
     *
     * @param comment 评论实体
     * @return 影响行数
     */
    int updateById(Comment comment);

    /**
     * 根据ID删除评论
     *
     * @param id 评论ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据ID查询评论
     *
     * @param id 评论ID
     * @return 评论实体
     */
    Comment selectByPrimaryKey(Long id);

    /**
     * 查询文章的最大楼层号
     *
     * @param articleId 文章ID
     * @return 最大楼层号
     */
    Integer selectMaxFloorByArticleId(Long articleId);

    /**
     * 更新评论的回复数
     *
     * @param commentId 评论ID
     * @param replyCount 回复数
     * @return 影响行数
     */
    int updateReplyCount(@Param("commentId") Long commentId, @Param("replyCount") Integer replyCount);

}