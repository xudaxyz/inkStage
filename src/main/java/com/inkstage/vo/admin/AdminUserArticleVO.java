package com.inkstage.vo.admin;

import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.enums.ReviewStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台用户最近文章VO
 */
@Data
public class AdminUserArticleVO {

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章状态
     */
    private ArticleStatus articleStatus;

    /**
     * 审核状态
     */
    private ReviewStatus reviewStatus;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 阅读量
     */
    private Integer readCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 点赞数
     */
    private Integer likeCount;
}
