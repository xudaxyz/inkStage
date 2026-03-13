package com.inkstage.vo.admin;

import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.article.TopStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台用户最近评论VO
 */
@Data
public class AdminUserCommentVO {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论状态
     */
    private ReviewStatus status;

    /**
     * 是否置顶
     */
    private TopStatus top;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回复数
     */
    private Integer replyCount;

    /**
     * 评论时间
     */
    private LocalDateTime createTime;
}
