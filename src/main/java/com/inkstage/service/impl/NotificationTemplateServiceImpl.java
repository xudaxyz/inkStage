package com.inkstage.service.impl;

import com.inkstage.entity.model.NotificationTemplate;
import com.inkstage.enums.notification.NotificationChannel;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.service.AdminNotificationTemplateService;
import com.inkstage.service.NotificationTemplateService;
import com.inkstage.vo.admin.AdminNotificationTemplatePreviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

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
    public Map<String, String> generateNotificationContent(NotificationType notificationType, Object... params) {
        // 获取标题、内容、url变量模板
        String variablesJsonStr = getVariablesFromTemplate(notificationType, NotificationChannel.SITE, params);
        AdminNotificationTemplatePreviewVO preview = adminNotificationTemplateService.renderTemplateByType(notificationType, NotificationChannel.SITE, variablesJsonStr);
        if (preview == null) {
            return null;
        }

        Map<String, String> notificationTemplatePreview = new HashMap<>();
        notificationTemplatePreview.put("title", preview.getTitle());
        notificationTemplatePreview.put("content", preview.getContent());
        notificationTemplatePreview.put("actionUrl", preview.getActionUrl());

        return notificationTemplatePreview;
    }


    @Override
    public String getVariablesFromTemplate(NotificationType notificationType, NotificationChannel notificationChannel, Object... params) {
        // 从数据库获取内容模板
        log.info("获取通知模板变量: notificationType={}, notificationChannel={}, params={}", notificationType, notificationChannel, params);
        NotificationTemplate template = adminNotificationTemplateService.getTemplateByTypeAndChannel(notificationType, notificationChannel);
        // 构建变量映射
        Map<String, Object> variables = new HashMap<>();
        // 这里可以根据通知类型和参数构建相应的变量
        // 例如：如果是文章相关通知，可以从params中获取文章信息
        log.info("生成的变量: {}", variables);
        return toJson(variables);
    }

    /**
     * 将Map转换为JSON字符串
     */
    private String toJson(Map<String, Object> map) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.error("转换Map为JSON失败: {}", e.getMessage());
            return "{}";
        }
    }

}

