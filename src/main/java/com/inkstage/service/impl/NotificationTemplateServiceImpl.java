package com.inkstage.service.impl;

import com.inkstage.entity.model.NotificationTemplate;
import com.inkstage.enums.NotificationChannel;
import com.inkstage.enums.NotificationType;
import com.inkstage.service.AdminNotificationTemplateService;
import com.inkstage.service.NotificationTemplateService;
import com.inkstage.utils.TemplateRenderUtils;
import com.inkstage.vo.admin.AdminNotificationTemplatePreviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

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
    public String generateTitle(NotificationType notificationType, Object... params) {
        // 获取标题模板
        String variablesJsonStr = getVariablesFromTemplate(notificationType, NotificationChannel.SITE, params);
        AdminNotificationTemplatePreviewVO preview = adminNotificationTemplateService.renderTemplateByType(notificationType, NotificationChannel.SITE, variablesJsonStr);
        if (preview != null && preview.getTitle() != null) {
            return preview.getTitle();
        }

        return null;
    }

    @Override
    public String generateContent(NotificationType notificationType, Object... params) {
        // 获取内容模板
        String variablesJsonStr = getVariablesFromTemplate(notificationType, NotificationChannel.SITE, params);
        AdminNotificationTemplatePreviewVO preview = adminNotificationTemplateService.renderTemplateByType(notificationType, NotificationChannel.SITE, variablesJsonStr);
        if (preview != null && preview.getContent() != null) {
            return preview.getContent();
        }

        // 没有找到内容模板，返回空字符串
        return "";
    }

    @Override
    public String generateActionUrl(NotificationType notificationType, Long relatedId) {
        // 从数据库获取actionUrl模板
        String variablesJson = getVariablesFromTemplate(notificationType, NotificationChannel.SITE, relatedId);
        AdminNotificationTemplatePreviewVO preview = adminNotificationTemplateService.renderTemplateByType(notificationType, NotificationChannel.SITE, variablesJson);
        if (preview != null && preview.getActionUrl() != null) {
            return preview.getActionUrl();
        }

        // 没有找到模板，返回空字符串
        return "";
    }

    @Override
    public String getVariablesFromTemplate(NotificationType notificationType, NotificationChannel notificationChannel, Object... params) {
        // 从数据库获取内容模板
        log.info("获取通知模板变量: notificationType={}, notificationChannel={}", notificationType, notificationChannel);
        NotificationTemplate template = adminNotificationTemplateService.getTemplateByType(notificationType, notificationChannel);
        String variablesJson = template != null ? template.getVariables() : null;
        Map<String, Object> variables = TemplateRenderUtils.buildVariables(variablesJson, params);
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

