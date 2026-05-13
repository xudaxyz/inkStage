package com.inkstage.cache.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.constant.CacheTTL;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.cache.service.NotificationSettingCacheService;
import com.inkstage.dto.front.NotificationSettingDTO;
import com.inkstage.entity.model.NotificationSetting;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.NotificationSettingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通知设置缓存服务实现类
 * 专门负责通知设置相关的缓存操作
 * 使用 CacheManager 实现缓存管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSettingCacheServiceImpl implements NotificationSettingCacheService {

    private final NotificationSettingMapper notificationSettingMapper;
    private final CacheManager cacheManager;

    /**
     * 获取通知设置（带缓存）
     *
     * @param userId 用户ID
     * @return 通知设置
     */
    @Override
    public NotificationSetting getNotificationSetting(Long userId) {
        String cacheKey = CacheKey.keyForNotificationSetting(userId);
        NotificationSetting cached = cacheManager.get(cacheKey, NotificationSetting.class);
        if (cached != null) {
            log.debug("从缓存获取通知设置, 用户ID: {}", userId);
            return cached;
        }

        NotificationSetting setting = notificationSettingMapper.selectByUserId(userId);
        if (setting == null) {
            // 如果用户没有设置，返回默认设置
            setting = getDefaultNotificationSetting(userId);
            // 保存默认设置到数据库
            notificationSettingMapper.insert(setting);
        }

        log.info("从数据库获取通知设置, 用户ID: {}", userId);
        cacheManager.set(cacheKey, setting, CacheTTL.NOTIFICATION_SETTING);
        return setting;
    }

    /**
     * 保存通知设置（会清除缓存）
     *
     * @param setting 通知设置
     * @return 是否保存成功
     */
    @Override
    public boolean saveNotificationSetting(NotificationSetting setting) {
        NotificationSetting existingSetting = notificationSettingMapper.selectByUserId(setting.getUserId());
        int result;
        if (existingSetting == null) {
            // 插入新设置
            result = notificationSettingMapper.insert(setting);
        } else {
            // 更新现有设置
            result = notificationSettingMapper.update(setting);
        }

        if (result > 0) {
            // 删除缓存，下次获取时重新加载
            String cacheKey = CacheKey.keyForNotificationSetting(setting.getUserId());
            cacheManager.delete(cacheKey);
            log.debug("保存通知设置成功并清除缓存, 用户ID: {}", setting.getUserId());
        }

        return result > 0;
    }

    /**
     * 更新通知设置（会清除缓存）
     *
     * @param userId                 用户ID
     * @param notificationSettingDTO 通知设置DTO
     * @return 是否更新成功
     */
    @Override
    public boolean updateNotificationSetting(Long userId, NotificationSettingDTO notificationSettingDTO) {
        try {
            String notificationTypeName = notificationSettingDTO.getNotificationType().getName();
            log.info("更新用户 {} 通知设置 {} 为: {}", userId, notificationTypeName, notificationSettingDTO.getNotificationValue());

            boolean success = notificationSettingMapper.updateNotificationSetting(userId, notificationTypeName, notificationSettingDTO.getNotificationValue());

            if (success) {
                // 删除缓存，下次获取时重新加载
                String cacheKey = CacheKey.keyForNotificationSetting(userId);
                cacheManager.delete(cacheKey);
                log.debug("更新通知设置成功并清除缓存, 用户ID: {}", userId);
            }

            return success;
        } catch (Exception e) {
            log.error("更新用户通知设置失败 {}", userId, e);
            throw new BusinessException("更新用户通知设置失败", e);
        }
    }

    /**
     * 获取默认通知设置
     *
     * @param userId 用户ID
     * @return 默认通知设置
     */
    @Override
    public NotificationSetting getDefaultNotificationSetting(Long userId) {
        NotificationSetting setting = new NotificationSetting();
        setting.setUserId(userId);
        // 默认开启所有通知
        setting.setArticlePublishNotification(true);
        setting.setArticleLikeNotification(true);
        setting.setArticleCollectionNotification(true);
        setting.setArticleCommentNotification(true);
        setting.setCommentReplyNotification(true);
        setting.setCommentLikeNotification(true);
        setting.setFollowNotification(true);
        setting.setMessageNotification(true);
        setting.setReportNotification(true);
        setting.setReportResultNotification(true);
        setting.setFeedbackNotification(true);
        setting.setSystemNotification(true);
        // 默认开启站内信通知
        setting.setSiteNotification(true);
        // 默认关闭邮件通知
        setting.setEmailNotification(false);
        return setting;
    }
}
