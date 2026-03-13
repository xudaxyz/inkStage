package com.inkstage.enums;

import lombok.Getter;

/**
 * 审核状态枚举类
 */

@Getter
public enum ReviewStatus implements EnumCode {
    All(0, "全部"),
    PENDING(1, "待审核"),
    APPROVED(2, "审核通过"),
    REJECTED(3, "审核拒绝"),
    APPEALING(4, "申诉中"),
    DISABLED(5, "禁用");

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
