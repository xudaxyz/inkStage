package com.inkstage.controller.front;

import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.entity.model.Tag;
import com.inkstage.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台标签Controller
 */
@RestController
@RequestMapping("/front/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * 获取所有标签
     *
     * @return 响应结果
     */
    @GetMapping
    public Result<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        if (tags != null && !tags.isEmpty()) {
            return Result.success(tags);
        } else {
            return Result.error(ResponseMessage.TAGS_ARE_EMPTY);
        }
    }

    /**
     * 获取激活状态的标签
     *
     * @return 响应结果
     */
    @GetMapping("/active")
    public Result<List<Tag>> getActiveTags() {
        List<Tag> tags = tagService.getActiveTags();
        if (tags != null && !tags.isEmpty()) {
            return Result.success(tags);
        } else {
            return Result.error(ResponseMessage.TAGS_ARE_EMPTY);
        }
    }

    /**
     * 根据ID获取标签
     *
     * @param id 标签ID
     * @return 响应结果
     */
    @GetMapping("/{id}")
    public Result<Tag> getTagById(@PathVariable Long id) {
        Tag tag = tagService.getTagById(id);
        if (tag != null) {
            return Result.success(tag);
        } else {
            return Result.error(ResponseMessage.TAG_NOT_FOUND);
        }
    }

    /**
     * 根据文章ID获取标签
     *
     * @param articleId 文章ID
     * @return 响应结果
     */
    @GetMapping("/article/{articleId}")
    public Result<List<Tag>> getTagsByArticleId(@PathVariable Long articleId) {
        List<Tag> tags = tagService.getTagsByArticleId(articleId);
        return Result.success(tags);
    }

}
