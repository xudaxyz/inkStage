package com.inkstage.controller.admin;

import com.inkstage.annotation.AdminAccess;
import com.inkstage.common.Result;
import com.inkstage.dto.admin.ManualNotificationDTO;
import com.inkstage.enums.notification.NotificationCategory;
import com.inkstage.service.AdminNotificationTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台通知管理控制器
 */
@RestController
@RequestMapping("/admin/notification")
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationController {

    private final AdminNotificationTemplateService templateService;

    /**
     * 手动发送通知
     */
    @PostMapping("/send")
    @AdminAccess
    public Result<Integer> sendNotification(@RequestBody ManualNotificationDTO dto) {
        int successCount = templateService.sendNotification(dto);
        if (successCount == 0) {
            return Result.error("未找到符合条件的用户或发送失败");
        }
        return Result.success(successCount);
    }

    /**
     * 获取通知分类列表
     */
    @GetMapping("/categories")
    @AdminAccess
    public Result<List<NotificationCategory>> getCategories() {
        return Result.success();
    }

    /**
     * 获取通知统计数据
     */
    @GetMapping("/stats")
    @AdminAccess
    public Result<?> getStats() {
        return Result.success();
    }
}