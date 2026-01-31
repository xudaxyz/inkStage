package com.inkstage.enums;

import lombok.Getter;

/**
 * 公告类型
 * 0:系统公告,1:活动通知,2:维护通知)
 */
@Getter
public enum AnnouncementType implements EnumCode {
    SYSTEM_NOTICE(0, "系统公告"),
    ACTIVITY_NOTICE(1, "活动通知"),
    MAINTENANCE_NOTICE(2, "维护通知");

    private final Integer code;
    private final String desc;

    AnnouncementType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
