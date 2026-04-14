package com.inkstage.enums;

import lombok.Getter;

@Getter
public enum ReportTargetType implements EnumCode {
    ARTICLE(1, "文章"),
    COMMENT(2, "评论"),
    USER(3, "用户"),
    SYSTEM(4, "系统"),
    OTHER(100, "其他");

    private final Integer code;
    private final String desc;

    ReportTargetType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReportTargetType fromCode(Integer code) {
        for (ReportTargetType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}