package com.inkstage.service.impl;

import com.inkstage.cache.service.CacheClearService;
import com.inkstage.common.ResponseMessage;
import com.inkstage.dto.front.ArticleCreateDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.entity.model.User;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.enums.article.TopStatus;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.enums.notification.NotificationTemplateVariable;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.mapper.UserMapper;
import com.inkstage.service.ArticleCommandService;
import com.inkstage.service.ArticleTagService;
import com.inkstage.service.CategoryService;
import com.inkstage.service.NotificationService;
import com.inkstage.utils.MarkdownUtils;
import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 文章命令服务实现类
 * 负责文章的写操作（创建、更新、删除）
 * 专注于业务逻辑，缓存清理委托给 ArticleCacheService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleCommandServiceImpl implements ArticleCommandService {

    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;
    private final ArticleTagService articleTagService;
    private final NotificationService notificationService;
    private final CategoryService categoryService;
    private final AsyncArticleProcessServiceImpl asyncArticleProcessService;
    private final CacheClearService cacheClearService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createArticle(ArticleCreateDTO articleCreateDTO) {
        // 从上下文获取用户信息
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            log.warn("创建文章失败, 未登录用户尝试创建文章");
            throw new BusinessException(ResponseMessage.NOT_LOGIN);
        }

        // 参数验证
        if (articleCreateDTO == null) {
            log.warn("创建文章参数为空");
            throw new BusinessException(ResponseMessage.PARAM_ERROR);
        }

        try {
            Article article = buildArticle(articleCreateDTO, currentUser);
            articleMapper.insert(article);

            // 处理标签关联
            articleTagService.handleArticleTags(article.getId(), articleCreateDTO.getTags());

            // 更新分类文章数量
            if (article.getCategoryId() != null) {
                categoryService.updateArticleCount(article.getCategoryId(), 1);
            }

            // 更新用户文章数
            User user = userMapper.findById(currentUser.getId());
            if (user != null) {
                int articleCount = user.getArticleCount() != null ? user.getArticleCount() : 0;
                user.setArticleCount(articleCount + 1);
                userMapper.updateByPrimaryKeySelective(user);
            }

            // 对于大文章，异步处理Markdown转换
            String content = articleCreateDTO.getContent();
            if (content.length() > 10000) {
                asyncArticleProcessService.processArticleMarkdown(article.getId(), content);
            }

            // 发送文章发布通知
            Map<String, Object> params = new HashMap<>();
            params.put(NotificationTemplateVariable.ARTICLE_TITLE.getKey(), article.getTitle());
            params.put(NotificationTemplateVariable.ARTICLE_ID.getKey(), article.getId());
            params.put(NotificationTemplateVariable.RELATED_ID.getKey(), article.getId());
            notificationService.sendNotificationWithTemplate(currentUser.getId(), NotificationType.ARTICLE_PUBLISH, params);

            log.info("文章创建成功, 文章ID: {}, 用户ID: {}", article.getId(), currentUser.getId());

            // 清理相关缓存
            cacheClearService.cleanCacheAfterArticleCreate(article.getId(), currentUser.getId());

            return article.getId();
        } catch (Exception e) {
            log.error("创建文章失败, 用户ID: {}", currentUser.getId(), e);
            throw new BusinessException(ResponseMessage.ARTICLE_PUBLISH_FAILED.format(ResponseMessage.ARTICLE_PUBLISH_FAILED.getMessage(), e.getMessage()), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticle(Long articleId, ArticleCreateDTO articleCreateDTO) {
        // 从上下文获取用户信息
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            log.warn("更新文章失败, 未登录用户尝试更新文章, 文章ID: {}", articleId);
            throw new BusinessException(ResponseMessage.NOT_LOGIN);
        }

        // 参数验证
        if (articleId == null || articleId <= 0) {
            log.warn("更新文章参数无效, 文章ID: {}", articleId);
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "文章ID无效");
        }
        if (articleCreateDTO == null) {
            log.warn("更新文章参数为空");
            throw new BusinessException(ResponseMessage.PARAM_ERROR);
        }

        try {
            // 检查文章是否存在且属于当前用户
            Article existingArticle = articleMapper.findById(articleId);
            if (existingArticle == null || !existingArticle.getUserId().equals(currentUser.getId())) {
                log.warn("更新文章失败, 文章不存在或无权限, 文章ID: {}, 用户ID: {}", articleId, currentUser.getId());
                throw new BusinessException(ResponseMessage.NO_PERMISSION, "文章不存在或无权限");
            }

            // 保存旧分类ID
            Long oldCategoryId = existingArticle.getCategoryId();

            // 构建更新后的文章实体
            existingArticle.setTitle(articleCreateDTO.getTitle());
            existingArticle.setContent(articleCreateDTO.getContent());

            // 对于大文章使用异步处理Markdown转换
            String content = articleCreateDTO.getContent();
            if (content.length() > 10000) {
                existingArticle.setContentHtml("<p>正在处理文章内容...</p>");
            } else {
                existingArticle.setContentHtml(MarkdownUtils.markdownToHtml(content));
            }

            existingArticle.setSummary(articleCreateDTO.getSummary());
            existingArticle.setCategoryId(articleCreateDTO.getCategoryId());
            existingArticle.setCoverImage(articleCreateDTO.getCoverImage());
            existingArticle.setArticleStatus(articleCreateDTO.getStatus());
            existingArticle.setReviewStatus(articleCreateDTO.getReviewStatus());
            existingArticle.setAllowComment(articleCreateDTO.getAllowComment());
            existingArticle.setOriginal(articleCreateDTO.getOriginal());
            existingArticle.setOriginalUrl(articleCreateDTO.getOriginalUrl());
            existingArticle.setMetaTitle(articleCreateDTO.getMetaTitle());
            existingArticle.setMetaKeywords(articleCreateDTO.getMetaKeywords());
            existingArticle.setMetaDescription(articleCreateDTO.getMetaDescription());
            existingArticle.setUpdateTime(LocalDateTime.now());

            // 处理文章标签关联
            articleTagService.handleArticleTags(articleId, articleCreateDTO.getTags());

            // 更新分类文章数量
            if (!existingArticle.getCategoryId().equals(oldCategoryId)) {
                if (oldCategoryId != null) {
                    categoryService.updateArticleCount(oldCategoryId, -1);
                }
                if (existingArticle.getCategoryId() != null) {
                    categoryService.updateArticleCount(existingArticle.getCategoryId(), 1);
                }
            }

            // 执行更新
            int result = articleMapper.update(existingArticle);
            boolean success = result > 0;

            if (success) {
                // 对于大文章，异步处理Markdown转换
                if (content.length() > 10000) {
                    asyncArticleProcessService.processArticleMarkdown(articleId, content);
                }

                // 清理文章详情、列表、搜索缓存
                cacheClearService.clearArticleDetailCache(articleId);
                cacheClearService.clearArticleListCache();
                cacheClearService.clearArticleSearchCache();

                log.info("文章更新成功, 文章ID: {}, 用户ID: {}", articleId, currentUser.getId());
            } else {
                log.warn("文章更新失败, 文章ID: {}, 用户ID: {}", articleId, currentUser.getId());
            }

            return success;
        } catch (Exception e) {
            log.error("更新文章失败, 文章ID: {}, 用户ID: {}", articleId, currentUser.getId(), e);
            throw new BusinessException(ResponseMessage.ARTICLE_UPDATE_FAILED.format(ResponseMessage.ARTICLE_UPDATE_FAILED.getMessage(), e.getMessage()), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteArticle(Long articleId) {
        try {
            log.debug("删除文章, 文章ID: {}", articleId);
            // 从上下文获取用户信息
            User currentUser = UserContext.getCurrentUser();
            // 检查文章是否存在且属于当前用户
            Article article = articleMapper.findById(articleId);
            if (article == null) {
                log.warn("文章不存在, 文章ID: {}", articleId);
                throw new BusinessException("文章不存在");
            }
            if (!article.getUserId().equals(currentUser.getId())) {
                log.warn("无权删除他人文章, 用户ID: {}, 文章ID: {}", currentUser.getId(), articleId);
                throw new BusinessException("无权删除他人文章");
            }
            // 执行删除操作
            int result = articleMapper.deleteById(articleId, currentUser.getId());
            boolean success = result > 0;
            if (success) {
                // 更新用户文章数
                User user = userMapper.findById(currentUser.getId());
                if (user != null) {
                    int articleCount = user.getArticleCount() != null ? user.getArticleCount() : 0;
                    if (articleCount > 0) {
                        user.setArticleCount(articleCount - 1);
                        userMapper.updateByPrimaryKeySelective(user);
                    }
                }

                // 清理所有相关缓存
                cacheClearService.clearArticleDetailCache(articleId);
                cacheClearService.clearArticleListCache();
                cacheClearService.clearHotArticleCache();
                cacheClearService.clearLatestArticleCache();
                cacheClearService.clearUserArticleListCache(currentUser.getId());
                cacheClearService.clearArticleSearchCache();
            }
            log.info("删除文章{}, 文章ID: {}", success ? "成功" : "失败", articleId);
            return success;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除文章失败, 文章ID: {}", articleId, e);
            throw new BusinessException("删除文章失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean permanentDeleteArticle(Long articleId) {
        try {
            log.debug("彻底删除文章, 文章ID: {}", articleId);
            Long currentUserId = UserContext.getCurrentUserId();
            // 检查文章是否存在且属于当前用户
            Article article = articleMapper.findById(articleId);
            if (article == null) {
                log.warn("文章ID: {}不存在", articleId);
                throw new BusinessException("文章不存在");
            }
            if (!article.getUserId().equals(currentUserId)) {
                log.warn("无权彻底删除他人文章, 用户ID: {}, 文章ID: {}", currentUserId, articleId);
                throw new BusinessException("无权删除他人文章");
            }
            // 执行彻底删除操作
            int result = articleMapper.permanentDeleteById(articleId, currentUserId);
            boolean success = result > 0;
            if (success) {
                // 更新用户文章数
                User user = userMapper.findById(currentUserId);
                if (user != null) {
                    int articleCount = user.getArticleCount() != null ? user.getArticleCount() : 0;
                    if (articleCount > 0) {
                        user.setArticleCount(articleCount - 1);
                        userMapper.updateByPrimaryKeySelective(user);
                    }
                }

                // 清理所有相关缓存
                cacheClearService.clearUserArticleListCache(currentUserId);
                cacheClearService.clearArticleDetailCache(articleId);
                cacheClearService.clearArticleListCache();
                cacheClearService.clearLatestArticleCache();
                cacheClearService.clearHotArticleCache();
                cacheClearService.clearArticleSearchCache();
            }
            log.info("彻底删除文章{}, 文章ID: {}", success ? "成功" : "失败", articleId);
            return success;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("彻底删除文章失败, 文章ID: {}", articleId, e);
            throw new BusinessException("彻底删除文章失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveDraft(Long id, ArticleCreateDTO dto) {
        // 从上下文获取用户信息
        User currentUser = UserContext.getCurrentUser();

        // 参数验证
        if (dto == null) {
            log.warn("保存草稿参数为空");
            throw new BusinessException(ResponseMessage.PARAM_ERROR);
        }
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            log.warn("草稿标题为空");
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "草稿标题不能为空");
        }

        try {
            if (id == null) {
                // 创建新草稿
                Article article = buildArticle(dto, currentUser);
                article.setArticleStatus(ArticleStatus.DRAFT);
                article.setReviewStatus(null);
                article.setPublishTime(null);
                articleMapper.insert(article);

                log.info("新草稿创建成功, 草稿ID: {}, 用户ID: {}", article.getId(), currentUser.getId());
                return article.getId();
            } else {
                // 更新现有草稿
                Article existingArticle = articleMapper.findById(id);
                if (existingArticle == null || !existingArticle.getUserId().equals(currentUser.getId())) {
                    log.warn("更新草稿失败, 草稿不存在或无权限, 草稿ID: {}, 用户ID: {}", id, currentUser.getId());
                    throw new BusinessException(ResponseMessage.NO_PERMISSION, "草稿不存在或无权限");
                }

                Article article = buildArticle(dto, currentUser);
                article.setId(id);
                article.setArticleStatus(ArticleStatus.DRAFT);
                article.setReviewStatus(null);
                article.setPublishTime(null);
                article.setCreateTime(existingArticle.getCreateTime());
                article.setReadCount(existingArticle.getReadCount());
                article.setLikeCount(existingArticle.getLikeCount());
                article.setCommentCount(existingArticle.getCommentCount());
                article.setCollectionCount(existingArticle.getCollectionCount());
                article.setShareCount(existingArticle.getShareCount());

                int updateResult = articleMapper.update(article);
                if (updateResult == 0) {
                    log.warn("草稿更新失败, 草稿ID: {}, 用户ID: {}", id, currentUser.getId());
                    throw new BusinessException(ResponseMessage.ARTICLE_DRAFT_FAILED, "草稿更新失败");
                }

                log.info("草稿更新成功, 草稿ID: {}, 用户ID: {}", id, currentUser.getId());
                return id;
            }
        } catch (Exception e) {
            log.error("保存草稿失败, 用户ID: {}", currentUser.getId(), e);
            throw new BusinessException(ResponseMessage.ARTICLE_DRAFT_FAILED.format(ResponseMessage.ARTICLE_DRAFT_FAILED.getMessage(), e.getMessage()), e);
        }
    }

    /**
     * 构建文章实体
     */
    private Article buildArticle(ArticleCreateDTO articleCreateDTO, User user) {
        Article article = new Article();
        article.setUserId(user.getId());
        article.setTitle(articleCreateDTO.getTitle());
        article.setContent(articleCreateDTO.getContent());
        article.setContentHtml(MarkdownUtils.markdownToHtml(articleCreateDTO.getContent()));
        article.setSummary(articleCreateDTO.getSummary());
        article.setCoverImage(articleCreateDTO.getCoverImage());
        article.setCategoryId(articleCreateDTO.getCategoryId());
        article.setArticleStatus(articleCreateDTO.getStatus());
        article.setReviewStatus(articleCreateDTO.getReviewStatus());
        article.setAllowComment(articleCreateDTO.getAllowComment());
        article.setOriginal(articleCreateDTO.getOriginal());
        article.setOriginalUrl(articleCreateDTO.getOriginalUrl());
        article.setMetaTitle(articleCreateDTO.getMetaTitle());
        article.setMetaDescription(articleCreateDTO.getMetaDescription());
        article.setMetaKeywords(articleCreateDTO.getMetaKeywords());
        article.setTop(TopStatus.NOT_TOP);
        article.setVisible(articleCreateDTO.getVisible());
        article.setAllowForward(articleCreateDTO.getAllowForward());
        article.setPublishTime(LocalDateTime.now());
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        article.setDeleted(DeleteStatus.NOT_DELETED);
        article.setReadCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setCollectionCount(0);
        article.setShareCount(0);
        article.setArticleVersion(1);
        return article;
    }
}
