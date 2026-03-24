package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.entity.model.User;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.mapper.UserMapper;
import com.inkstage.service.ArticleManagementService;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.MyArticleListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户文章管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleManagementServiceImpl implements ArticleManagementService {

    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteArticle(Long id) {
        try {
            log.debug("删除文章, 文章ID: {}", id);
            // 从上下文获取用户信息
            var currentUser = UserContext.getCurrentUser();
            // 检查文章是否存在且属于当前用户
            var article = articleMapper.findById(id);
            if (article == null) {
                log.warn("文章不存在, 文章ID: {}", id);
                throw new BusinessException("文章不存在");
            }
            if (!article.getUserId().equals(currentUser.getId())) {
                log.warn("无权删除他人文章, 用户ID: {}, 文章ID: {}", currentUser.getId(), id);
                throw new BusinessException("无权删除他人文章");
            }
            // 执行删除操作
            int result = articleMapper.deleteById(id, currentUser.getId());
            boolean success = result > 0;
            if (success) {
                // 更新用户文章数
                User user = userMapper.findById(currentUser.getId());
                if (user != null) {
                    int articleCount = user.getArticleCount() != null ? user.getArticleCount() : 0;
                    if (articleCount > 0) {
                        user.setArticleCount(articleCount - 1);
                        userMapper.updateByPrimaryKeySelective(user);
                    }
                }
            }
            log.info("删除文章{}, 文章ID: {}", success ? "成功" : "失败", id);
            return success;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除文章失败, 文章ID: {}", id, e);
            throw new BusinessException("删除文章失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean permanentDeleteArticle(Long id) {
        try {
            log.debug("彻底删除文章, 文章ID: {}", id);
            Long currentUserId = UserContext.getCurrentUserId();
            // 检查文章是否存在且属于当前用户
            var article = articleMapper.findById(id);
            if (article == null) {
                log.warn("文章ID: {}不存在", id);
                throw new BusinessException("文章不存在");
            }
            if (!article.getUserId().equals(currentUserId)) {
                log.warn("无权彻底删除他人文章, 用户ID: {}, 文章ID: {}", currentUserId, id);
                throw new BusinessException("无权删除他人文章");
            }
            // 执行彻底删除操作
            int result = articleMapper.permanentDeleteById(id, currentUserId);
            boolean success = result > 0;
            if (success) {
                // 更新用户文章数
                User user = userMapper.findById(currentUserId);
                if (user != null) {
                    int articleCount = user.getArticleCount() != null ? user.getArticleCount() : 0;
                    if (articleCount > 0) {
                        user.setArticleCount(articleCount - 1);
                        userMapper.updateByPrimaryKeySelective(user);
                    }
                }
            }
            log.info("彻底删除文章{}, 文章ID: {}", success ? "成功" : "失败", id);
            return success;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("彻底删除文章失败, 文章ID: {}", id, e);
            throw new BusinessException("彻底删除文章失败");
        }
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