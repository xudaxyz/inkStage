package com.inkstage.vo;

import lombok.Data;

/**
 * OAuth2令牌响应
 */
@Data
public class TokenResponse {

    /**
     * 访问令牌
     */
    private String access_token;

    /**
     * 令牌类型
     */
    private String token_type;

    /**
     * 刷新令牌
     */
    private String refresh_token;

    /**
     * 过期时间（秒）
     */
    private Integer expires_in;

    /**
     * 权限范围
     */
    private String scope;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

}