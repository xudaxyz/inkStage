package com.inkstage.service.impl;


import com.inkstage.cache.service.CacheClearService;
import com.inkstage.common.PageResult;
import com.inkstage.dto.admin.AdminArticleQueryDTO;
import com.inkstage.dto.admin.AdminArticleUpdateDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.entity.model.Tag;
import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.service.AdminArticleService;
import com.inkstage.service.ArticleTagService;
import com.inkstage.service.FileService;
import com.inkstage.service.NotificationService;
import com.inkstage.utils.ArticleUtils;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.admin.AdminArticleDetailVO;
import com.inkstage.vo.admin.AdminArticleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 管理员文章服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminArticleServiceImpl implements AdminArticleService {

    private final ArticleMapper articleMapper;
    private final FileService fileService;
    private final ArticleTagService articleTagService;
    private final NotificationService notificationService;
    private final CacheClearService cacheClearService;

    @Override
    public PageResult<AdminArticleVO> getAdminArticlesByPage(AdminArticleQueryDTO queryDTO) {
        try {
            log.debug("管理员获取文章列表, 页码: {}, 每页大小: {}, 关键词: {}, 分类ID: {}, 文章状态: {}",
                    queryDTO.getPageNum(), queryDTO.getPageSize(), queryDTO.getKeyword(),
                    queryDTO.getCategoryId(), queryDTO.getArticleStatus());

            // 计算偏移量
            int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
            queryDTO.setOffset(offset);

            // 查询文章列表
            var articleList = articleMapper.findAdminArticlesByPage(queryDTO);
            // 查询总记录数
            long total = articleMapper.countByPage(queryDTO);

            // 构建分页结果
            var pageResult = PageResult.build(
                    articleList,
                    total,
                    queryDTO.getPageNum(),
                    queryDTO.getPageSize()
            );

            log.info("管理员获取文章列表成功, 总数: {}", total);
            return pageResult;
        } catch (Exception e) {
            log.error("管理员获取文章列表失败, 页码: {}, 每页大小: {}", queryDTO.getPageNum(), queryDTO.getPageSize(), e);
            throw new BusinessException("获取文章列表失败");
        }
    }

    /**
     * 清理文章相关缓存（管理员操作后）
     */
    private void clearCacheAfterAdminOperation(Long articleId) {
        // 清理文章详情、列表、热门、搜索缓存
        cacheClearService.clearArticleDetailCache(articleId);
        cacheClearService.clearArticleListCache();
        cacheClearService.clearHotArticleCache();
        cacheClearService.clearArticleSearchCache();
    }

    @Override
    public boolean updateArticleStatus(Long id, ArticleStatus status) {
        try {
            log.debug("更新文章状态, 文章ID: {}, 状态: {}", id, status.getDesc());
            // 检查文章是否存在
            Article article = ArticleUtils.getArticleSafely(articleMapper, id);
            // 更新状态
            int result = articleMapper.updateStatus(id, status);
            ArticleUtils.checkOperationResult(result, id, "更新文章状态");

            // 发送通知
            if (status == ArticleStatus.OFFLINE) {
                // 文章下架通知
                notificationService.sendArticleNotification(article.getUserId(), NotificationType.ARTICLE_OFFLINE, article);
            } else if (status == ArticleStatus.PUBLISHED) {
                // 文章上架通知
                notificationService.sendArticleNotification(article.getUserId(), NotificationType.ARTICLE_ONLINE, article);
            }

            // 清理相关缓存
            clearCacheAfterAdminOperation(id);

            log.info("更新文章状态并发送通知成功, 文章ID: {}, 状态: {}", id, status.getDesc());
            return result > 0;
        } catch (Exception e) {
            log.error("更新文章状态失败, 文章ID: {}, 状态: {}", id, status.getDesc(), e);
            throw new BusinessException("更新文章状态失败");
        }
    }

    @Override
    public boolean approveArticle(Long id) {
        try {
            log.debug("审核通过文章, 文章ID: {}", id);
            // 检查文章是否存在
            Article article = ArticleUtils.getArticleSafely(articleMapper, id);
            // 更新审核状态为通过
            int result = articleMapper.updateReviewStatus(id, ReviewStatus.APPROVED);
            ArticleUtils.checkOperationResult(result, id, "审核通过文章");
            // 更新文章状态为已发布
            articleMapper.updateStatus(id, ArticleStatus.PUBLISHED);

            // 发送文章审核通过通知
            notificationService.sendArticleNotification(article.getUserId(), NotificationType.ARTICLE_REVIEW_APPROVE, article);
            // 清理相关缓存
            clearCacheAfterAdminOperation(id);

            log.info("审核通过文章成功, 文章ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("审核通过文章失败, 文章ID: {}", id, e);
            throw new BusinessException("审核通过文章失败");
        }
    }

    @Override
    public boolean rejectArticle(Long id, String reason) {
        try {
            log.debug("审核拒绝文章, 文章ID: {}, 原因: {}", id, reason);
            // 检查文章是否存在
            Article article = ArticleUtils.getArticleSafely(articleMapper, id);
            // 更新审核状态为拒绝
            int result = articleMapper.updateReviewStatus(id, ReviewStatus.REJECTED);
            ArticleUtils.checkOperationResult(result, id, "审核拒绝文章");
            // 发送通知
            notificationService.sendArticleNotification(article.getUserId(), NotificationType.ARTICLE_REVIEW_REJECT, article);
            log.info("审核拒绝文章并发送通知成功, 文章ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("审核拒绝文章失败, 文章ID: {}", id, e);
            throw new BusinessException("审核拒绝文章失败");
        }
    }

    @Override
    public boolean reprocessArticle(Long id) {
        try {
            log.debug("重新审核文章, 文章ID: {}", id);
            // 检查文章是否存在
            Article article = ArticleUtils.getArticleSafely(articleMapper, id);
            // 更新审核状态为待审核
            int result = articleMapper.updateReviewStatus(id, com.inkstage.enums.ReviewStatus.PENDING);
            ArticleUtils.checkOperationResult(result, id, "重新审核文章");
            // 发送通知
            notificationService.sendArticleNotification(article.getUserId(), NotificationType.ARTICLE_REVIEW_REPROCESS, article);
            log.info("重新审核文章并发送通知成功, 文章ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("重新审核文章失败, 文章ID: {}", id, e);
            throw new BusinessException("重新审核文章失败");
        }
    }

    @Override
    public AdminArticleDetailVO getAdminArticleDetail(Long id) {
        try {
            log.debug("获取管理员文章详情, 文章ID: {}", id);
            // 检查文章是否存在
            AdminArticleDetailVO adminArticleDetailVO = articleMapper.findAdminDetailById(id);
            if (adminArticleDetailVO == null) {
                throw new BusinessException("文章不存在");
            }
            fileService.ensureImageFullUrl(adminArticleDetailVO);

            // 获取标签列表
            List<Tag> tags = articleTagService.getTagsByArticleId(id);
            if (tags != null && !tags.isEmpty()) {
                adminArticleDetailVO.setTags(tags);
            }

            log.info("获取管理员文章详情成功, 文章ID: {}", id);
            return adminArticleDetailVO;
        } catch (Exception e) {
            log.error("获取管理员文章详情失败, 文章ID: {}", id, e);
            throw new BusinessException("获取文章详情失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean topArticle(Long id) {
        try {
            log.debug("置顶文章, 文章ID: {}", id);
            // 检查文章是否存在
            Article article = ArticleUtils.getArticleSafely(articleMapper, id);
            // 更新置顶状态
            int result = articleMapper.updateTopStatus(id, com.inkstage.enums.article.TopStatus.TOP);
            ArticleUtils.checkOperationResult(result, id, "置顶文章");
            // 发送通知
            notificationService.sendArticleNotification(article.getUserId(), NotificationType.ARTICLE_TOP, article);
            // 清理相关缓存
            clearCacheAfterAdminOperation(id);

            log.info("置顶文章并发送通知成功, 文章ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("置顶文章失败, 文章ID: {}", id, e);
            throw new BusinessException("置顶文章失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelTopArticle(Long id) {
        try {
            log.debug("取消置顶文章, 文章ID: {}", id);
            // 检查文章是否存在
            ArticleUtils.getArticleSafely(articleMapper, id);
            // 更新置顶状态
            int result = articleMapper.updateTopStatus(id, com.inkstage.enums.article.TopStatus.NOT_TOP);
            ArticleUtils.checkOperationResult(result, id, "取消置顶文章");
            // 清理相关缓存
            clearCacheAfterAdminOperation(id);

            log.info("取消置顶文章成功, 文章ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("取消置顶文章失败, 文章ID: {}", id, e);
            throw new BusinessException("取消置顶文章失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recommendArticle(Long id) {
        try {
            log.debug("推荐文章, 文章ID: {}", id);
            // 检查文章是否存在
            Article article = ArticleUtils.getArticleSafely(articleMapper, id);
            // 更新推荐状态
            int result = articleMapper.updateRecommendStatus(id, com.inkstage.enums.article.RecommendStatus.RECOMMENDED);
            ArticleUtils.checkOperationResult(result, id, "推荐文章");
            // 发送通知
            notificationService.sendArticleNotification(article.getUserId(), NotificationType.ARTICLE_RECOMMEND, article);
            // 清理相关缓存
            clearCacheAfterAdminOperation(id);

            log.info("推荐文章并发送通知成功, 文章ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("推荐文章失败, 文章ID: {}", id, e);
            throw new BusinessException("推荐文章失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelRecommendArticle(Long id) {
        try {
            log.debug("取消推荐文章, 文章ID: {}", id);
            // 检查文章是否存在
            ArticleUtils.getArticleSafely(articleMapper, id);
            // 更新推荐状态
            int result = articleMapper.updateRecommendStatus(id, com.inkstage.enums.article.RecommendStatus.NOT_RECOMMENDED);
            ArticleUtils.checkOperationResult(result, id, "取消推荐文章");
            // 清理相关缓存
            clearCacheAfterAdminOperation(id);

            log.info("取消推荐文章成功, 文章ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("取消推荐文章失败, 文章ID: {}", id, e);
            throw new BusinessException("取消推荐文章失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticleByAdmin(Long id, AdminArticleUpdateDTO updateDTO) {
        try {
            log.debug("管理员更新文章, 文章ID: {}", id);
            // 检查文章是否存在
            Article article = ArticleUtils.getArticleSafely(articleMapper, id);

            // 更新文章基本信息
            article.setTitle(updateDTO.getTitle());
            article.setCategoryId(updateDTO.getCategoryId());
            article.setArticleStatus(updateDTO.getArticleStatus());

            // 更新可选字段
            if (updateDTO.getSummary() != null) {
                article.setSummary(updateDTO.getSummary());
            }
            if (updateDTO.getContent() != null) {
                article.setContent(updateDTO.getContent());
                article.setContentHtml(updateDTO.getContent());
            }
            if (updateDTO.getCoverImage() != null) {
                article.setCoverImage(updateDTO.getCoverImage());
            }
            if (updateDTO.getMetaTitle() != null) {
                article.setMetaTitle(updateDTO.getMetaTitle());
            }
            if (updateDTO.getMetaDescription() != null) {
                article.setMetaDescription(updateDTO.getMetaDescription());
            }
            if (updateDTO.getMetaKeywords() != null) {
                article.setMetaKeywords(updateDTO.getMetaKeywords());
            }

            // 更新文章
            int result = articleMapper.update(article);
            ArticleUtils.checkOperationResult(result, id, "更新文章");

            // 处理标签
            articleTagService.handleArticleTags(id, updateDTO.getTags());

            // 清理相关缓存
            clearCacheAfterAdminOperation(id);

            log.info("管理员更新文章成功, 文章ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("管理员更新文章失败, 文章ID: {}", id, e);
            throw new BusinessException("更新文章失败");
        }
    }

    @Override
    public boolean deleteArticleByAdmin(Long id) {
        if (UserContext.isAdmin()) {
            return false;
        }

        try {
            log.debug("管理员删除文章, 文章ID: {}", id);
            // 检查文章是否存在
            Article article = ArticleUtils.getArticleSafely(articleMapper, id);
            int i = articleMapper.deleteByAdmin(id);
            ArticleUtils.checkOperationResult(i, id, "管理员删除文章");
            // 发送通知
            notificationService.sendArticleNotification(article.getUserId(), NotificationType.ARTICLE_DELETE, article);
            // 清理相关缓存
            clearCacheAfterAdminOperation(id);

            log.info("管理员删除文章并发送通知成功, 文章ID: {}", id);
            return i > 0;
        } catch (Exception e) {
            log.error("管理员删除文章失败, 文章ID: {}", id, e);
            throw new BusinessException("删除文章失败");
        }
    }

}
