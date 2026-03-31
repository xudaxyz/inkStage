package com.inkstage.service.impl;

import com.inkstage.cache.constant.RedisKeyConstants;
import com.inkstage.cache.utils.CacheKeyGenerator;
import com.inkstage.cache.utils.RedisCacheManager;
import com.inkstage.cache.utils.RedisUtil;
import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.dto.admin.AdminCommentQueryDTO;
import com.inkstage.dto.front.CommentDTO;
import com.inkstage.dto.front.CommentQueryDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.entity.model.Comment;
import com.inkstage.entity.model.User;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.enums.notification.NotificationTemplateVariable;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.article.TopStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.mapper.CommentMapper;
import com.inkstage.service.CommentService;
import com.inkstage.service.CountService;
import com.inkstage.service.FileService;
import com.inkstage.service.NotificationService;
import com.inkstage.utils.ArticleUtils;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.ArticleCommentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 评论服务实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final FileService fileService;
    private final RedisUtil redisUtil;
    private final CountService countService;
    private final NotificationService notificationService;
    private final ArticleMapper articleMapper;
    private final RedisCacheManager cacheManager;

    @Override
    public PageResult<ArticleCommentVO> getComments(CommentQueryDTO queryDTO) {
        if (queryDTO == null || queryDTO.getArticleId() == null || queryDTO.getPageNum() == null || queryDTO.getPageSize() == null) {
            log.warn("获取评论参数不完整, queryDTO: {}", queryDTO);
            return null;
        }

        log.info("获取文章评论 {}", queryDTO);

        // 获取文章的最新评论版本号
        Integer maxVersion = commentMapper.findMaxCommentVersionByArticleId(queryDTO.getArticleId());
        int commentVersion = maxVersion != null ? maxVersion : 1;

        // 生成缓存键
        String cacheKey = CacheKeyGenerator.generateCommentListKey(
                queryDTO.getArticleId(),
                queryDTO.getPageNum(),
                queryDTO.getPageSize(),
                queryDTO.getSortBy(),
                queryDTO.getMaxReplies(),
                commentVersion
        );

        try {
            // 尝试从缓存获取
            PageResult<ArticleCommentVO> pageResult = redisUtil.getWithType(cacheKey, new TypeReference<>() {
            });
            if (pageResult != null) {
                return pageResult;
            }

            int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
            queryDTO.setOffset(offset);
            // 查询评论列表
            List<ArticleCommentVO> articleCommentVOList = commentMapper.findCommentsByArticleId(queryDTO);
            if (articleCommentVOList == null || articleCommentVOList.isEmpty()) {
                return null;
            }

            // 将扁平评论列表转换为树形结构
            articleCommentVOList = buildCommentTree(articleCommentVOList, queryDTO.getMaxReplies(), queryDTO.getReplySortBy());

            // 查询总记录数
            Long total = commentMapper.countCommentsByArticleId(queryDTO);

            // 确保评论图片的URL完整
            fileService.ensureCommentImageAreFullUrl(articleCommentVOList);
            pageResult = PageResult.build(articleCommentVOList, total, queryDTO.getPageNum(), queryDTO.getPageSize());

            // 更新缓存
            redisUtil.set(cacheKey, pageResult, 5, TimeUnit.MINUTES);

            return pageResult;
        } catch (Exception e) {
            log.error("获取评论列表失败, 文章ID: {}", queryDTO.getArticleId(), e);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createComment(CommentDTO commentDTO) {
        if (commentDTO == null || commentDTO.getArticleId() == null || commentDTO.getContent() == null || commentDTO.getContent().trim().isEmpty()) {
            log.warn("创建评论参数不完整, commentDTO: {}", commentDTO);
            log.warn(ResponseMessage.COMMENT_CREATE_FAILED.getMessage());
            throw new BusinessException(ResponseMessage.COMMENT_CREATE_FAILED);
        }

        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            log.warn("用户未登录, 无法创建评论");
            log.warn(ResponseMessage.NOT_LOGIN.getMessage());
            throw new BusinessException(ResponseMessage.NOT_LOGIN);
        }

        // 判断文章是否存在
        Article article = ArticleUtils.getArticleSafely(articleMapper, commentDTO.getArticleId());

        log.info("创建评论: {}, 用户ID: {}", commentDTO, currentUser.getId());

        try {
            // 生成楼层号
            Integer maxFloor = commentMapper.findMaxFloorByArticleId(commentDTO.getArticleId());
            String floor = maxFloor == null ? "1" : String.valueOf(maxFloor + 1);

            // 创建评论实体
            Comment comment = createCommentEntity(commentDTO, currentUser.getId(), floor);

            // 插入评论
            int result = commentMapper.insert(comment);
            if (result <= 0) {
                log.warn("评论创建失败, 文章ID: {}", commentDTO.getArticleId());
                throw new BusinessException(ResponseMessage.COMMENT_CREATE_FAILED);
            }

            // 如果是回复评论，更新父评论的回复数
            int updated = 1;
            if (comment.getParentId() != null && comment.getParentId() > 0) {
                updated = updateParentCommentReplyCount(comment.getParentId());
            }

            if (updated >= 1) {
                // 清除评论列表缓存
                cacheManager.clearArticleCommentCache(comment.getArticleId());

                // 清除回复评论ID列表缓存
                if (comment.getParentId() != null && comment.getParentId() > 0) {
                    cacheManager.clearArticleCommentRepliesCache(comment.getParentId());
                }

                // 增加文章评论数
                countService.updateArticleCommentCount(comment.getArticleId(), updated);
                sendCommentNotification(comment, article, currentUser);
            }
            return updated >= 1;
        } catch (Exception e) {
            log.error("创建评论失败, 文章ID: {}", commentDTO.getArticleId(), e);
            throw new BusinessException(ResponseMessage.COMMENT_CREATE_FAILED, e.getMessage());
        }
    }

    /**
     * 创建评论实体
     */
    private Comment createCommentEntity(CommentDTO commentDTO, Long currentUserId, String floor) {
        Comment comment = new Comment();
        comment.setArticleId(commentDTO.getArticleId());
        comment.setParentId(commentDTO.getParentId() != null ? commentDTO.getParentId() : 0L);
        comment.setContent(commentDTO.getContent().trim());
        comment.setMentionUserIds(commentDTO.getMentionUserIds());
        comment.setFloor(floor);
        comment.setUserId(currentUserId);
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setStatus(ReviewStatus.PENDING);
        comment.setDeleted(DeleteStatus.NOT_DELETED);
        comment.setTop(TopStatus.NOT_TOP);
        comment.setTopOrder(0);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        return comment;
    }

    /**
     * 更新父评论的回复数
     */
    private int updateParentCommentReplyCount(Long parentId) {
        Comment parentComment = commentMapper.findById(parentId);
        if (parentComment != null) {
            int newReplyCount = parentComment.getReplyCount() != null ? parentComment.getReplyCount() + 1 : 1;
            return commentMapper.updateReplyCount(parentId, newReplyCount);
        }
        return 1;
    }

    /**
     * 发送评论通知
     */
    private void sendCommentNotification(Comment comment, Article article, User currentUser) {
        Map<String, Object> params = new HashMap<>();
        params.put(NotificationTemplateVariable.ARTICLE_TITLE.getKey(), article.getTitle());
        params.put(NotificationTemplateVariable.RELATED_ID.getKey(), comment.getArticleId());
        params.put(NotificationTemplateVariable.ARTICLE_ID.getKey(), comment.getArticleId());
        params.put(NotificationTemplateVariable.USERNAME.getKey(), currentUser.getNickname());
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            // 回复评论
            Comment parentComment = commentMapper.findById(comment.getParentId());

            if (parentComment != null && !parentComment.getUserId().equals(currentUser.getId())) {
                // 发送评论通知
                notificationService.sendNotificationWithTemplate(article.getUserId(), NotificationType.COMMENT_REPLY, params);
            }
        } else {
            // 文章评论
            Long articleUserId = article.getUserId();
            // 只有当评论者不是文章作者时才发送通知
            if (!currentUser.getId().equals(articleUserId)) {
                notificationService.sendNotificationWithTemplate(comment.getUserId(), NotificationType.ARTICLE_COMMENT, params);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateComment(CommentDTO commentDTO) {
        if (commentDTO == null || commentDTO.getId() == null || commentDTO.getContent() == null || commentDTO.getContent().trim().isEmpty()) {
            log.warn("更新评论参数不完整, commentDTO: {}", commentDTO);
            log.warn(ResponseMessage.COMMENT_UPDATE_FAILED.getMessage());
            throw new BusinessException(ResponseMessage.COMMENT_UPDATE_FAILED);
        }

        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            log.warn("用户未登录, 无法更新评论");
            log.warn(ResponseMessage.NOT_LOGIN.getMessage());
            throw new BusinessException(ResponseMessage.NOT_LOGIN);
        }

        log.info("更新评论: {}, 用户ID: {}", commentDTO, userId);

        try {
            // 查询评论
            Comment comment = commentMapper.findById(commentDTO.getId());
            if (comment == null) {
                log.warn("评论ID {} 不存在", commentDTO.getId());
                throw new BusinessException(ResponseMessage.COMMENT_NOT_FOUND);
            }

            // 验证权限
            if (!comment.getUserId().equals(userId)) {
                log.warn("用户无权限更新评论, 评论ID: {}, 用户ID: {}", commentDTO.getId(), userId);
                throw new BusinessException(ResponseMessage.UPDATED_FORBIDDEN);
            }

            // 更新评论
            comment.setContent(commentDTO.getContent().trim());
            comment.setUpdateTime(LocalDateTime.now());

            boolean updated = commentMapper.updateById(comment) > 0;
            if (updated) {
                // 清除评论列表缓存
                cacheManager.clearArticleCommentCache(comment.getArticleId());
            } else {
                log.warn("评论更新失败, 评论ID: {}", commentDTO.getId());
            }
            return updated;
        } catch (Exception e) {
            log.error("更新评论失败, 评论ID: {}", commentDTO.getId(), e);
            throw new BusinessException(ResponseMessage.COMMENT_UPDATE_FAILED, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long commentId) {
        if (commentId == null) {
            log.warn("删除评论参数为空");
            throw new BusinessException(ResponseMessage.COMMENT_DELETE_FAILED);
        }

        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            log.warn("用户未登录, 无法删除评论");
            throw new BusinessException(ResponseMessage.NOT_LOGIN);
        }

        log.info("删除评论: {}, 用户ID: {}", commentId, userId);

        try {
            // 查询评论
            Comment comment = commentMapper.findById(commentId);
            if (comment == null) {
                log.warn("评论ID: {} 不存在", commentId);
                throw new BusinessException(ResponseMessage.COMMENT_NOT_FOUND);
            }

            // 验证权限, 仅管理员和作者本身可以删除评论
            if (!comment.getUserId().equals(userId) && !UserContext.isAdmin()) {
                log.warn("用户无权限删除评论, 评论ID: {}, 用户ID: {}", commentId, userId);
                throw new BusinessException(ResponseMessage.DELETED_FORBIDDEN);
            }

            // 删除评论
            int result = commentMapper.deleteById(commentId);
            if (result <= 0) {
                log.warn("删除评论失败, 评论ID: {}", commentId);
                throw new BusinessException(ResponseMessage.COMMENT_DELETE_FAILED);
            }

            // 如果是回复评论，更新父评论的回复数
            int updated = 1;
            if (comment.getParentId() != null && comment.getParentId() > 0) {
                updated = updateParentCommentReplyCountDecrease(comment.getParentId());
            }

            if (updated >= 1) {
                // 减少文章评论数
                countService.updateArticleCommentCount(comment.getArticleId(), -updated);

                // 清除评论列表缓存
                cacheManager.clearArticleCommentCache(comment.getArticleId());
            }
            return updated >= 1;
        } catch (Exception e) {
            log.error("删除评论失败, 评论ID: {}", commentId, e);
            throw new BusinessException(ResponseMessage.COMMENT_DELETE_FAILED, e.getMessage());
        }
    }

    /**
     * 减少父评论的回复数
     */
    private int updateParentCommentReplyCountDecrease(Long parentId) {
        Comment parentComment = commentMapper.findById(parentId);
        if (parentComment != null) {
            int newReplyCount = parentComment.getReplyCount() != null && parentComment.getReplyCount() > 0 ? parentComment.getReplyCount() - 1 : 0;
            return commentMapper.updateReplyCount(parentId, newReplyCount);
        }
        return 1;
    }

    /**
     * 将扁平的评论列表转换为树形结构
     *
     * @param comments    扁平评论列表
     * @param maxReplies  子评论最大返回数量
     * @param replySortBy 子评论排序方式
     * @return 树形评论列表
     */
    private List<ArticleCommentVO> buildCommentTree(List<ArticleCommentVO> comments, Integer maxReplies, String replySortBy) {
        if (comments == null || comments.isEmpty()) {
            return null;
        }

        // 存储所有评论的Map, 用于快速查找
        Map<Long, ArticleCommentVO> commentMap = new HashMap<>();
        // 存储顶级评论
        List<ArticleCommentVO> topLevelComments = new ArrayList<>();

        // 第一步: 将所有评论放入Map, 并初始化回复列表
        for (ArticleCommentVO comment : comments) {
            comment.setReplies(new ArrayList<>());
            commentMap.put(comment.getId(), comment);
        }

        // 第二步: 构建评论树
        for (ArticleCommentVO comment : comments) {
            Long parentId = comment.getParentId();
            if (parentId == null || parentId == 0) {
                // 顶级评论
                topLevelComments.add(comment);
            } else {
                // 回复评论, 添加到父评论的回复列表中
                ArticleCommentVO parentComment = commentMap.get(parentId);
                if (parentComment != null) {
                    parentComment.getReplies().add(comment);
                }
            }
        }

        // 第三步: 对每个顶级评论的子评论进行排序和数量限制
        for (ArticleCommentVO topComment : topLevelComments) {
            List<ArticleCommentVO> replies = topComment.getReplies();
            if (replies != null && !replies.isEmpty()) {
                // 子评论排序
                if ("hot".equals(replySortBy)) {
                    replies.sort((a, b) -> {
                        int likeCountA = a.getLikeCount() != null ? a.getLikeCount() : 0;
                        int likeCountB = b.getLikeCount() != null ? b.getLikeCount() : 0;
                        return likeCountB - likeCountA;
                    });
                } else if ("new".equals(replySortBy)) {
                    replies.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
                }

                // 限制子评论数量
                if (maxReplies != null && maxReplies > 0 && replies.size() > maxReplies) {
                    topComment.setReplies(replies.subList(0, maxReplies));
                }
            }
        }

        return topLevelComments;
    }

    @Override
    public PageResult<ArticleCommentVO> getCommentsByPage(AdminCommentQueryDTO pageRequest) {
        try {
            log.info("管理员分页获取评论列表, 页码: {}, 每页大小: {}", pageRequest.getPageNum(), pageRequest.getPageSize());

            // 计算偏移量
            int offset = (pageRequest.getPageNum() - 1) * pageRequest.getPageSize();
            pageRequest.setOffset(offset);

            // 查询评论列表
            List<ArticleCommentVO> articleCommentVOList = commentMapper.findCommentsByPage(pageRequest);
            // 确保评论图片的URL完整
            fileService.ensureCommentImageAreFullUrl(articleCommentVOList);
            // 查询总记录数
            Long total = commentMapper.countCommentsByPage(pageRequest);

            // 构建分页结果
            PageResult<ArticleCommentVO> pageResult = PageResult.build(
                    articleCommentVOList,
                    total,
                    pageRequest.getPageNum(),
                    pageRequest.getPageSize()
            );

            log.info("管理员分页获取评论列表成功, 总数: {}, 页码: {}, 每页大小: {}", total, pageRequest.getPageNum(), pageRequest.getPageSize());
            return pageResult;
        } catch (Exception e) {
            log.error(ResponseMessage.COMMENT_NOT_FOUND.getMessage(), e);
            throw new BusinessException(ResponseMessage.COMMENT_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCommentStatus(Long id, ReviewStatus status, String reviewReason) {
        if (id == null || status == null) {
            log.warn("评论ID或状态为空, 评论ID: {}, 状态: {}", id, status);
            log.warn(ResponseMessage.COMMENT_UPDATE_FAILED.getMessage());
            throw new BusinessException(ResponseMessage.COMMENT_UPDATE_FAILED);
        }

        try {
            // 检查评论是否存在
            Comment comment = commentMapper.findById(id);
            if (comment == null) {
                log.warn("评论id {} 不存在", id);
                throw new BusinessException(ResponseMessage.COMMENT_NOT_FOUND);
            }

            // 获取当前管理员ID
            Long currentUserId = UserContext.getCurrentUserId();

            // 更新评论状态
            int result = commentMapper.updateStatus(id, status.getCode(), currentUserId, reviewReason);
            if (result <= 0) {
                log.warn("更新评论状态失败, 评论ID: {}", id);
                throw new BusinessException(ResponseMessage.COMMENT_UPDATE_FAILED);
            }

            // 发送通知
            if (status == ReviewStatus.REJECTED) {
                // 评论审核拒绝通知
                Map<String, Object> params = new HashMap<>();
                params.put(NotificationTemplateVariable.COMMENT_CONTENT.getKey(), comment.getContent());
                params.put(NotificationTemplateVariable.RELATED_ID.getKey(), comment.getArticleId());
                params.put(NotificationTemplateVariable.ARTICLE_ID.getKey(), comment.getArticleId());
                params.put(NotificationTemplateVariable.USERNAME.getKey(), UserContext.getCurrentUser().getNickname());
                notificationService.sendNotificationWithTemplate(comment.getUserId(), NotificationType.COMMENT_REVIEW_REJECT, params);
            }

            // 清除评论列表缓存
            cacheManager.clearArticleCommentCache(comment.getArticleId());

            return true;
        } catch (Exception e) {
            log.error("更新评论状态失败, 评论ID: {}", id, e);
            throw new BusinessException(ResponseMessage.COMMENT_UPDATE_FAILED, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCommentTop(Long id, TopStatus top, Integer topOrder) {
        if (id == null || top == null) {
            log.warn("评论ID或置顶状态为空, 评论ID: {}, 置顶状态: {}", id, top);
            log.warn(ResponseMessage.COMMENT_UPDATE_FAILED.getMessage());
            throw new BusinessException(ResponseMessage.COMMENT_UPDATE_FAILED);
        }

        // 检查评论是否存在
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            log.warn("评论不存在, 评论ID: {}", id);
            throw new BusinessException(ResponseMessage.COMMENT_NOT_FOUND);
        }

        // 检查是否为父评论，子评论无法置顶
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            log.warn("子评论无法置顶, 评论ID: {}", id);
            throw new BusinessException("子评论无法置顶");
        }

        // 如果是设置为置顶状态
        if (top == TopStatus.TOP) {
            // 检查文章是否已有置顶评论
            Comment existingTopComment = commentMapper.findTopCommentByArticleId(comment.getArticleId());
            if (existingTopComment != null && !existingTopComment.getId().equals(id)) {
                // 取消已有置顶评论
                commentMapper.updateTop(existingTopComment.getId(), TopStatus.NOT_TOP.getCode(), 0);
                log.info("取消已有置顶评论, 评论ID: {}", existingTopComment.getId());
            }
        }

        // 更新评论置顶状态
        int result = commentMapper.updateTop(id, top.getCode(), topOrder);
        if (result <= 0) {
            log.warn("更新评论置顶状态失败, 评论ID: {}", id);
            throw new BusinessException(ResponseMessage.COMMENT_UPDATE_FAILED);
        }

        // 清除评论列表缓存
        cacheManager.clearArticleCommentCache(comment.getArticleId());

        return true;

    }

    @Override
    public boolean adminUpdateComment(Comment comment) {
        if (comment == null || comment.getId() == null) {
            log.warn("评论ID为空");
            throw new BusinessException(ResponseMessage.COMMENT_UPDATE_FAILED);
        }
        Comment existedComments = commentMapper.findById(comment.getId());
        if (existedComments == null) {
            log.warn("评论{}不存在", comment.getId());
            throw new BusinessException(ResponseMessage.COMMENT_NOT_FOUND);
        }
        // 如果是子评论, 禁止置顶
        if (existedComments.getParentId() != null && existedComments.getParentId() > 0 && TopStatus.TOP.equals(comment.getTop())) {
            log.warn("子评论禁止置顶, 评论ID: {}", comment.getId());
            throw new BusinessException("子评论无法置顶");
        }
        return commentMapper.updateById(comment) > 0;
    }

    @Override
    public PageResult<ArticleCommentVO> getReplies(Long parentId, Integer pageNum, Integer pageSize, String sortBy) {
        if (parentId == null || pageNum == null || pageSize == null) {
            log.warn("获取子评论参数不完整, parentId: {}, pageNum: {}, pageSize: {}", parentId, pageNum, pageSize);
            return null;
        }

        log.info("获取子评论列表, 父评论ID: {}, 页码: {}, 每页大小: {}, 排序方式: {}", parentId, pageNum, pageSize, sortBy);

        // 生成缓存键
        String cacheKey = RedisKeyConstants.buildCacheKey(
                "comment:replies:",
                parentId + ":" + pageNum + ":" + pageSize + ":" + sortBy
        );

        try {
            // 尝试从缓存获取
            PageResult<ArticleCommentVO> pageResult = redisUtil.getWithType(cacheKey, new TypeReference<>() {
            });
            if (pageResult != null) {
                return pageResult;
            }

            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;

            // 查询子评论列表
            List<ArticleCommentVO> replies = commentMapper.findRepliesByParentId(parentId, offset, pageSize, sortBy);
            if (replies == null || replies.isEmpty()) {
                return null;
            }

            // 确保评论图片的URL完整
            fileService.ensureCommentImageAreFullUrl(replies);

            // 查询子评论总数
            Long total = commentMapper.countRepliesByParentId(parentId);

            // 构建分页结果
            pageResult = PageResult.build(replies, total, pageNum, pageSize);

            // 更新缓存
            redisUtil.set(cacheKey, pageResult, 5, TimeUnit.MINUTES);

            return pageResult;
        } catch (Exception e) {
            log.error("获取子评论列表失败, 父评论ID: {}", parentId, e);
            return null;
        }
    }

}