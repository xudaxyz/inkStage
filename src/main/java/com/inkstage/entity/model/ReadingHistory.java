package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 阅读历史实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReadingHistory extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 阅读进度（百分比）
     */
    private Integer progress;

    /**
     * 阅读时长（分钟）
     */
    private Integer duration;

    /**
     * 最后阅读时间
     */
    private LocalDateTime lastReadTime;

    /**
     * 滚动位置
     */
    private Integer scrollPosition;
}