package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 文章阅读统计表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleReadStat extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 用户ID(未登录用户为NULL)
     */
    private Long userId;

    /**
     * 阅读IP地址
     */
    private String ipAddress;

    /**
     * 用户代理信息
     */
    private String userAgent;

    /**
     * 阅读时间
     */
    private LocalDateTime viewTime;

    /**
     * 阅读时长(秒)
     */
    private Integer readDuration;

    /**
     * 是否完整阅读(0:否,1:是)
     */
    private StatusEnum complete;
}