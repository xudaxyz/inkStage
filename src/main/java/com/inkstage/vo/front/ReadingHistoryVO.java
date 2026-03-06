package com.inkstage.vo.front;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 阅读历史VO
 */
@Data
public class ReadingHistoryVO {

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章封面图
     */
    private String coverImage;

    /**
     * 作者ID
     */
    private Long userId;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 作者头像
     */
    private String avatar;

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

    /**
     * 阅读时间（格式化显示）
     */
    private String readTime;

    /**
     * 阅读日期（格式化显示）
     */
    private String readDate;

}