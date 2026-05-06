package com.inkstage.vo.front;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 专栏文章上下篇结果VO
 */
@Data
public class ColumnNeighborVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 专栏Id
     */
    private Long columnId;

    /**
     * 专栏名称
     */
    private String columnName;

    /**
     * 上一篇文章
     */
    private NeighborArticleVO prev;

    /**
     * 下一篇文章
     */
    private NeighborArticleVO next;

}