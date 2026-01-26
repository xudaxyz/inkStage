package com.inkstage.enums;

import lombok.Getter;

@Getter
public enum StatusEnum implements EnumCode {
    DISABLED(0,"禁用"),
    ENABLED(1,"启用");

    private final Integer code;
    private final String desc;

    StatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static StatusEnum fromCode(Integer code) {
        for (StatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
