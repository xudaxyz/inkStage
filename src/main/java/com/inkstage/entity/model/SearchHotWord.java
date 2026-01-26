package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 搜索热词实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchHotWord extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 热词关键词
     */
    private String keyword;

    /**
     * 搜索次数
     */
    private Integer searchCount;

    /**
     * 热度分数
     */
    private BigDecimal hotScore;

    /**
     * 状态
     */
    private StatusEnum status;
}
