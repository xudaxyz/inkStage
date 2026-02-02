package com.inkstage.controller.front;

import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.dto.front.CommentDTO;
import com.inkstage.dto.front.CommentQueryDTO;
import com.inkstage.service.CommentService;
import com.inkstage.vo.front.ArticleCommentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 前台评论Controller
 */
@Slf4j
@RestController
@RequestMapping("/front/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 获取评论列表
     *
     * @param queryDTO 评论查询参数
     * @return 评论列表
     */
    @GetMapping("/list")
    public Result<?> getComments(CommentQueryDTO queryDTO) {
        // 调用服务方法获取评论列表
        PageResult<ArticleCommentVO> result = commentService.getComments(queryDTO);
        if (result != null) {
            return Result.success(result);
        } else {
            return Result.success(ResponseMessage.NO_COMMENTS);
        }
    }

    /**
     * 创建评论
     *
     * @param commentDTO 评论DTO
     * @return 评论ID
     */
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public Result<?> createComment(@RequestBody CommentDTO commentDTO) {
        log.info("创建评论: {}", commentDTO);
        if (commentDTO.getContent() == null || commentDTO.getContent().trim().isEmpty()) {
            return Result.error(ResponseMessage.COMMENT_CONTENT_EMPTY);
        }
        boolean result = commentService.createComment(commentDTO);
        if (result) {
            return Result.success();
        } else {
            return Result.error(ResponseMessage.COMMENT_CREATE_FAILED);
        }
    }

    /**
     * 更新评论
     *
     * @param commentDTO 评论DTO
     * @return 是否更新成功
     */
    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public Result<?> updateComment(@RequestBody CommentDTO commentDTO) {
        log.info("更新评论: {}", commentDTO);

        if (commentDTO.getContent() == null || commentDTO.getContent().trim().isEmpty()) {
            return Result.error(ResponseMessage.COMMENT_CONTENT_EMPTY);
        }
        // 调用服务方法更新评论
        boolean result = commentService.updateComment(commentDTO);
        if (result) {
            return Result.success();
        } else {
            return Result.error(ResponseMessage.COMMENT_UPDATE_FAILED);
        }
    }

    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 是否删除成功
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteComment(@PathVariable Long id) {
        log.info("删除用户: {}评论", id);
        // 调用服务方法删除评论
        boolean result = commentService.deleteComment(id);
        if (result) {
            return Result.success();
        } else {
            return Result.error(ResponseMessage.COMMENT_DELETE_FAILED);
        }
    }

}