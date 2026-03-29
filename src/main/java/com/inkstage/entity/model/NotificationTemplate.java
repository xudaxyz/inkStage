package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.NotificationChannel;
import com.inkstage.enums.NotificationType;
import com.inkstage.enums.Priority;
import com.inkstage.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 通知模板实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationTemplate extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板编码(唯一标识)
     */
    private String code;

    /**
     * 模板名称
     */
    private String nameTemplate;

    /**
     * 通知标题模板(支持占位符如: {{username}})
     */
    private String titleTemplate;

    /**
     * 通知内容模板(支持占位符)
     */
    private String contentTemplate;

    /**
     * 通知类型(对应NotificationType的code)
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
     * 模板变量定义(描述各占位符含义)
     */
    private String variables;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 优先级(0:普通, 1:重要, 2:紧急)
     */
    private Priority priority;

    /**
     * 状态(0:禁用, 1:启用)
     */
    private StatusEnum status;

    /**
     * 创建人ID
     */
    private Long createUserId;

    /**
     * 更新人ID
     */
    private Long updateUserId;
}
