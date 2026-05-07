package com.inkstage.cache.service;

import com.inkstage.common.PageResult;
import com.inkstage.dto.front.ColumnQueryDTO;
import com.inkstage.vo.front.ArticleListVO;
import com.inkstage.vo.front.ColumnDetailVO;
import com.inkstage.vo.front.ColumnListVO;

import java.util.List;

/**
 * 专栏缓存服务接口
 * 专门负责专栏相关的缓存操作
 */
public interface ColumnCacheService {

    /**
     * 获取专栏详情
     *
     * @param columnId 专栏ID
     * @return 专栏详情
     */
    ColumnDetailVO getColumnDetail(Long columnId);

    /**
     * 获取专栏列表
     *
     * @param queryDTO 查询条件
     * @return 专栏分页列表
     */
    PageResult<ColumnListVO> getColumns(ColumnQueryDTO queryDTO);

    /**
     * 获取专栏文章分页列表
     *
     * @param columnId 专栏ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param sortBy 排序方式
     * @return 专栏文章分页列表
     */
    PageResult<ArticleListVO> getColumnArticles(Long columnId, Integer pageNum, Integer pageSize, String sortBy);

    /**
     * 获取热门专栏列表
     *
     * @param limit 数量限制
     * @return 热门专栏列表
     */
    List<ColumnListVO> getHotColumns(Integer limit);

    /**
     * 搜索专栏内的文章
     *
     * @param columnId 专栏ID
     * @param keyword 搜索关键词
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 专栏文章搜索结果
     */
    PageResult<ArticleListVO> searchColumnArticles(Long columnId, String keyword, Integer pageNum, Integer pageSize);
}