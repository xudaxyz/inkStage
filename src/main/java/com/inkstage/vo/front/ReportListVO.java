package com.inkstage.vo.front;

import com.inkstage.enums.HandleResultEnum;
import com.inkstage.enums.ReportStatus;
import com.inkstage.enums.ReportTargetType;
import com.inkstage.enums.ReportTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 举报列表VO
 */
@Data
public class ReportListVO {

    /**
     * 举报ID
     */
    private Long id;

    /**
     * 举报人ID
     */
    private Long reporterId;
    /**
     * 举报人名称
     */
    private String reporterName;

    /**
     * 被举报对象类型
     */
    private ReportTargetType reportedType;

    /**
     * 被举报对象ID
     */
    private Long reportedId;

    /**
     * 被举报对象名称
     */
    private String reportedName;

    /**
     * 相关对象ID
     */
    private Long relatedId;

    /**
     * 被举报内容
     */
    private String reportedContent;

    /**
     * 举报类型
     */
    private ReportTypeEnum reportType;

    /**
     * 举报状态
     */
    private ReportStatus reportStatus;

    /**
     * 举报时间
     */
    private LocalDateTime createTime;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 处理结果
     */
    private HandleResultEnum handleResult;
}
