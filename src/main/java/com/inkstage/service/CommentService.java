package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.dto.front.CommentDTO;
import com.inkstage.dto.front.CommentQueryDTO;
import com.inkstage.vo.front.ArticleCommentVO;

/**
 * 评论Service接口
 */
public interface CommentService {

    /**
     * 获取评论列表
     *
     * @param queryDTO 评论查询参数
     * @return 评论列表
     */
    PageResult<ArticleCommentVO> getComments(CommentQueryDTO queryDTO);

    /**
     * 创建评论
     *
     * @param commentDTO 评论DTO
     * @return 评论ID
     */
    boolean createComment(CommentDTO commentDTO);

    /**
     * 更新评论
     *
     * @param commentDTO 评论DTO
     * @return 是否更新成功
     */
    boolean updateComment(CommentDTO commentDTO);

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @return 是否删除成功
     */
    boolean deleteComment(Long commentId);

}