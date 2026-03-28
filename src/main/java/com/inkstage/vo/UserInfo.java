package com.inkstage.vo;

import com.inkstage.enums.user.Gender;
import com.inkstage.enums.user.UserRoleEnum;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private String username;

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

    /**
     * 文章数
     */
    private Integer articleCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 粉丝数
     */
    private Integer followerCount;

    /**
     * 关注数
     */
    private Integer followCount;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 用户角色
     */
    private UserRoleEnum role;

    /**
     * 用户角色ID
     */
    private Integer roleId;
}
