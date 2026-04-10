package com.inkstage.service.impl;

import com.inkstage.enums.notification.NotificationChannel;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.service.AdminNotificationTemplateService;
import com.inkstage.service.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知模板服务实现类
 * <p>
 * 支持从数据库读取模板，如果数据库中没有找到对应模板，则使用默认模板
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private final AdminNotificationTemplateService adminNotificationTemplateService;

    @Override
    public Map<String, Object> generateNotificationContent(NotificationType notificationType, Map<String, Object> params) {
        params = adminNotificationTemplateService.renderTemplateByType(notificationType, NotificationChannel.SITE, params);
        return params;
    }

}

