package com.inkstage.service;

import com.inkstage.enums.notification.NotificationChannel;
import com.inkstage.enums.notification.NotificationType;

import java.util.Map;

/**
 * 通知模板服务
 */
public interface NotificationTemplateService {

    /**
     * 生成通知标题
     */
    Map<String, String> generateNotificationContent(NotificationType type, Object... params);

    /**
     * 从模板中获取变量
     * @param notificationType 通知类型
     * @param notificationChannel 通知渠道
     * @param params 通知模板中的变量
     * @return 变量字符串
     */
    String getVariablesFromTemplate(NotificationType notificationType, NotificationChannel  notificationChannel, Object... params);

}
