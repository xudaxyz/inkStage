package com.inkstage.vo.admin.stat;

import lombok.Data;

/**
 * 核心数据VO
 */
@Data
public class CoreStatsVO {
    /**
     * 总用户数
     */
    private long totalUsers;
    /**
     * 用户增长 rate
     */
    private double usersGrowthRate;
    /**
     * 文章总数
     */
    private long totalArticles;
    /**
     * 文章增长 rate
     */
    private double articlesGrowthRate;
    /**
     * 标签总数
     */
    private long totalTags;
    /**
     * 标签增长 rate
     */
    private double tagsGrowthRate;
    /**
     * 评论总数
     */
    private long totalComments;
    /**
     * 评论增长 rate
     */
    private double commentsGrowthRate;
    /**
     * 浏览总数
     */
    private long totalViews;
    /**
     * 浏览增长 rate
     */
    private double viewsGrowthRate;

    /**
     * 收藏总数
     */
    private long totalCollections;
    /**
     * 收藏增长 rate
     */
    private double collectionsGrowthRate;
}
