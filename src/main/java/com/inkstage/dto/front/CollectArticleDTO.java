package com.inkstage.dto.front;

import com.inkstage.enums.CollectionStatus;
import com.inkstage.enums.common.DefaultStatus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 收藏文章DTO
 */
@Data
public class CollectArticleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 文章ID
     */
    private Long articleId;
    /**
     * 文件夹ID
     */
    private Long folderId;
    /**
     * 文件夹名称
     */
    private String folderName;

    /**
     * 文件夹描述
     */
    private String folderDescription;
    /**
     * 是否默认文件夹
     */
    private DefaultStatus defaultFolder;

    /**
     * 收藏状态
     */
    private CollectionStatus status;
}
