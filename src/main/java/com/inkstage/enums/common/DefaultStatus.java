package com.inkstage.enums.common;

import com.inkstage.enums.EnumCode;
import lombok.Getter;

/**
 * 是否默认状态枚举
 * YES: 是
 * NO: 否
 */
@Getter
public enum DefaultStatus implements EnumCode {
    YES(1, "是"),
    NO(0, "否");

    private final Integer code;
    private final String desc;

    DefaultStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
