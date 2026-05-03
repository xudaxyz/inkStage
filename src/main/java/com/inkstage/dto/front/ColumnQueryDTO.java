package com.inkstage.dto.front;

import com.inkstage.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专栏查询DTO
 * 用于分页查询专栏列表
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ColumnQueryDTO extends PageRequest {

    /**
     * 搜索关键词（匹配专栏名称或描述）
     */
    private String keyword;

    /**
     * 专栏创建者ID（用于查询指定用户的专栏）
     */
    private Long userId;

}
