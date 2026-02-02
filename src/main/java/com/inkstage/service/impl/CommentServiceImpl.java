package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
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
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.ArticleCommentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final FileService fileService;

    @Override
    public PageResult<ArticleCommentVO> getComments(CommentQueryDTO queryDTO) {
        log.info("获取文章评论 {}", queryDTO);
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
        return PageResult.build(articleCommentVOList, total, queryDTO.getPageNum(), queryDTO.getPageSize());
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

        return commentMapper.updateById(comment) > 0;
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

}