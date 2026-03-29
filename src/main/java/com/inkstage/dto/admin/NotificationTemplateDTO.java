package com.inkstage.dto.admin;

import com.inkstage.enums.NotificationChannel;
import com.inkstage.enums.NotificationType;
import com.inkstage.enums.Priority;
import com.inkstage.enums.StatusEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 通知模板创建DTO
 */
@Data
public class NotificationTemplateDTO {

    /**
     * 模板编码(唯一标识)
     */
    @NotBlank(message = "模板编码不能为空")
    private String code;

    /**
     * 模板名称
     */
    private String nameTemplate;

    /**
     * 通知标题模板
     */
    private String titleTemplate;

    /**
     * 通知内容模板
     */
    private String contentTemplate;

    /**
     * 通知类型
     */
    private NotificationType notificationType;

    /**
     * 通知渠道
     */
    private NotificationChannel notificationChannel;

    /**
     * 操作链接模板
     */
    private String actionUrlTemplate;

    /**
     * 模板变量定义
     */
    private String variables;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 优先级
     */
    private Priority priority;

    /**
     * 状态
     */
    private StatusEnum status;

}
