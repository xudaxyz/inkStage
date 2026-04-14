package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.*;
import com.inkstage.enums.common.DefaultStatus;
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
     * 举报人昵称
     */
    private String reporterName;

    /**
     * 被举报对象类型
     */
    private ReportTargetType reportedType;

    /**
     * 相关对象ID(文章ID、评论ID、用户ID等)
     */
    private Long relatedId;

    /**
     * 被举报对象ID
     */
    private Long reportedId;

    /**
     * 被举报对象用户名
     */
    private String reportedName;

    /**
     * 被举报的内容
     */
    private String reportedContent;

    /**
     * 举报类型
     */
    private ReportTypeEnum reportType;

    /**
     * 举报理由
     */
    private String reason;

    /**
     * 举报证据(JSON格式, 包含图片、视频等链接)
     */
    private String evidence;

    /**
     * 是否匿名举报(0:否,1:是)
     */
    private DefaultStatus anonymous;

    /**
     * 举报状态
     */
    private ReportStatus reportStatus;

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
     * 处理人昵称
     */
    private String handlerName;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;
}