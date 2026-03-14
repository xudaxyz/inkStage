package com.inkstage.controller.admin;

import com.inkstage.annotation.AdminAccess;
import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.dto.admin.AdminCommentQueryDTO;
import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.article.TopStatus;
import com.inkstage.service.CommentService;
import com.inkstage.vo.front.ArticleCommentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
     * 更新评论状态
     * @param id 评论ID
     * @param status 状态
     * @param reviewReason 审核原因
     * @return 操作结果
     */
    @PutMapping("/status/{id}")
    @AdminAccess
    public Result<?> updateCommentStatus(@PathVariable Long id, 
                                       @RequestParam ReviewStatus status, 
                                       @RequestParam(required = false) String reviewReason) {
        log.info("管理员更新评论状态, 评论ID: {}, 状态: {}, 审核原因: {}", id, status.getDesc(), reviewReason);
        boolean result = commentService.updateCommentStatus(id, status, reviewReason);
        return Result.success(result, ResponseMessage.COMMENT_STATUS_UPDATE_SUCCESS);
    }

    /**
     * 更新评论置顶状态
     * @param id 评论ID
     * @param top 置顶状态
     * @param topOrder 置顶顺序
     * @return 操作结果
     */
    @PutMapping("/top/{id}")
    @AdminAccess
    public Result<?> updateCommentTop(@PathVariable Long id, 
                                    @RequestParam TopStatus top, 
                                    @RequestParam Integer topOrder) {
        log.info("管理员更新评论置顶状态, 评论ID: {}, 置顶状态: {}, 置顶顺序: {}", id, top.getDesc(), topOrder);
        boolean result = commentService.updateCommentTop(id, top, topOrder);
        return Result.success(result, ResponseMessage.COMMENT_TOP_UPDATE_SUCCESS);
    }

}