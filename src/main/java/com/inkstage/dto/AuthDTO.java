package com.inkstage.dto;

import com.inkstage.enums.AuthOperationType;
import com.inkstage.enums.AuthType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 通用认证DTO（支持登录和注册）
 */
@Data
public class AuthDTO {
    /**
     * 操作类型：login（登录）/ register（注册）
     */
    @NotBlank(message = "操作类型不能为空")
    private AuthOperationType operationType;
    
    /**
     * 账号：用户名/邮箱/手机号
     */
    @NotBlank(message = "账号不能为空")
    @Size(min = 3, max = 50, message = "账号长度必须在3-50个字符之间")
    private String account;
    
    /**
     * 认证类型：password（密码认证）/ code（验证码认证）
     */
    @NotBlank(message = "认证类型不能为空")
    private AuthType authType;
    
    /**
     * 密码（认证类型为password时必填）
     */
    @Size(min = 6, max = 50, message = "密码长度必须在6-50个字符之间")
    private String password;
    
    /**
     * 验证码（认证类型为code时必填）
     */
    @Size(min = 4, max = 6, message = "验证码长度必须在4-6个字符之间")
    private String code;
    
    /**
     * 是否同意条款（操作类型为register时必填）
     */
    private boolean agreeTerms;
    
    /**
     * 客户端ID
     */
    @NotBlank(message = "客户端ID不能为空")
    private String clientId;
    
    /**
     * 客户端密钥
     */
    @NotBlank(message = "客户端密钥不能为空")
    private String clientSecret;
    
    /**
     * 权限范围
     */
    private String scope;
}
