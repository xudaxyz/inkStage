package com.inkstage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 搜索热词Mapper接口
 */
@Mapper
public interface SearchHotWordMapper {

    /**
     * 获取热词搜索数
     *
     * @param id 热词ID
     * @return 搜索数
     */
    Long getSearchCount(@Param("id") Long id);

    /**
     * 更新搜索热词搜索次数
     *
     * @param id    热词ID
     * @param delta 增量值（正数增加，负数减少）
     * @return 影响行数
     */
    int updateSearchCount(@Param("id") Long id, @Param("delta") int delta);

}