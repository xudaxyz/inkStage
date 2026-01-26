package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.MessageType;
import com.inkstage.enums.ReadStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 私信实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Message extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 私信内容
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
     * 消息类型
     */
    private MessageType type;

    /**
     * 附件URL（图片或文件）
     */
    private String attachmentUrl;

    /**
     * 对话ID，由sender_id和receiver_id生成的唯一标识
     */
    private String conversationId;

    /**
     * 对话内消息序号，用于保证消息顺序
     */
    private Long sequenceId;
}
