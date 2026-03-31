package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.AnnouncementType;
import com.inkstage.enums.common.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 系统公告表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemAnnouncement extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告类型(0:系统公告,1:活动通知,2:维护通知)
     */
    private AnnouncementType type;

    /**
     * 状态(0:未发布,1:已发布,2:已过期)
     */
    private StatusEnum status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 阅读量
     */
    private Integer readCount;

    /**
     * 创建人ID
     */
    private Long createUserId;

    /**
     * 更新人ID
     */
    private Long updateUserId;
}