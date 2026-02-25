package com.inkstage.vo.front;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 热门用户VO
 */
@Data
public class HotUserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 文章数量
     */
    private Integer articleCount;
    /**
     * 粉丝数量
     */
    private Integer followerCount;
    /**
     * 点赞数量
     */
    private Integer likeCount;
}
