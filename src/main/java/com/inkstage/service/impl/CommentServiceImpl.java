package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.constant.RedisKeyConstants;
import com.inkstage.dto.front.CommentDTO;
import com.inkstage.dto.front.CommentQueryDTO;
import com.inkstage.entity.model.Comment;
import com.inkstage.enums.DeleteStatus;
import com.inkstage.enums.ReviewStatus;
import com.inkstage.enums.article.TopStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.CommentMapper;
import com.inkstage.service.CommentService;
import com.inkstage.service.FileService;
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
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final FileService fileService;
    private final RedisUtil redisUtil;

    @Override
    public PageResult<ArticleCommentVO> getComments(CommentQueryDTO queryDTO) {
        log.info("获取文章评论 {}", queryDTO);

        // 生成缓存键
        String cacheKey = RedisKeyConstants.buildCacheKey(
                "comment:list",
                queryDTO.getArticleId() + ":" + queryDTO.getPageNum() + ":" + queryDTO.getPageSize()
        );

        // 尝试从缓存获取
        PageResult<ArticleCommentVO> pageResult = redisUtil.get(cacheKey, new TypeReference<>() {
        });
        if (pageResult != null) {
            log.info("从缓存获取评论列表成功, 缓存键: {}", cacheKey);
            return pageResult;
        }

        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        queryDTO.setOffset(offset);
        // 查询评论列表
        List<ArticleCommentVO> articleCommentVOList = commentMapper.selectCommentsByArticleId(queryDTO);
        if (articleCommentVOList == null || articleCommentVOList.isEmpty()) {
            log.info("文章评论为空");
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
        log.info("更新评论列表缓存, 缓存键: {}", cacheKey);

        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createComment(CommentDTO commentDTO) {
        Long currentUserId = UserContext.getCurrentUserId();
        log.info("创建评论: {}, 用户ID: {}", commentDTO, currentUserId);

        // 生成楼层号
        Integer maxFloor = commentMapper.selectMaxFloorByArticleId(commentDTO.getArticleId());
        String floor = maxFloor == null ? "1" : String.valueOf(maxFloor + 1);

        // 创建评论实体
        Comment comment = new Comment();
        comment.setArticleId(commentDTO.getArticleId());
        comment.setParentId(commentDTO.getParentId() != null ? commentDTO.getParentId() : 0L);
        comment.setContent(commentDTO.getContent());
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

        // 插入评论
        int result = commentMapper.insert(comment);
        log.info("评论创建结果: {}, ID: {}", result, comment.getId());
        if (result <= 0) {
            throw new BusinessException(ResponseMessage.COMMENT_CREATE_FAILED);
        }
        // 如果是回复评论，更新父评论的回复数
        int updated = 1;
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            Comment parentComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (parentComment != null) {
                int newReplyCount = parentComment.getReplyCount() != null ? parentComment.getReplyCount() + 1 : 1;
                updated = commentMapper.updateReplyCount(comment.getParentId(), newReplyCount);
            }
        }
        if (updated >= 1) {
            // 清除评论列表缓存
            String commentListPattern = RedisKeyConstants.buildCacheKey("comment:list", comment.getArticleId() + ":*");
            redisUtil.deletePattern(commentListPattern);
        }
        return updated >= 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateComment(CommentDTO commentDTO) {
        Long userId = UserContext.getCurrentUserId();
        log.info("更新评论: {}, 用户ID: {}", commentDTO, userId);

        // 查询评论
        Comment comment = commentMapper.selectByPrimaryKey(commentDTO.getId());
        if (comment == null) {
            throw new BusinessException(ResponseMessage.COMMENT_NOT_FOUND);
        }

        // 验证权限
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ResponseMessage.UPDATED_FORBIDDEN);
        }

        // 更新评论
        comment.setContent(commentDTO.getContent());
        comment.setUpdateTime(LocalDateTime.now());

        boolean updated = commentMapper.updateById(comment) > 0;
        if (updated) {
            // 清除评论列表缓存
            String commentListPattern = RedisKeyConstants.buildCacheKey("comment:list", comment.getArticleId() + ":*");
            redisUtil.deletePattern(commentListPattern);
            log.info("清除评论列表缓存, 缓存模式: {}", commentListPattern);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long commentId) {
        Long userId = UserContext.getCurrentUserId();
        log.info("删除评论: {}, 用户ID: {}", commentId, userId);
        // 查询评论
        Comment comment = commentMapper.selectByPrimaryKey(commentId);
        if (comment == null) {
            throw new BusinessException(ResponseMessage.COMMENT_NOT_FOUND);
        }

        // 验证权限
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ResponseMessage.DELETED_FORBIDDEN);
        }

        // 删除评论
        int result = commentMapper.deleteById(commentId);
        if (result <= 0) {
            throw new BusinessException(ResponseMessage.COMMENT_DELETE_FAILED);
        }

        // 如果是回复评论，更新父评论的回复数
        int updated = 1;
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            Comment parentComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (parentComment != null) {
                int newReplyCount = parentComment.getReplyCount() != null && parentComment.getReplyCount() > 0 ? parentComment.getReplyCount() - 1 : 0;
                updated = commentMapper.updateReplyCount(comment.getParentId(), newReplyCount);
            }
        }

        return updated >= 1;

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
        return incrementCommentCount(commentId, "like");
    }

    /**
     * 增加评论回复数
     *
     * @param commentId 评论ID
     * @return 增加后的回复数
     */
    public Long incrementCommentReplyCount(Long commentId) {
        return incrementCommentCount(commentId, "reply");
    }

    /**
     * 增加评论计数的通用方法
     *
     * @param commentId 评论ID
     * @param countType 计数类型
     * @return 增加后的计数值
     */
    private Long incrementCommentCount(Long commentId, String countType) {
        try {
            // 生成Redis计数器键
            String countKey = RedisKeyConstants.buildHotDataKey("comment", commentId + ":" + countType);

            // 增加Redis计数器
            Long count = redisUtil.increment(countKey);
            if (count != null) {
                log.info("评论{}数增加成功, 评论ID: {}, 计数: {}", countType, commentId, count);

                // 异步更新数据库（这里简化处理，实际项目中应使用@Async或消息队列）
                // 根据countType调用不同的更新方法

                return count;
            }
            return null;
        } catch (Exception e) {
            log.error("增加评论{}数失败, 评论ID: {}", countType, commentId, e);
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
    public Long getCommentCount(Long commentId, String countType) {
        try {
            // 生成Redis计数器键
            String countKey = RedisKeyConstants.buildHotDataKey("comment", commentId + ":" + countType);

            // 获取Redis计数器值
            Object value = redisUtil.get(countKey);
            if (value != null) {
                if (value instanceof Long) {
                    return (Long) value;
                } else if (value instanceof String) {
                    try {
                        return Long.parseLong((String) value);
                    } catch (NumberFormatException e) {
                        log.error("解析评论计数值失败, 评论ID: {}, 计数类型: {}", commentId, countType, e);
                    }
                }
            }

            // 如果Redis中不存在，从数据库获取并更新到Redis
            // 这里简化处理，实际项目中应从数据库查询
            return 0L;
        } catch (Exception e) {
            log.error("获取评论{}数失败, 评论ID: {}", countType, commentId, e);
            return 0L;
        }
    }

}