package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.vo.front.ArticleListVO;

/**
 * 文章搜索服务接口
 */
public interface ArticleSearchService {

    /**
     * 搜索文章
     * @param keyword   搜索关键词
     * @param sortBy    排序方式：relevance, publishTime, readCount
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @return 分页结果
     */
    PageResult<ArticleListVO> searchArticles(String keyword, String sortBy, Integer pageNum, Integer pageSize);
}
