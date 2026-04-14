package com.inkstage.controller.admin;

import com.inkstage.annotation.AdminAccess;
import com.inkstage.common.PageResult;
import com.inkstage.common.Result;
import com.inkstage.dto.admin.AdminReportQueryDTO;
import com.inkstage.dto.admin.ReportHandleDTO;
import com.inkstage.entity.model.Report;
import com.inkstage.service.ReportService;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.ReportListVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 后台举报Controller
 */
@RestController
@RequestMapping("/admin/report")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    /**
     * 获取举报列表
     *
     * @param adminReportQueryDTO 后台举报查询DTO
     * @return 举报列表
     */
    @GetMapping("/list")
    @AdminAccess
    public PageResult<ReportListVO> getReportList(AdminReportQueryDTO adminReportQueryDTO) {
        return reportService.getAdminReportList(adminReportQueryDTO);
    }

    /**
     * 获取举报详情
     *
     * @param id 举报ID
     * @return 举报详情
     */
    @GetMapping("/get/{id}")
    @AdminAccess
    public Result<Report> getReportById(@PathVariable Long id) {
        Report report = reportService.getReportById(id);
        return Result.success(report);
    }

    /**
     * 处理举报
     *
     * @param id              举报ID
     * @param reportHandleDTO 举报处理DTO
     */
    @PutMapping("/{id}/handle")
    @AdminAccess
    public void handleReport(@PathVariable Long id, @Valid @RequestBody ReportHandleDTO reportHandleDTO) {
        Long adminId = UserContext.getCurrentUserId();
        reportService.handleReport(id, reportHandleDTO, adminId);
    }

}
