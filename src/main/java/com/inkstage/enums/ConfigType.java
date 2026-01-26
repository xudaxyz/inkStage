package com.inkstage.enums;

import lombok.Getter;

/**
 * 配置类型枚举
 * SYSTEM: 系统配置
 * ALGORITHM: 算法配置
 * UPDATE: 更新配置
 */
@Getter
public enum ConfigType implements EnumCode {
    SYSTEM(0, "系统配置"),
    ALGORITHM(1, "算法配置"),
    UPDATE(2, "更新配置");

    private final Integer code;
    private final String desc;

    ConfigType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ConfigType fromCode(Integer code) {
        for (ConfigType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return SYSTEM;
    }

}