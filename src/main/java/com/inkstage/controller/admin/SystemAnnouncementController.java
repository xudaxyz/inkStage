package com.inkstage.controller.admin;

import com.inkstage.common.PageResult;
import com.inkstage.common.Result;
import com.inkstage.dto.admin.SystemAnnouncementDTO;
import com.inkstage.dto.admin.SystemAnnouncementQueryDTO;
import com.inkstage.entity.model.SystemAnnouncement;
import com.inkstage.service.SystemAnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台系统公告管理Controller
 */
@RestController
@RequestMapping("/admin/system-announcements")
@RequiredArgsConstructor

public class SystemAnnouncementController {

    private final SystemAnnouncementService announcementService;

    /**
     * 创建公告
     */
    @PostMapping("/create")
    public Result<Long> createAnnouncement(@Valid @RequestBody SystemAnnouncementDTO dto) {
        SystemAnnouncement announcement = new SystemAnnouncement();
        BeanUtils.copyProperties(dto, announcement);
        boolean success = announcementService.createAnnouncement(announcement);
        return success ? Result.success(announcement.getId()) : Result.error("创建失败");
    }

    /**
     * 更新公告
     */
    @PutMapping("/update/{id}")
    public Result<Void> updateAnnouncement(@PathVariable Long id,
                                          @Valid @RequestBody SystemAnnouncementDTO dto) {
        SystemAnnouncement announcement = new SystemAnnouncement();
        //TODO
        boolean success = announcementService.updateAnnouncement(announcement);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        boolean success = announcementService.deleteAnnouncement(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/get/{id}")
    public Result<SystemAnnouncement> getAnnouncement(@PathVariable Long id) {
        SystemAnnouncement announcement = announcementService.getAnnouncementById(id);
        return announcement != null ? Result.success(announcement) : Result.error("公告不存在");
    }

    /**
     * 分页查询公告列表
     */
    @GetMapping("/list")
    public Result<PageResult<SystemAnnouncement>> listAnnouncements(SystemAnnouncementQueryDTO queryDTO) {
        PageResult<SystemAnnouncement> result = announcementService.getAnnouncementPage(
                queryDTO.getPageNum(),
                queryDTO.getPageSize(),
                queryDTO.getType(),
                queryDTO.getStatus(),
                queryDTO.getKeyword()
        );
        return Result.success(result);
    }

    /**
     * 获取所有公告
     */
    @GetMapping("/all")
    public Result<List<SystemAnnouncement>> getAllAnnouncements() {
        List<SystemAnnouncement> list = announcementService.getAllAnnouncements();
        return Result.success(list);
    }

    /**
     * 获取已发布的公告
     */
    @GetMapping("/published")
    public Result<List<SystemAnnouncement>> getPublishedAnnouncements() {
        List<SystemAnnouncement> list = announcementService.getPublishedAnnouncements();
        return Result.success(list);
    }

    /**
     * 发布公告
     */
    @PutMapping("/publish/{id}")
    public Result<Void> publishAnnouncement(@PathVariable Long id) {
        boolean success = announcementService.publishAnnouncement(id);
        return success ? Result.success() : Result.error("发布失败");
    }

    /**
     * 过期公告
     */
    @PutMapping("/expire/{id}")
    public Result<Void> expireAnnouncement(@PathVariable Long id) {
        boolean success = announcementService.expireAnnouncement(id);
        return success ? Result.success() : Result.error("操作失败");
    }

    /**
     * 增加阅读量
     */
    @PutMapping("/read/{id}")
    public Result<Void> incrementReadCount(@PathVariable Long id) {
        boolean success = announcementService.incrementReadCount(id);
        return success ? Result.success() : Result.error("操作失败");
    }
}
