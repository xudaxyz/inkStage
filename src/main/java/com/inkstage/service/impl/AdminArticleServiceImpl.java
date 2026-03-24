package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.dto.admin.AdminArticleQueryDTO;
import com.inkstage.dto.admin.AdminArticleUpdateDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.entity.model.Tag;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.service.AdminArticleService;
import com.inkstage.service.ArticleTagService;
import com.inkstage.service.FileService;
import com.inkstage.service.util.ArticleServiceUtils;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.admin.AdminArticleDetailVO;
import com.inkstage.vo.admin.AdminArticleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 管理员文章服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminArticleServiceImpl implements AdminArticleService {

    private final ArticleMapper articleMapper;
    private final FileService fileService;
    private final ArticleTagService articleTagService;

    @Override
    public PageResult<AdminArticleVO> getAdminArticlesByPage(AdminArticleQueryDTO queryDTO) {
        try {
            log.debug("管理员获取文章列表, 页码: {}, 每页大小: {}, 关键词: {}, 分类ID: {}, 文章状态: {}",
                    queryDTO.getPageNum(), queryDTO.getPageSize(), queryDTO.getKeyword(),
                    queryDTO.getCategoryId(), queryDTO.getArticleStatus());

            // 计算偏移量
            int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
            queryDTO.setOffset(offset);

            // 查询文章列表
            var articleList = articleMapper.findAdminArticlesByPage(queryDTO);
            // 查询总记录数
            long total = articleMapper.countByPage(queryDTO);

            // 构建分页结果
            var pageResult = PageResult.build(
                    articleList,
                    total,
                    queryDTO.getPageNum(),
                    queryDTO.getPageSize()
            );

            log.info("管理员获取文章列表成功, 总数: {}", total);
            return pageResult;
        } catch (Exception e) {
            log.error("管理员获取文章列表失败, 页码: {}, 每页大小: {}", queryDTO.getPageNum(), queryDTO.getPageSize(), e);
            throw new BusinessException("获取文章列表失败");
        }
    }

    @Override
    public boolean updateArticleStatus(Long id, ArticleStatus status) {
        try {
            log.debug("更新文章状态, 文章ID: {}, 状态: {}", id, status.getDesc());
            // 检查文章是否存在
            ArticleServiceUtils.getArticleSafely(articleMapper, id);
            // 更新状态
            int result = articleMapper.updateStatus(id, status);
            ArticleServiceUtils.checkOperationResult(result, id, "更新文章状态");
            return result > 0;
        } catch (Exception e) {
            log.error("更新文章状态失败, 文章ID: {}, 状态: {}", id, status.getDesc(), e);
            throw new BusinessException("更新文章状态失败");
        }
    }

    @Override
    public boolean approveArticle(Long id) {
        try {
            log.debug("审核通过文章, 文章ID: {}", id);
            // 检查文章是否存在
            ArticleServiceUtils.getArticleSafely(articleMapper, id);
            // 更新审核状态为通过
            int result = articleMapper.updateReviewStatus(id, com.inkstage.enums.ReviewStatus.APPROVED);
            ArticleServiceUtils.checkOperationResult(result, id, "审核通过文章");
            // 更新文章状态为已发布
            articleMapper.updateStatus(id, ArticleStatus.PUBLISHED);
            log.info("审核通过文章成功, 文章ID: {}", id);
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("审核通过文章失败, 文章ID: {}", id, e);
            throw new BusinessException("审核通过文章失败");
        }
    }

    @Override
    public boolean rejectArticle(Long id, String reason) {
        try {
            log.debug("审核拒绝文章, 文章ID: {}, 原因: {}", id, reason);
            // 检查文章是否存在
            ArticleServiceUtils.getArticleSafely(articleMapper, id);
            // 更新审核状态为拒绝
            int result = articleMapper.updateReviewStatus(id, com.inkstage.enums.ReviewStatus.REJECTED);
            ArticleServiceUtils.checkOperationResult(result, id, "审核拒绝文章");
            // 这里可以添加拒绝原因的存储逻辑
            // 例如：articleMapper.updateRejectReason(id, reason);
            log.info("审核拒绝文章成功, 文章ID: {}", id);
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("审核拒绝文章失败, 文章ID: {}", id, e);
            throw new BusinessException("审核拒绝文章失败");
        }
    }

    @Override
    public boolean reprocessArticle(Long id) {
        try {
            log.debug("重新审核文章, 文章ID: {}", id);
            // 检查文章是否存在
            ArticleServiceUtils.getArticleSafely(articleMapper, id);
            // 更新审核状态为待审核
            int result = articleMapper.updateReviewStatus(id, com.inkstage.enums.ReviewStatus.PENDING);
            ArticleServiceUtils.checkOperationResult(result, id, "重新审核文章");
            log.info("重新审核文章成功, 文章ID: {}", id);
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("重新审核文章失败, 文章ID: {}", id, e);
            throw new BusinessException("重新审核文章失败");
        }
    }

    @Override
    public AdminArticleDetailVO getAdminArticleDetail(Long id) {
        try {
            log.debug("获取管理员文章详情, 文章ID: {}", id);
            // 检查文章是否存在
            AdminArticleDetailVO adminArticleDetailVO = articleMapper.findAdminDetailById(id);
            if (adminArticleDetailVO == null) {
                throw new BusinessException("文章不存在");
            }
            fileService.ensureAdminArticleDetailIsFullUrl(adminArticleDetailVO);

            // 获取标签列表
            List<Tag> tags = articleTagService.getTagsByArticleId(id);
            if (tags != null && !tags.isEmpty()) {
                adminArticleDetailVO.setTags(tags);
            }

            log.info("获取管理员文章详情成功, 文章ID: {}", id);
            return adminArticleDetailVO;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取管理员文章详情失败, 文章ID: {}", id, e);
            throw new BusinessException("获取文章详情失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean topArticle(Long id) {
        try {
            log.debug("置顶文章, 文章ID: {}", id);
            // 检查文章是否存在
            ArticleServiceUtils.getArticleSafely(articleMapper, id);
            // 更新置顶状态
            int result = articleMapper.updateTopStatus(id, com.inkstage.enums.article.TopStatus.TOP);
            ArticleServiceUtils.checkOperationResult(result, id, "置顶文章");
            log.info("置顶文章成功, 文章ID: {}", id);
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("置顶文章失败, 文章ID: {}", id, e);
            throw new BusinessException("置顶文章失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelTopArticle(Long id) {
        try {
            log.debug("取消置顶文章, 文章ID: {}", id);
            // 检查文章是否存在
            ArticleServiceUtils.getArticleSafely(articleMapper, id);
            // 更新置顶状态
            int result = articleMapper.updateTopStatus(id, com.inkstage.enums.article.TopStatus.NOT_TOP);
            ArticleServiceUtils.checkOperationResult(result, id, "取消置顶文章");
            log.info("取消置顶文章成功, 文章ID: {}", id);
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("取消置顶文章失败, 文章ID: {}", id, e);
            throw new BusinessException("取消置顶文章失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recommendArticle(Long id) {
        try {
            log.debug("推荐文章, 文章ID: {}", id);
            // 检查文章是否存在
            ArticleServiceUtils.getArticleSafely(articleMapper, id);
            // 更新推荐状态
            int result = articleMapper.updateRecommendStatus(id, com.inkstage.enums.article.RecommendStatus.RECOMMENDED);
            ArticleServiceUtils.checkOperationResult(result, id, "推荐文章");
            log.info("推荐文章成功, 文章ID: {}", id);
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("推荐文章失败, 文章ID: {}", id, e);
            throw new BusinessException("推荐文章失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelRecommendArticle(Long id) {
        try {
            log.debug("取消推荐文章, 文章ID: {}", id);
            // 检查文章是否存在
            ArticleServiceUtils.getArticleSafely(articleMapper, id);
            // 更新推荐状态
            int result = articleMapper.updateRecommendStatus(id, com.inkstage.enums.article.RecommendStatus.NOT_RECOMMENDED);
            ArticleServiceUtils.checkOperationResult(result, id, "取消推荐文章");
            log.info("取消推荐文章成功, 文章ID: {}", id);
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("取消推荐文章失败, 文章ID: {}", id, e);
            throw new BusinessException("取消推荐文章失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticleByAdmin(Long id, AdminArticleUpdateDTO updateDTO) {
        try {
            log.debug("管理员更新文章, 文章ID: {}", id);
            // 检查文章是否存在
            Article article = ArticleServiceUtils.getArticleSafely(articleMapper, id);

            // 更新文章基本信息
            article.setTitle(updateDTO.getTitle());
            article.setCategoryId(updateDTO.getCategoryId());
            article.setArticleStatus(updateDTO.getArticleStatus());

            // 更新可选字段
            if (updateDTO.getSummary() != null) {
                article.setSummary(updateDTO.getSummary());
            }
            if (updateDTO.getContent() != null) {
                article.setContent(updateDTO.getContent());
                article.setContentHtml(updateDTO.getContent());
            }
            if (updateDTO.getCoverImage() != null) {
                article.setCoverImage(updateDTO.getCoverImage());
            }
            if (updateDTO.getMetaTitle() != null) {
                article.setMetaTitle(updateDTO.getMetaTitle());
            }
            if (updateDTO.getMetaDescription() != null) {
                article.setMetaDescription(updateDTO.getMetaDescription());
            }
            if (updateDTO.getMetaKeywords() != null) {
                article.setMetaKeywords(updateDTO.getMetaKeywords());
            }

            // 更新文章
            int result = articleMapper.update(article);
            ArticleServiceUtils.checkOperationResult(result, id, "更新文章");

            // 处理标签
            articleTagService.handleArticleTags(id, updateDTO.getTags());

            log.info("管理员更新文章成功, 文章ID: {}", id);
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("管理员更新文章失败, 文章ID: {}", id, e);
            throw new BusinessException("更新文章失败");
        }
    }

    @Override
    public boolean deleteArticleByAdmin(Long id) {
        if (UserContext.isAdmin()) {
            return false;
        }

        log.debug("管理员删除文章, 文章ID: {}", id);
        int i = articleMapper.deleteByAdmin(id);
        ArticleServiceUtils.checkOperationResult(i, id, "管理员删除文章");
        return i > 0;
    }


}
