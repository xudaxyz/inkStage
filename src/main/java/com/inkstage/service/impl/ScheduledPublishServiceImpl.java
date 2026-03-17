package com.inkstage.service.impl;

import com.inkstage.entity.model.Article;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时发布服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledPublishServiceImpl {

    private final ArticleMapper articleMapper;

    /**
     * 定时检查并发布文章
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void checkAndPublishArticles() {
        log.info("开始检查定时发布文章");

        try {
            LocalDateTime now = LocalDateTime.now();
            // 查询所有到达定时发布时间且状态为待发布的文章
            List<Article> articles = articleMapper.findScheduledArticles(now);

            log.info("发现 {} 篇需要定时发布的文章", articles.size());

            for (Article article : articles) {
                // 更新文章状态为已发布
                article.setArticleStatus(ArticleStatus.PUBLISHED);
                article.setPublishTime(now);
                article.setUpdateTime(now);

                int result = articleMapper.update(article);
                if (result > 0) {
                    log.info("文章 {} 定时发布成功", article.getId());
                } else {
                    log.warn("文章 {} 定时发布失败", article.getId());
                }
            }
        } catch (Exception e) {
            log.error("定时发布文章检查失败", e);
        }
    }
}
