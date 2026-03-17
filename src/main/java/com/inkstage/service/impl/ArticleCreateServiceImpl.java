package com.inkstage.service.impl;

import com.inkstage.common.ResponseMessage;
import com.inkstage.dto.front.ArticleCreateDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.entity.model.Tag;
import com.inkstage.entity.model.User;
import com.inkstage.enums.DeleteStatus;
import com.inkstage.enums.NotificationType;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.mapper.UserMapper;
import com.inkstage.service.ArticleCreateService;
import com.inkstage.service.ArticleTagService;
import com.inkstage.service.CategoryService;
import com.inkstage.service.NotificationService;
import com.inkstage.service.TagService;

import com.inkstage.utils.UserContext;
import com.inkstage.utils.MarkdownUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 文章创建和更新服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleCreateServiceImpl implements ArticleCreateService {

    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;
    private final TagService tagService;
    private final ArticleTagService articleTagService;
    private final NotificationService notificationService;
    private final CategoryService categoryService;
    private final AsyncArticleProcessServiceImpl asyncArticleProcessService;

    /**
     * 创建文章
     */
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
            handleArticleTags(article.getId(), articleCreateDTO.getTags());

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
            notificationService.sendNotificationWithTemplate(
                    currentUser.getId(),
                    NotificationType.ARTICLE_PUBLISH,
                    article.getId(),
                    0L, // 系统发送
                    article.getTitle()
            );

            log.info("文章创建成功, 文章ID: {}, 用户ID: {}", article.getId(), currentUser.getId());
            return article.getId();
        } catch (Exception e) {
            log.error("创建文章失败, 用户ID: {}", currentUser.getId(), e);
            throw new BusinessException(ResponseMessage.ARTICLE_PUBLISH_FAILED.format(ResponseMessage.ARTICLE_PUBLISH_FAILED.getMessage(), e.getMessage()), e);
        }
    }

    /**
     * 保存草稿
     */
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
     * 更新文章
     */
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
            if (content.length() > 10000) { // 内容超过10000字的文章视为大文章
                // 先设置一个临时的HTML内容
                existingArticle.setContentHtml("<p>正在处理文章内容...</p>");
            } else {
                // 小文章直接同步处理
                existingArticle.setContentHtml(MarkdownUtils.markdownToHtml(content));
            }

            existingArticle.setSummary(articleCreateDTO.getSummary());
            existingArticle.setCoverImage(articleCreateDTO.getCoverImage());
            existingArticle.setCategoryId(articleCreateDTO.getCategoryId());
            existingArticle.setArticleStatus(articleCreateDTO.getStatus());
            existingArticle.setReviewStatus(articleCreateDTO.getReviewStatus());
            existingArticle.setAllowComment(articleCreateDTO.getAllowComment());
            existingArticle.setOriginal(articleCreateDTO.getOriginal());
            existingArticle.setOriginalUrl(articleCreateDTO.getOriginalUrl());
            existingArticle.setMetaTitle(articleCreateDTO.getMetaTitle());
            existingArticle.setMetaDescription(articleCreateDTO.getMetaDescription());
            existingArticle.setMetaKeywords(articleCreateDTO.getMetaKeywords());
            existingArticle.setScheduledPublishTime(articleCreateDTO.getScheduledPublishTime());
            existingArticle.setLastEditTime(LocalDateTime.now());
            existingArticle.setVisible(articleCreateDTO.getVisible());
            existingArticle.setAllowForward(articleCreateDTO.getAllowForward());
            existingArticle.setTop(articleCreateDTO.getTop());
            existingArticle.setUpdateTime(LocalDateTime.now());

            // 如果状态变为已发布或待发布，更新发布时间
            if (ArticleStatus.PUBLISHED == articleCreateDTO.getStatus() || ArticleStatus.PENDING_PUBLISH == articleCreateDTO.getStatus()) {
                if (existingArticle.getPublishTime() == null) {
                    existingArticle.setPublishTime(LocalDateTime.now());
                }
            }

            // 更新文章
            int updateResult = articleMapper.update(existingArticle);
            if (updateResult <= 0) {
                log.warn("文章更新失败, 文章ID: {}, 用户ID: {}", articleId, currentUser.getId());
                throw new BusinessException(ResponseMessage.ARTICLE_UPDATE_FAILED, "文章更新失败");
            }

            // 处理分类文章数量更新
            Long newCategoryId = articleCreateDTO.getCategoryId();
            if (!oldCategoryId.equals(newCategoryId)) {
                // 减少旧分类的文章数
                categoryService.updateArticleCount(oldCategoryId, -1);
                // 增加新分类的文章数
                if (newCategoryId != null) {
                    categoryService.updateArticleCount(newCategoryId, 1);
                }
            }

            // 处理标签关联
            handleArticleTags(articleId, articleCreateDTO.getTags());

            // 对于大文章，异步处理Markdown转换
            String largeContent = articleCreateDTO.getContent();
            if (largeContent.length() > 10000) {
                asyncArticleProcessService.processArticleMarkdown(articleId, largeContent);
            }

            log.info("文章更新成功, 文章ID: {}, 用户ID: {}", articleId, currentUser.getId());
            return true;
        } catch (Exception e) {
            log.error("更新文章失败, 文章ID: {}, 用户ID: {}", articleId, currentUser.getId(), e);
            throw new BusinessException(ResponseMessage.ARTICLE_UPDATE_FAILED.getMessage() + ": " + e.getMessage(), e);
        }
    }

    /**
     * 构建文章实体
     */
    private Article buildArticle(ArticleCreateDTO dto, User user) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());

        // 对于大文章使用异步处理Markdown转换
        String content = dto.getContent();
        if (content.length() > 10000) { // 内容超过10000字的文章视为大文章
            // 先设置一个临时的HTML内容
            article.setContentHtml("<p>正在处理文章内容...</p>");
        } else {
            // 小文章直接同步处理
            article.setContentHtml(MarkdownUtils.markdownToHtml(content));
        }

        // 处理摘要
        String summary = dto.getSummary();
        if (summary == null || summary.trim().isEmpty()) {
            // 如果没有提供摘要, 自动从内容中提取
            summary = generateSummary(dto.getContent());

            // 如果内容为空，从标题生成摘要
            if (summary.isEmpty() && dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
                summary = dto.getTitle().trim();
                if (summary.length() > 100) {
                    summary = summary.substring(0, 100) + "...";
                }
            }
        }
        article.setSummary(summary);
        article.setCoverImage(dto.getCoverImage());
        article.setUserId(user.getId());
        article.setCategoryId(dto.getCategoryId());
        article.setArticleStatus(dto.getStatus());
        article.setReviewStatus(dto.getReviewStatus());
        article.setVisible(dto.getVisible());
        article.setAllowComment(dto.getAllowComment());
        article.setAllowForward(dto.getAllowForward());
        article.setOriginal(dto.getOriginal());
        article.setOriginalUrl(dto.getOriginalUrl());
        article.setMetaTitle(dto.getMetaTitle());
        article.setMetaDescription(dto.getMetaDescription());
        article.setMetaKeywords(dto.getMetaKeywords());
        article.setScheduledPublishTime(dto.getScheduledPublishTime());
        article.setTop(dto.getTop());
        article.setLastEditTime(LocalDateTime.now());
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        article.setDeleted(DeleteStatus.NOT_DELETED);

        // 初始化统计数据
        article.setReadCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setCollectionCount(0);
        article.setShareCount(0);

        // 设置发布时间
        if (ArticleStatus.PUBLISHED == dto.getStatus() || ArticleStatus.PENDING_PUBLISH == dto.getStatus()) {
            article.setPublishTime(LocalDateTime.now());
        }

        return article;
    }

    /**
     * 生成文章摘要
     */
    private String generateSummary(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }

        // 移除HTML标签(如果有)
        String plainText = content.replaceAll("<[^>]*>", "");

        // 移除Markdown标记
        plainText = plainText
                .replaceAll("#+ ", "") // 标题
                .replaceAll("\\*\\*", "") // 加粗
                .replaceAll("_+", "") // 斜体
                .replaceAll("`+", "") // 代码
                .replaceAll(">+", "") // 引用
                .replaceAll("\\*+", "") // 列表
                .replaceAll("-+", "") // 分割线
                .trim();

        // 截取前200字
        if (plainText.length() > 200) {
            // 尝试在句子结束处截断，使摘要更自然
            int endIndex = getEndIndex(plainText);
            plainText = plainText.substring(0, endIndex) + "...";
        }

        return plainText;
    }

    private int getEndIndex(String plainText) {
        int endIndex = plainText.lastIndexOf(".", 200);
        if (endIndex == -1) {
            endIndex = plainText.lastIndexOf("。", 200);
        }
        if (endIndex == -1) {
            endIndex = plainText.lastIndexOf("!", 200);
        }
        if (endIndex == -1) {
            endIndex = plainText.lastIndexOf("?", 200);
        }
        if (endIndex == -1) {
            endIndex = plainText.lastIndexOf(" ", 200);
        }
        if (endIndex == -1) {
            endIndex = 200;
        } else {
            endIndex += 1; // 包含标点符号
        }
        return endIndex;
    }

    /**
     * 处理文章标签关联
     */
    public void handleArticleTags(Long articleId, List<Tag> tags) {
        if (articleId == null) {
            log.warn("处理文章标签关联失败, 文章ID为空");
            return;
        }

        // 收集所有标签ID
        List<Long> allTagIds = new ArrayList<>();

        // 处理标签列表
        if (tags == null || tags.isEmpty()) {
            return;
        }
        for (Tag tag : tags) {
            if (tag == null || tag.getName() == null || tag.getName().trim().isEmpty()) {
                continue;
            }

            if (tag.getId() != null && tag.getId() > 0) {
                // 已存在的标签，直接使用其ID
                allTagIds.add(tag.getId());
            } else {
                // 新标签，需要创建
                try {
                    Long tagId = tagService.createTagIfNotExists(tag);
                    if (tagId != null) {
                        allTagIds.add(tagId);
                    }
                } catch (Exception e) {
                    // 标签创建失败，记录日志但不影响文章发布
                    log.error("创建标签失败, 标签名称: {}", tag.getName(), e);
                }
            }
        }

        if (allTagIds.isEmpty()) {
            // 没有标签, 清除现有关联
            articleTagService.deleteArticleTagsByArticleId(articleId);
            return;
        }

        // 保存文章标签关联
        articleTagService.saveArticleTags(articleId, allTagIds);
    }
}
