package com.inkstage.controller.front;

import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.dto.front.ArticleCreateDTO;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.service.ArticleLikeService;
import com.inkstage.service.ArticleService;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;
import com.inkstage.vo.front.MyArticleListVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台文章Controller
 */
@Slf4j
@RestController
@RequestMapping("/front/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;

    /**
     * 创建/发布文章
     *
     * @param articleCreateDTO 文章创建DTO
     * @return 响应结果
     */
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public Result<Long> createArticle(@Valid @RequestBody ArticleCreateDTO articleCreateDTO) {
        log.info("创建文章DTO: {}", articleCreateDTO);
        // 检查文章DTO参数
        checkArticleDTO(articleCreateDTO);

        Long articleId = articleService.createArticle(articleCreateDTO);
        if (articleId != null) {
            return Result.success(articleId, "文章创建成功");
        } else {
            return Result.error("文章创建失败");
        }
    }

    /**
     * 更新文章
     *
     * @param id               文章ID
     * @param articleCreateDTO 文章更新DTO
     * @return 响应结果
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> updateArticle(@PathVariable("id") Long id, @Valid @RequestBody ArticleCreateDTO articleCreateDTO) {
        log.info("更新文章DTO: {}, 文章ID: {}", articleCreateDTO, id);
        // 检查文章DTO参数
        checkArticleDTO(articleCreateDTO);
        boolean success = articleService.updateArticle(id, articleCreateDTO);
        if (success) {
            return Result.success("文章更新成功");
        } else {
            return Result.error("文章更新失败");
        }
    }

    /**
     * 保存草稿
     *
     * @param id               文章ID(可选)
     * @param articleCreateDTO 文章DTO
     * @return 响应结果
     */
    @PutMapping("/draft/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<Long> saveDraft(@PathVariable(required = false) Long id, @Valid @RequestBody ArticleCreateDTO articleCreateDTO) {
        // 检查文章DTO参数
        checkArticleDTO(articleCreateDTO);
        Long articleId = articleService.saveDraft(id, articleCreateDTO);
        return articleId != null ? Result.success(articleId, "草稿保存成功") : Result.error("草稿保存失败");
    }

    /**
     * 删除文章(将文章移至回收站)
     *
     * @param id 文章ID
     * @return 响应结果
     */
    @PostMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> deleteArticle(@PathVariable Long id) {
        boolean success = articleService.deleteArticle(id);
        return success ? Result.success(true, "文章已移至回收站") : Result.error("文章删除失败");
    }

    /**
     * 彻底删除文章
     *
     * @param id 文章ID
     * @return 响应结果
     */
    @PostMapping("/permanent-delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> permanentDeleteArticle(@PathVariable Long id) {
        boolean success = articleService.permanentDeleteArticle(id);
        return success ? Result.success(true, "文章删除成功") : Result.error("文章删除失败");
    }

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情VO
     */
    @GetMapping("/{id}")
    public Result<ArticleDetailVO> getArticleDetail(@PathVariable Long id) {
        log.info("获取文章详情，文章ID: {}", id);
        ArticleDetailVO articleDetail = articleService.getArticleDetail(id);
        if (articleDetail != null) {
            return Result.success(articleDetail);
        } else {
            return Result.error(ResponseMessage.ARTICLE_NOT_FOUND);
        }
    }

    /**
     * 检查文章DTO参数
     *
     * @param articleCreateDTO 文章创建DTO
     */
    private void checkArticleDTO(ArticleCreateDTO articleCreateDTO) {

        String title = articleCreateDTO.getTitle();
        String content = articleCreateDTO.getContent();
        List<Long> tagsId = articleCreateDTO.getTagIds();

        if (title == null) {
            throw new BusinessException(ResponseMessage.TITLE_IS_EMPTY);
        }

        if (content == null) {
            throw new BusinessException(ResponseMessage.CONTENT_IS_EMPTY);
        }

        if (tagsId != null && !tagsId.isEmpty() && tagsId.size() > 10) {
            throw new BusinessException(ResponseMessage.TAG_COUNT_EXCEEDED, "10");
        }
    }

    /**
     * 获取指定用户的文章列表
     *
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 文章列表分页结果
     */
    @GetMapping("/user/{userId}")
    public Result<PageResult<ArticleListVO>> getUserArticles(@PathVariable Long userId,
                                                             @RequestParam(defaultValue = "1") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("获取用户文章列表, 用户ID: {}, 页码: {}, 每页大小: {}", userId, page, size);
        PageResult<ArticleListVO> pageResult = articleService.getUserArticles(userId, page, size);
        return Result.success(pageResult, "获取用户文章列表成功");
    }

    /**
     * 获取作者相关文章（排除当前文章）
     *
     * @param userId           用户ID
     * @param excludeArticleId 排除的文章ID
     * @param limit            限制数量
     * @return 相关文章列表
     */
    @GetMapping("/author/related")
    public Result<List<ArticleListVO>> getAuthorRelatedArticles(@RequestParam Long userId,
                                                                @RequestParam Long excludeArticleId,
                                                                @RequestParam(defaultValue = "3") Integer limit) {
        log.info("获取作者相关文章, 用户ID: {}, 排除文章ID: {}, 限制数量: {}", userId, excludeArticleId, limit);
        List<ArticleListVO> relatedArticles = articleService.getAuthorRelatedArticles(userId, excludeArticleId, limit);
        return Result.success(relatedArticles, "获取作者相关文章成功");
    }

    /**
     * 点赞文章
     *
     * @param articleId 文章ID
     * @return 响应结果
     */
    @PostMapping("/like/{articleId}")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> likeArticle(@PathVariable Long articleId) {
        log.info("点赞文章, 文章ID: {}", articleId);
        boolean success = articleLikeService.likeArticle(articleId);
        return success ? Result.success(true, "点赞成功") : Result.error("点赞失败");
    }

    /**
     * 取消点赞
     *
     * @param articleId 文章ID
     * @return 响应结果
     */
    @DeleteMapping("/like/{articleId}")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> unlikeArticle(@PathVariable Long articleId) {
        log.info("取消点赞, 文章ID: {}", articleId);
        boolean success = articleLikeService.unlikeArticle(articleId);
        return success ? Result.success(true, "取消点赞成功") : Result.error("取消点赞失败");
    }

    /**
     * 检查文章是否已点赞
     *
     * @param articleId 文章ID
     * @return 响应结果
     */
    @GetMapping("/like/{articleId}/status")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> checkArticleLikeStatus(@PathVariable Long articleId) {
        log.info("检查文章点赞状态, 文章ID: {}", articleId);
        boolean isLiked = articleLikeService.isArticleLiked(articleId);
        return Result.success(isLiked, "获取点赞状态成功");
    }


    /**
     * 增加文章阅读数
     *
     * @param articleId 文章ID
     * @return 响应结果
     */
    @PostMapping("/read/{articleId}")
    public Result<?> incrementArticleReadCount(@PathVariable Long articleId) {
        log.info("增加文章阅读数, 文章ID: {}", articleId);
        articleService.incrementArticleReadCount(articleId, 1);
        return Result.success();
    }

    /**
     * 获取当前用户的文章列表
     *
     * @param articleStatus 我的文章状态
     * @param keyword       搜索关键词
     * @param page          页码
     * @param size          每页大小
     * @return 文章列表分页结果
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public Result<PageResult<MyArticleListVO>> getMyArticles(
            @RequestParam ArticleStatus articleStatus,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("获取当前用户文章列表, 状态: {}, 关键词: {}, 页码: {}, 每页大小: {}", articleStatus, keyword, page, size);
        PageResult<MyArticleListVO> pageResult = articleService.getMyArticles(articleStatus, keyword, page, size);
        return Result.success(pageResult);
    }

}

