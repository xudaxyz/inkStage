package com.inkstage.enums.common;

import com.inkstage.enums.EnumCode;
import lombok.Getter;

/**
 * 优先级枚举类
 */
@Getter
public enum Priority implements EnumCode {
    URGENT(0, "紧急"),
    HIGH(1, "高"),
    HIGH_NORMAL(2, "较高"),
    NORMAL(3, "中等"),
    LOW(4, "低"),
    VERY_LOW(5, "非常低");

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