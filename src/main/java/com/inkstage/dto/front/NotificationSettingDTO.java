package com.inkstage.dto.front;

import com.inkstage.enums.notification.NotificationType;
import lombok.Data;

/**
 * 通知设置DTO
 */
@Data
public class NotificationSettingDTO {
    /**
     * 通知类型
     */
    private NotificationType notificationType;
    /**
     * 通知值
     */
    private Boolean notificationValue;
}
