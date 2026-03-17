package com.inkstage.dto.front;

import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.enums.AllowStatus;
import com.inkstage.enums.article.OriginalStatus;
import com.inkstage.enums.VisibleStatus;
import com.inkstage.enums.article.TopStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 文章创建/更新DTO
 */
@Data
public class ArticleCreateDTO {

    /**
     * 文章标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题不能超过100个字符")
    private String title;

    /**
     * 文章内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 分类ID
     */
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    /**
     * 标签列表
     */
    private List<Long> tagIds;

    /**
     * 封面图URL
     */
    private String coverImage;

    /**
     * 文章状态
     */
    @NotNull(message = "状态不能为空")
    private ArticleStatus status;

    /**
     * 可见性状态
     */
    @NotNull(message = "可见性不能为空")
    private VisibleStatus visible;

    /**
     * 是否允许评论
     */
    @NotNull(message = "评论设置不能为空")
    private AllowStatus allowComment;

    /**
     * 是否允许转发
     */
    @NotNull(message = "转发设置不能为空")
    private AllowStatus allowForward;

    /**
     * 是否原创
     */
    @NotNull(message = "文章类型不能为空")
    private OriginalStatus original;

    /**
     * 原创链接
     */
    private String originalUrl;

    /**
     * 置顶状态
     */
    private TopStatus top;

    /**
     * 审核状态
     */
    private ReviewStatus reviewStatus;
}
