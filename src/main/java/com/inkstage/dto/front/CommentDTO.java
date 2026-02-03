package com.inkstage.dto.front;

import lombok.Data;

/**
 * 评论DTO，用于评论的创建和更新
 */
@Data
public class CommentDTO {

    /**
     * 评论ID，用于更新操作
     */
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 父评论ID(顶级评论为0)
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * @ 提及的用户ID列表
     */
    private String mentionUserIds;
}
