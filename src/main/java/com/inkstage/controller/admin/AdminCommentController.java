package com.inkstage.controller.admin;

import com.inkstage.annotation.AdminAccess;
import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.dto.admin.AdminCommentQueryDTO;
import com.inkstage.entity.model.Comment;
import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.article.TopStatus;
import com.inkstage.service.CommentService;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.ArticleCommentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 后台评论Controller
 */
@Slf4j
@RestController
@RequestMapping("/admin/comment")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    /**
     * 分页获取评论列表
     *
     * @param commentQueryDTO 评论查询参数
     * @return 分页结果
     */
    @PostMapping("/list")
    @AdminAccess
    public Result<?> listComments(@RequestBody AdminCommentQueryDTO commentQueryDTO) {
        log.info("管理员分页获取评论列表, 查询参数: {}", commentQueryDTO);
        PageResult<ArticleCommentVO> result = commentService.getCommentsByPage(commentQueryDTO);
        return Result.success(result, ResponseMessage.COMMENT_LIST_SUCCESS);
    }

    /**
     * 更新评论
     *
     * @param id           评论ID
     * @param content      评论内容
     * @param reviewStatus 审核状态
     * @param top          置顶状态
     * @param reviewReason 审核原因
     * @return 操作结果
     */
    @PutMapping("/update/{id}")
    @AdminAccess
    public Result<?> updateComment(@PathVariable Long id,
                                   @RequestParam String content,
                                   @RequestParam(required = false) ReviewStatus reviewStatus,
                                   @RequestParam(required = false) TopStatus top,
                                   @RequestParam(required = false) String reviewReason) {
        log.info("管理员更新评论相关信息, 评论ID: {}, 评论内容: {}, 审核状态: {}, 置顶状态: {}, 审核原因: {}", id, content, reviewStatus, top, reviewReason);
        Comment comment = new Comment();
        comment.setId(id);
        comment.setContent(content);
        comment.setTop(top);
        comment.setReviewReason(reviewReason);
        comment.setReviewUserId(UserContext.getCurrentUserId());
        if (reviewStatus != null) {
            comment.setStatus(reviewStatus);
            comment.setReviewTime(LocalDateTime.now());
        }
        boolean result = commentService.adminUpdateComment(comment);
        return Result.success(result, ResponseMessage.COMMENT_UPDATE_SUCCESS);
    }

    /**
     * 更新评论审核状态
     *
     * @param id           评论ID
     * @param reviewStatus 审核状态
     * @param reviewReason 审核原因
     * @return 操作结果
     */
    @PutMapping("/update-status/{id}")
    @AdminAccess
    public Result<?> updateCommentStatus(@PathVariable Long id,
                                         @RequestParam ReviewStatus reviewStatus,
                                         @RequestParam(required = false) String reviewReason) {
        log.info("管理员更新评论状态, 评论ID: {}, 审核状态: {}, 审核原因: {}", id, reviewStatus.getDesc(), reviewReason);
        boolean result = commentService.updateCommentStatus(id, reviewStatus, reviewReason);
        return Result.success(result, ResponseMessage.COMMENT_REVIEW_STATUS_UPDATE_SUCCESS);
    }

    /**
     * 更新评论置顶状态
     *
     * @param id  评论ID
     * @param top 置顶状态
     * @return 操作结果
     */
    @PutMapping("/update-top/{id}")
    @AdminAccess
    public Result<?> updateCommentTop(@PathVariable Long id,
                                      @RequestParam TopStatus top) {
        log.info("管理员更新评论置顶状态, 评论ID: {}, 置顶状态: {}", id, top.getDesc());
        boolean result = commentService.updateCommentTop(id, top, null);
        return Result.success(result, ResponseMessage.COMMENT_TOP_UPDATE_SUCCESS);
    }

    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    @AdminAccess
    public Result<?> deleteComment(@PathVariable Long id) {
        log.info("管理员删除评论, 评论ID: {}", id);
        boolean result = commentService.deleteComment(id);
        return Result.success(result, ResponseMessage.COMMENT_DELETE_SUCCESS);
    }

}