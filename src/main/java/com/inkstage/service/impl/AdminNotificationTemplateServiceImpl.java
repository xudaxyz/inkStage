package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.dto.NotificationMessageDTO;
import com.inkstage.dto.admin.ManualNotificationDTO;
import com.inkstage.dto.admin.NotificationTemplateQueryDTO;
import com.inkstage.entity.model.NotificationTemplate;
import com.inkstage.entity.model.User;
import com.inkstage.enums.common.StatusEnum;
import com.inkstage.enums.notification.NotificationChannel;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.NotificationTemplateMapper;
import com.inkstage.service.AdminNotificationTemplateService;
import com.inkstage.service.UserService;
import com.inkstage.utils.SpELTemplateRender;
import com.inkstage.utils.TemplateValidator;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.admin.AdminNotificationTemplatePreviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.inkstage.config.rabbitmq.RabbitMQConfig.NOTIFICATION_EXCHANGE;
import static com.inkstage.config.rabbitmq.RabbitMQConfig.NOTIFICATION_ROUTING_KEY;

/**
 * 通知模板管理服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationTemplateServiceImpl implements AdminNotificationTemplateService {

    private final NotificationTemplateMapper templateMapper;
    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 创建通知模板
     *
     * @param template 通知模板对象
     * @return 是否创建成功
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional
    public boolean createTemplate(NotificationTemplate template) {
        // 检查编码是否已存在
        if (templateMapper.existsByCode(template.getCode())) {
            throw new BusinessException("模板编码已存在: " + template.getCode());
        }

        // 验证模板语法
        boolean valid = validateTemplate(template);
        if (!valid) {
            throw new BusinessException("模板语法验证失败: " + template.getCode());
        }

        // 设置创建信息
        User currentUser = UserContext.getCurrentUser();
        template.setCreateUserId(currentUser.getId());
        template.setUpdateUserId(currentUser.getId());
        template.setCreateUsername(currentUser.getNickname());
        template.setUpdateUsername(currentUser.getNickname());
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());
        template.setTemplateVersion(1L);

        // 默认启用
        if (template.getStatus() == null) {
            template.setStatus(StatusEnum.ENABLED);
        }

        // 默认渠道
        if (template.getNotificationChannel() == null) {
            template.setNotificationChannel(NotificationChannel.SITE);
        }

        return templateMapper.insert(template) > 0;
    }

    /**
     * 更新通知模板
     *
     * @param template 通知模板对象
     * @return 是否更新成功
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional
    public boolean updateTemplate(NotificationTemplate template) {
        NotificationTemplate existing = templateMapper.selectById(template.getId());
        if (existing == null) {
            throw new BusinessException("模板不存在: " + template.getId());
        }

        // 如果修改了编码，检查新编码是否已存在
        if (template.getCode() != null && !template.getCode().isEmpty() && !template.getCode().equals(existing.getCode())) {
            if (templateMapper.existsByCode(template.getCode())) {
                throw new BusinessException("模板编码已存在: " + template.getCode());
            }
        }

        // 验证模板语法
        validateTemplate(template);

        User currentUser = UserContext.getCurrentUser();
        template.setUpdateUserId(currentUser.getId());
        template.setUpdateUsername(currentUser.getNickname());
        template.setUpdateTime(LocalDateTime.now());

        return templateMapper.update(template) > 0;
    }

    /**
     * 删除通知模板
     *
     * @param id 模板ID
     * @return 是否删除成功
     */
    @Override
    @Transactional
    public boolean deleteTemplate(Long id) {
        return templateMapper.deleteById(id) > 0;
    }

    /**
     * 根据ID获取通知模板
     *
     * @param id 模板ID
     * @return 通知模板对象
     */
    @Override
    public NotificationTemplate getTemplateById(Long id) {
        return templateMapper.selectById(id);
    }

    /**
     * 根据编码获取通知模板
     *
     * @param code 模板编码
     * @return 通知模板对象
     */
    @Override
    public NotificationTemplate getTemplateByCode(String code) {
        return templateMapper.selectByCode(code);
    }

    /**
     * 分页查询通知模板
     *
     * @param templateQuery 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult<NotificationTemplate> getTemplatePage(NotificationTemplateQueryDTO templateQuery) {
        int offset = (templateQuery.getPageNum() - 1) * templateQuery.getPageSize();
        List<NotificationTemplate> list = templateMapper.selectPageByQuery(templateQuery, offset, templateQuery.getPageSize());
        long total = templateMapper.countByQuery(templateQuery);
        return PageResult.build(list, total, templateQuery.getPageNum(), templateQuery.getPageSize());
    }

    /**
     * 获取所有通知模板
     *
     * @return 通知模板列表
     */
    @Override
    public List<NotificationTemplate> getAllTemplates() {
        return templateMapper.selectAll();
    }

    /**
     * 根据类型获取通知模板
     *
     * @param notificationType 通知类型
     * @return 通知模板列表
     */
    @Override
    public List<NotificationTemplate> getTemplatesByType(NotificationType notificationType) {
        return templateMapper.selectByType(notificationType);
    }

    @Override
    public NotificationTemplate getTemplateByTypeAndChannel(NotificationType notificationType, NotificationChannel channel) {
        if (channel == null) {
            channel = NotificationChannel.SITE;
        }
        return templateMapper.selectByTypeAndChannel(notificationType, channel);
    }

    /**
     * 更新通知模板状态
     *
     * @param id     模板ID
     * @param status 状态
     * @return 是否更新成功
     */
    @Override
    @Transactional
    public boolean updateTemplateStatus(Long id, StatusEnum status) {
        NotificationTemplate template = new NotificationTemplate();
        template.setId(id);
        template.setStatus(status);
        template.setUpdateUserId(UserContext.getCurrentUserId());
        template.setUpdateUsername(UserContext.getCurrentUser().getNickname());
        return templateMapper.update(template) > 0;
    }

    /**
     * 检查模板编码是否存在
     *
     * @param code 模板编码
     * @return 是否存在
     */
    @Override
    public boolean isCodeExists(String code) {
        return templateMapper.existsByCode(code);
    }

    @Override
    public AdminNotificationTemplatePreviewVO renderTemplate(String templateCode, String variables) {
        NotificationTemplate template = templateMapper.selectByCode(templateCode);
        if (template == null) {
            throw new BusinessException("模板不存在: " + templateCode);
        }

        Map<String, Object> variablesMap = parseVariables(variables);
        return renderTemplateInternal(template, variablesMap);
    }

    @Override
    public AdminNotificationTemplatePreviewVO renderTemplateByType(NotificationType type, NotificationChannel channel, String variables) {
        if (channel == null) {
            channel = NotificationChannel.SITE;
        }

        NotificationTemplate template = templateMapper.selectByTypeAndChannel(type, channel);
        if (template == null) {
            log.warn("未找到通知类型 {} 渠道 {} 的模板", type, channel);
            return null;
        }

        Map<String, Object> variablesMap = parseVariables(variables);
        return renderTemplateInternal(template, variablesMap);
    }

    /**
     * 解析变量JSON字符串
     */
    private Map<String, Object> parseVariables(String variables) {
        if (variables == null || variables.isEmpty()) {
            return new HashMap<>();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(variables, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (Exception e) {
            log.error("解析变量JSON失败: {}", e.getMessage());
            throw new BusinessException("变量格式错误: " + e.getMessage());
        }
    }

    /**
     * 内部模板渲染方法
     */
    private AdminNotificationTemplatePreviewVO renderTemplateInternal(NotificationTemplate template, Map<String, Object> variables) {
        AdminNotificationTemplatePreviewVO result = new AdminNotificationTemplatePreviewVO();

        // 渲染标题
        String title = SpELTemplateRender.render(template.getTitleTemplate(), variables);
        result.setTitle(title);

        // 渲染内容
        String content = SpELTemplateRender.render(template.getContentTemplate(), variables);
        result.setContent(content);

        // 渲染链接
        if (template.getActionUrlTemplate() != null && !template.getActionUrlTemplate().isEmpty()) {
            String actionUrl = SpELTemplateRender.render(template.getActionUrlTemplate(), variables);
            result.setActionUrl(actionUrl);
        }

        result.setNotificationType(template.getNotificationType());

        return result;
    }

    @Override
    public int sendNotification(ManualNotificationDTO manualNotice) {
        List<Long> targetUserIds = getUserIdsByType(manualNotice.getUserType(), manualNotice.getUserIds(), manualNotice.getRoleCode());
        if (targetUserIds.isEmpty()) {
            return 0;
        }

        // 渲染模板一次，避免重复渲染
        AdminNotificationTemplatePreviewVO rendered = renderTemplate(manualNotice.getTemplateCode(), manualNotice.getVariables());
        if (rendered == null) {
            return 0;
        }

        // 批量发送通知
        return sendBatchNotifications(targetUserIds, rendered, manualNotice.getRelatedId(), manualNotice.getSenderId());
    }

    /**
     * 批量发送通知
     */
    private int sendBatchNotifications(List<Long> userIds, AdminNotificationTemplatePreviewVO rendered, Long relatedId, Long senderId) {
        int successCount = 0;
        final int BATCH_SIZE = 100; // 每批发送100条

        try {
            // 分批处理
            for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, userIds.size());
                List<Long> batchUserIds = userIds.subList(i, endIndex);

                // 批量发送
                successCount += sendBatch(batchUserIds, rendered, relatedId, senderId);
            }
        } catch (Exception e) {
            log.error("批量发送通知失败", e);
        }

        return successCount;
    }

    /**
     * 发送一批通知
     */
    private int sendBatch(List<Long> userIds, AdminNotificationTemplatePreviewVO rendered, Long relatedId, Long senderId) {
        int successCount = 0;

        for (Long userId : userIds) {
            try {
                NotificationMessageDTO message = new NotificationMessageDTO();
                message.setUserId(userId);
                message.setTitle(rendered.getTitle());
                message.setContent(rendered.getContent());
                message.setNotificationType(rendered.getNotificationType());
                message.setRelatedId(relatedId);
                message.setSenderId(senderId != null ? senderId : 0L);
                message.setActionUrl(rendered.getActionUrl());

                rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, NOTIFICATION_ROUTING_KEY, message);
                successCount++;
            } catch (Exception e) {
                log.error("发送通知失败，用户ID: {}", userId, e);
            }
        }

        return successCount;
    }

    /**
     * 根据用户类型获取用户ID列表
     */
    private List<Long> getUserIdsByType(String userType, List<Long> userIds, String roleCode) {
        return switch (userType) {
            case "all" -> userService.getAllUserIds();
            case "specific" -> userIds != null ? userIds : List.of();
            case "role" -> userService.getUserIdsByRoleCode(roleCode);
            default -> List.of();
        };
    }

    @Override
    public boolean validateTemplate(NotificationTemplate template) {
        // 使用 TemplateValidator 验证模板
        return TemplateValidator.validate(template);
    }
}
