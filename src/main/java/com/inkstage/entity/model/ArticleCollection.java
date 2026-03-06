package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.CollectionStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 文章收藏实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleCollection extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收藏文件夹ID
     */
    private Long folderId;

    /**
     * 收藏状态
     */
    private CollectionStatus status;

    /**
     * 收藏时间
     */
    private LocalDateTime collectTime;
}