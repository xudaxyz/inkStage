package com.inkstage.cache.service.impl;

import com.inkstage.cache.service.CommentCacheService;
import com.inkstage.common.PageResult;
import com.inkstage.dto.admin.AdminCommentQueryDTO;
import com.inkstage.dto.front.CommentQueryDTO;
import com.inkstage.mapper.CommentMapper;
import com.inkstage.service.FileService;
import com.inkstage.utils.CommentUtils;
import com.inkstage.cache.constant.RedisKeyConstants;
import com.inkstage.vo.front.ArticleCommentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论缓存服务实现类
 * 专门负责评论相关的缓存操作
 * 使用Spring Cache注解实现声明式缓存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentCacheServiceImpl implements CommentCacheService {

    private final CommentMapper commentMapper;
    private final FileService fileService;

    // ==================== 前台评论查询缓存 ====================

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_COMMENT_LIST,
            key = "#queryDTO.articleId + ':' + #queryDTO.pageNum + ':' + #queryDTO.pageSize + ':' + (#queryDTO.sortBy ?: 'default') + ':' + (#queryDTO.maxReplies ?: 0) + ':' + #root.target.getCommentVersion(#queryDTO.articleId)",
            unless = "#result == null")
    public PageResult<ArticleCommentVO> getComments(CommentQueryDTO queryDTO) {
        if (queryDTO == null || queryDTO.getArticleId() == null || queryDTO.getPageNum() == null || queryDTO.getPageSize() == null) {
            log.warn("获取评论参数不完整, queryDTO: {}", queryDTO);
            return null;
        }

        try {
            int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
            queryDTO.setOffset(offset);
            // 查询评论列表
            List<ArticleCommentVO> articleCommentVOList = commentMapper.findCommentsByArticleId(queryDTO);
            if (articleCommentVOList == null || articleCommentVOList.isEmpty()) {
                return null;
            }

            // 将扁平评论列表转换为树形结构
            articleCommentVOList = CommentUtils.buildCommentTree(articleCommentVOList, queryDTO.getMaxReplies(), queryDTO.getReplySortBy());
            if (articleCommentVOList.isEmpty()) {
                return null;
            }

            // 查询总记录数
            Long total = commentMapper.countCommentsByArticleId(queryDTO);

            // 确保评论图片的URL完整
            fileService.ensureCommentImageAreFullUrl(articleCommentVOList);

            return PageResult.build(articleCommentVOList, total, queryDTO.getPageNum(), queryDTO.getPageSize());
        } catch (Exception e) {
            log.error("获取评论列表失败, 文章ID: {}", queryDTO.getArticleId(), e);
            return null;
        }
    }

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_COMMENT_REPLIES,
            key = "#parentId + ':' + #pageNum + ':' + #pageSize + ':' + (#sortBy ?: 'default')",
            unless = "#result == null")
    public PageResult<ArticleCommentVO> getReplies(Long parentId, Integer pageNum, Integer pageSize, String sortBy) {
        if (parentId == null || pageNum == null || pageSize == null) {
            log.warn("获取子评论参数不完整, parentId: {}, pageNum: {}, pageSize: {}", parentId, pageNum, pageSize);
            return null;
        }

        try {
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
            return PageResult.build(replies, total, pageNum, pageSize);
        } catch (Exception e) {
            log.error("获取子评论列表失败, 父评论ID: {}", parentId, e);
            return null;
        }
    }

    @Override
    public PageResult<ArticleCommentVO> getCommentsByPage(AdminCommentQueryDTO queryDTO) {
        try {
            // 计算偏移量
            int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
            queryDTO.setOffset(offset);

            // 查询评论列表
            List<ArticleCommentVO> articleCommentVOList = commentMapper.findCommentsByPage(queryDTO);
            // 确保评论图片的URL完整
            fileService.ensureCommentImageAreFullUrl(articleCommentVOList);
            // 查询总记录数
            Long total = commentMapper.countCommentsByPage(queryDTO);

            // 构建分页结果
            PageResult<ArticleCommentVO> pageResult = PageResult.build(
                    articleCommentVOList,
                    total,
                    queryDTO.getPageNum(),
                    queryDTO.getPageSize()
            );

            log.info("管理员分页获取评论列表成功, 总数: {}, 页码: {}, 每页大小: {}", total, queryDTO.getPageNum(), queryDTO.getPageSize());
            return pageResult;
        } catch (Exception e) {
            log.error("管理员分页获取评论列表失败", e);
            return null;
        }
    }

    /**
     * 获取评论版本号
     */
    public int getCommentVersion(Long articleId) {
        Integer maxVersion = commentMapper.findMaxCommentVersionByArticleId(articleId);
        return maxVersion != null ? maxVersion : 1;
    }
}
