package com.inkstage.service.impl;

import com.inkstage.dto.front.NotificationSettingDTO;
import com.inkstage.entity.model.NotificationSetting;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.NotificationSettingMapper;
import com.inkstage.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通知设置服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSettingServiceImpl implements NotificationSettingService {

    private final NotificationSettingMapper notificationSettingMapper;

    @Override
    public NotificationSetting getNotificationSetting(Long userId) {
        NotificationSetting setting = notificationSettingMapper.selectByUserId(userId);
        if (setting == null) {
            // 如果用户没有设置，返回默认设置
            setting = getDefaultNotificationSetting(userId);
            // 保存默认设置到数据库
            notificationSettingMapper.insert(setting);
        }
        return setting;
    }

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
        return result > 0;
    }

    @Override
    public boolean isNotificationEnabled(Long userId, NotificationType notificationType) {
        Integer result = notificationSettingMapper.checkNotificationEnabled(userId, notificationType.getCode());
        if (result == null) {
            return false;
        } else {
            return result == 1;
        }
    }

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

    @Override
    public boolean updateNotificationSetting(Long userId, NotificationSettingDTO notificationSettingDTO) {
        try {
            String notificationTypeName = notificationSettingDTO.getNotificationType().getName();
            log.info("更新用户 {} 通知设置 {} 为: {}", userId, notificationTypeName, notificationSettingDTO.getNotificationValue());
            return notificationSettingMapper.updateNotificationSetting(userId, notificationTypeName, notificationSettingDTO.getNotificationValue());
        } catch (Exception e) {
            log.error("更新用户通知设置失败 {}", userId, e);
            throw new BusinessException("更新用户通知设置失败", e);
        }
    }

}
