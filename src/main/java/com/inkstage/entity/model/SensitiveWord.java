package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.common.Priority;
import com.inkstage.enums.common.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 敏感词表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SensitiveWord extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 敏感词
     */
    private String word;

    /**
     * 敏感词分类
     */
    private String category;

    /**
     * 敏感级别
     */
    private Priority level;

    /**
     * 状态(0:禁用,1:正常)
     */
    private StatusEnum status;
}