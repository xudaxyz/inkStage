package com.inkstage.controller.admin;

import com.inkstage.annotation.AdminAccess;
import com.inkstage.common.PageResult;
import com.inkstage.common.Result;
import com.inkstage.dto.admin.ManualNotificationDTO;
import com.inkstage.dto.admin.NotificationTemplateDTO;
import com.inkstage.dto.admin.NotificationTemplateQueryDTO;
import com.inkstage.entity.model.NotificationTemplate;
import com.inkstage.enums.StatusEnum;
import com.inkstage.service.AdminNotificationTemplateService;
import com.inkstage.vo.TemplatePreviewVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台通知模板管理Controller
 */
@RestController
@RequestMapping("/admin/notification-templates")
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationTemplateController {

    private final AdminNotificationTemplateService templateService;

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
    public Result<PageResult<NotificationTemplate>> listTemplates(NotificationTemplateQueryDTO notificationTemplateQuery) {
        PageResult<NotificationTemplate> result = templateService.getTemplatePage(notificationTemplateQuery);
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
                                                     @RequestBody String variables) {
        TemplatePreviewVO result = templateService.renderTemplate(code, variables);
        if (result == null) {
            return Result.error("模板渲染失败");
        }
        return Result.success(result);

    }

    /**
     * 手动发送通知
     */
    @PostMapping("/send")
    @AdminAccess
    public Result<Integer> sendNotification(@Valid @RequestBody ManualNotificationDTO manualNotification) {
        int successCount = templateService.sendNotification(manualNotification);

        if (successCount == 0) {
            return Result.error("未找到符合条件的用户或发送失败");
        }
        return Result.success(successCount);
    }

    /**
     * 将创建DTO转换为实体
     */
    private NotificationTemplate convertToEntity(NotificationTemplateDTO dto) {
        NotificationTemplate template = new NotificationTemplate();
        template.setCode(dto.getCode());
        template.setNameTemplate(dto.getNameTemplate());
        template.setTitleTemplate(dto.getTitleTemplate());
        template.setContentTemplate(dto.getContentTemplate());
        template.setNotificationType(dto.getNotificationType());
        template.setNotificationChannel(dto.getNotificationChannel());
        template.setActionUrlTemplate(dto.getActionUrlTemplate());
        template.setVariables(dto.getVariables());
        template.setDescription(dto.getDescription());
        template.setPriority(dto.getPriority());
        return template;
    }

}

