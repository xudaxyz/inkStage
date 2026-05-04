package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.VisibleStatus;
import com.inkstage.enums.common.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 专栏实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Column extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 专栏创建者ID
     */
    private Long userId;

    /**
     * 专栏名称
     */
    private String name;

    /**
     * 专栏别名(URL友好)
     */
    private String slug;

    /**
     * 专栏描述
     */
    private String description;

    /**
     * 专栏封面图URL
     */
    private String coverImage;

    /**
     * 专栏内文章数量
     */
    private Integer articleCount;

    /**
     * 专栏订阅数
     */
    private Integer subscriptionCount;

    /**
     * 专栏总阅读量
     */
    private Integer readCount;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 是否可见
     */
    private VisibleStatus visible;

    /**
     * 状态(0:禁用,1:正常)
     */
    private StatusEnum status;
}