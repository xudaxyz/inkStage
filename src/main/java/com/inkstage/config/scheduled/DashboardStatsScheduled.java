package com.inkstage.config.scheduled;

import com.inkstage.service.DashboardStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 仪表盘统计数据定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardStatsScheduled {

    private final DashboardStatsService dashboardStatsService;

    /**
     * 更新核心统计数据
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void updateCoreStats() {
        log.info("开始更新仪表盘核心统计数据");
        try {
            dashboardStatsService.calculateCoreStats();
            log.info("仪表盘核心统计数据更新成功");
        } catch (Exception e) {
            log.error("仪表盘核心统计数据更新失败", e);
        }
    }

    /**
     * 更新趋势数据
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void updateTrendData() {
        log.info("开始更新仪表盘趋势数据");
        try {
            dashboardStatsService.calculateTrendData();
            log.info("仪表盘趋势数据更新成功");
        } catch (Exception e) {
            log.error("仪表盘趋势数据更新失败", e);
        }
    }

    /**
     * 更新分布数据
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void updateDistributionData() {
        log.info("开始更新仪表盘分布数据");
        try {
            dashboardStatsService.calculateDistributionData();
            log.info("仪表盘分布数据更新成功");
        } catch (Exception e) {
            log.error("仪表盘分布数据更新失败", e);
        }
    }

    /**
     * 更新过期趋势数据
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanOldTrendData() {
        log.info("开始清理过期的仪表盘趋势数据");
        try {
            dashboardStatsService.cleanOldTrendData();
            log.info("过期的仪表盘趋势数据清理成功");
        } catch (Exception e) {
            log.error("过期的仪表盘趋势数据清理失败", e);
        }
    }

    /**
     * 更新实时数据
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void updateRealtimeData() {
        log.debug("开始更新仪表盘实时数据");
        try {
            dashboardStatsService.calculateTrendData();
            log.debug("仪表盘实时数据更新成功");
        } catch (Exception e) {
            log.error("仪表盘实时数据更新失败", e);
        }
    }
}
