package com.inkstage.enums.auth;

import com.fasterxml.jackson.annotation.JsonValue;
import com.inkstage.enums.EnumCode;
import lombok.Getter;

/**
 * 认证类型枚举
 */
@Getter
public enum AuthType implements EnumCode {
    USERNAME(1, "用户名密码认证"),
    EMAIL(2, "邮箱认证"),
    PHONE(3, "手机号认证"),
    GITHUB(4, "GitHub认证"),
    QQ(5, "QQ认证"),
    WECHAT(6, "微信认证"),
    OTHER(99, "其他");

    private final Integer code;
    private final String desc;

    AuthType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 自定义序列化方法, 返回枚举名称
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }

    @Override
    public Integer getCode() {
        return this.code;
    }
}
