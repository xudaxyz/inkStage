package com.inkstage.cache.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.constant.CacheTTL;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.cache.service.CommentCacheService;
import com.inkstage.common.PageResult;
import com.inkstage.dto.admin.AdminCommentQueryDTO;
import com.inkstage.dto.front.CommentQueryDTO;
import com.inkstage.mapper.CommentMapper;
import com.inkstage.service.FileService;
import com.inkstage.utils.CommentUtils;
import com.inkstage.vo.front.ArticleCommentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.util.List;

/**
 * 评论缓存服务实现类
 * 专门负责评论相关的缓存操作
 * 使用 CacheManager 实现缓存管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentCacheServiceImpl implements CommentCacheService {

    private final CommentMapper commentMapper;
    private final FileService fileService;
    private final CacheManager cacheManager;

    // ==================== 前台评论查询缓存 ====================

    @Override
    public PageResult<ArticleCommentVO> getComments(CommentQueryDTO queryDTO) {
        if (queryDTO == null || queryDTO.getArticleId() == null || queryDTO.getPageNum() == null || queryDTO.getPageSize() == null) {
            log.warn("获取评论参数不完整, queryDTO: {}", queryDTO);
            return null;
        }

        try {
            int commentVersion = getCommentVersion(queryDTO.getArticleId());
            String cacheKey = CacheKey.keyForCommentList(
                    queryDTO.getArticleId(),
                    queryDTO.getPageNum(),
                    queryDTO.getPageSize(),
                    queryDTO.getSortBy(),
                    queryDTO.getMaxReplies(),
                    commentVersion
            );

            PageResult<ArticleCommentVO> result = cacheManager.getWithType(cacheKey, new TypeReference<>() {
            });
            if (result != null) {
                log.debug("从缓存获取评论列表, 文章ID: {}", queryDTO.getArticleId());
                return result;
            }

            int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
            queryDTO.setOffset(offset);
            List<ArticleCommentVO> articleCommentVOList = commentMapper.findCommentsByArticleId(queryDTO);
            if (articleCommentVOList == null || articleCommentVOList.isEmpty()) {
                return null;
            }

            articleCommentVOList = CommentUtils.buildCommentTree(articleCommentVOList, queryDTO.getMaxReplies(), queryDTO.getReplySortBy());
            if (articleCommentVOList.isEmpty()) {
                return null;
            }
            fileService.ensureImageFullUrl(articleCommentVOList);

            Long total = commentMapper.countCommentsByArticleId(queryDTO);

            result = PageResult.build(articleCommentVOList, total, queryDTO.getPageNum(), queryDTO.getPageSize());
            cacheManager.setWithRandomOffset(cacheKey, result, CacheTTL.COMMENT_LIST);
            return result;
        } catch (Exception e) {
            log.error("获取评论列表失败, 文章ID: {}", queryDTO.getArticleId(), e);
            return null;
        }
    }

    @Override
    public PageResult<ArticleCommentVO> getReplies(Long parentId, Integer pageNum, Integer pageSize, String sortBy) {
        if (parentId == null || pageNum == null || pageSize == null) {
            log.warn("获取子评论参数不完整, parentId: {}, pageNum: {}, pageSize: {}", parentId, pageNum, pageSize);
            return null;
        }

        try {
            String cacheKey = CacheKey.keyForCommentReply(parentId, pageNum, pageSize, sortBy);
            PageResult<ArticleCommentVO> result = cacheManager.getWithType(cacheKey, new TypeReference<>() {
            });
            if (result != null) {
                log.debug("从缓存获取子评论列表, 父评论ID: {}", parentId);
                return result;
            }

            int offset = (pageNum - 1) * pageSize;

            List<ArticleCommentVO> replies = commentMapper.findRepliesByParentId(parentId, offset, pageSize, sortBy);
            if (replies == null || replies.isEmpty()) {
                return null;
            }

            Long total = commentMapper.countRepliesByParentId(parentId);

            fileService.ensureImageFullUrl(replies);

            result = PageResult.build(replies, total, pageNum, pageSize);
            cacheManager.setWithRandomOffset(cacheKey, result, CacheTTL.COMMENT_REPLIES);
            return result;
        } catch (Exception e) {
            log.error("获取子评论列表失败, 父评论ID: {}", parentId, e);
            return null;
        }
    }

    @Override
    public PageResult<ArticleCommentVO> getCommentsByPage(AdminCommentQueryDTO queryDTO) {
        try {
            int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
            queryDTO.setOffset(offset);

            List<ArticleCommentVO> articleCommentVOList = commentMapper.findCommentsByPage(queryDTO);
            Long total = commentMapper.countCommentsByPage(queryDTO);

            fileService.ensureImageFullUrl(articleCommentVOList);

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
