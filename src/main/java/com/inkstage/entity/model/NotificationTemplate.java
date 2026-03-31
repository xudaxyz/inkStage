package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.notification.NotificationChannel;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.enums.common.Priority;
import com.inkstage.enums.common.StatusEnum;
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
     * 通知标题模板(支持占位符如: {{username}})
     */
    private String titleTemplate;

    /**
     * 通知内容模板(支持占位符)
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
     * 状态(0:禁用, 1:启用)
     */
    private StatusEnum status;

    /**
     * 其他数据
     */
    private String extraData;

    /**
     * 创建人ID
     */
    private Long createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 更新人ID
     */
    private Long updateUserId;

    /**
     * 更新人名称
     */
    private String updateUserName;
}
