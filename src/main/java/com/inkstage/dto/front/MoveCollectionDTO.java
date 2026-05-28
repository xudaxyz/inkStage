package com.inkstage.dto.front;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 移动收藏文章DTO
 */
@Data
public class MoveCollectionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    /**
     * 目标文件夹ID
     */
    @NotNull(message = "目标文件夹ID不能为空")
    private Long targetFolderId;
}
