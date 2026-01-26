package com.inkstage.enums;

import lombok.Getter;

@Getter
public enum FeedbackStatus implements EnumCode {
    PENDING(0, "待处理"),
    IN_PROGRESS(1, "处理中"),
    RESOLVED(2, "已解决"),
    REJECTED(3, "已驳回"),
    CLOSED(4, "已关闭");

    private final Integer code;
    private final String desc;

    FeedbackStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FeedbackStatus fromCode(Integer code) {
        for (FeedbackStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

}