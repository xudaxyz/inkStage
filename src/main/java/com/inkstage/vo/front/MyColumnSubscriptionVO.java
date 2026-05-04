package com.inkstage.vo.front;

import com.inkstage.enums.common.StatusEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 我的订阅专栏VO
 * 用于用户个人中心展示自己订阅的专栏
 */
@Data
public class MyColumnSubscriptionVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 专栏ID
     */
    private Long id;

    /**
     * 专栏名称
     */
    private String name;

    /**
     * 专栏别名（URL友好）
     */
    private String slug;

    /**
     * 专栏描述
     */
    private String description;

    /**
     * 专栏封面图URL
     */
    private String coverImage;

    /**
     * 专栏内文章数量
     */
    private Integer articleCount;

    /**
     * 专栏总阅读量
     */
    private Integer readCount;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 状态（0:禁用, 1:正常）
     */
    private StatusEnum status;

    /**
     * 创建者昵称
     */
    private String nickname;

    /**
     * 创建者头像
     */
    private String avatar;

    /**
     * 订阅时间
     */
    private LocalDateTime subscriptionTime;
}
