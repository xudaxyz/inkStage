package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.dto.admin.AdminReportQueryDTO;
import com.inkstage.dto.admin.ReportHandleDTO;
import com.inkstage.dto.front.ReportCreateDTO;
import com.inkstage.entity.model.Report;
import com.inkstage.enums.ReportStatus;
import com.inkstage.enums.ReportTargetType;
import com.inkstage.enums.ReportTypeEnum;
import com.inkstage.vo.front.ReportListVO;

/**
 * 举报服务接口
 */
public interface ReportService {

    /**
     * 创建举报
     *
     * @param reportCreateDTO 举报创建DTO
     * @return 举报ID
     */
    Long createReport(ReportCreateDTO reportCreateDTO);

    /**
     * 获取举报详情
     *
     * @param reportId 举报ID
     * @return 举报详情
     */
    Report getReportById(Long reportId);

    /**
     * 获取用户的举报列表
     *
     * @param reportStatus 举报状态
     * @param pageNum      当前页码
     * @param pageSize     每页大小
     * @return 举报列表
     */
    PageResult<ReportListVO> getReportList(ReportStatus reportStatus, int pageNum, int pageSize);

    /**
     * 获取后台举报列表
     *
     * @param adminReportQueryDTO 后台举报查询DTO
     * @return 举报列表
     */
    PageResult<ReportListVO> getAdminReportList(AdminReportQueryDTO adminReportQueryDTO);

    /**
     * 处理举报
     *
     * @param reportId        举报ID
     * @param reportHandleDTO 举报处理DTO
     * @param handlerId       处理人ID
     */
    void handleReport(Long reportId, ReportHandleDTO reportHandleDTO, Long handlerId);

    /**
     * 检查重复举报
     * @param reporterId 举报人ID
     * @param reportedId 被举报对象ID
     * @param relatedId  相关对象ID
     * @param reportedType 被举报对象类型
     * @param reportType   举报类型
     * @return 重复举报数量
     */
    int checkDuplicateReport(Long reporterId, Long reportedId, Long relatedId, ReportTargetType reportedType, ReportTypeEnum reportType);
}
