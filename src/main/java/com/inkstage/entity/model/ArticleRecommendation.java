package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.RecommendType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 文章推荐关系实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleRecommendation extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 文章ID
     */
    private Long articleId;
    
    /**
     * 相关文章ID
     */
    private Long relatedArticleId;
    
    /**
     * 相关度分数
     */
    private BigDecimal relevanceScore;
    
    /**
     * 推荐类型
     */
    private RecommendType recommendType;
}