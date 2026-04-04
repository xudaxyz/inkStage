package com.inkstage.cache.service;

import com.inkstage.common.PageResult;
import com.inkstage.dto.admin.AdminCommentQueryDTO;
import com.inkstage.dto.front.CommentQueryDTO;
import com.inkstage.vo.front.ArticleCommentVO;

/**
 * 评论缓存服务接口
 * 专门负责评论相关的缓存操作
 * 遵循项目架构风格，参考 ArticleCacheService
 */
public interface CommentCacheService {

    // ==================== 前台评论查询缓存 ====================

    /**
     * 获取文章评论列表（带缓存）
     *
     * @param queryDTO 查询参数
     * @return 评论列表
     */
    PageResult<ArticleCommentVO> getComments(CommentQueryDTO queryDTO);

    /**
     * 获取子评论列表（带缓存）
     *
     * @param parentId 父评论ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param sortBy   排序方式
     * @return 子评论列表
     */
    PageResult<ArticleCommentVO> getReplies(Long parentId, Integer pageNum, Integer pageSize, String sortBy);

    // ==================== 后台评论查询缓存 ====================

    /**
     * 管理员分页获取评论列表（带缓存）
     *
     * @param queryDTO 查询参数
     * @return 评论列表
     */
    PageResult<ArticleCommentVO> getCommentsByPage(AdminCommentQueryDTO queryDTO);

    // ==================== 缓存清理方法 ====================

    /**
     * 清除文章评论列表缓存
     *
     * @param articleId 文章ID
     */
    void clearArticleCommentCache(Long articleId);

    /**
     * 清除子评论列表缓存
     *
     * @param parentId 父评论ID
     */
    void clearCommentRepliesCache(Long parentId);

    /**
     * 清除所有评论缓存
     */
    void clearAllCommentCache();

    // ==================== 场景化缓存清理 ====================

    /**
     * 创建评论后清理缓存
     *
     * @param articleId 文章ID
     * @param parentId  父评论ID（如果有）
     */
    void cleanCacheAfterCommentCreate(Long articleId, Long parentId);

    /**
     * 更新评论后清理缓存
     *
     * @param commentId 评论ID
     * @param articleId 文章ID
     */
    void cleanCacheAfterCommentUpdate(Long commentId, Long articleId);

    /**
     * 删除评论后清理缓存
     *
     * @param articleId 文章ID
     * @param parentId  父评论ID（如果有）
     */
    void cleanCacheAfterCommentDelete(Long articleId, Long parentId);

    /**
     * 管理员审核评论后清理缓存
     *
     * @param articleId 文章ID
     */
    void cleanCacheAfterCommentReview(Long articleId);

    /**
     * 管理员置顶评论后清理缓存
     *
     * @param articleId 文章ID
     */
    void cleanCacheAfterCommentTop(Long articleId);
}
