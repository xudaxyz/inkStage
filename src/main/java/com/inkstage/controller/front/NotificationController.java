package com.inkstage.controller.front;

import com.inkstage.annotation.UserAccess;
import com.inkstage.common.PageResult;
import com.inkstage.common.Result;
import com.inkstage.entity.model.Notification;
import com.inkstage.enums.notification.NotificationCategory;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.service.NotificationService;
import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


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
    @UserAccess
    public Result<PageResult<Notification>> getNotificationList(
            @RequestParam(value = "notificationType", required = false) NotificationType notificationType,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageResult<Notification> pageResult = notificationService.getNotificationListWithPage(notificationType, pageNum, pageSize);
        log.info("通知列表: {}", pageResult);
        return Result.success(pageResult);
    }

    /**
     * 分页获取通知列表
     */
    @GetMapping("/list/page")
    @UserAccess
    public Result<PageResult<Notification>> getNotificationListWithPage(
            @RequestParam(value = "notificationType", required = false) NotificationType notificationType,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageResult<Notification> pageResult = notificationService.getNotificationListWithPage(notificationType, pageNum, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 标记通知为已读
     */
    @PutMapping("/read/{id}")
    @UserAccess
    public Result<Boolean> markAsRead(@PathVariable Long id) {
        boolean result = notificationService.markAsRead(id);
        return Result.success(result);
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read/all")
    @UserAccess
    public Result<Boolean> markAllAsRead() {
        Long userId = UserContext.getCurrentUserId();
        boolean result = notificationService.markAllAsRead(userId);
        return Result.success(result);
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/delete/{id}")
    @UserAccess
    public Result<Boolean> deleteNotification(@PathVariable Long id) {
        boolean result = notificationService.deleteNotification(id);
        return Result.success(result);
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread/count")
    @UserAccess
    public Result<Integer> getUnreadCount() {
        Long userId = UserContext.getCurrentUserId();
        int count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 同步未读通知数量
     */
    @PostMapping("/unread/sync")
    @UserAccess
    public Result<Void> syncUnreadCount() {
        Long userId = UserContext.getCurrentUserId();
        notificationService.syncUnreadCount(userId);
        return Result.success();
    }

    /**
     * 按分类获取通知列表
     */
    @GetMapping("/category/{category}")
    @UserAccess
    public Result<PageResult<Notification>> getNotificationsByCategory(
            @PathVariable NotificationCategory category,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getCurrentUserId();
        PageResult<Notification> pageResult = notificationService.getNotificationsByCategory(userId, category, pageNum, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 按分类获取未读通知数量
     */
    @GetMapping("/unread/count/category")
    @UserAccess
    public Result<Map<NotificationCategory, Integer>> getUnreadCountByCategory() {
        Long userId = UserContext.getCurrentUserId();
        Map<NotificationCategory, Integer> countMap = notificationService.getUnreadCountByCategory(userId);
        return Result.success(countMap);
    }

    /**
     * 按分类标记通知为已读
     */
    @PutMapping("/read/category/{category}")
    @UserAccess
    public Result<Boolean> markAsReadByCategory(@PathVariable NotificationCategory category) {
        Long userId = UserContext.getCurrentUserId();
        boolean result = notificationService.markAsReadByCategory(userId, category);
        return Result.success(result);
    }

    /**
     * 清空通知
     */
    @DeleteMapping("/clear")
    @UserAccess
    public Result<Boolean> clearNotifications() {
        Long userId = UserContext.getCurrentUserId();
        boolean result = notificationService.clearNotifications(userId);
        return Result.success(result);
    }

    /**
     * 获取聚合后的通知
     */
    @GetMapping("/aggregated")
    @UserAccess
    public Result<List<Notification>> getAggregatedNotifications() {
        Long userId = UserContext.getCurrentUserId();
        List<Notification> notifications = notificationService.getAggregatedNotifications(userId);
        return Result.success(notifications);
    }
}