package com.inkstage.dto.front;

import com.inkstage.enums.CollectionStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 收藏文件夹DTO
 * 用于创建和更新收藏文件夹
 */
@Data
public class CollectionFolderDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件夹名称
     */
    @NotBlank(message = "文件夹名称不能为空")
    private String name;

    /**
     * 文件夹描述
     */
    private String description;

    /**
     * 收藏夹状态（公开/私密）
     */
    private CollectionStatus status;
}
