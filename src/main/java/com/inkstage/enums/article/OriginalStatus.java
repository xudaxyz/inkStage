package com.inkstage.enums.article;

import com.inkstage.enums.EnumCode;
import lombok.Getter;

@Getter
public enum OriginalStatus implements EnumCode {
    REPRINT(0, "转载"),
    ORIGINAL(1, "原创"),
    OTHER(2, "其他");

    private final Integer code;
    private final String desc;

    OriginalStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
