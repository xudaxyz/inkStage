package com.inkstage.service;

import com.inkstage.dto.front.NotificationSettingDTO;
import com.inkstage.entity.model.NotificationSetting;
import com.inkstage.enums.notification.NotificationType;

/**
 * 通知设置服务
 */
public interface NotificationSettingService {

    /**
     * 获取用户的通知设置
     */
    NotificationSetting getNotificationSetting(Long userId);

    /**
     * 保存用户的通知设置
     */
    boolean saveNotificationSetting(NotificationSetting setting);

    /**
     * 检查用户是否开启了某种类型的通知
     */
    boolean isNotificationEnabled(Long userId, NotificationType notificationType);

    /**
     * 获取默认的通知设置
     */
    NotificationSetting getDefaultNotificationSetting(Long userId);

    /**
     * 更新单个通知设置
     */
    boolean updateNotificationSetting(Long userId, NotificationSettingDTO notificationSettingDTO);
}
