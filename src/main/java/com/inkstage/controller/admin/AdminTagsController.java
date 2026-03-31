package com.inkstage.controller.admin;

import com.inkstage.annotation.AdminAccess;
import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.entity.model.Tag;
import com.inkstage.enums.common.StatusEnum;
import com.inkstage.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员标签Controller
 */
@Slf4j
@RestController
@RequestMapping("/admin/tag")
@RequiredArgsConstructor
public class AdminTagsController {

    private final TagService tagService;

    /**
     * 分页获取标签列表
     *
     * @param keyword 关键字
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 响应结果
     */
    @GetMapping("/list")
    @AdminAccess
    public Result<PageResult<Tag>> listTags(@RequestParam(required = false) String keyword, 
                                           @RequestParam(defaultValue = "1") Integer pageNum, 
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("管理员获取标签列表, 关键字: {}, 页码: {}, 页大小: {}", keyword, pageNum, pageSize);
        PageResult<Tag> pageResult = tagService.getAdminTags(keyword, pageNum, pageSize);
        return Result.success(pageResult, ResponseMessage.TAG_LIST_SUCCESS);
    }

    /**
     * 根据ID获取标签详情
     *
     * @param id 标签ID
     * @return 响应结果
     */
    @GetMapping("/detail/{id}")
    @AdminAccess
    public Result<Tag> getTagDetail(@PathVariable Long id) {
        log.info("管理员获取标签详情, 标签ID: {}", id);
        Tag tag = tagService.getTagById(id);
        return Result.success(tag, ResponseMessage.TAG_DETAIL_SUCCESS);
    }

    /**
     * 添加标签
     *
     * @param tag 标签信息
     * @return 响应结果
     */
    @PostMapping("/add")
    @AdminAccess
    public Result<Tag> addTag(@RequestBody Tag tag) {
        log.info("管理员添加标签, 标签信息: {}", tag);
        Tag addedTag = tagService.addTag(tag);
        return Result.success(addedTag, ResponseMessage.TAG_CREATE_SUCCESS);
    }

    /**
     * 更新标签
     *
     * @param id  标签ID
     * @param tag 标签信息
     * @return 响应结果
     */
    @PutMapping("/update/{id}")
    @AdminAccess
    public Result<Tag> updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        log.info("管理员更新标签, 标签ID: {}, 标签信息: {}", id, tag);
        tag.setId(id);
        Tag updatedTag = tagService.updateTag(tag);
        return Result.success(updatedTag, ResponseMessage.TAG_UPDATE_SUCCESS);
    }

    /**
     * 删除标签
     *
     * @param id 标签ID
     * @return 响应结果
     */
    @DeleteMapping("/delete/{id}")
    @AdminAccess
    public Result<Void> deleteTag(@PathVariable Long id) {
        log.info("管理员删除标签, 标签ID: {}", id);
        tagService.deleteTag(id);
        return Result.success(ResponseMessage.TAG_DELETE_SUCCESS);
    }

    /**
     * 更新标签状态
     *
     * @param id     标签ID
     * @param status 状态
     * @return 响应结果
     */
    @PutMapping("/status/{id}")
    @AdminAccess
    public Result<Tag> updateTagStatus(@PathVariable Long id, @RequestParam StatusEnum status) {
        log.info("管理员更新标签状态, 标签ID: {}, 状态: {}", id, status);
        Tag updatedTag = tagService.updateTagStatus(id, status);
        return Result.success(updatedTag, ResponseMessage.TAG_STATUS_UPDATE_SUCCESS);
    }

}
