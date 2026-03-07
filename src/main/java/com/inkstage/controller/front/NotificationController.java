package com.inkstage.controller.front;

import com.inkstage.common.PageResult;
import com.inkstage.common.Result;
import com.inkstage.entity.model.Notification;
import com.inkstage.enums.NotificationType;
import com.inkstage.service.NotificationService;
import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * 通知控制器
 */
@Slf4j
@RestController
@RequestMapping("/front/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 获取通知列表（支持分页）
     */
    @GetMapping("/list")
    public Result<PageResult<Notification>> getNotificationList(
            @RequestParam(value = "type", required = false) Integer typeCode,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        NotificationType type = typeCode != null ? NotificationType.fromCode(typeCode) : null;
        PageResult<Notification> pageResult = notificationService.getNotificationListWithPage(type, pageNum, pageSize);
        log.info("通知列表: {}", pageResult);
        return Result.success(pageResult);
    }

    /**
     * 分页获取通知列表
     */
    @GetMapping("/list/page")
    public Result<PageResult<Notification>> getNotificationListWithPage(
            @RequestParam(value = "type", required = false) Integer typeCode,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        NotificationType type = typeCode != null ? NotificationType.fromCode(typeCode) : null;
        PageResult<Notification> pageResult = notificationService.getNotificationListWithPage(type, pageNum, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 标记通知为已读
     */
    @PutMapping("/read/{id}")
    public Result<Boolean> markAsRead(@PathVariable Long id) {
        boolean result = notificationService.markAsRead(id);
        return Result.success(result);
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read/all")
    public Result<Boolean> markAllAsRead() {
        Long userId = UserContext.getCurrentUserId();
        boolean result = notificationService.markAllAsRead(userId);
        return Result.success(result);
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteNotification(@PathVariable Long id) {
        boolean result = notificationService.deleteNotification(id);
        return Result.success(result);
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread/count")
    public Result<Integer> getUnreadCount() {
        Long userId = UserContext.getCurrentUserId();
        int count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 同步未读通知数量
     */
    @PostMapping("/unread/sync")
    public Result<Void> syncUnreadCount() {
        Long userId = UserContext.getCurrentUserId();
        notificationService.syncUnreadCount(userId);
        return Result.success();
    }
}