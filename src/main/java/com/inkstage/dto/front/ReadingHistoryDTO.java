package com.inkstage.dto.front;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 阅读历史DTO
 */
@Data
public class ReadingHistoryDTO {

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
     * 滚动位置
     */
    private Integer scrollPosition;

}