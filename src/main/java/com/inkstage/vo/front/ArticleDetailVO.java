package com.inkstage.vo.front;

import com.inkstage.entity.model.Tag;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.enums.AllowStatus;
import com.inkstage.enums.VisibleStatus;
import com.inkstage.enums.article.OriginalStatus;
import com.inkstage.enums.user.Gender;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章详情VO
 */
@Data
public class ArticleDetailVO implements Serializable {

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
     * 文章内容
     */
    private String content;

    /**
     * 文章HTML内容
     */
    private String contentHtml;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 封面图URL
     */
    private String coverImage;

    /**
     * 文章状态
     */
    private ArticleStatus status;

    /**
     * 可见性状态
     */
    private VisibleStatus visible;

    /**
     * 是否允许评论
     */
    private AllowStatus allowComment;

    /**
     * 是否允许转发
     */
    private AllowStatus allowForward;

    /**
     * 是否原创
     */
    private OriginalStatus original;

    /**
     * 原创链接
     */
    private String originalUrl;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 最后编辑时间
     */
    private LocalDateTime lastEditTime;

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
     * 是否已点赞
     */
    private Boolean isLiked;

    /**
     * 是否已收藏
     */
    private Boolean isCollected;

    // 作者相关信息
    /**
     * 作者ID
     */
    private Long userId;
    /**
     * 作者昵称
     */
    private String nickname;

    /**
     * 作者头像
     */
    private String avatar;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 性别(0:未知,1:男,2:女)
     */
    private Gender gender;

    /**
     * 作者文章数
     */
    private Integer articleCount;

    /**
     * 作者粉丝数
     */
    private Integer followerCount;

    //分类信息
    /**
     * 分类ID
     */
    private Long categoryId;
    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 标签列表
     */
    private List<Tag> tags;
}
