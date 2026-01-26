package com.inkstage.enums;

import lombok.Getter;

/**
 * 搜索类型枚举
 * ALL: 全部
 * ARTICLE: 文章
 * USER: 用户
 * TAG: 标签
 */
@Getter
public enum SearchType implements EnumCode {
    ALL(0, "全部"),
    ARTICLE(1, "文章"),
    USER(2, "用户"),
    TAG(3, "标签");

    private final Integer code;
    private final String desc;

    SearchType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SearchType fromCode(Integer code) {
        for (SearchType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return ALL;
    }
}