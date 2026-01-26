package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.TemplateType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 短信模板实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SmsTemplate extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板编码（唯一）
     */
    private String code;

    /**
     * 模板类型（0:系统通知,1:活动通知,2:重要更新,3:其他）
     */
    private TemplateType type;

    /**
     * 短信内容
     */
    private String content;

    /**
     * 模板变量（JSON格式）
     */
    private String variables;

    /**
     * 状态（0:待审核,1:审核通过,2:审核不通过,3:禁用）
     */
    private ReviewStatus status;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 审核人ID
     */
    private Long auditUserId;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 创建人ID
     */
    private Long creatorId;
}