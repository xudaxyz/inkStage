package com.inkstage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * OAuth2注册请求DTO
 */
@Data
public class OAuth2RegisterDTO {

    /**
     * 授权类型
     */
    @NotBlank(message = "授权类型不能为空")
    private String grant_type;

    /**
     * 用户名/邮箱/手机号
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50个字符之间")
    private String password;

    /**
     * 客户端ID
     */
    @NotBlank(message = "客户端ID不能为空")
    private String client_id;

    /**
     * 客户端密钥
     */
    @NotBlank(message = "客户端密钥不能为空")
    private String client_secret;

    /**
     * 权限范围
     */
    private String scope;

    /**
     * 注册类型
     */
    private String register_type;

    /**
     * 是否同意条款
     */
    private boolean agree_terms;
}