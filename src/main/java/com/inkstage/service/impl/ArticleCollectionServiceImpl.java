package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.constant.RedisKeyConstants;
import com.inkstage.dto.front.CollectArticleDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.entity.model.ArticleCollection;
import com.inkstage.entity.model.CollectionFolder;
import com.inkstage.enums.CollectionStatus;
import com.inkstage.enums.DeleteStatus;
import com.inkstage.enums.NotificationType;
import com.inkstage.mapper.ArticleCollectionMapper;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.mapper.CollectionFolderMapper;
import com.inkstage.service.*;
import com.inkstage.utils.RedisUtil;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.CollectionArticleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 文章收藏服务实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleCollectionServiceImpl implements ArticleCollectionService {

    private final ArticleCollectionMapper articleCollectionMapper;
    private final CollectionFolderMapper collectionFolderMapper;
    private final RedisUtil redisUtil;
    private final CountService countService;
    private final FileService fileService;
    private final CollectionFolderService collectionFolderService;
    private final NotificationService notificationService;
    private final ArticleMapper articleMapper;

    @Override
    @Transactional
    public boolean collectArticle(CollectArticleDTO collectArticleDTO) {
        log.info("收藏文章, 文章ID: {}, 文件夹ID: {}, 文件夹名称: {}",
                collectArticleDTO.getArticleId(), collectArticleDTO.getFolderId(), collectArticleDTO.getFolderName());

        Long userId = UserContext.getCurrentUser().getId();
        // 检查是否已收藏
        if (isArticleCollected(collectArticleDTO.getArticleId())) {
            log.warn("用户已收藏该文章, 文章ID: {}, 用户ID: {}", collectArticleDTO.getArticleId(), userId);
            return false;
        }

        Long folderId = collectArticleDTO.getFolderId();
        // 处理文件夹
        if (collectArticleDTO.getFolderName() != null && !collectArticleDTO.getFolderName().isEmpty()) {
            // 创建新收藏夹
            folderId = collectionFolderService.createCollectionFolder(collectArticleDTO);
        } else if (folderId == null || folderId == 0) {
            // 使用默认收藏夹
            CollectionFolder defaultFolder = collectionFolderService.getDefaultFolder(userId);
            folderId = defaultFolder.getId();
        }

        // 创建收藏记录
        ArticleCollection collection = new ArticleCollection();
        collection.setArticleId(collectArticleDTO.getArticleId());
        collection.setUserId(userId);
        collection.setFolderId(folderId);
        collection.setStatus(CollectionStatus.PUBLIC);
        collection.setCollectTime(LocalDateTime.now());
        collection.setCreateTime(LocalDateTime.now());
        collection.setDeleted(DeleteStatus.NOT_DELETED);
        int result = articleCollectionMapper.insert(collection);

        if (result > 0) {
            // 增加收藏数
            countService.updateArticleCollectionCount(collectArticleDTO.getArticleId(), result);
            // 更新收藏文件夹文章数量
            if (folderId != null && folderId > 0) {
                collectionFolderMapper.updateArticleCount(folderId, result);
                log.info("更新收藏文件夹文章数量, 文件夹ID: {}, 增加数量: {}", folderId, result);
            }
            // 缓存收藏状态
            String collectKey = RedisKeyConstants.buildCacheKey("article:collect:", collectArticleDTO.getArticleId() + ":" + userId);
            redisUtil.set(collectKey, true, 24, TimeUnit.HOURS);

            // 发送收藏通知
            String currentUserNickname = UserContext.getCurrentUser().getNickname();
            // 从文章服务获取文章信息
            Article article = articleMapper.findById(collectArticleDTO.getArticleId());
            if (article != null) {
                Long articleUserId = article.getUserId();
                String articleTitle = article.getTitle();

                // 只有当收藏者不是文章作者时才发送通知
                if (!userId.equals(articleUserId)) {
                    notificationService.sendNotificationWithTemplate(
                            articleUserId,
                            NotificationType.ARTICLE_COLLECTION,
                            collectArticleDTO.getArticleId(),
                            userId,
                            currentUserNickname,
                            articleTitle
                    );
                }
            }

            log.info("收藏成功, 文章ID: {}, 用户ID: {}, 文件夹ID: {}", collectArticleDTO.getArticleId(), userId, folderId);
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

        // 查询收藏记录，获取文件夹ID
        ArticleCollection collection = articleCollectionMapper.findByArticleIdAndUserId(articleId, userId);
        Long folderId = collection != null ? collection.getFolderId() : null;

        // 删除收藏记录
        int result = articleCollectionMapper.deleteByArticleIdAndUserId(articleId, userId);

        if (result > 0) {
            // 减少收藏数
            countService.updateArticleCollectionCount(articleId, -result);
            // 更新收藏文件夹文章数量
            if (folderId != null && folderId > 0) {
                collectionFolderMapper.updateArticleCount(folderId, -result);
                log.info("更新收藏文件夹文章数量, 文件夹ID: {}, 减少数量: {}", folderId, result);
            }
            // 删除缓存
            String collectKey = RedisKeyConstants.buildCacheKey("article:collect:", articleId + ":" + userId);
            redisUtil.delete(collectKey);
            log.info("取消收藏成功, 文章ID: {}, 用户ID: {}, 文件夹ID: {}", articleId, userId, folderId);
            return true;
        }
        return false;
    }

    @Override
    public boolean isArticleCollected(Long articleId) {
        Long userId = UserContext.getCurrentUser().getId();
        // 先从缓存获取
        String collectKey = RedisKeyConstants.buildCacheKey("article:collect:", articleId + ":" + userId);
        Boolean isCollected = redisUtil.get(collectKey, Boolean.class);
        if (isCollected != null) {
            return isCollected;
        }

        // 从数据库查询
        boolean result = articleCollectionMapper.findByArticleIdAndUserId(articleId, userId) != null;

        // 更新缓存
        redisUtil.set(collectKey, result, 24, TimeUnit.HOURS);
        return result;
    }

    @Override
    public PageResult<CollectionArticleVO> getCollectionArticles(Long folderId, Integer page, Integer size, String sortBy, String sortOrder, String keyword) {
        log.info("获取用户收藏文章列表, 文件夹ID: {}, 页码: {}, 每页大小: {}, 排序字段: {}, 排序方向: {}, 关键词: {}",
                folderId, page, size, sortBy, sortOrder, keyword);

        Long userId = UserContext.getCurrentUser().getId();

        // 计算偏移量
        int offset = (page - 1) * size;

        // 查询文章列表
        List<CollectionArticleVO> collectionArticlesVO = articleCollectionMapper.findCollectionArticles(
                userId, folderId, keyword, sortBy, sortOrder, offset, size);

        // 确保图片URL完整
        fileService.ensureCollectionArticleImgAreFullUrl(collectionArticlesVO);
        // 查询总数
        long total = articleCollectionMapper.countCollectionArticles(userId, folderId, keyword);

        // 构建分页结果
        PageResult<CollectionArticleVO> pageResult = new PageResult<>();
        pageResult.setRecord(collectionArticlesVO);
        pageResult.setTotal(total);
        pageResult.setPageSize(size);
        pageResult.setPageNum(page);
        pageResult.setPages((int) ((total + size - 1) / size));

        return pageResult;
    }

    @Override
    public List<CollectionFolder> getCollectionFolders() {
        log.info("获取用户收藏文件夹列表");
        Long userId = UserContext.getCurrentUser().getId();

        // 获取用户的所有收藏文件夹
        List<CollectionFolder> folders = collectionFolderMapper.selectByUserId(userId);

        // 如果没有文件夹，创建一个默认收藏文件夹
        if (folders == null || folders.isEmpty()) {
            collectionFolderService.createDefaultFolder(userId);
            folders = collectionFolderMapper.selectByUserId(userId);
        }

        return folders;
    }

    @Override
    public long getTotalCollectionCount() {
        log.info("获取用户总收藏数");
        Long userId = UserContext.getCurrentUser().getId();

        // 不指定文件夹ID和关键词，获取用户的总收藏数
        return articleCollectionMapper.countCollectionArticles(userId, null, null);
    }

    @Override
    @Transactional
    public boolean moveCollectionArticle(Long articleId, Long targetFolderId) {
        log.info("移动收藏文章到其他文件夹, 文章ID: {}, 目标文件夹ID: {}", articleId, targetFolderId);

        Long userId = UserContext.getCurrentUser().getId();

        // 检查是否已收藏
        if (!isArticleCollected(articleId)) {
            log.warn("用户未收藏该文章, 文章ID: {}, 用户ID: {}", articleId, userId);
            return false;
        }

        // 获取当前收藏记录
        ArticleCollection collection = articleCollectionMapper.findByArticleIdAndUserId(articleId, userId);
        if (collection == null) {
            log.warn("收藏记录不存在, 文章ID: {}, 用户ID: {}", articleId, userId);
            return false;
        }

        // 检查目标文件夹是否存在且属于当前用户
        CollectionFolder targetFolder = collectionFolderService.getCollectionFolderById(targetFolderId);
        if (targetFolder == null) {
            log.warn("目标文件夹不存在或无权限, 文件夹ID: {}, 用户ID: {}", targetFolderId, userId);
            return false;
        }

        Long sourceFolderId = collection.getFolderId();

        // 更新收藏记录的文件夹ID
        collection.setFolderId(targetFolderId);
        collection.setUpdateTime(LocalDateTime.now());
        int updateResult = articleCollectionMapper.update(collection);

        if (updateResult > 0) {
            // 更新源文件夹文章数量（减少）
            if (sourceFolderId != null && sourceFolderId > 0) {
                collectionFolderMapper.updateArticleCount(sourceFolderId, -1);
                log.info("更新源收藏文件夹文章数量, 文件夹ID: {}, 减少数量: 1", sourceFolderId);
            }

            // 更新目标文件夹文章数量（增加）
            if (targetFolderId > 0) {
                collectionFolderMapper.updateArticleCount(targetFolderId, 1);
                log.info("更新目标收藏文件夹文章数量, 文件夹ID: {}, 增加数量: 1", targetFolderId);
            }

            log.info("移动收藏文章成功, 文章ID: {}, 源文件夹ID: {}, 目标文件夹ID: {}", articleId, sourceFolderId, targetFolderId);
            return true;
        }

        return false;
    }

}