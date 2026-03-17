package com.inkstage.vo.front;

import com.inkstage.enums.CollectionStatus;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.enums.article.OriginalStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏文章列表VO
 */
@Data
public class CollectionArticleVO {

    /**
     * 收藏ID
     */
    private Long collectionId;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 文章标题
     */
    private String title;

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
    private ArticleStatus articleStatus;

    /**
     * 作者id
     */
    private Long userId;

    /**
     * 作者名称
     */
    private String nickname;

    /**
     * 作者头像
     */
    private String avatar;

    /**
     * 分类ID
     */
    private String categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 原创状态
     */
    private OriginalStatus originalStatus;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 收藏时间
     */
    private LocalDateTime collectTime;

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
     * 收藏状态（public/private）
     */
    private CollectionStatus collectionStatus;

    /**
     * 文件夹ID
     */
    private Long folderId;

    /**
     * 文件夹名称
     */
    private String folderName;

}
