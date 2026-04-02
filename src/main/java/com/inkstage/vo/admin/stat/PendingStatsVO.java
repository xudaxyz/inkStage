package com.inkstage.vo.admin.stat;

import lombok.Data;

/**
 * 待审核数据VO
 */
@Data
public class PendingStatsVO {
    private int pendingArticles;
    private int pendingTags;
    private int pendingComments;
    private int pendingUsers;
}
