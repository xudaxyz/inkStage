package com.inkstage.service;

import com.inkstage.vo.admin.DashboardStatsVO;
import com.inkstage.vo.admin.stat.TrendDataVO;

import java.util.List;

/**
 * 仪表盘统计数据服务接口
 */
public interface DashboardStatsService {

    /**
     * 获取仪表盘统计数据
     * @param limit 期限 3/7/30天
     * @return 仪表盘统计数据VO
     */
    DashboardStatsVO getDashboardStats(int limit);

    /**
     * 刷新仪表盘统计数据
     *
     * @return 是否刷新成功
     */
    boolean refreshDashboardStats();

    /**
     * 计算并存储核心统计数据
     */
    void calculateCoreStats();

    /**
     * 计算并存储趋势数据
     */
    void calculateTrendData();

    /**
     * 计算并存储分布数据
     */
    void calculateDistributionData();

    /**
     * 清理过期的趋势数据
     */
    void cleanOldTrendData();

    /**
     * 获取指定时间范围的趋势数据
     *
     * @param statKey 统计键（如 "views" 或 "new_users"）
     * @param days 天数（3、7、30）
     * @return 趋势数据列表
     */
    List<TrendDataVO> getTrendData(String statKey, int days);
}
