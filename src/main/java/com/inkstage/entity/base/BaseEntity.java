package com.inkstage.entity.base;

import com.inkstage.enums.DeleteStatus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体类基类，包含所有实体类的公共字段
 */
@Data
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否已删除（0:未删除,1:已删除）
     */
    private DeleteStatus deleted;

    /**
     * 删除时间
     */
    private LocalDateTime deletedTime;
}
