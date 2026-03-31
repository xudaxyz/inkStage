package com.inkstage.enums.notification;

import lombok.Getter;

/**
 * 通知状态枚举
 */
@Getter
public enum NotificationStatus {
    PENDING("待处理"),
    SENT("已发送"),
    READ("已读"),
    FAILED("发送失败");

    private final String description;

    NotificationStatus(String description) {
        this.description = description;
    }
}