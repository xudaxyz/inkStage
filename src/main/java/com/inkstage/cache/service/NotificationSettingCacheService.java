package com.inkstage.cache.service;

import com.inkstage.dto.front.NotificationSettingDTO;
import com.inkstage.entity.model.NotificationSetting;

/**
 * 通知设置缓存服务接口
 * 专门负责通知设置相关的缓存操作
 */
public interface NotificationSettingCacheService {

    /**
     * 获取通知设置（带缓存）
     *
     * @param userId 用户ID
     * @return 通知设置对象
     */
    NotificationSetting getNotificationSetting(Long userId);

    /**
     * 保存通知设置（清除缓存）
     *
     * @param setting 通知设置对象
     * @return 是否成功
     */
    boolean saveNotificationSetting(NotificationSetting setting);

    /**
     * 更新通知设置（清除缓存）
     *
     * @param userId 用户ID
     * @param notificationSettingDTO 通知设置DTO
     * @return 是否成功
     */
    boolean updateNotificationSetting(Long userId, NotificationSettingDTO notificationSettingDTO);

    /**
     * 获取用户默认的通知设置
     * @param userId 用户id
     * @return 默认的通知设置
     */
    NotificationSetting getDefaultNotificationSetting(Long userId);
}
