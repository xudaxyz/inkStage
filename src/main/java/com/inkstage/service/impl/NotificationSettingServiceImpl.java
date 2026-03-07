package com.inkstage.service.impl;

import com.inkstage.entity.model.NotificationSetting;
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
        if (existingSetting == null) {
            // 插入新设置
            int result = notificationSettingMapper.insert(setting);
            return result > 0;
        } else {
            // 更新现有设置
            int result = notificationSettingMapper.update(setting);
            return result > 0;
        }
    }

    @Override
    public boolean isNotificationEnabled(Long userId, String notificationType) {
        NotificationSetting setting = getNotificationSetting(userId);
        return switch (notificationType) {
            case "articlePublish" -> setting.getArticlePublishNotification();
            case "articleLike" -> setting.getArticleLikeNotification();
            case "articleCollection" -> setting.getArticleCollectionNotification();
            case "articleComment" -> setting.getArticleCommentNotification();
            case "commentReply" -> setting.getCommentReplyNotification();
            case "commentLike" -> setting.getCommentLikeNotification();
            case "follow" -> setting.getFollowNotification();
            case "message" -> setting.getMessageNotification();
            case "report" -> setting.getReportNotification();
            case "feedback" -> setting.getFeedbackNotification();
            case "system" -> setting.getSystemNotification();
            default -> true; // 默认为开启
        };
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
        setting.setFeedbackNotification(true);
        setting.setSystemNotification(true);
        // 默认开启站内信通知
        setting.setSiteNotification(true);
        // 默认关闭邮件通知
        setting.setEmailNotification(false);
        return setting;
    }
}
