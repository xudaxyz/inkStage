package com.inkstage.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 通用分页请求
 */
@Data
public class PageRequest {

    /**
     * 当前页码, 默认为1
     */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    /**
     * 每页大小, 默认为10, 最大为100
     */
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 10;

    /**
     * 分页偏移量, 默认为0
     */
    @Min(value = 0, message = "偏移量不能小于0")
    private Integer offset = 0;

    /**
     * 排序字段, 默认为空
     */
    private String sortBy;

    /**
     * 排序方式, ASC 或 DESC
     */
    private String sortOrder;

    /**
     * 获取分页偏移量
     *
     * @return 偏移量
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}