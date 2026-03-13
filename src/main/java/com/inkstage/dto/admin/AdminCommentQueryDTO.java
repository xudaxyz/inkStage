package com.inkstage.dto.admin;

import com.inkstage.common.PageRequest;
import com.inkstage.enums.ReviewStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后台评论管理查询DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AdminCommentQueryDTO extends PageRequest {

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 查询关键词
     */
    private String keyword;

    /**
     * 评论状态
     */
    private ReviewStatus status;

}
