package com.inkstage.enums;

import lombok.Getter;

/**
 * 优先级枚举类
 */
@Getter
public enum Priority implements EnumCode {
    LOW(3, "低"),
    NORMAL(2, "中"),
    HIGH(1, "高"),
    URGENT(0, "紧急");

    private final Integer code;
    private final String desc;

    Priority(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Priority fromCode(Integer code) {
        for (Priority priority : values()) {
            if (priority.code.equals(code)) {
                return priority;
            }
        }
        return null;
    }
}