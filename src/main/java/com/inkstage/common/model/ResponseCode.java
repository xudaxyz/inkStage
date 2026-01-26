package com.inkstage.common.model;

import lombok.Getter;

/**
 * 响应状态码枚举
 * 遵循restful API设计规范，采用HTTP状态码作为基础
 */
@Getter
public enum ResponseCode {
    SUCCESS(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    INTERNAL_SERVER_ERROR(500),
    BUSINESS_ERROR(500),
    ERROR(500),
    ;

    private final int code;

    ResponseCode(int code) {
        this.code = code;
    }
}
