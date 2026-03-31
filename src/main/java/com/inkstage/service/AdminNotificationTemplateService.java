package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.dto.admin.ManualNotificationDTO;
import com.inkstage.dto.admin.NotificationTemplateQueryDTO;
import com.inkstage.entity.model.NotificationTemplate;
import com.inkstage.enums.notification.NotificationChannel;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.enums.common.StatusEnum;
import com.inkstage.vo.admin.AdminNotificationTemplatePreviewVO;

import java.util.List;

/**
 * 通知模板管理服务(后台管理)
 */
public interface AdminNotificationTemplateService {

    /**
     * 创建通知模板
     */
    boolean createTemplate(NotificationTemplate template);

    /**
     * 更新通知模板
     */
    boolean updateTemplate(NotificationTemplate template);

    /**
     * 删除通知模板
     */
    boolean deleteTemplate(Long id);

    /**
     * 根据ID获取模板
     */
    NotificationTemplate getTemplateById(Long id);

    /**
     * 根据编码获取模板
     */
    NotificationTemplate getTemplateByCode(String code);

    /**
     * 分页查询模板列表
     */
    PageResult<NotificationTemplate> getTemplatePage(NotificationTemplateQueryDTO notificationTemplateQuery);

    /**
     * 获取所有模板
     */
    List<NotificationTemplate> getAllTemplates();

    /**
     * 根据类型获取模板
     */
    List<NotificationTemplate> getTemplatesByType(NotificationType notificationType);

    /**
     * 根据类型和渠道获取单个模板
     */
    NotificationTemplate getTemplateByTypeAndChannel(NotificationType notificationType, NotificationChannel channel);

    /**
     * 启用/禁用模板
     */
    boolean updateTemplateStatus(Long id, StatusEnum status);

    /**
     * 检查编码是否已存在
     */
    boolean isCodeExists(String code);

    /**
     * 渲染模板
     *
     * @param templateCode 模板编码
     * @param variables    变量JSON字符串
     * @return 渲染后的标题、内容、类型和链接
     */
    AdminNotificationTemplatePreviewVO renderTemplate(String templateCode, String variables);

    /**
     * 根据通知类型渲染模板
     *
     * @param type      通知类型
     * @param channel   通知渠道
     * @param variables 变量JSON字符串
     * @return 渲染后的标题、内容、类型和链接
     */
    AdminNotificationTemplatePreviewVO renderTemplateByType(NotificationType type, NotificationChannel channel, String variables);

    /**
     * 手动发送通知
     *
     * @param manualNotification 手动通知DTO
     * @return 成功发送的数量
     */
    int sendNotification(ManualNotificationDTO manualNotification);

    /**
     * 验证模板语法
     *
     * @param template 通知模板
     * @return 验证结果，true表示验证通过
     */
    boolean validateTemplate(NotificationTemplate template);
}
