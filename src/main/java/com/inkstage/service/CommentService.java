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

    /**
     * 管理员分页获取评论列表
     *
     * @param pageRequest 分页请求参数
     * @return 评论列表
     */
    PageResult<ArticleCommentVO> getCommentsByPage(com.inkstage.dto.admin.AdminCommentQueryDTO pageRequest);

    /**
     * 管理员更新评论状态
     *
     * @param id 评论ID
     * @param status 状态
     * @param reviewReason 审核原因
     * @return 是否更新成功
     */
    boolean updateCommentStatus(Long id, com.inkstage.enums.ReviewStatus status, String reviewReason);

    /**
     * 管理员更新评论置顶状态
     *
     * @param id 评论ID
     * @param top 置顶状态
     * @param topOrder 置顶顺序
     * @return 是否更新成功
     */
    boolean updateCommentTop(Long id, com.inkstage.enums.article.TopStatus top, Integer topOrder);

}