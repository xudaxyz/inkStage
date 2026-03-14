package com.inkstage.dto.front;

import com.inkstage.common.PageRequest;
import com.inkstage.enums.article.ArticleStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 我的文章查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MyArticleQueryDTO extends PageRequest {

    /**
     * 文章状态
     */
    private ArticleStatus articleStatus;

    /**
     * 搜索关键词
     */
    private String keyword;
}
