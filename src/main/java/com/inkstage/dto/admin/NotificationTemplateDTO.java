package com.inkstage.dto.admin;

import com.inkstage.enums.notification.NotificationChannel;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.enums.common.Priority;
import com.inkstage.enums.common.StatusEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通知模板创建DTO
 */
@Data
public class NotificationTemplateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板编码(唯一标识)
     */
    @NotBlank(message = "模板编码不能为空")
    private String code;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 通知类型
     */
    private NotificationType notificationType;

    /**
     * 通知渠道
     */
    private NotificationChannel notificationChannel;

    /**
     * 通知标题模板
     */
    private String titleTemplate;

    /**
     * 通知内容模板
     */
    private String contentTemplate;

    /**
     * 操作链接模板
     */
    private String actionUrlTemplate;

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

    /**
     * 额外数据
     */
    private String extraData;

}
