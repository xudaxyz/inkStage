package com.inkstage.controller.admin;

import com.inkstage.annotation.AdminAccess;
import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.dto.admin.AdminArticleQueryDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.service.ArticleService;
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

    private final ArticleService articleService;

    /**
     * 分页获取文章列表
     * @param articleQueryDTO 文章查询参数
     * @return 分页结果
     */
    @PostMapping("/list")
    @AdminAccess
    public Result<?> listArticles(@RequestBody AdminArticleQueryDTO articleQueryDTO) {
        log.info("管理员分页获取文章列表, 查询参数: {}", articleQueryDTO);
        PageResult<AdminArticleVO> result = articleService.getAdminArticlesByPage(articleQueryDTO);
        return Result.success(result, ResponseMessage.ARTICLE_LIST_SUCCESS);
    }

    /**
     * 根据ID获取文章详情
     * @param id 文章ID
     * @return 文章信息
     */
    @GetMapping("/detail/{id}")
    @AdminAccess
    public Result<?> getArticleDetail(@PathVariable Long id) {
        log.info("管理员获取文章详情, 文章ID: {}", id);
        AdminArticleDetailVO articleDetail = articleService.getAdminArticleDetail(id);
        return Result.success(articleDetail, ResponseMessage.ARTICLE_DETAIL_SUCCESS);
    }

    /**
     * 更新文章状态
     * @param id 文章ID
     * @param articleStatus 文章状态
     * @return 更新后的文章
     */
    @PutMapping("/status/{id}")
    @AdminAccess
    public Result<?> updateArticleStatus(@PathVariable Long id, @RequestBody ArticleStatus articleStatus) {
        log.info("管理员更新文章状态, 文章ID: {}, 状态: {}", id, articleStatus);
        if (articleStatus == null) {
            return Result.error(ResponseMessage.PARAM_ERROR);
        }
        Article article = articleService.updateArticleStatus(id, articleStatus);
        return Result.success(article, ResponseMessage.ARTICLE_STATUS_UPDATE_SUCCESS);
    }

    /**
     * 删除文章
     * @param id 文章ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @AdminAccess
    public Result<?> deleteArticle(@PathVariable Long id) {
        log.info("管理员删除文章, 文章ID: {}", id);
        boolean deleted = articleService.deleteArticle(id);
        if (deleted) {
            return Result.success(true, ResponseMessage.ARTICLE_DELETE_SUCCESS);
        } else {
            return Result.error(ResponseMessage.ERROR);
        }
    }

    /**
     * 审核通过文章
     * @param id 文章ID
     * @return 审核结果
     */
    @PutMapping("/approve/{id}")
    @AdminAccess
    public Result<?> approveArticle(@PathVariable Long id) {
        log.info("管理员审核通过文章, 文章ID: {}", id);
        boolean approved = articleService.approveArticle(id);
        if (approved) {
            return Result.success(true, "审核通过成功");
        } else {
            return Result.error(ResponseMessage.ERROR);
        }
    }

    /**
     * 审核拒绝文章
     * @param id 文章ID
     * @param reason 拒绝原因
     * @return 审核结果
     */
    @PutMapping("/reject/{id}")
    @AdminAccess
    public Result<?> rejectArticle(@PathVariable Long id, @RequestBody String reason) {
        log.info("管理员审核拒绝文章, 文章ID: {}, 原因: {}", id, reason);
        boolean rejected = articleService.rejectArticle(id, reason);
        if (rejected) {
            return Result.success(true, "审核拒绝成功");
        } else {
            return Result.error(ResponseMessage.ERROR);
        }
    }

    /**
     * 重新审核文章
     * @param id 文章ID
     * @return 审核结果
     */
    @PutMapping("/reprocess/{id}")
    @AdminAccess
    public Result<?> reprocessArticle(@PathVariable Long id) {
        log.info("管理员重新审核文章, 文章ID: {}", id);
        boolean reprocessed = articleService.reprocessArticle(id);
        if (reprocessed) {
            return Result.success(true, "重新审核成功");
        } else {
            return Result.error(ResponseMessage.ERROR);
        }
    }

}