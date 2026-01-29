package com.inkstage.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Gender implements EnumCode {
    /**
     * 未知
     */
    UNKNOWN(0, "UNKNOWN"),
    /**
     * 男
     */
    MALE(1, "MALE"),
    /**
     * 女
     */
    FEMALE(2, "FEMALE"),
    /**
     * 保密
     */
    SECRET(3, "SECRET");

    private final Integer code;
    private final String desc;

    Gender(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Gender fromCode(Integer code) {
        for (Gender gender : values()) {
            if (gender.code.equals(code)) {
                return gender;
            }
        }
        return null;
    }

    /**
     * 自定义反序列化方法，忽略大小写
     */
    @JsonCreator
    public static Gender fromString(String value) {
        if (value == null) {
            return null;
        }
        for (Gender gender : Gender.values()) {
            if (gender.name().equalsIgnoreCase(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Invalid Gender: " + value);
    }

    /**
     * 自定义序列化方法，返回枚举名称
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }

}