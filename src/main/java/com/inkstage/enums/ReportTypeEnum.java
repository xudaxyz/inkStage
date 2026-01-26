package com.inkstage.enums;

import lombok.Getter;

@Getter
public enum ReportTypeEnum implements EnumCode {
    PORNOGRAPHY(1, "色情低俗"),
    VIOLENCE(2, "暴力恐怖"),
    POLITICAL(3, "政治敏感"),
    ADVERTISING(4, "广告营销"),
    COPYRIGHT(5, "抄袭侵权"),
    SPAM(6, "垃圾信息"),
    ABUSE(7, "骚扰辱骂"),
    FRAUD(8, "欺诈虚假"),
    OTHER(9, "其他");

    private final Integer code;
    private final String desc;

    ReportTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReportTypeEnum fromCode(Integer code) {
        for (ReportTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}