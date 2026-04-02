package com.inkstage.vo.admin.stat;

import lombok.Data;

/**
 * 核心数据VO
 */
@Data
public class CoreStatsVO {
    private long totalUsers;
    private double usersGrowthRate;
    private long totalArticles;
    private double articlesGrowthRate;
    private long totalTags;
    private double tagsGrowthRate;
    private long totalComments;
    private double commentsGrowthRate;
    private long totalViews;
    private double viewsGrowthRate;
    private long totalCollections;
    private double collectionsGrowthRate;
}
