package com.inkstage.service.impl;

import com.inkstage.common.PageRequest;
import com.inkstage.common.PageResult;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.service.ArticleSearchService;
import com.inkstage.service.FileService;
import com.inkstage.utils.RedisUtil;
import com.inkstage.vo.front.ArticleListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 文章搜索服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleSearchServiceImpl implements ArticleSearchService {

    private final ArticleMapper articleMapper;
    private final FileService fileService;
    private final RedisUtil redisUtil;

    @Override
    public PageResult<ArticleListVO> searchArticles(String keyword, String sortBy, Integer pageNum, Integer pageSize) {
        try {
            // 生成缓存键
            String cacheKey = "article:search:" + keyword + ":" + sortBy + ":" + pageNum + ":" + pageSize;

            // 尝试从缓存获取
            var pageResult = redisUtil.getWithType(cacheKey, new tools.jackson.core.type.TypeReference<PageResult<ArticleListVO>>() {});
            if (pageResult != null) {
                return pageResult;
            }

            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;

            // 查询搜索结果
            var articleList = articleMapper.searchArticles(keyword, sortBy, offset, pageSize);
            // 确保文章相关图片正常显示
            fileService.ensureArticleImageAreFullUrl(articleList);
            // 查询总记录数
            long total = articleMapper.countSearchArticles(keyword);

            // 构建分页结果
            pageResult = PageResult.build(
                    articleList,
                    total,
                    pageNum,
                    pageSize
            );

            // 只缓存有结果的搜索
            if (total > 0) {
                // 更新缓存
                redisUtil.set(cacheKey, pageResult, 30, TimeUnit.MINUTES);
            }

            return pageResult;
        } catch (Exception e) {
            log.error("搜索文章失败, 关键词: {}, 排序方式: {}", keyword, sortBy, e);
            throw new BusinessException("搜索文章失败");
        }
    }
}