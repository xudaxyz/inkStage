package com.inkstage.enums;

import lombok.Getter;

@Getter
public enum UserRoleEnum implements EnumCode {
    SUPER_ADMIN(0, "超级管理员"),
    ADMIN(1, "管理员"),
    USER(2, "普通用户");

    private final Integer code;
    private final String desc;

    UserRoleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserRoleEnum fromCode(Integer code) {
        for (UserRoleEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return USER;
    }

}
