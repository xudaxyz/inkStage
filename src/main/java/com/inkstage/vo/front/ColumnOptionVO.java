package com.inkstage.vo.front;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 专栏选项VO
 * 用于创建文章时选择专栏，仅包含ID和名称
 */
@Data
public class ColumnOptionVO implements Serializable {

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

}