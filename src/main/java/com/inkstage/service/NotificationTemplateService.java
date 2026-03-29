package com.inkstage.service;

import com.inkstage.enums.NotificationChannel;
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


    /**
     * 从模板中获取变量
     * @param notificationType 通知类型
     * @param notificationChannel 通知渠道
     * @param params 通知模板中的变量
     * @return 变量字符串
     */
    String getVariablesFromTemplate(NotificationType notificationType, NotificationChannel  notificationChannel, Object... params);

}
