package com.inkstage.vo.front;

import com.inkstage.enums.user.Gender;
import com.inkstage.enums.user.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户公开资料VO
 */
@Data
public class UserPublicProfileVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 封面图URL
     */
    private String coverImage;

    /**
     * 个人签名
     */
    private String signature;

    /**
     * 性别
     */
    private Gender gender;

    /**
     * 所在地
     */
    private String location;

    /**
     * 个人网站
     */
    private String website;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 文章数量
     */
    private Integer articleCount;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 点赞数量
     */
    private Integer likeCount;

    /**
     * 关注数量
     */
    private Integer followCount;

    /**
     * 粉丝数量
     */
    private Integer followerCount;

    /**
     * 状态
     */
    private UserStatus status;

}
