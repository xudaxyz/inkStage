package com.inkstage.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 收藏状态枚举
 */
@Getter
public enum CollectionStatus implements EnumCode {
    PUBLIC(0, "公开"),
    PRIVATE(1, "私密");

    private final Integer code;
    private final String desc;

    CollectionStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CollectionStatus fromCode(Integer code) {
        for (CollectionStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 自定义反序列化方法, 忽略大小写
     */
    @JsonCreator
    public static CollectionStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        for (CollectionStatus status : CollectionStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的 ArticleStatus: " + value);
    }

    /**
     * 自定义序列化方法, 返回枚举名称
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }
}
