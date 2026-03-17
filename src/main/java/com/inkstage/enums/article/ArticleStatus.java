package com.inkstage.enums.article;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.inkstage.enums.EnumCode;
import lombok.Getter;

@Getter
public enum ArticleStatus implements EnumCode {
    ALL(0, "全部"),
    DRAFT(1, "草稿"),
    PENDING_PUBLISH(2, "待发布"),
    PUBLISHED(3, "已发布"),
    OFFLINE(4, "已下架"),
    RECYCLE(5, "回收站");

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
        if (value == null || value.isEmpty()) {
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