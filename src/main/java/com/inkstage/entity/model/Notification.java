package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.*;
import com.inkstage.enums.PushedStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 通知实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Notification extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 接收通知的用户ID
     */
    private Long userId;

    /**
     * 通知类型
     */
    private NotificationType type;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 是否已读
     */
    private ReadStatus read;

    /**
     * 已读时间
     */
    private String readTime;

    /**
     * 关联ID(如文章ID、评论ID、私信ID等)
     */
    private Long relatedId;

    /**
     * 关联类型
     */
    private ReportTargetType relatedType;

    /**
     * 发送通知的用户ID(系统通知为0)
     */
    private Long senderId;

    /**
     * 是否已推送
     */
    private PushedStatus pushed;

    /**
     * 推送时间
     */
    private String pushTime;

    /**
     * 通知优先级
     */
    private Priority priority;

    /**
     * 操作链接(用于直接跳转到相关内容)
     */
    private String actionUrl;

    /**
     * 额外数据(用于存储通知相关的扩展信息)
     */
    private String extraData;
}
