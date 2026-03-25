package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.entity.model.NotificationTemplate;
import com.inkstage.enums.NotificationChannel;
import com.inkstage.enums.NotificationType;
import com.inkstage.enums.StatusEnum;
import com.inkstage.vo.TemplatePreviewVO;

import java.util.List;
import java.util.Map;

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
    PageResult<NotificationTemplate> getTemplatePage(Integer pageNum, Integer pageSize,
                                                      NotificationType type, StatusEnum status,
                                                      String keyword);

    /**
     * 获取所有模板
     */
    List<NotificationTemplate> getAllTemplates();

    /**
     * 根据类型获取模板
     */
    List<NotificationTemplate> getTemplatesByType(NotificationType type);

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
     * @param variables    变量映射
     * @return 渲染后的标题、内容、类型和链接
     */
    TemplatePreviewVO renderTemplate(String templateCode, Map<String, Object> variables);

    /**
     * 根据通知类型渲染模板
     *
     * @param type      通知类型
     * @param channel   通知渠道
     * @param variables 变量映射
     * @return 渲染后的标题、内容、类型和链接
     */
    TemplatePreviewVO renderTemplateByType(NotificationType type, NotificationChannel channel, Map<String, Object> variables);
}
