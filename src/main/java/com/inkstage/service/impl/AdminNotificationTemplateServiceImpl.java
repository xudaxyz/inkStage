package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.entity.model.NotificationTemplate;
import com.inkstage.enums.NotificationChannel;
import com.inkstage.enums.NotificationType;
import com.inkstage.enums.StatusEnum;
import com.inkstage.mapper.NotificationTemplateMapper;
import com.inkstage.service.AdminNotificationTemplateService;
import com.inkstage.utils.TemplateRenderUtils;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.TemplatePreviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 通知模板管理服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationTemplateServiceImpl implements AdminNotificationTemplateService {

    private final NotificationTemplateMapper templateMapper;

    @Override
    @Transactional
    public boolean createTemplate(NotificationTemplate template) {
        // 检查编码是否已存在
        if (templateMapper.existsByCode(template.getCode())) {
            throw new RuntimeException("模板编码已存在: " + template.getCode());
        }

        // 设置创建信息
        template.setCreateUserId(UserContext.getCurrentUserId());
        template.setUpdateUserId(UserContext.getCurrentUserId());
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());

        // 默认启用
        if (template.getStatus() == null) {
            template.setStatus(StatusEnum.ENABLED);
        }

        // 默认渠道
        if (template.getChannel() == null) {
            template.setChannel(NotificationChannel.SITE);
        }

        return templateMapper.insert(template) > 0;
    }

    @Override
    @Transactional
    public boolean updateTemplate(NotificationTemplate template) {
        NotificationTemplate existing = templateMapper.selectById(template.getId());
        if (existing == null) {
            throw new RuntimeException("模板不存在: " + template.getId());
        }

        // 如果修改了编码，检查新编码是否已存在
        if (template.getCode() != null && !template.getCode().isEmpty() && !template.getCode().equals(existing.getCode())) {
            if (templateMapper.existsByCode(template.getCode())) {
                throw new RuntimeException("模板编码已存在: " + template.getCode());
            }
        }

        template.setUpdateUserId(UserContext.getCurrentUserId());
        template.setUpdateTime(LocalDateTime.now());

        return templateMapper.update(template) > 0;
    }

    @Override
    @Transactional
    public boolean deleteTemplate(Long id) {
        return templateMapper.deleteById(id) > 0;
    }

    @Override
    public NotificationTemplate getTemplateById(Long id) {
        return templateMapper.selectById(id);
    }

    @Override
    public NotificationTemplate getTemplateByCode(String code) {
        return templateMapper.selectByCode(code);
    }

    @Override
    public PageResult<NotificationTemplate> getTemplatePage(Integer pageNum, Integer pageSize,
                                                             NotificationType type, StatusEnum status,
                                                             String keyword) {
        int offset = (pageNum - 1) * pageSize;
        var list = templateMapper.selectPage(offset, pageSize, type, status, keyword);
        long total = templateMapper.count(type, status, keyword);
        return PageResult.build(list, total, pageNum, pageSize);
    }

    @Override
    public List<NotificationTemplate> getAllTemplates() {
        return templateMapper.selectAll();
    }

    @Override
    public List<NotificationTemplate> getTemplatesByType(NotificationType type) {
        return templateMapper.selectByType(type);
    }

    @Override
    @Transactional
    public boolean updateTemplateStatus(Long id, StatusEnum status) {
        NotificationTemplate template = new NotificationTemplate();
        template.setId(id);
        template.setStatus(status);
        template.setUpdateUserId(UserContext.getCurrentUserId());
        return templateMapper.update(template) > 0;
    }

    @Override
    public boolean isCodeExists(String code) {
        return templateMapper.existsByCode(code);
    }

    @Override
    public TemplatePreviewVO renderTemplate(String templateCode, Map<String, Object> variables) {
        NotificationTemplate template = templateMapper.selectByCode(templateCode);
        if (template == null) {
            throw new RuntimeException("模板不存在: " + templateCode);
        }

        return renderTemplateInternal(template, variables);
    }

    @Override
    public TemplatePreviewVO renderTemplateByType(NotificationType type, NotificationChannel channel, Map<String, Object> variables) {
        if (channel == null) {
            channel = NotificationChannel.SITE;
        }

        NotificationTemplate template = templateMapper.selectByTypeAndChannel(type, channel);
        if (template == null) {
            log.warn("未找到通知类型 {} 渠道 {} 的模板", type, channel);
            return null;
        }

        return renderTemplateInternal(template, variables);
    }

    /**
     * 内部模板渲染方法
     */
    private TemplatePreviewVO renderTemplateInternal(NotificationTemplate template, Map<String, Object> variables) {
        TemplatePreviewVO result = new TemplatePreviewVO();

        // 渲染标题
        String title = TemplateRenderUtils.renderString(template.getTitleTemplate(), variables);
        result.setTitle(title);

        // 渲染内容
        String content = TemplateRenderUtils.renderString(template.getContentTemplate(), variables);
        result.setContent(content);

        // 渲染链接
        if (template.getActionUrlTemplate() != null && !template.getActionUrlTemplate().isEmpty()) {
            String actionUrl = TemplateRenderUtils.renderString(template.getActionUrlTemplate(), variables);
            result.setActionUrl(actionUrl);
        }

        result.setType(template.getType());

        return result;
    }
}
