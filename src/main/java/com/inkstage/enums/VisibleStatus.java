package com.inkstage.enums;

import lombok.Getter;

/**
 * 可见状态
 */
@Getter
public enum VisibleStatus implements EnumCode {
    PRIVATE(0, "私有"),
    PUBLIC(1, "公开"),
    FOLLOWERS_ONLY(2, "仅粉丝可见");

    private final Integer code;
    private final String desc;

    VisibleStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
