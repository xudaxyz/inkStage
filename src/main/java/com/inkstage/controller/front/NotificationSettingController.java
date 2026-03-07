package com.inkstage.controller.front;

import com.inkstage.common.Result;
import com.inkstage.entity.model.NotificationSetting;
import com.inkstage.service.NotificationSettingService;
import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 通知设置控制器
 */
@RestController
@RequestMapping("/front/notification/setting")
@RequiredArgsConstructor
public class NotificationSettingController {

    private final NotificationSettingService notificationSettingService;

    /**
     * 获取用户的通知设置
     */
    @GetMapping("/get")
    public Result<NotificationSetting> getNotificationSetting() {
        Long userId = UserContext.getCurrentUserId();
        NotificationSetting setting = notificationSettingService.getNotificationSetting(userId);
        return Result.success(setting);
    }

    /**
     * 保存用户的通知设置
     */
    @PutMapping("/save")
    public Result<Boolean> saveNotificationSetting(@RequestBody NotificationSetting setting) {
        Long userId = UserContext.getCurrentUserId();
        setting.setUserId(userId);
        boolean result = notificationSettingService.saveNotificationSetting(setting);
        return Result.success(result);
    }

    /**
     * 恢复默认通知设置
     */
    @PutMapping("/reset")
    public Result<Boolean> resetNotificationSetting() {
        Long userId = UserContext.getCurrentUserId();
        NotificationSetting defaultSetting = notificationSettingService.getDefaultNotificationSetting(userId);
        boolean result = notificationSettingService.saveNotificationSetting(defaultSetting);
        return Result.success(result);
    }
}
