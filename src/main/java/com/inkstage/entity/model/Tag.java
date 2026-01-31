package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 标签实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Tag extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 标签名称
     */
    private String name;
    
    /**
     * 标签别名(URL友好)
     */
    private String slug;
    
    /**
     * 标签描述
     */
    private String description;
    
    /**
     * 标签下文章数量
     */
    private Integer articleCount;
    
    /**
     * 标签使用次数
     */
    private Integer usageCount;
    
    /**
     * 状态(0:禁用,1:正常)
     */
    private StatusEnum status;
}