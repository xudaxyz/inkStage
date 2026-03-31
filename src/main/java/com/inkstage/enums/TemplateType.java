package com.inkstage.enums;

import lombok.Getter;

/**
 * 模板类型枚举
 */
@Getter
public enum TemplateType implements EnumCode {
    SYSTEM_NOTICE(0, "系统通知"),
    ACTIVITY_NOTICE(1, "活动通知"),
    IMPORTANT_UPDATE(2, "重要更新"),
    OTHER(3, "其他");
    private final Integer code;
    private final String desc;

    TemplateType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TemplateType fromCode(Integer code) {
        for (TemplateType status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

}
