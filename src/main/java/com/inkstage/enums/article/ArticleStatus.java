package com.inkstage.enums.article;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.inkstage.enums.EnumCode;
import lombok.Getter;

@Getter
public enum ArticleStatus implements EnumCode {
    DRAFT(0, "草稿"),
    PENDING(1, "待审核"),
    PUBLISHED(2, "已发布"),
    OFFLINE(3, "已下架");

    private final Integer code;
    private final String desc;

    ArticleStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ArticleStatus fromCode(Integer code) {
        for (ArticleStatus status : values()) {
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
    public static ArticleStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        for (ArticleStatus status : ArticleStatus.values()) {
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