package com.inkstage.dto.admin;

import com.inkstage.common.PageRequest;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.enums.common.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知模板查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationTemplateQueryDTO extends PageRequest {

    /**
     * 通知类型
     */
    private NotificationType notificationType;

    /**
     * 状态
     */
    private StatusEnum status;

    /**
     * 关键词(搜索名称或编码)
     */
    private String keyword;
}
