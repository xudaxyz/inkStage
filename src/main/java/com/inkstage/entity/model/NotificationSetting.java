package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 通知设置实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationSetting extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    // ==================== 可关闭的通知设置 ====================
    // 【文章互动】
    /**
     * 是否接收文章发布通知
     */
    private Boolean articlePublishNotification;

    /**
     * 是否接收文章点赞通知
     */
    private Boolean articleLikeNotification;

    /**
     * 是否接收文章收藏通知
     */
    private Boolean articleCollectionNotification;

    /**
     * 是否接收文章评论通知
     */
    private Boolean articleCommentNotification;

    // 【评论互动】
    /**
     * 是否接收评论回复通知
     */
    private Boolean commentReplyNotification;

    /**
     * 是否接收评论点赞通知
     */
    private Boolean commentLikeNotification;

    // 【社交互动】
    /**
     * 是否接收关注通知
     */
    private Boolean followNotification;

    /**
     * 是否接收私信通知
     */
    private Boolean messageNotification;

    // 【系统通知】
    /**
     * 是否接收举报通知
     */
    private Boolean reportNotification;

    /**
     * 是否接收举报处理通知
     */
    private Boolean reportResultNotification;

    /**
     * 是否接收反馈处理通知
     */
    private Boolean feedbackNotification;

    /**
     * 是否接收系统通知
     */
    private Boolean systemNotification;

    // ==================== 通知渠道设置 ====================
    /**
     * 是否通过邮件接收通知
     */
    private Boolean emailNotification;

    /**
     * 是否通过站内信接收通知
     */
    private Boolean siteNotification;
}
