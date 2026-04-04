package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.service.ArticleCommandService;
import com.inkstage.service.ArticleManagementService;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.MyArticleListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户文章管理服务实现类
 * 职责：文章删除操作委托给 ArticleCommandService，专注于用户文章查询
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleManagementServiceImpl implements ArticleManagementService {

    private final ArticleMapper articleMapper;
    private final ArticleCommandService articleCommandService;

    @Override
    public boolean deleteArticle(Long id) {
        return articleCommandService.deleteArticle(id);
    }

    @Override
    public boolean permanentDeleteArticle(Long id) {
        return articleCommandService.permanentDeleteArticle(id);
    }

    @Override
    public PageResult<MyArticleListVO> getMyArticles(ArticleStatus articleStatus, String keyword, Integer page, Integer size) {
        try {
            // 从上下文获取用户信息
            var currentUser = UserContext.getCurrentUser();
            log.debug("获取当前用户文章列表, 用户ID: {}, 状态: {}, 关键词: {}, 页码: {}, 每页大小: {}",
                    currentUser.getId(), articleStatus.getDesc(), keyword, page, size);

            // 计算偏移量
            int offset = (page - 1) * size;

            // 查询当前用户文章列表
            var myArticleList = articleMapper.findMyArticles(currentUser.getId(), articleStatus, keyword, offset, size);
            // 查询总记录数
            long total = articleMapper.countMyArticles(currentUser.getId(), articleStatus, keyword);

            // 构建分页结果
            var pageResult = PageResult.build(
                    myArticleList,
                    total,
                    page,
                    size
            );

            log.info("获取当前用户文章列表成功, 总数: {}, 页码: {}, 每页大小: {}", total, page, size);
            return pageResult;
        } catch (Exception e) {
            log.error("获取当前用户文章列表失败, 状态: {}, 关键词: {}, 页码: {}, 每页大小: {}", articleStatus.getDesc(), keyword, page, size, e);
            throw new BusinessException("获取文章列表失败");
        }
    }
}
