package com.inkstage.enums;

import lombok.Getter;

@Getter
public enum ReportStatus implements EnumCode {
    PENDING(0, "待处理"),
    IN_PROGRESS(1, "处理中"),
    ACCEPTED(2, "已受理"),
    REJECTED(3, "已驳回"),
    CLOSED(4, "已结案");

    private final Integer code;
    private final String desc;

    ReportStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReportStatus fromCode(Integer code) {
        for (ReportStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}