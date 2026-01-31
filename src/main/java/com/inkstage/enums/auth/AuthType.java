package com.inkstage.enums.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.inkstage.enums.EnumCode;
import lombok.Getter;

/**
 * 认证类型枚举
 */
@Getter
public enum AuthType implements EnumCode {
    PASSWORD(1, "密码认证"),
    CODE(2, "验证码认证");

    private final Integer code;
    private final String desc;

    AuthType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 自定义反序列化方法, 忽略大小写
     */
    @JsonCreator
    public static AuthType fromString(String value) {
        if (value == null) {
            return null;
        }
        for (AuthType authType : AuthType.values()) {
            if (authType.name().equalsIgnoreCase(value)) {
                return authType;
            }
        }
        throw new IllegalArgumentException("Invalid AuthType: " + value);
    }

    /**
     * 自定义序列化方法, 返回枚举名称
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }
}
