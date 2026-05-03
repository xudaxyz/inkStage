package com.inkstage.vo.front;

import com.inkstage.enums.common.StatusEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 专栏详情VO
 * 用于前台专栏详情展示，包含作者信息和文章列表
 */
@Data
public class ColumnDetailVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 专栏ID
     */
    private Long id;

    /**
     * 专栏名称
     */
    private String name;

    /**
     * 专栏别名（URL友好）
     */
    private String slug;

    /**
     * 专栏描述
     */
    private String description;

    /**
     * 专栏封面图URL
     */
    private String coverImage;

    /**
     * 专栏内文章数量
     */
    private Integer articleCount;

    /**
     * 专栏总阅读量
     */
    private Integer readCount;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 状态（0:禁用, 1:正常）
     */
    private StatusEnum status;

    /**
     * 专栏创建者ID
     */
    private Long userId;

    /**
     * 专栏创建者昵称
     */
    private String nickname;

    /**
     * 专栏创建者头像URL
     */
    private String avatar;

    /**
     * 专栏创建者个人签名
     */
    private String signature;

    /**
     * 专栏文章列表
     */
    private List<ArticleListVO> articles;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
