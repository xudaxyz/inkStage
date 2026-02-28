package com.inkstage.service.impl;

import com.inkstage.constant.RedisKeyConstants;
import com.inkstage.entity.model.ArticleCollection;
import com.inkstage.enums.DeleteStatus;
import com.inkstage.mapper.ArticleCollectionMapper;
import com.inkstage.service.ArticleCollectionService;
import com.inkstage.service.CountService;
import com.inkstage.utils.RedisUtil;
import com.inkstage.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 文章收藏服务实现类
 */
@Slf4j
@Service
public class ArticleCollectionServiceImpl implements ArticleCollectionService {

    private final ArticleCollectionMapper articleCollectionMapper;
    private final RedisUtil redisUtil;
    private final CountService countService;

    @Autowired
    public ArticleCollectionServiceImpl(ArticleCollectionMapper articleCollectionMapper, RedisUtil redisUtil, CountService countService) {
        this.articleCollectionMapper = articleCollectionMapper;
        this.redisUtil = redisUtil;
        this.countService = countService;
    }

    @Override
    @Transactional
    public boolean collectArticle(Long articleId, Long folderId) {
        log.info("收藏文章, 文章ID: {}, 文件夹ID: {}", articleId, folderId);

        Long userId = UserContext.getCurrentUser().getId();
        // 检查是否已收藏
        if (isArticleCollected(articleId)) {
            log.warn("用户已收藏该文章, 文章ID: {}, 用户ID: {}", articleId, userId);
            return false;
        }

        // 创建收藏记录
        ArticleCollection collection = new ArticleCollection();
        collection.setArticleId(articleId);
        collection.setUserId(userId);
        collection.setFolderId(folderId);
        collection.setCreateTime(LocalDateTime.now());
        collection.setDeleted(DeleteStatus.NOT_DELETED);
        int result = articleCollectionMapper.insert(collection);

        if (result > 0) {
            // 增加收藏数
            countService.updateArticleCollectionCount(articleId, result);
            // 缓存收藏状态
            String collectKey = RedisKeyConstants.buildCacheKey("article:collect", articleId + ":" + userId);
            redisUtil.set(collectKey, true, 24, TimeUnit.HOURS);
            log.info("收藏成功, 文章ID: {}, 用户ID: {}", articleId, userId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean unCollectArticle(Long articleId) {
        log.info("取消收藏, 文章ID: {}", articleId);
        Long userId = UserContext.getCurrentUser().getId();

        // 检查是否已收藏
        if (!isArticleCollected(articleId)) {
            log.warn("用户未收藏该文章, 文章ID: {}, 用户ID: {}", articleId, userId);
            return false;
        }

        // 删除收藏记录
        int result = articleCollectionMapper.deleteByArticleIdAndUserId(articleId, userId);

        if (result > 0) {
            // 减少收藏数
            countService.updateArticleCollectionCount(articleId, -result);
            // 删除缓存
            String collectKey = RedisKeyConstants.buildCacheKey("article:collect", articleId + ":" + userId);
            redisUtil.delete(collectKey);
            log.info("取消收藏成功, 文章ID: {}, 用户ID: {}", articleId, userId);
            return true;
        }
        return false;
    }

    @Override
    public boolean isArticleCollected(Long articleId) {
        Long userId = UserContext.getCurrentUser().getId();
        // 先从缓存获取
        String collectKey = RedisKeyConstants.buildCacheKey("article:collect", articleId + ":" + userId);
        Boolean isCollected = redisUtil.get(collectKey, Boolean.class);
        if (isCollected != null) {
            return isCollected;
        }

        // 从数据库查询
        int count = articleCollectionMapper.countByArticleIdAndUserId(articleId, userId);
        boolean result = count > 0;

        // 更新缓存
        redisUtil.set(collectKey, result, 24, TimeUnit.HOURS);
        return result;
    }

}