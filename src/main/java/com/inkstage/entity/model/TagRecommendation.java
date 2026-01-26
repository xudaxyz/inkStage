package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 标签推荐关系实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TagRecommendation extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 相关标签ID
     */
    private Long relatedTagId;

    /**
     * 相关度分数
     */
    private BigDecimal relevanceScore;
}