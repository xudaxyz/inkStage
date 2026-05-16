package com.inkstage.service.impl;

import com.inkstage.entity.model.Article;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.utils.MarkdownUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文章异步处理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncArticleProcessServiceImpl {

    private final ArticleMapper articleMapper;

    /**
     * 异步处理文章内容（Markdown转换）
     *
     * @param articleId       文章ID
     * @param markdownContent Markdown内容
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void processArticleContent(Long articleId, String markdownContent) {
        log.info("开始异步处理文章内容，文章ID: {}", articleId);

        try {
            String htmlContent = MarkdownUtils.markdownToHtml(markdownContent);

            Article article = new Article();
            article.setId(articleId);
            article.setContentHtml(htmlContent);

            int result = articleMapper.update(article);
            if (result > 0) {
                log.info("文章内容处理完成，文章ID: {}", articleId);
            } else {
                log.warn("文章内容处理更新失败，文章ID: {}", articleId);
            }
        } catch (Exception e) {
            log.error("异步处理文章内容失败，文章ID: {}", articleId, e);
        }
    }
}
