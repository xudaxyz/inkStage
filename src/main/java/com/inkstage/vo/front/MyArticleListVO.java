package com.inkstage.vo.front;

import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.VisibleStatus;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.enums.article.OriginalStatus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 我的文章列表VO
 */
@Data
public class MyArticleListVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
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
     * 作者id
     */
    private Long userId;

    /**
     * 文章状态
     */
    private ArticleStatus articleStatus;

    /**
     * 审核状态
     */
    private ReviewStatus reviewStatus;

    /**
     * 可见性状态
     */
    private VisibleStatus visible;

    /**
     * 是否原创
     */
    private OriginalStatus original;

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
}
