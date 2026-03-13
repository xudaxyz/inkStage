package com.inkstage.controller.admin;

import com.inkstage.common.Result;
import com.inkstage.dto.admin.AdminCommentQueryDTO;
import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.article.TopStatus;
import com.inkstage.service.CommentService;
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
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键词
     * @param articleId 文章ID
     * @param userId 用户ID
     * @param status 状态
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<?> getCommentsByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long articleId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) ReviewStatus status) {
        try {
            log.info("管理员分页获取评论列表, 页码: {}, 每页大小: {}, 关键词: {}, 文章ID: {}, 用户ID: {}, 状态: {}", 
                    pageNum, pageSize, keyword, articleId, userId, status);

            AdminCommentQueryDTO commentQueryDTO = new AdminCommentQueryDTO();
            commentQueryDTO.setPageNum(pageNum);
            commentQueryDTO.setPageSize(pageSize);
            commentQueryDTO.setKeyword(keyword);
            commentQueryDTO.setArticleId(articleId);
            commentQueryDTO.setUserId(userId);
            if (status != null) {
                commentQueryDTO.setStatus(ReviewStatus.All);
            }

            var result = commentService.getCommentsByPage(commentQueryDTO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("管理员分页获取评论列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新评论状态
     * @param id 评论ID
     * @param status 状态
     * @param reviewReason 审核原因
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public Result<?> updateCommentStatus(@PathVariable Long id, 
                                       @RequestParam ReviewStatus status, 
                                       @RequestParam(required = false) String reviewReason) {
        try {
            log.info("管理员更新评论状态, 评论ID: {}, 状态: {}, 审核原因: {}", id, status.getDesc(), reviewReason);
            boolean result = commentService.updateCommentStatus(id, status, reviewReason);
            return Result.success(result);
        } catch (Exception e) {
            log.error("管理员更新评论状态失败, 评论ID: {}", id, e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新评论置顶状态
     * @param id 评论ID
     * @param top 置顶状态
     * @param topOrder 置顶顺序
     * @return 操作结果
     */
    @PutMapping("/{id}/top")
    public Result<?> updateCommentTop(@PathVariable Long id, 
                                    @RequestParam TopStatus top, 
                                    @RequestParam Integer topOrder) {
        try {
            log.info("管理员更新评论置顶状态, 评论ID: {}, 置顶状态: {}, 置顶顺序: {}", id, top.getDesc(), topOrder);
            boolean result = commentService.updateCommentTop(id, top, topOrder);
            return Result.success(result);
        } catch (Exception e) {
            log.error("管理员更新评论置顶状态失败, 评论ID: {}", id, e);
            return Result.error(e.getMessage());
        }
    }

}