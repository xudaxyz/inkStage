package com.inkstage.vo.admin;

import com.inkstage.vo.admin.stat.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 仪表盘统计数据VO
 */
@Data
public class DashboardStatsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 核心统计数据
     */
    private CoreStatsVO coreStats;

    /**
     * 待审核数据
     */
    private PendingStatsVO pendingStats;

    /**
     * 浏览量趋势数据
     */
    private List<TrendDataVO> viewsTrend;

    /**
     * 用户增长趋势数据
     */
    private List<TrendDataVO> userGrowthTrend;

    /**
     * 文章分类分布数据
     */
    private List<DistributionDataVO> articleCategoryDistribution;

    /**
     * 最近活动数据
     */
    private List<ActivityStatVO> recentActivities;


}
