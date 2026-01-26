package com.inkstage.enums;

import lombok.Getter;

/**
 * 推荐类型枚举
 * CONTENT_BASED: 基于内容
 * USER_BEHAVIOR: 基于用户行为
 * HYBRID: 混合推荐
 */
@Getter
public enum RecommendType implements EnumCode {
    CONTENT_BASED(0, "基于内容"),
    USER_BEHAVIOR(1, "基于用户行为"),
    HYBRID(2, "混合推荐");

    private final Integer code;
    private final String desc;

    RecommendType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RecommendType fromCode(Integer code) {
        for (RecommendType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return CONTENT_BASED;
    }
}