package com.inkstage.vo.admin;

import com.inkstage.enums.VerificationStatus;
import com.inkstage.enums.VisibleStatus;
import com.inkstage.enums.user.Gender;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.enums.user.UserStatus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台用户详情VO
 */
@Data
public class AdminUserDetailVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 邮箱是否已验证
     */
    private VerificationStatus emailVerified;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 手机号是否已验证
     */
    private VerificationStatus phoneVerified;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 个人封面图
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
     * 注册IP
     */
    private String registerIp;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 隐私设置
     */
    private VisibleStatus privacy;

    /**
     * 用户状态
     */
    private UserStatus userStatus;

    /**
     * 用户角色
     */
    private UserRoleEnum userRole;

    /**
     * 最近发布的文章
     */
    private List<AdminUserArticleVO> recentArticles;

    /**
     * 最近发布的评论
     */
    private List<AdminUserCommentVO> recentComments;
}
