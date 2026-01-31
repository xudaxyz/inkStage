package com.inkstage.enums;

import lombok.Getter;

/**
 * 允许状态
 */
@Getter
public enum AllowStatus implements EnumCode {
    PROHIBITED(0, "禁止"),
    ALLOWED(1, "允许");

    private final Integer code;
    private final String desc;

    AllowStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
