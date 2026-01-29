package com.inkstage.vo;

import com.inkstage.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

/**
 * 用户信息
 */
@Data
public class UserInfo {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 个人主页封面图
     */
    private String coverImage;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 性别
     */
    private Gender gender;

    /**
     * 生日
     */
    private LocalDate birthDate;

    /**
     * 所在地区
     */
    private String location;
}
