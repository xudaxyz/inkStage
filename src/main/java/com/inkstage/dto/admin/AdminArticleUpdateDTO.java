package com.inkstage.dto.admin;

import com.inkstage.entity.model.Tag;
import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.article.ArticleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * 管理员更新文章DTO
 */
@Data
public class AdminArticleUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    @Length(min = 2, max = 100, message = "标题长度应在2-100个字符之间")
    private String title;

    /**
     * 作者昵称
     */
    @NotBlank(message = "作者不能为空")
    @Length(min = 2, max = 20, message = "作者名称长度应在2-20个字符之间")
    private String nickname;

    /**
     * 分类ID
     */
    @NotNull(message = "请选择分类")
    private Long categoryId;

    /**
     * 标签列表
     */
    private List<Tag> tags;

    /**
     * 文章状态
     */
    @NotNull(message = "文章状态不能为空")
    private ArticleStatus articleStatus;

    /**
     * 审核状态
     */
    private ReviewStatus reviewStatus;

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
     * 封面图片URL
     */
    private String coverImage;

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
}
