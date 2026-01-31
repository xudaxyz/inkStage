package com.inkstage.enums.user;

import com.inkstage.enums.EnumCode;
import lombok.Getter;

@Getter
public enum UserStatus implements EnumCode {
    DISABLED(0, "禁用"),
    NORMAL(1, "正常"),
    PENDING(2, "待审核");

    private final Integer code;
    private final String desc;

    UserStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserStatus fromCode(Integer code) {
        for (UserStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}