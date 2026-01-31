package com.inkstage.vo.front;

import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.enums.VisibleStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章列表VO
 */
@Data
public class ArticleListVO {

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
     * 作者信息
     */
    private AuthorInfoVO author;

    /**
     * 分类信息
     */
    private CategoryInfoVO category;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 文章状态
     */
    private ArticleStatus status;

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

    /**
     * 收藏数
     */
    private Integer collectionCount;

    /**
     * 作者信息VO
     */
    @Data
    public static class AuthorInfoVO {
        private Long id;
        private String name;
        private String avatar;
    }

    /**
     * 分类信息VO
     */
    @Data
    public static class CategoryInfoVO {
        private Long id;
        private String name;
    }
}
