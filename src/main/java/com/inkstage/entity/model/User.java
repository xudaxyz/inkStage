package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.Gender;
import com.inkstage.enums.UserStatus;
import com.inkstage.enums.VerificationStatus;
import com.inkstage.enums.VisibleStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 加密后的密码, 用于身份验证
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 邮箱是否已验证（0:未验证,1:已验证）
     */
    private VerificationStatus emailVerified;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 手机号是否已验证（0:未验证,1:已验证）
     */
    private VerificationStatus phoneVerified;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 个人主页封面图
     */
    private String coverImage;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 性别（0:未知,1:男,2:女）
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
     * 个人网站
     */
    private String website;

    /**
     * 关注数
     */
    private Integer followCount;

    /**
     * 粉丝数
     */
    private Integer followerCount;

    /**
     * 文章数
     */
    private Integer articleCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 获赞数
     */
    private Integer likeCount;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 用户注册时的IP地址
     */
    private String registerIp;

    /**
     * 用户注册的时间戳
     */
    private LocalDateTime registerTime;

    /**
     * 隐私设置：0-公开, 1-私有, 2-仅关注者可见
     */
    private VisibleStatus privacy;

    /**
     * 状态（0:禁用,1:正常,2:待审核）
     */
    private UserStatus status;
}