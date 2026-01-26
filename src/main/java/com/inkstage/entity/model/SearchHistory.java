package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.SearchType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 搜索历史实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchHistory extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 搜索类型
     */
    private SearchType searchType;

    /**
     * 搜索时间
     */
    private String searchTime;

    /**
     * 搜索IP地址
     */
    private String ipAddress;
}