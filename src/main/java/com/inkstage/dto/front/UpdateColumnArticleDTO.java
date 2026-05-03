package com.inkstage.dto.front;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新专栏文章排序DTO
 * 用于更新文章在专栏内的排序位置
 */
@Data
public class UpdateColumnArticleDTO {

    /**
     * 专栏ID
     */
    @NotNull(message = "专栏ID不能为空")
    private Long columnId;

    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    /**
     * 文章在专栏内的排序顺序（可选，不传则追加到末尾）
     */
    private Integer sortOrder;
}
