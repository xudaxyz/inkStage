package com.inkstage.enums;

import lombok.Getter;

@Getter
public enum TopStatus implements EnumCode{
    NOT_TOP(0, "未置顶"),
    TOP(1, "已置顶");

    private final Integer code;
    private final String desc;

    TopStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
