package com.inkstage.dto.admin;

import com.inkstage.common.PageRequest;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.enums.article.TopStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后台文章管理查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminArticleQueryDTO extends PageRequest {

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 文章状态
     */
    private ArticleStatus articleStatus;

    /**
     * 是否置顶
     */
    private TopStatus topStatus;

}
