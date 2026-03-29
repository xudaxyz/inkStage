package com.inkstage.vo.front;

import com.inkstage.enums.VisibleStatus;
import com.inkstage.enums.article.ArticleStatus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章列表VO
 */
@Data
public class ArticleListVO implements Serializable {

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
     * 封面图URL
     */
    private String coverImage;

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
     * 可见性状态
     */
    private VisibleStatus visible;

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

}
