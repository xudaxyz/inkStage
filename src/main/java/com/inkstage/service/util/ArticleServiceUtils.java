package com.inkstage.service.util;

import com.inkstage.common.ResponseMessage;
import com.inkstage.entity.model.Article;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * 文章服务工具类
 * 提供文章服务相关的通用方法，如参数验证、异常处理等
 */
@Slf4j
public class ArticleServiceUtils {

    /**
     * 验证文章ID是否有效
     * @param id 文章ID
     * @param operation 操作名称
     */
    public static void validateArticleId(Long id, String operation) {
        if (id == null || id <= 0) {
            log.warn("{}参数无效, 文章ID: {}", operation, id);
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "文章ID无效");
        }
    }

    /**
     * 验证用户ID是否有效
     * @param userId 用户ID
     * @param operation 操作名称
     */
    public static void validateUserId(Long userId, String operation) {
        if (userId == null || userId <= 0) {
            log.warn("{}参数无效, 用户ID: {}", operation, userId);
            throw new BusinessException(ResponseMessage.PARAM_ERROR, "用户ID无效");
        }
    }

    /**
     * 验证分页参数
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 验证后的分页参数数组 [pageNum, pageSize]
     */
    public static int[] validatePageParams(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        return new int[]{pageNum, pageSize};
    }

    /**
     * 验证限制参数
     * @param limit 限制数量
     * @param defaultLimit 默认值
     * @return 验证后的限制数量
     */
    public static int validateLimit(Integer limit, int defaultLimit) {
        return limit == null || limit <= 0 ? defaultLimit : limit;
    }

    /**
     * 检查文章是否存在
     * @param article 文章对象
     * @param articleId 文章ID
     */
    public static void checkArticleExists(Article article, Long articleId) {
        if (article == null) {
            log.warn("文章不存在, 文章ID: {}", articleId);
            throw new BusinessException("文章不存在");
        }
    }

    /**
     * 检查操作结果是否成功
     * @param result 操作结果
     * @param articleId 文章ID
     * @param operation 操作名称
     */
    public static void checkOperationResult(int result, Long articleId, String operation) {
        if (result == 0) {
            log.warn("{}失败, 文章ID: {}", operation, articleId);
            throw new BusinessException(operation + "失败");
        }
    }

    /**
     * 安全地获取文章
     * @param articleMapper 文章Mapper
     * @param articleId 文章ID
     * @return 文章对象
     */
    public static Article getArticleSafely(ArticleMapper articleMapper, Long articleId) {
        Article article = articleMapper.findById(articleId);
        checkArticleExists(article, articleId);
        return article;
    }
}
