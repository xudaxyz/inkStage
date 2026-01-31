package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.DefaultStatus;
import com.inkstage.enums.FeedbackStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 反馈表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Feedback extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 反馈人ID
     */
    private Long userId;

    /**
     * 反馈类型ID (参考反馈类型表)
     */
    private Integer feedbackTypeId;

    /**
     * 反馈标题
     */
    private String title;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 反馈证据URL列表
     */
    private String evidenceUrls;

    /**
     * 是否匿名(0:否,1:是)
     */
    private DefaultStatus anonymous;

    /**
     * 反馈状态(0:待处理,1:处理中,2:已解决,3:已驳回,4:已关闭)
     */
    private FeedbackStatus status;

    /**
     * 处理人ID
     */
    private Long handleUserId;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;
}