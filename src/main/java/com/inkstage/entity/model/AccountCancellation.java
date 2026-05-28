package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.CancellationStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 账号注销实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountCancellation extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 是否清除内容
     */
    private Boolean cleanContent;

    /**
     * 是否清除互动
     */
    private Boolean cleanInteraction;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 计划执行时间
     */
    private LocalDateTime scheduledTime;

    /**
     * 注销状态
     */
    private CancellationStatus status;
}
