package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.StatusEnum;
import com.inkstage.enums.TemplateType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 邮件模板实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EmailTemplate extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板编码(唯一)
     */
    private String code;

    /**
     * 模板类型(0:系统通知,1:活动通知,2:重要更新,3:其他)
     */
    private TemplateType type;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件内容(支持HTML)
     */
    private String content;

    /**
     * 模板变量(JSON格式)
     */
    private String variables;

    /**
     * 状态(0:禁用,1:启用)
     */
    private StatusEnum status;

    /**
     * 创建人ID
     */
    private Long creatorId;
}