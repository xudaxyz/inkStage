package com.inkstage.enums;

import lombok.Getter;

@Getter
public enum PushedStatus implements EnumCode {
    PUSHED(0, "已推送"),
    NOT_PUSHED(1, "未推送");

    private final Integer code;
    private final String desc;

    PushedStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
