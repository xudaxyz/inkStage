package com.inkstage.vo.front;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 专栏内文章上下篇VO
 */
@Data
public class NeighborArticleVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 上一篇文章ID
     */
    private Long id;

    /**
     * 上一篇文章标题
     */
    private String title;

}