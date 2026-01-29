package com.inkstage.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 认证操作类型枚举
 */
@Getter
public enum AuthOperationType implements EnumCode {
    LOGIN(1, "登录"),
    REGISTER(2, "注册");

    private final Integer code;
    private final String desc;

    AuthOperationType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 自定义反序列化方法，忽略大小写
     */
    @JsonCreator
    public static AuthOperationType fromString(String value) {
        if (value == null) {
            return null;
        }
        for (AuthOperationType authOperationType : AuthOperationType.values()) {
            if (authOperationType.name().equalsIgnoreCase(value)) {
                return authOperationType;
            }
        }
        throw new IllegalArgumentException("Invalid AuthOperationType: " + value);
    }

    /**
     * 自定义序列化方法，返回枚举名称
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }
}
