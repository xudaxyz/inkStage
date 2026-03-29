package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 分类实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类别名(URL友好)
     */
    private String slug;
    
    /**
     * 父分类ID(0表示顶级分类)
     */
    private Long parentId;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 分类描述
     */
    private String description;
    
    /**
     * 分类下文章数量
     */
    private Integer articleCount;
    
    /**
     * 状态(0:禁用,1:正常)
     */
    private StatusEnum status;

    /**
     * 分类版本号，用于缓存控制
     */
    private Integer categoryVersion = 1;
}