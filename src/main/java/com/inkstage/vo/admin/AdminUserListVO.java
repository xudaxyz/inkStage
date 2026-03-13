package com.inkstage.vo.admin;

import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.enums.user.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台用户列表VO
 */
@Data
public class AdminUserListVO {

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
     * 手机号
     */
    private String phone;

    /**
     * 用户角色
     */
    private UserRoleEnum role;

    /**
     * 用户状态
     */
    private UserStatus status;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 文章数
     */
    private Integer articleCount;

    /**
     * 评论数
     */
    private Integer commentCount;
}
