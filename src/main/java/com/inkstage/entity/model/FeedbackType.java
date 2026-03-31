package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.common.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 反馈类型表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeedbackType extends BaseEntity {

    /**
     * 反馈类型名称
     */
    private String name;

    /**
     * 反馈类型编码
     */
    private Integer code;

    /**
     * 反馈类型描述
     */
    private String description;

    /**
     * 状态(0:禁用,1:正常)
     */
    private StatusEnum status;
}