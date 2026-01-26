package com.inkstage.enums;

import lombok.Getter;

@Getter
public enum VerificationStatus implements EnumCode {
    UNVERIFIED(0, "未验证"),
    VERIFIED(1, "已验证");

    private final Integer code;
    private final String desc;

    VerificationStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static VerificationStatus fromCode(Integer code) {
        for (VerificationStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}