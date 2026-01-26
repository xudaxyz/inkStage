package com.inkstage.enums;

import lombok.Getter;

@Getter
public enum ReadStatus implements EnumCode {
    UNREAD(0, "未读"),
    READ(1, "已读");

    private final Integer code;
    private final String desc;

    ReadStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReadStatus fromCode(Integer code) {
        for (ReadStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}