package com.inkstage.controller.admin;

import com.inkstage.annotation.AdminAccess;
import com.inkstage.common.PageResult;
import com.inkstage.common.Result;
import com.inkstage.dto.NotificationMessageDTO;
import com.inkstage.dto.admin.ManualNotificationDTO;
import com.inkstage.dto.admin.NotificationTemplateDTO;
import com.inkstage.dto.admin.NotificationTemplateQueryDTO;
import com.inkstage.entity.model.NotificationTemplate;
import com.inkstage.enums.StatusEnum;
import com.inkstage.service.AdminNotificationTemplateService;
import com.inkstage.service.UserService;
import com.inkstage.vo.TemplatePreviewVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.inkstage.config.rabbitmq.RabbitMQConfig.NOTIFICATION_EXCHANGE;
import static com.inkstage.config.rabbitmq.RabbitMQConfig.NOTIFICATION_ROUTING_KEY;

/**
 * 后台通知模板管理Controller
 */
@RestController
@RequestMapping("/admin/notification-templates")
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationTemplateController {

    private final AdminNotificationTemplateService templateService;
    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 创建通知模板
     */
    @PostMapping("/create")
    @AdminAccess
    public Result<Long> createTemplate(@Valid @RequestBody NotificationTemplateDTO dto) {
        NotificationTemplate template = convertToEntity(dto);
        boolean success = templateService.createTemplate(template);
        return success ? Result.success(template.getId()) : Result.error("创建失败");
    }

    /**
     * 更新通知模板
     */
    @PutMapping("/update/{id}")
    @AdminAccess
    public Result<Void> updateTemplate(@PathVariable Long id,
                                       @Valid @RequestBody NotificationTemplateDTO dto) {
        NotificationTemplate template = convertToEntity(dto);
        template.setId(id);
        boolean success = templateService.updateTemplate(template);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除通知模板
     */
    @DeleteMapping("/delete/{id}")
    @AdminAccess
    public Result<Void> deleteTemplate(@PathVariable Long id) {
        boolean success = templateService.deleteTemplate(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 获取模板详情
     */
    @GetMapping("/detail/{id}")
    @AdminAccess
    public Result<NotificationTemplate> getTemplate(@PathVariable Long id) {
        NotificationTemplate template = templateService.getTemplateById(id);
        return template != null ? Result.success(template) : Result.error("模板不存在");
    }

    /**
     * 根据编码获取模板
     */
    @GetMapping("/detail/code/{code}")
    @AdminAccess
    public Result<NotificationTemplate> getTemplateByCode(@PathVariable String code) {
        NotificationTemplate template = templateService.getTemplateByCode(code);
        return template != null ? Result.success(template) : Result.error("模板不存在");
    }

    /**
     * 分页查询模板列表
     */
    @GetMapping("/list")
    @AdminAccess
    public Result<PageResult<NotificationTemplate>> listTemplates(NotificationTemplateQueryDTO queryDTO) {
        PageResult<NotificationTemplate> result = templateService.getTemplatePage(
                queryDTO.getPageNum(),
                queryDTO.getPageSize(),
                queryDTO.getType(),
                queryDTO.getStatus(),
                queryDTO.getKeyword()
        );
        return Result.success(result);
    }

    /**
     * 获取所有模板
     */
    @GetMapping("/all")
    @AdminAccess
    public Result<List<NotificationTemplate>> getAllTemplates() {
        List<NotificationTemplate> list = templateService.getAllTemplates();
        return Result.success(list);
    }

    /**
     * 启用模板
     */
    @PutMapping("/enable/{id}")
    @AdminAccess
    public Result<Void> enableTemplate(@PathVariable Long id) {
        boolean success = templateService.updateTemplateStatus(id, StatusEnum.ENABLED);
        return success ? Result.success() : Result.error("启用失败");
    }

    /**
     * 禁用模板
     */
    @PutMapping("/disable/{id}")
    @AdminAccess
    public Result<Void> disableTemplate(@PathVariable Long id) {
        boolean success = templateService.updateTemplateStatus(id, StatusEnum.DISABLED);
        return success ? Result.success() : Result.error("禁用失败");
    }

    /**
     * 检查编码是否存在
     */
    @GetMapping("/check-code")
    @AdminAccess
    public Result<Boolean> checkCodeExists(@RequestParam String code) {
        boolean exists = templateService.isCodeExists(code);
        return Result.success(exists);
    }

    /**
     * 预览模板渲染效果
     */
    @PostMapping("/preview/{code}")
    @AdminAccess
    public Result<TemplatePreviewVO> previewTemplate(@PathVariable String code,
                                                     @RequestBody Map<String, Object> variables) {
        try {
            TemplatePreviewVO result = templateService.renderTemplate(code, variables);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("模板渲染失败: " + e.getMessage());
        }
    }

    /**
     * 手动发送通知
     */
    @PostMapping("/send")
    @AdminAccess
    public Result<Integer> sendNotification(@Valid @RequestBody ManualNotificationDTO dto) {
        try {
            List<Long> userIds = getUserIdsByType(dto);
            if (userIds.isEmpty()) {
                return Result.error("未找到符合条件的用户");
            }

            // todo 应该放在service层
            int successCount = 0;
            for (Long userId : userIds) {
                try {
                    TemplatePreviewVO rendered = templateService.renderTemplate(dto.getTemplateCode(), dto.getVariables());
                    if (rendered == null) {
                        continue;
                    }
                    // TODO 使用批量发送
                    NotificationMessageDTO message = new NotificationMessageDTO();
                    message.setUserId(userId);
                    message.setTitle(rendered.getTitle());
                    message.setContent(rendered.getContent());

                    message.setType(rendered.getType());
                    message.setRelatedId(dto.getRelatedId());
                    message.setSenderId(dto.getSenderId() != null ? dto.getSenderId() : 0L);
                    message.setActionUrl(rendered.getActionUrl());

                    rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, NOTIFICATION_ROUTING_KEY, message);
                    successCount++;
                } catch (Exception e) {
                    log.error("发送通知失败，用户ID: {}", userId, e);
                }
            }

            return Result.success(successCount);
        } catch (Exception e) {
            log.error("手动发送通知失败", e);
            return Result.error("发送失败: " + e.getMessage());
        }
    }

    /**
     * 将创建DTO转换为实体
     */
    private NotificationTemplate convertToEntity(NotificationTemplateDTO dto) {
        NotificationTemplate template = new NotificationTemplate();
        template.setCode(dto.getCode());
        template.setName(dto.getName());
        template.setTitleTemplate(dto.getTitleTemplate());
        template.setContentTemplate(dto.getContentTemplate());
        template.setType(dto.getType());
        template.setChannel(dto.getChannel());
        template.setActionUrlTemplate(dto.getActionUrlTemplate());
        template.setVariables(dto.getVariables());
        template.setDescription(dto.getDescription());
        template.setPriority(dto.getPriority());
        return template;
    }

    /**
     * 根据用户类型获取用户ID列表
     */
    private List<Long> getUserIdsByType(ManualNotificationDTO dto) {
        return switch (dto.getUserType()) {
            case "all" -> userService.getAllUserIds();
            case "specific" -> dto.getUserIds() != null ? dto.getUserIds() : new ArrayList<>();
            case "role" -> userService.getUserIdsByRoleCode(dto.getRoleCode());
            default -> new ArrayList<>();
        };
    }
}
