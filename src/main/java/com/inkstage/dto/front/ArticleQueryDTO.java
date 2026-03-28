package com.inkstage.dto.front;

import com.inkstage.enums.article.ArticleStatus;
import lombok.Data;

/**
 * 文章查询DTO
 */
@Data
public class ArticleQueryDTO {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    private Integer pageSize = 10;

    /**
     * 页大小
     */
    private Integer offset = 0;
    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标签
     */
    private Long tagId;

    /**
     * 文章状态
     */
    private ArticleStatus status;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 排序字段
     */
    private String sortBy = "publishTime";

    /**
     * 排序方向(asc/desc)
     */
    private String sortOrder = "desc";
}
