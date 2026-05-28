package com.inkstage.service.impl;

import com.inkstage.cache.service.CacheClearService;
import com.inkstage.cache.service.InteractionCacheService;
import com.inkstage.constant.InkConstant;
import com.inkstage.entity.model.Article;
import com.inkstage.entity.model.ArticleLike;
import com.inkstage.enums.CountType;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.mapper.ArticleLikeMapper;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.notification.param.ArticleLikeParam;
import com.inkstage.service.ArticleLikeService;
import com.inkstage.service.NotificationService;
import com.inkstage.utils.SnowflakeIdGenerator;
import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 文章点赞服务实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleLikeServiceImpl implements ArticleLikeService {

    private final ArticleLikeMapper articleLikeMapper;
    private final ArticleMapper articleMapper;
    private final CountProducer countProducer;
    private final NotificationService notificationService;
    private final InteractionCacheService interactionCacheService;
    private final CacheClearService cacheClearService;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    @Transactional
    public boolean likeArticle(Long articleId) {
        Long userId = UserContext.getCurrentUser().getId();
        log.info("点赞文章, 文章ID: {}, 用户ID: {}", articleId, userId);

        // 检查是否已点赞
        if (interactionCacheService.isArticleLiked(articleId, userId)) {
            log.warn("用户已点赞该文章, 文章ID: {}, 用户ID: {}", articleId, userId);
            return false;
        }

        // 创建点赞记录
        ArticleLike articleLike = new ArticleLike();
        articleLike.setArticleId(articleId);
        articleLike.setUserId(userId);
        articleLike.setCreateTime(LocalDateTime.now());
        articleLike.setDeleted(DeleteStatus.NOT_DELETED);
        articleLike.setId(snowflakeIdGenerator.nextId());
        int result = articleLikeMapper.insert(articleLike);

        if (result > 0) {
            // 增加点赞数
            countProducer.sendCountMessage(CountType.ARTICLE_LIKE, articleId, 1);

            String currentUserNickname = UserContext.getCurrentUser().getNickname();
            Article article = articleMapper.findById(articleId);
            if (article != null) {
                Long articleUserId = article.getUserId();
                countProducer.sendCountMessage(CountType.USER_LIKE, articleUserId, 1);
                String articleTitle = article.getTitle();

                // 只有当点赞者不是文章作者时才发送通知
                if (!userId.equals(articleUserId)) {
                    ArticleLikeParam param = ArticleLikeParam.builder()
                            .userId(articleUserId)
                            .username(currentUserNickname)
                            .articleTitle(articleTitle)
                            .articleId(articleId)
                            .articleUrl(InkConstant.ARTICLE_URL + articleId)
                            .senderId(userId)
                            .notificationType(NotificationType.ARTICLE_LIKE)
                            .build();
                    notificationService.send(param);
                }
            }

            // 清理点赞状态缓存
            cacheClearService.clearArticleLikeCache(articleId, userId);
            log.info("点赞成功, 文章ID: {}, 用户ID: {}", articleId, userId);
            return true;
        }
        return false;

    }

    @Override
    @Transactional
    public boolean unlikeArticle(Long articleId) {
        Long userId = UserContext.getCurrentUser().getId();
        log.info("取消点赞, 文章ID: {}, 用户ID: {}", articleId, userId);

        // 检查是否已点赞
        if (!interactionCacheService.isArticleLiked(articleId, userId)) {
            log.warn("用户未点赞该文章, 文章ID: {}, 用户ID: {}", articleId, userId);
            return false;
        }

        // 删除点赞记录
        int result = articleLikeMapper.purgeByArticleIdAndUserId(articleId, userId);

        if (result > 0) {
            countProducer.sendCountMessage(CountType.ARTICLE_LIKE, articleId, -1);
            Article article = articleMapper.findById(articleId);
            if (article != null) {
                Long articleUserId = article.getUserId();
                countProducer.sendCountMessage(CountType.USER_LIKE, articleUserId, -1);
            }
            cacheClearService.clearArticleLikeCache(articleId, userId);
            log.info("取消点赞成功, 文章ID: {}, 用户ID: {}", articleId, userId);
            return true;
        }
        return false;
    }

    @Override
    public boolean isArticleLiked(Long articleId) {
        Long userId = UserContext.getCurrentUser().getId();
        // 使用缓存服务查询点赞状态
        return interactionCacheService.isArticleLiked(articleId, userId);
    }

}