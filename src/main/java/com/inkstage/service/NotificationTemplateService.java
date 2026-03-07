package com.inkstage.service;

import com.inkstage.enums.NotificationType;

/**
 * 通知模板服务
 */
public interface NotificationTemplateService {

    /**
     * 生成通知标题
     */
    String generateTitle(NotificationType type, Object... params);

    /**
     * 生成通知内容
     */
    String generateContent(NotificationType type, Object... params);

    /**
     * 生成通知操作链接
     */
    String generateActionUrl(NotificationType type, Long relatedId);
}
