package com.inkstage.enums;

import lombok.Getter;

@Getter
public enum ArticleStatus implements EnumCode {
    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    PENDING(2, "待审核"),
    OFFLINE(3, "已下架");

    private final Integer code;
    private final String desc;

    ArticleStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ArticleStatus fromCode(Integer code) {
        for (ArticleStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

}