package com.inkstage.dto.admin;

import com.inkstage.common.PageRequest;
import com.inkstage.enums.ReportStatus;
import com.inkstage.enums.ReportTargetType;
import com.inkstage.enums.ReportTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后台举报查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminReportQueryDTO extends PageRequest {

    /**
     * 举报状态
     */
    private ReportStatus reportStatus;

    /**
     * 举报类型
     */
    private ReportTypeEnum reportType;

    /**
     * 被举报对象类型
     */
    private ReportTargetType reportedType;

    /**
     * 举报人ID
     */
    private Long reporterId;

    /**
     * 被举报对象ID
     */
    private Long reportedId;

    /**
     * 处理人ID
     */
    private Long handlerId;
}
