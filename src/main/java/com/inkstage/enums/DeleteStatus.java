package com.inkstage.enums;

import lombok.Getter;

/**
 * 删除状态枚举
 * NOT_DELETED: 未删除
 * DELETED: 已删除
 */
@Getter
public enum DeleteStatus implements EnumCode{
    NOT_DELETED(0, "未删除"),
    DELETED(1, "已删除");

    private final Integer code;
    private final String desc;

    DeleteStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
