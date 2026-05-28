package com.inkstage.enums;

import lombok.Getter;

/**
 * 账号注销状态
 */
@Getter
public enum CancellationStatus implements EnumCode {
    PENDING(0, "待执行"),
    COMPLETED(1, "已完成"),
    CANCELLED(2, "已取消");

    private final Integer code;
    private final String desc;

    CancellationStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CancellationStatus fromCode(Integer code) {
        for (CancellationStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
