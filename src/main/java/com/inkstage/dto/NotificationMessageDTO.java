package com.inkstage.dto;

import com.inkstage.enums.notification.NotificationType;
import com.inkstage.enums.ReportTargetType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通知消息DTO
 */
@Data
public class NotificationMessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 接收通知的用户ID
     */
    private Long userId;

    /**
     * 通知类型
     */
    private NotificationType notificationType;

    /**
     * 通知内容
     */
    private String content;

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
     * 操作链接(用于直接跳转到相关内容)
     */
    private String actionUrl;

    /**
     * 额外数据(用于存储通知相关的扩展信息)
     */
    private String extraData;

    /**
     * 通知标题
     */
    private String title;
}
