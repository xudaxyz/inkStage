package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.dto.admin.AdminCommentQueryDTO;
import com.inkstage.dto.front.CommentDTO;
import com.inkstage.dto.front.CommentQueryDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.entity.model.Comment;
import com.inkstage.enums.CommentCountType;
import com.inkstage.enums.DeleteStatus;
import com.inkstage.enums.NotificationType;
import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.article.TopStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.mapper.CommentMapper;
import com.inkstage.service.CommentService;
import com.inkstage.service.CountService;
import com.inkstage.service.FileService;
import com.inkstage.service.NotificationService;
import com.inkstage.utils.CacheKeyGenerator;
import com.inkstage.utils.RedisCacheManager;
import com.inkstage.utils.RedisUtil;
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

        // 生成缓存键
        String cacheKey = CacheKeyGenerator.generateCommentListKey(
                queryDTO.getArticleId(),
                queryDTO.getPageNum(),
                queryDTO.getPageSize()
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
            articleCommentVOList = buildCommentTree(articleCommentVOList);

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

        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) {
            log.warn("用户未登录, 无法创建评论");
            log.warn(ResponseMessage.NOT_LOGIN.getMessage());
            throw new BusinessException(ResponseMessage.NOT_LOGIN);
        }

        log.info("创建评论: {}, 用户ID: {}", commentDTO, currentUserId);

        try {
            // 生成楼层号
            Integer maxFloor = commentMapper.findMaxFloorByArticleId(commentDTO.getArticleId());
            String floor = maxFloor == null ? "1" : String.valueOf(maxFloor + 1);

            // 创建评论实体
            Comment comment = createCommentEntity(commentDTO, currentUserId, floor);

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

                // 增加文章评论数
                countService.updateArticleCommentCount(comment.getArticleId(), updated);

                // 发送评论通知
                sendCommentNotification(comment, currentUserId);
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
    private void sendCommentNotification(Comment comment, Long currentUserId) {
        String currentUserNickname = UserContext.getCurrentUser().getNickname();

        if (comment.getParentId() != null && comment.getParentId() > 0) {
            // 回复评论
            Comment parentComment = commentMapper.findById(comment.getParentId());
            if (parentComment != null && !parentComment.getUserId().equals(currentUserId)) {
                notificationService.sendNotificationWithTemplate(
                        parentComment.getUserId(),
                        NotificationType.COMMENT_REPLY,
                        comment.getArticleId(),
                        currentUserId,
                        currentUserNickname,
                        comment.getContent()
                );
            }
        } else {
            // 文章评论
            Article article = articleMapper.findById(comment.getArticleId());
            if (article != null) {
                Long articleUserId = article.getUserId();
                String articleTitle = article.getTitle();

                // 只有当评论者不是文章作者时才发送通知
                if (!currentUserId.equals(articleUserId)) {
                    notificationService.sendNotificationWithTemplate(
                            articleUserId,
                            NotificationType.ARTICLE_COMMENT,
                            comment.getArticleId(),
                            currentUserId,
                            currentUserNickname,
                            articleTitle,
                            comment.getContent()
                    );
                }
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
     * @param comments 扁平评论列表
     * @return 树形评论列表
     */
    private List<ArticleCommentVO> buildCommentTree(List<ArticleCommentVO> comments) {
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

        return topLevelComments;
    }

    /**
     * 增加评论点赞数
     *
     * @param commentId 评论ID
     * @return 增加后的点赞数
     */
    public Long incrementCommentLikeCount(Long commentId) {
        return incrementCommentCount(commentId, CommentCountType.LIKE);
    }

    /**
     * 增加评论回复数
     *
     * @param commentId 评论ID
     * @return 增加后的回复数
     */
    public Long incrementCommentReplyCount(Long commentId) {
        return incrementCommentCount(commentId, CommentCountType.REPLY);
    }

    /**
     * 增加评论计数的通用方法
     *
     * @param commentId 评论ID
     * @param countType 计数类型
     * @return 增加后的计数值
     */
    private Long incrementCommentCount(Long commentId, CommentCountType countType) {
        if (commentId == null || countType == null) {
            return null;
        }

        try {
            // 生成Redis计数器键
            String countKey = CacheKeyGenerator.generateCommentCountKey(commentId, countType.getValue());

            // 增加Redis计数器
            Long count = redisUtil.increment(countKey);
            if (count != null) {
                log.info("评论{}增加成功, 评论ID: {}, 计数: {}", countType.getDesc(), commentId, count);

                // 异步更新数据库（这里简化处理，实际项目中应使用@Async或消息队列）
                // 根据countType调用不同的更新方法

                return count;
            }
            return null;
        } catch (Exception e) {
            log.error("增加评论{}失败, 评论ID: {}", countType.getDesc(), commentId, e);
            return null;
        }
    }

    /**
     * 获取评论计数
     *
     * @param commentId 评论ID
     * @param countType 计数类型
     * @return 计数值
     */
    public Long getCommentCount(Long commentId, CommentCountType countType) {
        if (commentId == null || countType == null) {
            log.warn("评论ID或计数类型为空, 评论ID: {}, 计数类型: {}", commentId, countType);
            return 0L;
        }

        try {
            // 生成Redis计数器键
            String countKey = CacheKeyGenerator.generateCommentCountKey(commentId, countType.getValue());

            // 获取Redis计数器值
            Object value = redisUtil.get(countKey);
            if (value != null) {
                if (value instanceof Long) {
                    return (Long) value;
                } else if (value instanceof String) {
                    try {
                        return Long.parseLong((String) value);
                    } catch (NumberFormatException e) {
                        log.error("解析评论计数值失败, 评论ID: {}, 计数类型: {}", commentId, countType.getDesc(), e);
                    }
                }
            }

            // 如果Redis中不存在，从数据库获取并更新到Redis
            // 这里简化处理，实际项目中应从数据库查询
            return 0L;
        } catch (Exception e) {
            log.error("获取评论{}失败, 评论ID: {}", countType.getDesc(), commentId, e);
            return 0L;
        }
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
                notificationService.sendNotificationWithTemplate(
                        comment.getUserId(),
                        NotificationType.COMMENT_REVIEW_REJECT,
                        comment.getArticleId(),
                        0L, // 系统发送
                        reviewReason
                );
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

        try {
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

            // 发送通知
            if (top == TopStatus.TOP) {
                // 评论置顶通知
                notificationService.sendNotificationWithTemplate(
                        comment.getUserId(),
                        NotificationType.COMMENT_TOP,
                        comment.getArticleId(),
                        0L, // 系统发送
                        "您的评论已被置顶"
                );
            }

            // 清除评论列表缓存
            cacheManager.clearArticleCommentCache(comment.getArticleId());

            return true;
        } catch (Exception e) {
            log.error("更新评论置顶状态失败, 评论ID: {}", id, e);
            throw new BusinessException(ResponseMessage.COMMENT_UPDATE_FAILED, e.getMessage());
        }
    }

    @Override
    public boolean adminUpdateComment(Comment comment) {
        if (comment == null || comment.getId() == null) {
            log.warn("评论ID为空");
            throw new BusinessException(ResponseMessage.COMMENT_UPDATE_FAILED);
        }
        return commentMapper.updateById(comment) > 0;
    }

}