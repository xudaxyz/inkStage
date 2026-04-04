package com.inkstage.utils;

import com.inkstage.vo.front.ArticleCommentVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论工具类
 * 提供评论相关的工具方法
 */
public class CommentUtils {

    /**
     * 将扁平的评论列表转换为树形结构
     *
     * @param comments    扁平评论列表
     * @param maxReplies  子评论最大返回数量
     * @param replySortBy 子评论排序方式
     * @return 树形评论列表
     */
    public static List<ArticleCommentVO> buildCommentTree(List<ArticleCommentVO> comments, Integer maxReplies, String replySortBy) {
        if (comments == null || comments.isEmpty()) {
            return new ArrayList<>();
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

        // 第二步: 构建评论树 - 实现知乎式评论结构（只有一级和二级评论）
        for (ArticleCommentVO comment : comments) {
            Long parentId = comment.getParentId();
            if (parentId == null || parentId == 0) {
                // 顶级评论
                topLevelComments.add(comment);
            } else {
                // 查找顶级评论：如果父评论是二级评论，找到对应的顶级评论
                ArticleCommentVO topComment = findTopComment(commentMap, parentId);
                if (topComment != null) {
                    // 将所有回复都添加到顶级评论的回复列表中
                    topComment.getReplies().add(comment);
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

    /**
     * 查找顶级评论
     *
     * @param commentMap 评论Map
     * @param commentId  评论ID
     * @return 顶级评论
     */
    private static ArticleCommentVO findTopComment(Map<Long, ArticleCommentVO> commentMap, Long commentId) {
        ArticleCommentVO comment = commentMap.get(commentId);
        if (comment == null) {
            return null;
        }
        // 如果是顶级评论，直接返回
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            return comment;
        }
        // 递归查找顶级评论
        return findTopComment(commentMap, comment.getParentId());
    }
}
