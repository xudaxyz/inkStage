package com.inkstage.enums;

import lombok.Getter;

@Getter
public enum OriginalStatus {
    REPRINT(0, "转载"),
    ORIGINAL(1, "原创");

    private final Integer code;
    private final String desc;

    OriginalStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
