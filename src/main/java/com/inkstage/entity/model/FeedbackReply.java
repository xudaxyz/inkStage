package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 反馈回复表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeedbackReply extends BaseEntity {

    /**
     * 反馈ID
     */
    private Long feedbackId;

    /**
     * 回复人ID
     */
    private Long userId;

    /**
     * 回复内容
     */
    private String content;
}