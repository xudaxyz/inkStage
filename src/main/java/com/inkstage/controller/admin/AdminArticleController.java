package com.inkstage.controller.admin;

import com.inkstage.annotation.AdminAccess;
import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.dto.admin.AdminArticleQueryDTO;
import com.inkstage.dto.admin.AdminArticleUpdateDTO;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.service.AdminArticleService;
import com.inkstage.vo.admin.AdminArticleDetailVO;
import com.inkstage.vo.admin.AdminArticleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 后台文章Controller
 */
@Slf4j
@RestController
@RequestMapping("/admin/article")
@RequiredArgsConstructor
public class AdminArticleController {

    private final AdminArticleService adminArticleService;

    /**
     * 分页获取文章列表
     *
     * @param articleQueryDTO 文章查询参数
     * @return 分页结果
     */
    @PostMapping("/list")
    @AdminAccess
    public Result<?> listArticles(@RequestBody AdminArticleQueryDTO articleQueryDTO) {
        log.info("管理员分页获取文章列表, 查询参数: {}", articleQueryDTO);
        PageResult<AdminArticleVO> result = adminArticleService.getAdminArticlesByPage(articleQueryDTO);
        return Result.success(result, ResponseMessage.ARTICLE_LIST_SUCCESS);
    }

    /**
     * 根据ID获取文章详情
     *
     * @param id 文章ID
     * @return 文章信息
     */
    @GetMapping("/detail/{id}")
    @AdminAccess
    public Result<?> getArticleDetail(@PathVariable Long id) {
        log.info("管理员获取文章详情, 文章ID: {}", id);
        AdminArticleDetailVO articleDetail = adminArticleService.getAdminArticleDetail(id);
        return Result.success(articleDetail, ResponseMessage.ARTICLE_DETAIL_SUCCESS);
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @AdminAccess
    public Result<?> deleteArticle(@PathVariable Long id) {
        log.info("管理员删除文章, 文章ID: {}", id);
        boolean deleted = adminArticleService.deleteArticleByAdmin(id);
        if (deleted) {
            return Result.success(ResponseMessage.ARTICLE_DELETE_SUCCESS);
        } else {
            return Result.error(ResponseMessage.ARTICLE_DELETE_ERROR);
        }
    }

    /**
     * 审核通过文章
     *
     * @param id 文章ID
     * @return 审核结果
     */
    @PutMapping("/approve/{id}")
    @AdminAccess
    public Result<?> approveArticle(@PathVariable Long id) {
        log.info("管理员审核通过文章, 文章ID: {}", id);
        boolean approved = adminArticleService.approveArticle(id);
        if (approved) {
            return Result.success(true, "审核通过成功");
        } else {
            return Result.error(ResponseMessage.ERROR);
        }
    }

    /**
     * 审核拒绝文章
     *
     * @param id     文章ID
     * @param reason 拒绝原因
     * @return 审核结果
     */
    @PutMapping("/reject/{id}")
    @AdminAccess
    public Result<?> rejectArticle(@PathVariable Long id, @RequestBody String reason) {
        log.info("管理员审核拒绝文章, 文章ID: {}, 原因: {}", id, reason);
        boolean rejected = adminArticleService.rejectArticle(id, reason);
        if (rejected) {
            return Result.success(true, "审核拒绝成功");
        } else {
            return Result.error(ResponseMessage.ERROR);
        }
    }

    /**
     * 重新审核文章
     *
     * @param id 文章ID
     * @return 审核结果
     */
    @PutMapping("/reprocess/{id}")
    @AdminAccess
    public Result<?> reprocessArticle(@PathVariable Long id) {
        log.info("管理员重新审核文章, 文章ID: {}", id);
        boolean reprocessed = adminArticleService.reprocessArticle(id);
        if (reprocessed) {
            return Result.success(true, "重新审核成功");
        } else {
            return Result.error(ResponseMessage.ERROR);
        }
    }

    /**
     * 置顶文章
     *
     * @param id 文章ID
     * @return 置顶结果
     */
    @PutMapping("/top/{id}")
    @AdminAccess
    public Result<?> topArticle(@PathVariable Long id) {
        log.info("管理员置顶文章, 文章ID: {}", id);
        boolean topped = adminArticleService.topArticle(id);
        if (topped) {
            return Result.success(true, "置顶成功");
        } else {
            return Result.error(ResponseMessage.ERROR);
        }
    }

    /**
     * 取消置顶文章
     *
     * @param id 文章ID
     * @return 取消置顶结果
     */
    @PutMapping("/cancel-top/{id}")
    @AdminAccess
    public Result<?> cancelTopArticle(@PathVariable Long id) {
        log.info("管理员取消置顶文章, 文章ID: {}", id);
        boolean cancelled = adminArticleService.cancelTopArticle(id);
        if (cancelled) {
            return Result.success(true, "取消置顶成功");
        } else {
            return Result.error(ResponseMessage.ERROR);
        }
    }

    /**
     * 推荐文章
     *
     * @param id 文章ID
     * @return 推荐结果
     */
    @PutMapping("/recommend/{id}")
    @AdminAccess
    public Result<?> recommendArticle(@PathVariable Long id) {
        log.info("管理员推荐文章, 文章ID: {}", id);
        boolean recommended = adminArticleService.recommendArticle(id);
        if (recommended) {
            return Result.success(true, "推荐成功");
        } else {
            return Result.error(ResponseMessage.ERROR);
        }
    }

    /**
     * 取消推荐文章
     *
     * @param id 文章ID
     * @return 取消推荐结果
     */
    @PutMapping("/cancel-recommend/{id}")
    @AdminAccess
    public Result<?> cancelRecommendArticle(@PathVariable Long id) {
        log.info("管理员取消推荐文章, 文章ID: {}", id);
        boolean cancelled = adminArticleService.cancelRecommendArticle(id);
        if (cancelled) {
            return Result.success(true, "取消推荐成功");
        } else {
            return Result.error(ResponseMessage.ERROR);
        }
    }

    /**
     * 管理员更新文章
     *
     * @param id        文章ID
     * @param updateDTO 更新文章DTO
     * @return 更新结果
     */
    @PutMapping("/update/{id}")
    @AdminAccess
    public Result<?> updateArticle(@PathVariable Long id, @RequestBody AdminArticleUpdateDTO updateDTO) {
        log.info("管理员更新文章:{}, 更新内容: {}", id, updateDTO);
        boolean updated = adminArticleService.updateArticleByAdmin(id, updateDTO);
        if (updated) {
            return Result.success(true, "更新文章成功");
        } else {
            return Result.error(ResponseMessage.ERROR);
        }
    }

    /**
     * 管理员更新文章状态
     *
     * @param id            文章ID
     * @param articleStatus 文章状态
     * @return 更新结果
     */
    @PutMapping("/update/article-status/{id}")
    @AdminAccess
    public Result<?> updateArticleStatus(@PathVariable Long id, @RequestParam("articleStatus") ArticleStatus articleStatus) {
        log.info("管理员更新文章:{}, 更新状态: {}", id, articleStatus);
        boolean updated = adminArticleService.updateArticleStatus(id, articleStatus);
        if (updated) {
            return Result.success(true, "更新文章状态成功");
        } else {
            return Result.error(ResponseMessage.ERROR);
        }
    }

}