package com.inkstage.enums.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.inkstage.enums.EnumCode;
import lombok.Getter;

@Getter
public enum StatusEnum implements EnumCode {
    DISABLED(0,"禁用"),
    ENABLED(1,"启用");

    private final Integer code;
    private final String desc;

    StatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static StatusEnum fromCode(Integer code) {
        for (StatusEnum status : values()) {
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
    public static StatusEnum fromString(String value) {
        if (value == null) {
            return null;
        }
        for (StatusEnum status : StatusEnum.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        // 尝试从数字字符串转换
        try {
            int code = Integer.parseInt(value);
            return fromCode(code);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无效的 StatusEnum: " + value);
        }
    }
}
