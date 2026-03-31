package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.common.DefaultStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 收藏文件夹实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CollectionFolder extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文件夹名称
     */
    private String name;

    /**
     * 文件夹描述
     */
    private String description;

    /**
     * 文件夹内文章数量
     */
    private Integer articleCount;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 是否默认文件夹(0:否,1:是)
     */
    private DefaultStatus defaultFolder;
}