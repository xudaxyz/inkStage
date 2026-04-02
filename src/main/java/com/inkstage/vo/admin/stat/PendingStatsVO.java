package com.inkstage.vo.admin.stat;

import lombok.Data;

/**
 * 待审核数据VO
 */
@Data
public class PendingStatsVO {
    /**
     * 待审核文章数
     */
    private int pendingArticles;
    /**
     * 待审核标签数
     */
    private int pendingTags;
    /**
     * 待审核评论数
     */
    private int pendingComments;
    /**
     * 待审核用户数
     */
    private int pendingUsers;
}
