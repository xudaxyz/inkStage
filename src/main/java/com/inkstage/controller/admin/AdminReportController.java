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
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * 后台举报Controller
 */
@Slf4j
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
    @PostMapping("/list")
    @AdminAccess
    public Result<?> getReportList(@RequestBody AdminReportQueryDTO adminReportQueryDTO) {
        log.info("管理员获取举报列表, 查询参数: {}", adminReportQueryDTO);
        PageResult<ReportListVO> adminReportList = reportService.getAdminReportList(adminReportQueryDTO);
        return Result.success(adminReportList);
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
    @PutMapping("/handle/{id}")
    @AdminAccess
    public Result<?> handleReport(@PathVariable Long id, @Valid @RequestBody ReportHandleDTO reportHandleDTO) {
        log.info("管理员处理举报, 举报ID: {}, 处理结果: {}", id, reportHandleDTO);
        Long adminId = UserContext.getCurrentUserId();
        reportService.handleReport(id, reportHandleDTO, adminId);
        return Result.success("处理举报完成");
    }

}
