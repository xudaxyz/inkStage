package com.inkstage.service.impl;

import com.inkstage.entity.model.Article;
import com.inkstage.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 文章分享服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleShareServiceImpl {

    private final ArticleMapper articleMapper;

    /**
     * 生成文章分享链接
     *
     * @param articleId 文章ID
     * @return 分享链接
     */
    @Transactional(rollbackFor = Exception.class)
    public String generateShareLink(Long articleId) {
        log.info("生成文章分享链接，文章ID: {}", articleId);

        try {
            // 查询文章
            Article article = articleMapper.findById(articleId);
            if (article == null) {
                log.warn("文章不存在，文章ID: {}", articleId);
                throw new RuntimeException("文章不存在");
            }

            // 如果没有分享令牌，生成一个
            String shareToken = article.getShareToken();
            if (shareToken == null || shareToken.isEmpty()) {
                shareToken = generateShareToken();
                article.setShareToken(shareToken);
                articleMapper.update(article);
                log.info("生成新的分享令牌，文章ID: {}, 令牌: {}", articleId, shareToken);
            }

            // 生成分享链接
            String shareLink = "/share/article/" + shareToken;
            log.info("文章分享链接生成成功，文章ID: {}, 链接: {}", articleId, shareLink);
            return shareLink;
        } catch (Exception e) {
            log.error("生成文章分享链接失败，文章ID: {}", articleId, e);
            throw new RuntimeException("生成分享链接失败", e);
        }
    }

    /**
     * 根据分享令牌获取文章
     *
     * @param shareToken 分享令牌
     * @return 文章
     */
    public Article getArticleByShareToken(String shareToken) {
        log.info("根据分享令牌获取文章，令牌: {}", shareToken);

        try {
            // 查询文章
            Article article = articleMapper.findByShareToken(shareToken);
            if (article == null) {
                log.warn("分享链接无效，令牌: {}", shareToken);
                return null;
            }

            // 增加分享数
            article.setShareCount(article.getShareCount() != null ? article.getShareCount() + 1 : 1);
            articleMapper.update(article);
            log.info("文章分享数增加成功，文章ID: {}", article.getId());

            return article;
        } catch (Exception e) {
            log.error("根据分享令牌获取文章失败，令牌: {}", shareToken, e);
            return null;
        }
    }

    /**
     * 生成分享令牌
     *
     * @return 分享令牌
     */
    private String generateShareToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
