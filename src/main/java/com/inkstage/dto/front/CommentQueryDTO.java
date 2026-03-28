package com.inkstage.dto.front;

import lombok.Data;

/**
 * 评论查询DTO
 */
@Data
public class CommentQueryDTO {

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 跳过记录数
     */
    private Integer offset = 0;

    /**
     * 排序方式：hot（最热）、new（最新）
     */
    private String sortBy = "hot";

    /**
     * 子评论最大返回数量，默认3条
     */
    private Integer maxReplies = 3;

    /**
     * 子评论排序方式：hot（最热）、new（最新）
     */
    private String replySortBy = "hot";
}
