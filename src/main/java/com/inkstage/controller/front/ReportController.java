package com.inkstage.controller.front;

import com.inkstage.annotation.UserAccess;
import com.inkstage.common.PageResult;
import com.inkstage.common.Result;
import com.inkstage.dto.front.ReportCreateDTO;
import com.inkstage.entity.model.Report;
import com.inkstage.enums.ReportStatus;
import com.inkstage.service.ReportService;
import com.inkstage.vo.front.ReportListVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 前台举报Controller
 */
@RestController
@RequestMapping("/front/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 提交举报
     *
     * @param reportCreateDTO 举报创建DTO
     * @return 举报ID
     */
    @PostMapping("/create")
    @UserAccess
    public Result<Long> createReport(@Valid @RequestBody ReportCreateDTO reportCreateDTO) {
        Long reportId = reportService.createReport(reportCreateDTO);
        return Result.success(reportId, "举报成功");
    }

    /**
     * 获取用户的举报列表
     *
     * @param reportStatus 举报状态
     * @return 举报列表
     */
    @GetMapping("/my-report")
    @UserAccess
    public PageResult<ReportListVO> getMyReportList(@RequestParam ReportStatus reportStatus,
                                                    @RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        return reportService.getReportList(reportStatus, pageNum, pageSize);
    }

    /**
     * 获取举报详情
     *
     * @param id 举报ID
     * @return 举报详情
     */
    @GetMapping("/get/{id}")
    @UserAccess
    public Result<Report> getReportById(@PathVariable Long id) {
        Report report = reportService.getReportById(id);
        if (report == null) {
            return Result.error("举报详情不存在");
        } else {
            return Result.success(report);
        }
    }
}
