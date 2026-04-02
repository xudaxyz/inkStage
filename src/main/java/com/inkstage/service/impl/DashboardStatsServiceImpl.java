package com.inkstage.service.impl;

import com.inkstage.entity.model.DashboardStats;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.mapper.*;
import com.inkstage.service.DashboardStatsService;
import com.inkstage.vo.admin.DashboardStatsVO;
import com.inkstage.vo.admin.stat.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘统计数据服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardStatsServiceImpl implements DashboardStatsService {

    private final DashboardStatsMapper dashboardStatsMapper;
    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final TagMapper tagMapper;
    private final CommentMapper commentMapper;
    private final ArticleCollectionMapper articleCollectionMapper;

    @Override
    public DashboardStatsVO getDashboardStats(int limit) {
        DashboardStatsVO dashboardStatsVO = new DashboardStatsVO();

        dashboardStatsVO.setCoreStats(getCoreStats());
        dashboardStatsVO.setPendingStats(getPendingStats());
        dashboardStatsVO.setViewsTrend(getViewsTrend(limit));
        dashboardStatsVO.setUserGrowthTrend(getUserGrowthTrend(limit));
        dashboardStatsVO.setArticleCategoryDistribution(getArticleCategoryDistribution());
        dashboardStatsVO.setRecentActivities(getRecentActivities());

        return dashboardStatsVO;
    }

    @Override
    @Transactional
    public boolean refreshDashboardStats() {
        try {
            calculateCoreStats();
            calculateTrendData();
            calculateDistributionData();
            log.info("仪表盘统计数据刷新成功");
            return true;
        } catch (Exception e) {
            log.error("仪表盘统计数据刷新失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public void calculateCoreStats() {
        long totalUsers = userMapper.countAll();
        saveOrUpdateStats("total_users", String.valueOf(totalUsers), "counter", null);

        long totalArticles = articleMapper.countAll();
        saveOrUpdateStats("total_articles", String.valueOf(totalArticles), "counter", null);

        long totalTags = tagMapper.countAll();
        saveOrUpdateStats("total_tags", String.valueOf(totalTags), "counter", null);

        long totalComments = commentMapper.countAll();
        saveOrUpdateStats("total_comments", String.valueOf(totalComments), "counter", null);

        long totalViews = articleMapper.countTotalReads();
        saveOrUpdateStats("total_views", String.valueOf(totalViews), "counter", null);

        long totalCollections = articleCollectionMapper.countAll();
        saveOrUpdateStats("total_collections", String.valueOf(totalCollections), "counter", null);
    }

    @Override
    @Transactional
    public void calculateTrendData() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long todayViews = articleMapper.countReadsByDate(today);
        saveOrUpdateStats("views", String.valueOf(todayViews), "trend", today);

        long todayNewUsers = userMapper.countNewUsersByDate(today);
        saveOrUpdateStats("new_users", String.valueOf(todayNewUsers), "trend", today);
    }

    @Override
    @Transactional
    public void calculateDistributionData() {
        List<Map<String, Object>> categoryDistribution = articleMapper.getCategoryDistribution();
        int totalCount = categoryDistribution.stream()
                .mapToInt(item -> ((Number) item.get("count")).intValue())
                .sum();

        StringBuilder distributionBuilder = new StringBuilder("{");
        for (int i = 0; i < categoryDistribution.size(); i++) {
            Map<String, Object> item = categoryDistribution.get(i);
            String categoryName = (String) item.get("category_name");
            int count = ((Number) item.get("count")).intValue();
            double percentage = totalCount > 0 ? (count * 100.0 / totalCount) : 0;

            if (i > 0) {
                distributionBuilder.append(",");
            }
            distributionBuilder.append("\"").append(categoryName).append("\":")
                    .append(String.format("%.2f", percentage));
        }
        distributionBuilder.append("}");

        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        saveOrUpdateStats("article_categories", distributionBuilder.toString(), "distribution", currentMonth);
    }

    @Override
    @Transactional
    public void cleanOldTrendData() {
        String thirtyDaysAgo = LocalDate.now().minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        dashboardStatsMapper.deleteOldTrendData(thirtyDaysAgo);
    }

    private CoreStatsVO getCoreStats() {
        CoreStatsVO coreStats = new CoreStatsVO();

        DashboardStats totalUsersStats = dashboardStatsMapper.findByStatKey("total_users");
        long totalUsers = totalUsersStats != null ? Long.parseLong(totalUsersStats.getStatValue()) : 0;
        DashboardStats totalArticlesStats = dashboardStatsMapper.findByStatKey("total_articles");
        long totalArticles = totalArticlesStats != null ? Long.parseLong(totalArticlesStats.getStatValue()) : 0;
        DashboardStats totalTagsStats = dashboardStatsMapper.findByStatKey("total_tags");
        long totalTags = totalTagsStats != null ? Long.parseLong(totalTagsStats.getStatValue()) : 0;
        DashboardStats totalCommentsStats = dashboardStatsMapper.findByStatKey("total_comments");
        long totalComments = totalCommentsStats != null ? Long.parseLong(totalCommentsStats.getStatValue()) : 0;
        DashboardStats totalViewsStats = dashboardStatsMapper.findByStatKey("total_views");
        long totalViews = totalViewsStats != null ? Long.parseLong(totalViewsStats.getStatValue()) : 0;
        DashboardStats totalCollectionsStats = dashboardStatsMapper.findByStatKey("total_collections");
        long totalCollections = totalCollectionsStats != null ? Long.parseLong(totalCollectionsStats.getStatValue()) : 0;

        coreStats.setTotalUsers(totalUsers);
        coreStats.setTotalArticles(totalArticles);
        coreStats.setTotalTags(totalTags);
        coreStats.setTotalComments(totalComments);
        coreStats.setTotalViews(totalViews);
        coreStats.setTotalCollections(totalCollections);

        coreStats.setUsersGrowthRate(calculateGrowthRate("total_users"));
        coreStats.setArticlesGrowthRate(calculateGrowthRate("total_articles"));
        coreStats.setTagsGrowthRate(calculateGrowthRate("total_tags"));
        coreStats.setCommentsGrowthRate(calculateGrowthRate("total_comments"));
        coreStats.setViewsGrowthRate(calculateGrowthRate("total_views"));
        coreStats.setCollectionsGrowthRate(calculateGrowthRate("total_collections"));

        return coreStats;
    }

    private double calculateGrowthRate(String statKey) {
        try {
            DashboardStats currentStats = dashboardStatsMapper.findByStatKey(statKey);
            if (currentStats == null) {
                return 0.0;
            }

            LocalDate yesterday = LocalDate.now().minusDays(1);
            String yesterdayStr = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            DashboardStats yesterdayStats = dashboardStatsMapper.findByStatKeyAndTimeValue(statKey + "_history", yesterdayStr);

            if (yesterdayStats == null) {
                return 0.0;
            }

            long currentValue = Long.parseLong(currentStats.getStatValue());
            long yesterdayValue = Long.parseLong(yesterdayStats.getStatValue());

            if (yesterdayValue == 0) {
                return currentValue > 0 ? 100.0 : 0.0;
            }

            return ((currentValue - yesterdayValue) * 100.0) / yesterdayValue;
        } catch (Exception e) {
            log.warn("计算增长率失败: {}", statKey, e);
            return 0.0;
        }
    }

    private PendingStatsVO getPendingStats() {
        PendingStatsVO pendingStats = new PendingStatsVO();

        pendingStats.setPendingArticles((int) articleMapper.countPendingReviews());
        pendingStats.setPendingTags((int) tagMapper.countPendingReviews());
        pendingStats.setPendingComments((int) commentMapper.countPendingReviews());
        pendingStats.setPendingUsers((int) userMapper.countPendingReviews());

        return pendingStats;
    }

    private List<TrendDataVO> getViewsTrend(int limit) {
        return getTrendData("views", limit);
    }

    private List<TrendDataVO> getUserGrowthTrend(int limit) {
        return getTrendData("new_users", limit);
    }

    @Override
    public List<TrendDataVO> getTrendData(String statKey, int days) {
        List<DashboardStats> trendDataList = dashboardStatsMapper.findTrendData(statKey, days);
        List<TrendDataVO> trendDataVOList = new ArrayList<>();

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);

            TrendDataVO trendDataVO = new TrendDataVO();
            trendDataVO.setTimeValue(dateStr);

            long value = trendDataList.stream()
                    .filter(stats -> dateStr.equals(stats.getTimeValue()))
                    .findFirst()
                    .map(stats -> {
                        try {
                            return Long.parseLong(stats.getStatValue());
                        } catch (NumberFormatException e) {
                            return 0L;
                        }
                    })
                    .orElse(0L);

            trendDataVO.setValue(value);
            trendDataVOList.add(trendDataVO);
        }

        return trendDataVOList;
    }


    private List<DistributionDataVO> getArticleCategoryDistribution() {
        List<Map<String, Object>> categoryDistribution = articleMapper.getCategoryDistribution();
        List<DistributionDataVO> distributionDataVOList = new ArrayList<>();

        int totalCount = categoryDistribution.stream()
                .mapToInt(item -> {
                    Object countObj = item.get("count");
                    return countObj instanceof Number ? ((Number) countObj).intValue() : 0;
                })
                .sum();

        for (Map<String, Object> item : categoryDistribution) {
            String categoryName = (String) item.get("category_name");
            int count = 0;
            Object countObj = item.get("count");
            if (countObj instanceof Number) {
                count = ((Number) countObj).intValue();
            }

            double percentage = totalCount > 0 ? (count * 100.0 / totalCount) : 0;

            DistributionDataVO dataVO = new DistributionDataVO();
            dataVO.setName(categoryName);
            dataVO.setValue(count);
            dataVO.setPercentage(Math.round(percentage * 100.0) / 100.0);
            distributionDataVOList.add(dataVO);
        }

        return distributionDataVOList;
    }

    private List<ActivityStatVO> getRecentActivities() {
        return new ArrayList<>();
    }

    private void saveOrUpdateStats(String statKey, String statValue, String dataType, String timeValue) {
        DashboardStats existingStats;
        if (timeValue != null) {
            existingStats = dashboardStatsMapper.findByStatKeyAndTimeValue(statKey, timeValue);
        } else {
            existingStats = dashboardStatsMapper.findByStatKey(statKey);
        }

        if (existingStats != null) {
            existingStats.setStatValue(statValue);
            dashboardStatsMapper.update(existingStats);
        } else {
            DashboardStats newStats = new DashboardStats();
            newStats.setStatKey(statKey);
            newStats.setStatValue(statValue);
            newStats.setDataType(dataType);
            newStats.setTimeValue(timeValue);
            newStats.setDeleted(DeleteStatus.NOT_DELETED);
            dashboardStatsMapper.insert(newStats);
        }
    }
}
