package com.inkstage.vo.admin;

import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.enums.article.TopStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台文章管理VO
 */
@Data
public class AdminArticleVO {

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 作者名称
     */
    private String nickname;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 文章状态
     */
    private ArticleStatus articleStatus;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 阅读量
     */
    private Integer readCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 是否置顶
     */
    private TopStatus top;

    /**
     * 文章审核状态
     */
    private ReviewStatus reviewStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
