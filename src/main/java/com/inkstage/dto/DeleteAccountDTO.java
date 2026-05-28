package com.inkstage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteAccountDTO {

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 是否清除内容
     */
    private Boolean cleanContent = false;

    /**
     * 是否清除互动数据
     */
    private Boolean cleanInteraction = false;
}
