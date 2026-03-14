package com.inkstage.service.impl;

import com.inkstage.common.ResponseMessage;
import com.inkstage.dto.front.ArticleCreateDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.entity.model.User;
import com.inkstage.enums.DeleteStatus;
import com.inkstage.enums.NotificationType;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.service.ArticleCreateService;
import com.inkstage.service.NotificationService;
import com.inkstage.service.TagService;

import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章创建和更新服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleCreateServiceImpl implements ArticleCreateService {

    private final ArticleMapper articleMapper;
    private final TagService tagService;
    private final NotificationService notificationService;

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
        if (articleCreateDTO.getTitle() == null || articleCreateDTO.getTitle().trim().isEmpty()) {
            log.warn("文章标题为空");
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "文章标题不能为空");
        }
        if (articleCreateDTO.getContent() == null || articleCreateDTO.getContent().trim().isEmpty()) {
            log.warn("文章内容为空");
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "文章内容不能为空");
        }

        try {
            Article article = buildArticle(articleCreateDTO, currentUser);
            articleMapper.insert(article);

            // 处理标签关联
            handleArticleTags(article.getId(), articleCreateDTO.getTagIds());

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

                // 处理标签关联
                handleArticleTags(article.getId(), dto.getTagIds());

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

                // 处理标签关联
                handleArticleTags(id, dto.getTagIds());

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
        if (articleCreateDTO.getTitle() == null || articleCreateDTO.getTitle().trim().isEmpty()) {
            log.warn("文章标题为空");
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "文章标题不能为空");
        }
        if (articleCreateDTO.getContent() == null || articleCreateDTO.getContent().trim().isEmpty()) {
            log.warn("文章内容为空");
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "文章内容不能为空");
        }

        try {
            // 检查文章是否存在且属于当前用户
            Article existingArticle = articleMapper.findById(articleId);
            if (existingArticle == null || !existingArticle.getUserId().equals(currentUser.getId())) {
                log.warn("更新文章失败, 文章不存在或无权限, 文章ID: {}, 用户ID: {}", articleId, currentUser.getId());
                throw new BusinessException(ResponseMessage.NO_PERMISSION, "文章不存在或无权限");
            }

            // 构建更新后的文章实体
            existingArticle.setTitle(articleCreateDTO.getTitle());
            existingArticle.setContent(articleCreateDTO.getContent());
            existingArticle.setContentHtml(articleCreateDTO.getContent()); // 暂时直接使用内容, 后续需要添加 Markdown 转换
            existingArticle.setSummary(articleCreateDTO.getSummary());
            existingArticle.setCoverImage(articleCreateDTO.getCoverImage());
            existingArticle.setCategoryId(articleCreateDTO.getCategoryId());
            existingArticle.setArticleStatus(articleCreateDTO.getStatus());
            existingArticle.setReviewStatus(articleCreateDTO.getReviewStatus());
            existingArticle.setAllowComment(articleCreateDTO.getAllowComment());
            existingArticle.setOriginal(articleCreateDTO.getOriginal());
            existingArticle.setOriginalUrl(articleCreateDTO.getOriginalUrl());
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

            // 处理标签关联
            handleArticleTags(articleId, articleCreateDTO.getTagIds());

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
        article.setContentHtml(dto.getContent()); // 暂时直接使用内容, 后续需要添加 Markdown 转换

        // 处理摘要
        String summary = dto.getSummary();
        if (summary == null || summary.trim().isEmpty()) {
            // 如果没有提供摘要, 自动从内容中提取前200字
            summary = generateSummary(dto.getContent());
        }
        article.setSummary(summary);
        article.setCoverImage(dto.getCoverImage());
        article.setUserId(user.getId());
        article.setAuthorName(user.getNickname());
        article.setCategoryId(dto.getCategoryId());
        article.setArticleStatus(dto.getStatus());
        article.setReviewStatus(dto.getReviewStatus());
        article.setVisible(dto.getVisible());
        article.setAllowComment(dto.getAllowComment());
        article.setAllowForward(dto.getAllowForward());
        article.setOriginal(dto.getOriginal());
        article.setOriginalUrl(dto.getOriginalUrl());
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
                .replaceAll("#+", "") // 标题
                .replaceAll("\\*\\*", "") // 加粗
                .replaceAll("_+", "") // 斜体
                .replaceAll("`+", "") // 代码
                .replaceAll(">+", "") // 引用
                .replaceAll("\\*+", "") // 列表
                .replaceAll("-+", "") // 分割线
                .trim();

        // 截取前200字
        if (plainText.length() > 200) {
            plainText = plainText.substring(0, 200) + "...";
        }

        return plainText;
    }

    /**
     * 处理文章标签关联
     */
    private void handleArticleTags(Long articleId, List<Long> tagIds) {
        if (articleId == null) {
            log.warn("处理文章标签关联失败, 文章ID为空");
            return;
        }
        if (tagIds == null || tagIds.isEmpty()) {
            // 没有标签, 清除现有关联
            tagService.deleteArticleTagsByArticleId(articleId);
            return;
        }

        // 保存文章标签关联
        tagService.saveArticleTags(articleId, tagIds);
    }
}
