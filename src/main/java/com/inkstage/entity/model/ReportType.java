package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 举报类型实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportType extends BaseEntity {
    
    /**
     * 举报类型名称
     */
    private String name;
    
    /**
     * 举报类型编码(唯一)
     */
    private String code;
    
    /**
     * 优先级
     */
    private Priority priority;
    
    /**
     * 状态
     */
    private StatusEnum status;
    
    /**
     * 适用对象类型
     */
    private ReportTargetType targetType;
    
    /**
     * 举报类型描述
     */
    private String description;
}