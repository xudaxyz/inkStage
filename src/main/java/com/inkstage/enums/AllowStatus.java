package com.inkstage.enums;

import lombok.Getter;

/**
 * 允许状态
 */
@Getter
public enum AllowStatus implements EnumCode {
    NOT_ALLOWED(0, "不允许"),
    ALLOWED(1, "允许");

    private final Integer code;
    private final String desc;

    AllowStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
