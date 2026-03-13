package com.inkstage.controller.admin;

import com.inkstage.common.PageResult;
import com.inkstage.common.Result;
import com.inkstage.entity.model.Tag;
import com.inkstage.enums.StatusEnum;
import com.inkstage.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员标签Controller
 */
@RestController
@RequestMapping("/admin/tag")
@RequiredArgsConstructor
public class AdminTagsController {

    private final TagService tagService;

    /**
     * 分页获取标签
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 响应结果
     */
    @GetMapping("/all")
    public Result<PageResult<Tag>> getAllTags(@RequestParam String keyword, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageResult<Tag> pageResult = tagService.getAdminTags(keyword, pageNum, pageSize);
        return Result.success(pageResult);
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
        return Result.success(tag);
    }

    /**
     * 添加标签
     *
     * @param tag 标签信息
     * @return 响应结果
     */
    @PostMapping
    public Result<Tag> addTag(@RequestBody Tag tag) {
        Tag addedTag = tagService.addTag(tag);
        return Result.success(addedTag);
    }

    /**
     * 更新标签
     *
     * @param id  标签ID
     * @param tag 标签信息
     * @return 响应结果
     */
    @PutMapping("/{id}")
    public Result<Tag> updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        tag.setId(id);
        Tag updatedTag = tagService.updateTag(tag);
        return Result.success(updatedTag);
    }

    /**
     * 删除标签
     *
     * @param id 标签ID
     * @return 响应结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success();
    }

    /**
     * 更新标签状态
     *
     * @param id     标签ID
     * @param status 状态
     * @return 响应结果
     */
    @PutMapping("/{id}/status")
    public Result<Tag> updateTagStatus(@PathVariable Long id, @RequestParam StatusEnum status) {
        Tag updatedTag = tagService.updateTagStatus(id, status);
        return Result.success(updatedTag);
    }

}
