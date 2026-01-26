package com.inkstage.enums;

import lombok.Getter;

/**
 * 审核状态枚举类
 */

@Getter
public enum ReviewStatus implements EnumCode {
    PENDING(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已拒绝"),
    DISABLED(3, "禁用");

    private final Integer code;
    private final String desc;

    ReviewStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReviewStatus fromCode(Integer code) {
        for (ReviewStatus type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }


}
