package com.inkstage.cache.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.constant.CacheTTL;
import com.inkstage.cache.service.CacheManager;
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
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.util.List;

/**
 * 专栏缓存服务实现类
 * 专门负责专栏相关的缓存操作
 * 使用 CacheManager 实现缓存管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ColumnCacheServiceImpl implements ColumnCacheService {

    private final ColumnMapper columnMapper;
    private final ArticleColumnMapper articleColumnMapper;
    private final FileService fileService;
    private final CacheManager cacheManager;

    @Override
    public ColumnDetailVO getColumnDetail(Long columnId) {
        String cacheKey = CacheKey.keyForColumnDetail(columnId);
        ColumnDetailVO columnDetailVO = cacheManager.get(cacheKey, ColumnDetailVO.class);
        if (columnDetailVO != null) {
            log.debug("从缓存获取专栏详情, id: {}", columnId);
            return columnDetailVO;
        }

        columnDetailVO = columnMapper.findDetailById(columnId);
        if (columnDetailVO == null) {
            log.warn("专栏详情不存在: {}", columnId);
            throw new BusinessException("专栏不存在");
        }

        fileService.ensureImageFullUrl(columnDetailVO);

        log.debug("从数据库获取专栏详情, id: {}", columnId);
        cacheManager.set(cacheKey, columnDetailVO, CacheTTL.COLUMN_DETAIL);
        return columnDetailVO;
    }

    @Override
    public PageResult<ColumnListVO> getColumns(ColumnQueryDTO queryDTO) {
        String cacheKey = CacheKey.keyForColumnList(queryDTO.getPageNum(), queryDTO.getPageSize(), queryDTO.getKeyword());
        PageResult<ColumnListVO> result = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (result != null) {
            log.debug("从缓存获取专栏列表, pageNum: {}, pageSize: {}", queryDTO.getPageNum(), queryDTO.getPageSize());
            return result;
        }

        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        long total = columnMapper.countColumnList(queryDTO);
        List<ColumnListVO> columnListVOList = columnMapper.findColumnList(queryDTO, offset, queryDTO.getPageSize());

        fileService.ensureImageFullUrl(columnListVOList);

        result = PageResult.build(columnListVOList, total, queryDTO.getPageNum(), queryDTO.getPageSize());
        log.info("从数据库获取专栏列表成功, 总数: {}, 页码: {}, 每页大小: {}", total, queryDTO.getPageNum(), queryDTO.getPageSize());
        cacheManager.set(cacheKey, result, CacheTTL.COLUMN_LIST);
        return result;
    }

    @Override
    public PageResult<ArticleListVO> getColumnArticles(Long columnId, Integer pageNum, Integer pageSize, String sortBy) {
        String cacheKey = CacheKey.keyForColumnArticles(columnId, pageNum, pageSize, sortBy);
        PageResult<ArticleListVO> result = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (result != null) {
            log.debug("从缓存获取专栏文章分页列表, columnId: {}, pageNum: {}, pageSize: {}", columnId, pageNum, pageSize);
            return result;
        }

        int offset = (pageNum - 1) * pageSize;
        List<ArticleListVO> articleList = articleColumnMapper.findArticlesByColumnId(columnId, offset, pageSize, sortBy);
        long total = articleColumnMapper.countArticlesByColumnId(columnId);

        fileService.ensureImageFullUrl(articleList);

        result = PageResult.build(articleList, total, pageNum, pageSize);
        log.info("从数据库获取专栏文章分页列表成功, 专栏ID: {}, 总数: {}, 页码: {}, 每页大小: {}", columnId, total, pageNum, pageSize);
        cacheManager.set(cacheKey, result, CacheTTL.COLUMN_ARTICLES);
        return result;
    }

    @Override
    public List<ColumnListVO> getHotColumns(Integer limit) {
        String cacheKey = CacheKey.keyForColumnHot(limit);
        List<ColumnListVO> hotColumns = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (hotColumns != null) {
            log.debug("从缓存获取热门专栏, limit: {}", limit);
            return hotColumns;
        }

        hotColumns = columnMapper.findHotColumns(limit);

        fileService.ensureImageFullUrl(hotColumns);

        log.info("从数据库获取热门专栏成功, limit: {}", limit);
        cacheManager.set(cacheKey, hotColumns, CacheTTL.COLUMN_HOT);
        return hotColumns;
    }

    @Override
    public PageResult<ArticleListVO> searchColumnArticles(Long columnId, String keyword, Integer pageNum, Integer pageSize) {
        String cacheKey = CacheKey.keyForColumnArticleSearch(columnId, keyword, pageNum, pageSize);
        PageResult<ArticleListVO> result = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (result != null) {
            log.debug("从缓存获取专栏文章搜索结果, columnId: {}, keyword: {}", columnId, keyword);
            return result;
        }

        int offset = (pageNum - 1) * pageSize;
        List<ArticleListVO> articleList = articleColumnMapper.searchArticlesByColumnId(columnId, keyword, offset, pageSize);
        long total = articleColumnMapper.countSearchArticlesByColumnId(columnId, keyword);

        fileService.ensureImageFullUrl(articleList);

        result = PageResult.build(articleList, total, pageNum, pageSize);
        log.info("从数据库搜索专栏文章成功, 专栏ID: {}, 关键词: {}, 总数: {}", columnId, keyword, total);
        if (total > 0) {
            cacheManager.set(cacheKey, result, CacheTTL.COLUMN_ARTICLES);
        } else {
            cacheManager.set(cacheKey, result, CacheTTL.FIVE_MINUTES);
        }
        return result;
    }
}
