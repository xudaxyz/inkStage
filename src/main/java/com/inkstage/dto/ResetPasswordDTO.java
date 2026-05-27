package com.inkstage.dto;

import com.inkstage.enums.auth.AuthType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 重置密码请求DTO（忘记密码）
 */
@Data
public class ResetPasswordDTO {

    /**
     * 账号：邮箱或手机号
     */
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * 认证类型：EMAIL或PHONE
     */
    private AuthType authType;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String newPassword;

    /**
     * 确认新密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
