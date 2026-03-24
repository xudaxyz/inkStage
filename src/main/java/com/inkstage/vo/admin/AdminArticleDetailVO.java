package com.inkstage.vo.admin;

import com.inkstage.entity.model.Tag;
import com.inkstage.enums.AllowStatus;
import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.VisibleStatus;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.enums.article.OriginalStatus;
import com.inkstage.enums.article.RecommendStatus;
import com.inkstage.enums.article.TopStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员文章详情VO
 */
@Data
public class AdminArticleDetailVO {

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
     * 文章内容
     */
    private String content;

    /**
     * 文章HTML内容
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
     * 作者昵称
     */
    private String nickname;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

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
     * 是否置顶
     */
    private TopStatus top;

    /**
     * 是否推荐
     */
    private RecommendStatus recommended;

    /**
     * 可见性状态
     */
    private VisibleStatus visible;

    /**
     * 允许评论状态
     */
    private AllowStatus allowComment;

    /**
     * 允许转发状态
     */
    private AllowStatus allowForward;

    /**
     * 是否原创
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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后编辑时间
     */
    private LocalDateTime lastEditTime;

    /**
     * 标签列表
     */
    private List<Tag> tags;
}
