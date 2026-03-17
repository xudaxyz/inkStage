package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.*;
import com.inkstage.enums.article.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 文章实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Article extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章HTML内容(Markdown转换结果)
     */
    private String contentHtml;

    /**
     * 文章封面图URL
     */
    private String coverImage;

    /**
     * 作者ID
     */
    private Long userId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 状态(1:草稿,2:待发布,3:已发布,4:已下架,5:回收站)
     */
    private ArticleStatus articleStatus;

    /**
     * 审核状态(1:待审核,2:审核通过,3:审核拒绝,4:申诉中)
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
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 收藏数
     */
    private Integer collectionCount;

    /**
     * 分享数
     */
    private Integer shareCount;

    /**
     * 是否置顶(0:否,1:是)
     */
    private TopStatus top;

    /**
     * 是否推荐(0:否,1:是)
     */
    private RecommendStatus recommended;

    /**
     * 可见性状态：0-私有, 1-公开, 2-仅关注者可见
     */
    private VisibleStatus visible;

    /**
     * 允许评论状态：0-不允许, 1-允许
     */
    private AllowStatus allowComment;

    /**
     * 允许转发状态：0-不允许, 1-允许
     */
    private AllowStatus allowForward;

    /**
     * 是否原创(0:转载,1:原创)
     */
    private OriginalStatus original;

    /**
     * 转载来源URL
     */
    private String originalUrl;

    /**
     * SEO标题
     */
    private String metaTitle;

    /**
     * SEO描述
     */
    private String metaDescription;

    /**
     * SEO关键词
     */
    private String metaKeywords;

    /**
     * 定时发布时间
     */
    private LocalDateTime scheduledPublishTime;

    /**
     * 分享令牌
     */
    private String shareToken;

    /**
     * 最后一次编辑的时间
     */
    private LocalDateTime lastEditTime;
}
