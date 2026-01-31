package com.inkstage.controller.front;

import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.dto.front.ArticleCreateDTO;
import com.inkstage.exception.BusinessException;
import com.inkstage.service.ArticleService;
import com.inkstage.utils.UserContext;
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
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 创建/发布文章
     *
     * @param articleCreateDTO 文章创建DTO
     * @return 响应结果
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result<Long> createArticle(@Valid @RequestBody ArticleCreateDTO articleCreateDTO) {
        log.info("创建文章DTO: {}", articleCreateDTO);
        // 检查文章DTO参数
        checkArticleDTO(articleCreateDTO);

        String currentUserId = UserContext.getCurrentUserId();
        Long userId = Long.parseLong(currentUserId);
        // 暂时使用用户ID作为作者名称, 后续需要从用户服务获取
        Long articleId = articleService.createArticle(articleCreateDTO, userId);
        return Result.success(articleId, "文章创建成功");
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
        String currentUserId = UserContext.getCurrentUserId();
        Long userId = Long.parseLong(currentUserId);
        Long articleId = articleService.saveDraft(id, articleCreateDTO, userId);
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
        String currentUserId = UserContext.getCurrentUserId();
        Long userId = Long.parseLong(currentUserId);
        boolean success = articleService.deleteArticle(id, userId);
        return success ? Result.success(true, "文章删除成功") : Result.error("文章删除失败");
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

