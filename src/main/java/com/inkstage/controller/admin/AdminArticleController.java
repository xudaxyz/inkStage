package com.inkstage.controller.admin;

import com.inkstage.common.Result;
import com.inkstage.dto.admin.AdminArticleQueryDTO;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.service.ArticleService;
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
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键词
     * @param categoryId 分类ID
     * @param articleStatus 文章状态
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<?> getArticlesByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) ArticleStatus articleStatus) {
        try {
            log.info("管理员分页获取文章列表, 页码: {}, 每页大小: {}, 关键词: {}, 分类ID: {}, 文章状态: {}", 
                    pageNum, pageSize, keyword, categoryId, articleStatus);

            AdminArticleQueryDTO articleQueryDTO = new AdminArticleQueryDTO();
            articleQueryDTO.setPageNum(pageNum);
            articleQueryDTO.setPageSize(pageSize);
            articleQueryDTO.setKeyword(keyword);
            articleQueryDTO.setCategoryId(categoryId);
            if (articleStatus != null) {
                articleQueryDTO.setArticleStatus(articleStatus);
            }

            var result = articleService.getAdminArticlesByPage(articleQueryDTO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("管理员分页获取文章列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取文章详情
     * @param id 文章ID
     * @return 文章信息
     */
    @GetMapping("/{id}")
    public Result<?> getArticleById(@PathVariable Long id) {
        try {
            log.info("管理员获取文章详情, 文章ID: {}", id);
            var article = articleService.getArticleById(id);
            return Result.success(article);
        } catch (Exception e) {
            log.error("管理员获取文章详情失败, 文章ID: {}", id, e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新文章状态
     * @param id 文章ID
     * @param articleStatus 文章状态
     * @return 更新后的文章
     */
    @PutMapping("/{id}/status")
    public Result<?> updateArticleStatus(@PathVariable Long id, @RequestParam ArticleStatus articleStatus) {
        try {
            log.info("管理员更新文章状态, 文章ID: {}, 状态: {}", id, articleStatus);
            if (articleStatus == null) {
                return Result.error("无效的文章状态");
            }
            var article = articleService.updateArticleStatus(id, articleStatus);
            return Result.success(article);
        } catch (Exception e) {
            log.error("管理员更新文章状态失败, 文章ID: {}, 状态: {}", id, articleStatus, e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除文章
     * @param id 文章ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteArticle(@PathVariable Long id) {
        try {
            log.info("管理员删除文章, 文章ID: {}", id);
            boolean deleted = articleService.deleteArticle(id);
            if (deleted) {
                return Result.success(true);
            } else {
                return Result.error("删除文章失败");
            }
        } catch (Exception e) {
            log.error("管理员删除文章失败, 文章ID: {}", id, e);
            return Result.error(e.getMessage());
        }
    }

}