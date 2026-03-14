package com.inkstage.service.impl;

import com.inkstage.mapper.ArticleMapper;
import com.inkstage.service.ArticleStatsService;
import com.inkstage.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
            log.debug("增加文章阅读数, 文章ID: {}, 增量: {}", articleId, count);
            
            // 先更新Redis中的计数（使用Redis的原子操作）
            String cacheKey = "article:read:count:" + articleId;
            redisUtil.increment(cacheKey, count);
            
            // 然后异步更新数据库（这里简化处理，实际项目中可以使用消息队列）
            articleMapper.updateReadCount(articleId, count);
            
            log.info("增加文章阅读数成功, 文章ID: {}, 增量: {}", articleId, count);
        } catch (Exception e) {
            log.error("增加文章阅读数失败, 文章ID: {}, 增量: {}", articleId, count, e);
            // 这里不抛出异常，避免影响用户体验
        }
    }
}