package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 举报实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Report extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 举报人ID
     */
    private Long reporterId;

    /**
     * 被举报对象类型
     */
    private ReportTargetType reportedType;

    /**
     * 被举报对象ID
     */
    private Long reportedId;

    /**
     * 举报类型
     */
    private ReportTypeEnum reportType;

    /**
     * 举报理由
     */
    private String reason;

    /**
     * 举报证据（JSON格式，包含图片、视频等链接）
     */
    private String evidence;

    /**
     * 是否匿名举报（0:否,1:是）
     */
    private DefaultStatus anonymous;

    /**
     * 举报状态
     */
    private ReportStatus status;

    /**
     * 处理结果
     */
    private HandleResultEnum handleResult;

    /**
     * 处理理由
     */
    private String handleReason;

    /**
     * 处理人ID
     */
    private Long handlerId;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;
}