package com.inkstage.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 删除状态枚举
 * NOT_DELETED: 未删除
 * DELETED: 已删除
 */
@Getter
public enum DeleteStatus implements EnumCode {
    NOT_DELETED(0, "未删除"),
    DELETED(1, "已删除");

    private final Integer code;
    private final String desc;

    DeleteStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 自定义反序列化方法, 忽略大小写
     */
    @JsonCreator
    public static DeleteStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        for (DeleteStatus status : DeleteStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的 DeleteStatus: " + value);
    }

    /**
     * 自定义序列化方法, 返回枚举名称
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }

}
