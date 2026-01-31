package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.article.TopStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 父评论ID(0表示主评论)
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论楼层号
     */
    private String floor;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回复数
     */
    private Integer replyCount;

    /**
     * 状态(0:待审核,1:已通过,2:已拒绝)
     */
    private ReviewStatus status;

    /**
     * 是否置顶(0:否,1:是)
     */
    private TopStatus top;

    /**
     * 置顶顺序(数值越大, 优先级越高)
     */
    private Integer topOrder;

    /**
     * 审核人ID
     */
    private Long reviewUserId;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 审核拒绝原因
     */
    private String reviewReason;

    /**
     * 评论IP地址
     */
    private String ipAddress;

    /**
     * 用户代理信息
     */
    private String userAgent;

    /**
     * @ 提及的用户ID列表
     */
    private String mentionUserIds;

    /**
     * 被举报次数
     */
    private Integer reportCount;
}