package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 专栏订阅实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ColumnSubscription extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订阅者ID
     */
    private Long userId;

    /**
     * 专栏ID
     */
    private Long columnId;

    /**
     * 订阅时间
     */
    private LocalDateTime subscriptionTime;
}
