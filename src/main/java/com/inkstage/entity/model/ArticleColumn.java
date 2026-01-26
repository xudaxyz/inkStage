package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 文章专栏关联表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleColumn extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 专栏ID
     */
    private Long columnId;

    /**
     * 文章在专栏内的排序顺序
     */
    private Integer sortOrder;
}