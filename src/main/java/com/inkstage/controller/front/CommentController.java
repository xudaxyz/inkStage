package com.inkstage.controller.front;

import com.inkstage.annotation.UserAccess;
import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.dto.front.CommentDTO;
import com.inkstage.dto.front.CommentQueryDTO;
import com.inkstage.service.CommentService;
import com.inkstage.vo.front.ArticleCommentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @UserAccess
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
    @UserAccess
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
    @UserAccess
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

    /**
     * 获取子评论列表
     *
     * @param parentId 父评论ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param sortBy 排序方式：hot（最热）、new（最新）
     * @return 子评论列表
     */
    @GetMapping("/replies")
    public Result<?> getReplies(@RequestParam Long parentId, 
                               @RequestParam(defaultValue = "1") Integer pageNum, 
                               @RequestParam(defaultValue = "10") Integer pageSize, 
                               @RequestParam(defaultValue = "hot") String sortBy) {
        // 调用服务方法获取子评论列表
        PageResult<ArticleCommentVO> result = commentService.getReplies(parentId, pageNum, pageSize, sortBy);
        if (result != null) {
            return Result.success(result);
        } else {
            return Result.success(ResponseMessage.NO_COMMENTS);
        }
    }

}