package com.inkstage.controller.front;

import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.dto.front.ArticleCreateDTO;
import com.inkstage.exception.BusinessException;
import com.inkstage.service.ArticleService;
import com.inkstage.vo.front.ArticleDetailVO;
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
     * @param id 文章ID
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
    @PutMapping("/{id}/draft")
    @PreAuthorize("isAuthenticated()")
    public Result<Long> saveDraft(@PathVariable(required = false) Long id, @Valid @RequestBody ArticleCreateDTO articleCreateDTO) {
        // 检查文章DTO参数
        checkArticleDTO(articleCreateDTO);
        Long articleId = articleService.saveDraft(id, articleCreateDTO);
        return articleId != null ? Result.success(articleId, "草稿保存成功") : Result.error("草稿保存失败");
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 响应结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> deleteArticle(@PathVariable Long id) {
        boolean success = articleService.deleteArticle(id);
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
        return Result.success(articleDetail, "文章详情获取成功");
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
}

