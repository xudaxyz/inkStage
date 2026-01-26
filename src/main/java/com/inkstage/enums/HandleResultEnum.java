package com.inkstage.enums;

import lombok.Getter;

@Getter
public enum HandleResultEnum implements EnumCode {
    WARNING(0, "警告"),
    DELETE_CONTENT(1, "删除内容"),
    BAN_USER(2, "封禁账号"),
    OTHER(3, "其他");

    private final Integer code;
    private final String desc;

    HandleResultEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static HandleResultEnum fromCode(Integer code) {
        for (HandleResultEnum result : values()) {
            if (result.code.equals(code)) {
                return result;
            }
        }
        return null;
    }
}