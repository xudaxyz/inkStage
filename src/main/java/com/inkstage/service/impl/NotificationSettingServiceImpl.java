package com.inkstage.service.impl;

import com.inkstage.cache.service.NotificationSettingCacheService;
import com.inkstage.dto.front.NotificationSettingDTO;
import com.inkstage.entity.model.NotificationSetting;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

/**
 * 通知设置服务实现类
 * 专注于业务逻辑，缓存操作委托给 NotificationSettingCacheService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSettingServiceImpl implements NotificationSettingService {

    private final NotificationSettingCacheService notificationSettingCacheService;

    @Override
    public NotificationSetting getNotificationSetting(Long userId) {
        return notificationSettingCacheService.getNotificationSetting(userId);
    }

    @Override
    public boolean saveNotificationSetting(NotificationSetting setting) {
        return notificationSettingCacheService.saveNotificationSetting(setting);
    }

    @Override
    public boolean isNotificationEnabled(Long userId, NotificationType notificationType) {
        // 1. 先检查该通知类型是否有设置字段
        if (!notificationType.hasSettingField()) {
            return true; // 没有设置字段，默认启用，不查询数据库
        }

        // 2. 有字段，查询用户设置（从缓存服务获取）
        NotificationSetting setting = notificationSettingCacheService.getNotificationSetting(userId);

        // 3. 通过反射获取字段值
        String fieldName = notificationType.getSettingField();

        // 4. 返回设置值，如果为空默认启用
        return getFieldValue(setting, fieldName);
    }

    @Override
    public NotificationSetting getDefaultNotificationSetting(Long userId) {
        return notificationSettingCacheService.getDefaultNotificationSetting(userId);
    }

    @Override
    public boolean updateNotificationSetting(Long userId, NotificationSettingDTO notificationSettingDTO) {
        return notificationSettingCacheService.updateNotificationSetting(userId, notificationSettingDTO);
    }

    /**
     * 通过反射获取字段值
     */
    private Boolean getFieldValue(NotificationSetting setting, String fieldName) {
        if (setting == null) {
            return true;
        }
        try {
            Field field = NotificationSetting.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(setting);
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            return true;
        } catch (Exception e) {
            log.error("通过反射获取通知设置字段失败, 字段名: {}, 错误: {}", fieldName, e.getMessage());
            return true;
        }
    }
}
