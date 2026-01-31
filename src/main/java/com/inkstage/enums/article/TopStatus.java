package com.inkstage.enums.article;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.inkstage.enums.EnumCode;
import lombok.Getter;

@Getter
public enum TopStatus implements EnumCode {
    NOT_TOP(0, "未置顶"),
    TOP(1, "已置顶");

    private final Integer code;
    private final String desc;

    TopStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 自定义反序列化方法, 忽略大小写
     */
    @JsonCreator
    public static TopStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        for (TopStatus status : TopStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的 TopStatus: " + value);
    }

    /**
     * 自定义序列化方法, 返回枚举名称
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }
}
