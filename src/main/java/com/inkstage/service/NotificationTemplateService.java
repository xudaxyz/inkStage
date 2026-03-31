package com.inkstage.service;

import com.inkstage.enums.notification.NotificationType;

import java.util.Map;

/**
 * 通知模板服务
 */
public interface NotificationTemplateService {


    /**
     * 生成通知内容
     *
     * @param notificationType 通知类型
     * @param params           通知模板中的变量
     * @return 通知内容
     */
    Map<String, Object> generateNotificationContent(NotificationType notificationType, Map<String, Object> params);
}
