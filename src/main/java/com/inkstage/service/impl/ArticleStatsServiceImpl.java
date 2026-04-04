package com.inkstage.service.impl;

import com.inkstage.cache.constant.RedisKeyConstants;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.service.ArticleStatsService;
import com.inkstage.cache.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 文章统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleStatsServiceImpl implements ArticleStatsService {

    private final ArticleMapper articleMapper;
    private final RedisUtil redisUtil;

    @Override
    public void incrementArticleReadCount(Long articleId, int count) {
        try {
            log.info("增加文章阅读数, 文章ID: {}, 增量: {}", articleId, count);

            // 先更新Redis中的计数（使用Redis的原子操作）
            String cacheKey = RedisKeyConstants.buildArticleCountCacheKey(articleId, "read");
            redisUtil.increment(cacheKey, count);

            // 设置过期时间为1小时
            redisUtil.expire(cacheKey, 1, TimeUnit.HOURS);

            // 异步更新数据库
            syncArticleReadCount(articleId, count);

            log.info("增加文章阅读数成功, 文章ID: {}, 增量: {}", articleId, count);
        } catch (Exception e) {
            log.error("增加文章阅读数失败, 文章ID: {}, 增量: {}", articleId, count, e);
            // 这里不抛出异常，避免影响用户体验
        }
    }

    @Async
    public void syncArticleReadCount(Long articleId, int count) {
        try {
            articleMapper.updateReadCount(articleId, count);
            log.info("同步文章阅读数到数据库成功, 文章ID: {}, 增量: {}", articleId, count);
        } catch (Exception e) {
            log.error("同步文章阅读数到数据库失败, 文章ID: {}, 增量: {}", articleId, count, e);
        }
    }
}