package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.dto.admin.AdminReportQueryDTO;
import com.inkstage.dto.admin.ReportHandleDTO;
import com.inkstage.dto.front.ReportCreateDTO;
import com.inkstage.entity.model.Report;
import com.inkstage.entity.model.User;
import com.inkstage.enums.*;
import com.inkstage.enums.common.DefaultStatus;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.enums.notification.NotificationTemplateVariable;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ReportMapper;
import com.inkstage.service.NotificationService;
import com.inkstage.service.ReportService;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.ReportListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 举报服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReport(ReportCreateDTO reportCreateDTO) {
        // 检查是否重复举报
        User currentUser = UserContext.getCurrentUser();
        int hasReported = checkDuplicateReport(currentUser.getId(), reportCreateDTO.getReportedId(), reportCreateDTO.getRelatedId(), reportCreateDTO.getReportedType(), reportCreateDTO.getReportType());
        if (hasReported > 0) {
            throw new BusinessException(ResponseMessage.ALREADY_REPORTED);
        }

        // 创建举报对象
        Report report = new Report();
        report.setReporterId(currentUser.getId());
        report.setReporterName(currentUser.getNickname());
        report.setReportedType(reportCreateDTO.getReportedType());
        report.setRelatedId(reportCreateDTO.getRelatedId());
        report.setReportedId(reportCreateDTO.getReportedId());
        report.setReportedName(reportCreateDTO.getReportedName());
        report.setReportType(reportCreateDTO.getReportType());
        report.setReason(reportCreateDTO.getReason());
        report.setEvidence(reportCreateDTO.getEvidence());
        report.setAnonymous(reportCreateDTO.getAnonymous() == null ? DefaultStatus.NO : reportCreateDTO.getAnonymous());
        report.setReportStatus(ReportStatus.PENDING);
        report.setCreateTime(LocalDateTime.now());
        report.setDeleted(DeleteStatus.NOT_DELETED);

        // 保存举报
        reportMapper.insert(report);

        log.info("用户 {} 举报了 {} {}，举报类型：{}",
                currentUser.getId(), reportCreateDTO.getReportedType().getDesc(),
                reportCreateDTO.getReportedId(), reportCreateDTO.getReportType().getDesc());

        return report.getId();
    }

    @Override
    public Report getReportById(Long reportId) {
        Report report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("举报不存在");
        }

        return report;
    }

    @Override
    public PageResult<ReportListVO> getReportList(ReportStatus reportStatus, int pageNum, int pageSize) {
        Long userId = UserContext.getCurrentUserId();
        int offset = (pageNum - 1) * pageSize;
        // 分页查询当前用户的举报列表
        int total = reportMapper.countByStatus(userId, reportStatus);
        List<ReportListVO> reportListVOs = reportMapper.selectList(userId, reportStatus, pageNum, offset);

        return PageResult.build(reportListVOs, (long) total, pageNum, pageSize);
    }

    @Override
    public PageResult<ReportListVO> getAdminReportList(AdminReportQueryDTO adminReportQueryDTO) {
        // 分页查询
        int total = reportMapper.countByAdminQuery(adminReportQueryDTO);
        int offset = (adminReportQueryDTO.getPageNum() - 1) * adminReportQueryDTO.getPageSize();
        adminReportQueryDTO.setOffset(offset);
        List<ReportListVO> reportListVOs = reportMapper.selectAdminReportList(adminReportQueryDTO);

        return PageResult.build(reportListVOs, (long) total, adminReportQueryDTO.getPageNum(), adminReportQueryDTO.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleReport(Long reportId, ReportHandleDTO reportHandleDTO, Long handlerId) {
        Report report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("举报不存在");
        }

        // 更新举报状态和处理结果
        report.setReportStatus(reportHandleDTO.getReportStatus());
        report.setHandleResult(reportHandleDTO.getHandleResult());
        report.setHandleReason(reportHandleDTO.getHandleReason());
        report.setHandlerId(handlerId);
        report.setHandleTime(LocalDateTime.now());
        report.setUpdateTime(LocalDateTime.now());

        reportMapper.update(report);

        Map<String, Object> params = new HashMap<>();
        params.put(NotificationTemplateVariable.RELATED_ID.getKey(), report.getRelatedId());
        notificationService.sendNotificationWithTemplate(report.getReporterId(), NotificationType.REPORT, params);


        log.info("管理员 {} 处理了举报 {}，处理结果：{}",
                handlerId, reportId, reportHandleDTO.getHandleResult().getDesc());
    }

    @Override
    public int checkDuplicateReport(Long reporterId, Long reportedId, Long relatedId, ReportTargetType reportedType, ReportTypeEnum reportType) {
        if (reporterId == null && reportedId == null && relatedId == null) {
            throw new BusinessException("举报人和被举报人ID不能为空");
        }
        return reportMapper.count(reporterId, reportedId, relatedId, reportedType, reportType);
    }

}
