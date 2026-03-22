package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.dto.admin.AdminCommentQueryDTO;
import com.inkstage.dto.front.CommentDTO;
import com.inkstage.dto.front.CommentQueryDTO;
import com.inkstage.entity.model.Comment;
import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.article.TopStatus;
import com.inkstage.vo.front.ArticleCommentVO;

/**
 * 评论服务接口
 * 提供评论的创建、查询、更新、删除等核心功能
 */
public interface CommentService {

    /**
     * 获取评论列表
     *
     * @param queryDTO 评论查询参数，包含文章ID、分页等信息
     * @return 评论列表，按树形结构组织
     */
    PageResult<ArticleCommentVO> getComments(CommentQueryDTO queryDTO);

    /**
     * 创建评论
     *
     * @param commentDTO 评论DTO，包含文章ID、内容、父评论ID等信息
     * @return 是否创建成功
     */
    boolean createComment(CommentDTO commentDTO);

    /**
     * 更新评论
     *
     * @param commentDTO 评论DTO，包含评论ID、更新后的内容等信息
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
     * @param pageRequest 分页请求参数，包含关键词、状态等过滤条件
     * @return 评论列表
     */
    PageResult<ArticleCommentVO> getCommentsByPage(AdminCommentQueryDTO pageRequest);

    /**
     * 管理员更新评论状态
     *
     * @param id           评论ID
     * @param status       状态
     * @param reviewReason 审核原因
     * @return 是否更新成功
     */
    boolean updateCommentStatus(Long id, ReviewStatus status, String reviewReason);

    /**
     * 管理员更新评论置顶状态
     *
     * @param id       评论ID
     * @param top      置顶状态
     * @param topOrder 置顶顺序
     * @return 是否更新成功
     */
    boolean updateCommentTop(Long id, TopStatus top, Integer topOrder);

    /**
     * 管理员更新评论信息
     *
     * @param comment 评论信息
     * @return 是否更新成功
     */
    boolean adminUpdateComment(Comment comment);

}