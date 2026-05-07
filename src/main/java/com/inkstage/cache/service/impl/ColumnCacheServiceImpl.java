package com.inkstage.cache.service.impl;

import com.inkstage.cache.constant.RedisKeyConstants;
import com.inkstage.cache.service.ColumnCacheService;
import com.inkstage.common.PageResult;
import com.inkstage.dto.front.ColumnQueryDTO;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleColumnMapper;
import com.inkstage.mapper.ColumnMapper;
import com.inkstage.service.FileService;
import com.inkstage.vo.front.ArticleListVO;
import com.inkstage.vo.front.ColumnDetailVO;
import com.inkstage.vo.front.ColumnListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 专栏缓存服务实现类
 * 专门负责专栏相关的缓存操作
 * 使用Spring Cache注解实现声明式缓存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ColumnCacheServiceImpl implements ColumnCacheService {

    private final ColumnMapper columnMapper;
    private final ArticleColumnMapper articleColumnMapper;
    private final FileService fileService;

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_COLUMN_DETAIL, key = "#columnId", unless = "#result == null")
    public ColumnDetailVO getColumnDetail(Long columnId) {
        ColumnDetailVO columnDetailVO = columnMapper.findDetailById(columnId);
        if (columnDetailVO == null) {
            log.warn("专栏详情不存在: {}", columnId);
            throw new BusinessException("专栏不存在");
        }

        fileService.ensureImageFullUrl(columnDetailVO);

        log.debug("从数据库获取专栏详情, id: {}", columnId);
        return columnDetailVO;
    }

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_COLUMN_LIST,
            key = "#queryDTO.pageNum + ':' + #queryDTO.pageSize + ':' + (#queryDTO.keyword ?: '')",
            unless = "#result == null")
    public PageResult<ColumnListVO> getColumns(ColumnQueryDTO queryDTO) {
        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        long total = columnMapper.countColumnList(queryDTO);
        List<ColumnListVO> columnListVOList = columnMapper.findColumnList(queryDTO, offset, queryDTO.getPageSize());

        fileService.ensureImageFullUrl(columnListVOList);

        log.info("获取专栏列表成功, 总数: {}, 页码: {}, 每页大小: {}",
                total, queryDTO.getPageNum(), queryDTO.getPageSize());

        return PageResult.build(columnListVOList, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_COLUMN_ARTICLES,
            key = "#columnId + ':' + #pageNum + ':' + #pageSize + ':' + (#sortBy ?: 'ASC')",
            unless = "#result == null")
    public PageResult<ArticleListVO> getColumnArticles(Long columnId, Integer pageNum, Integer pageSize, String sortBy) {
        int offset = (pageNum - 1) * pageSize;
        List<ArticleListVO> articleList = articleColumnMapper.findArticlesByColumnId(columnId, offset, pageSize, sortBy);
        long total = articleColumnMapper.countArticlesByColumnId(columnId);

        fileService.ensureImageFullUrl(articleList);

        log.info("获取专栏文章分页列表成功, 专栏ID: {}, 总数: {}, 页码: {}, 每页大小: {}",
                columnId, total, pageNum, pageSize);

        return PageResult.build(articleList, total, pageNum, pageSize);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_COLUMN_HOT, key = "#limit", unless = "#result == null or #result.isEmpty()")
    public List<ColumnListVO> getHotColumns(Integer limit) {
        List<ColumnListVO> hotColumns = columnMapper.findHotColumns(limit);

        fileService.ensureImageFullUrl(hotColumns);

        log.info("获取热门专栏成功, limit: {}", limit);
        return hotColumns;
    }

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_COLUMN_ARTICLES,
            key = "'search:' + #columnId + ':' + #keyword + ':' + #pageNum + ':' + #pageSize",
            unless = "#result == null or #result.total == 0")
    public PageResult<ArticleListVO> searchColumnArticles(Long columnId, String keyword, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<ArticleListVO> articleList = articleColumnMapper.searchArticlesByColumnId(columnId, keyword, offset, pageSize);
        long total = articleColumnMapper.countSearchArticlesByColumnId(columnId, keyword);

        fileService.ensureImageFullUrl(articleList);

        log.info("搜索专栏文章成功, 专栏ID: {}, 关键词: {}, 总数: {}", columnId, keyword, total);

        return PageResult.build(articleList, total, pageNum, pageSize);
    }
}