package com.inkstage.controller.admin;

import com.inkstage.common.Result;
import com.inkstage.service.DashboardStatsService;
import com.inkstage.vo.admin.DashboardStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 仪表盘统计数据控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardStatsController {

    private final DashboardStatsService dashboardStatsService;

    /**
     * 获取仪表盘统计数据
     *
     * @return 仪表盘统计数据
     */
    @GetMapping("/stats")
    public Result<DashboardStatsVO> getDashboardStats(@RequestParam("days") int days) {
        DashboardStatsVO dashboardStats = dashboardStatsService.getDashboardStats(days);
        log.info("getDashboardStats: {}", dashboardStats);
        return Result.success(dashboardStats);
    }

    /**
     * 刷新仪表盘统计数据
     *
     * @return 是否刷新成功
     */
    @PostMapping("/refresh")
    public Result<Boolean> refreshDashboardStats() {
        boolean result = dashboardStatsService.refreshDashboardStats();
        return Result.success(result);
    }

}
