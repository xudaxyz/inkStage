package com.inkstage.dto.front;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 专栏创建DTO
 * 用于创建和更新专栏
 */
@Data
public class ColumnCreateDTO {

    /**
     * 专栏名称
     */
    @NotBlank(message = "专栏名称不能为空")
    @Size(max = 100, message = "专栏名称不能超过100个字符")
    private String name;

    /**
     * 专栏别名（URL友好，可选）
     * 目前留空，后续可扩展自动生成或用户自定义
     */
    @Size(max = 100, message = "专栏别名不能超过100个字符")
    private String slug;

    /**
     * 专栏描述
     */
    @Size(max = 500, message = "专栏描述不能超过500个字符")
    private String description;

    /**
     * 专栏封面图URL
     */
    private String coverImage;

    /**
     * 排序顺序
     */
    private Integer sortOrder;
}
