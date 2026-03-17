package com.inkstage.controller.front;

import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.dto.front.ArticleCreateDTO;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.service.ArticleLikeService;
import com.inkstage.service.ArticleService;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;
import com.inkstage.vo.front.MyArticleListVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.inkstage.annotation.UserAccess;
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
    @UserAccess
    public Result<Long> createArticle(@Valid @RequestBody ArticleCreateDTO articleCreateDTO) {
        log.info("创建文章DTO: {}", articleCreateDTO);

        Long articleId = articleService.createArticle(articleCreateDTO);
        if (articleId != null) {
            return Result.success(articleId, ResponseMessage.SUCCESS);
        } else {
            return Result.error(ResponseMessage.ARTICLE_DELETE_FAILED);
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
    @UserAccess
    public Result<Boolean> updateArticle(@PathVariable("id") Long id, @Valid @RequestBody ArticleCreateDTO articleCreateDTO) {
        log.info("更新文章DTO: {}, 文章ID: {}", articleCreateDTO, id);
        boolean success = articleService.updateArticle(id, articleCreateDTO);
        if (success) {
            return Result.success(ResponseMessage.UPDATE_SUCCESS);
        } else {
            return Result.error(ResponseMessage.ARTICLE_UPDATE_FAILED);
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
    @UserAccess
    public Result<Long> saveDraft(@PathVariable(required = false) Long id, @Valid @RequestBody ArticleCreateDTO articleCreateDTO) {
        Long articleId = articleService.saveDraft(id, articleCreateDTO);
        return articleId != null ? Result.success(articleId, ResponseMessage.ARTICLE_DRAFT_SUCCESS) : Result.error(ResponseMessage.ARTICLE_DRAFT_FAILED);
    }

    /**
     * 删除文章(将文章移至回收站)
     *
     * @param id 文章ID
     * @return 响应结果
     */
    @DeleteMapping("/delete/{id}")
    @UserAccess
    public Result<Boolean> deleteArticle(@PathVariable Long id) {
        boolean success = articleService.deleteArticle(id);
        return success ? Result.success(true, ResponseMessage.ARTICLE_DELETE_SUCCESS) : Result.error(ResponseMessage.ERROR);
    }

    /**
     * 彻底删除文章
     *
     * @param id 文章ID
     * @return 响应结果
     */
    @DeleteMapping("/permanent-delete/{id}")
    @UserAccess
    public Result<Boolean> permanentDeleteArticle(@PathVariable Long id) {
        boolean success = articleService.permanentDeleteArticle(id);
        return success ? Result.success(true, ResponseMessage.SUCCESS) : Result.error(ResponseMessage.ERROR);
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
        return Result.success(pageResult, ResponseMessage.ARTICLE_LIST_SUCCESS);
    }

    /**
     * 获取作者相关文章（排除当前文章）
     *
     * @param userId           用户ID
     * @param excludeArticleId 排除的文章ID
     * @param limit            限制数量
     * @return 相关文章列表
     */
    @GetMapping("/user-related")
    public Result<List<ArticleListVO>> getUserRelatedArticles(@RequestParam Long userId,
                                                                @RequestParam Long excludeArticleId,
                                                                @RequestParam(defaultValue = "3") Integer limit) {
        log.info("获取作者相关文章, 用户ID: {}, 排除文章ID: {}, 限制数量: {}", userId, excludeArticleId, limit);
        List<ArticleListVO> relatedArticles = articleService.getUserRelatedArticles(userId, excludeArticleId, limit);
        return Result.success(relatedArticles, ResponseMessage.ARTICLE_LIST_SUCCESS);
    }

    /**
     * 点赞文章
     *
     * @param articleId 文章ID
     * @return 响应结果
     */
    @PostMapping("/like/{articleId}")
    @UserAccess
    public Result<Boolean> likeArticle(@PathVariable Long articleId) {
        log.info("点赞文章, 文章ID: {}", articleId);
        boolean success = articleLikeService.likeArticle(articleId);
        return success ? Result.success(true, ResponseMessage.ARTICLE_LIKE_SUCCESS) : Result.error(ResponseMessage.ARTICLE_LIKE_FAILED);
    }

    /**
     * 取消点赞
     *
     * @param articleId 文章ID
     * @return 响应结果
     */
    @DeleteMapping("/like/{articleId}")
    @UserAccess
    public Result<Boolean> unlikeArticle(@PathVariable Long articleId) {
        log.info("取消点赞, 文章ID: {}", articleId);
        boolean success = articleLikeService.unlikeArticle(articleId);
        return success ? Result.success(true, ResponseMessage.ARTICLE_UNLIKE_SUCCESS) : Result.error(ResponseMessage.ARTICLE_UNLIKE_FAILED);
    }

    /**
     * 检查文章是否已点赞
     *
     * @param articleId 文章ID
     * @return 响应结果
     */
    @GetMapping("/like/{articleId}/status")
    @UserAccess
    public Result<Boolean> checkArticleLikeStatus(@PathVariable Long articleId) {
        log.info("检查文章点赞状态, 文章ID: {}", articleId);
        boolean isLiked = articleLikeService.isArticleLiked(articleId);
        return Result.success(isLiked, ResponseMessage.SUCCESS);
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
     * @param pageNum       页码
     * @param pageSize      每页大小
     * @return 文章列表分页结果
     */
    @GetMapping("/my-articles")
    @UserAccess
    public Result<PageResult<MyArticleListVO>> getMyArticles(
            @RequestParam ArticleStatus articleStatus,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取当前用户文章列表, 状态: {}, 关键词: {}, 页码: {}, 每页大小: {}", articleStatus, keyword, pageNum, pageSize);
        com.inkstage.dto.front.MyArticleQueryDTO queryDTO = new com.inkstage.dto.front.MyArticleQueryDTO();
        queryDTO.setArticleStatus(articleStatus);
        queryDTO.setKeyword(keyword);
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        PageResult<MyArticleListVO> pageResult = articleService.getMyArticles(queryDTO);
        return Result.success(pageResult);
    }

}

