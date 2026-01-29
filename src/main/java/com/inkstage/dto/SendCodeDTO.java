package com.inkstage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发送验证码请求DTO
 */
@Data
public class SendCodeDTO {

    /**
     * 账号: 邮箱或手机号
     */
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * 验证码类型：email或phone
     */
    @NotBlank(message = "验证码类型不能为空")
    private String type;

    /**
     * 验证码用途：register、login、reset_password等
     */
    @NotBlank(message = "验证码用途不能为空")
    private String purpose;
}
