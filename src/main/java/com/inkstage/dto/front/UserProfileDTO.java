package com.inkstage.dto.front;

import com.inkstage.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileDTO {

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    /**
     * 性别
     */
    private Gender gender;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 位置
     */
    private String location;

    /**
     * 签名
     */
    private String signature;

    /**
     * 头像
     */
    private String coverImage;
}
