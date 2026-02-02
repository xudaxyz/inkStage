package com.inkstage.service.impl;


import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.dto.front.ArticleCreateDTO;
import com.inkstage.dto.front.ArticleQueryDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.entity.model.Tag;
import com.inkstage.entity.model.User;
import com.inkstage.enums.DeleteStatus;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.service.ArticleService;
import com.inkstage.service.FileService;
import com.inkstage.service.TagService;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {


    private final ArticleMapper articleMapper;
    private final TagService tagService;
    private final FileService fileService;

    @Override
    @Transactional
    public Long createArticle(ArticleCreateDTO articleCreateDTO) {
        // 从上下文获取用户信息
        log.info("从上下文中获取当前用户信息");
        User currentUser = UserContext.getCurrentUser();
        try {
            log.info("创建文章, 用户ID: {}, 标题: {}", currentUser.getId(), articleCreateDTO.getTitle());

            Article article = buildArticle(articleCreateDTO, currentUser);
            articleMapper.insert(article);

            // 处理标签关联
            handleArticleTags(article.getId(), articleCreateDTO.getTagIds());

            log.info("文章创建成功, 文章ID: {}", article.getId());
            return article.getId();
        } catch (Exception e) {
            log.error("创建文章失败, 用户ID: {}, 标题: {}", currentUser.getId(), articleCreateDTO.getTitle(), e);
            throw new BusinessException(ResponseMessage.ARTICLE_PUBLISH_FAILED, e.getMessage());
        }
    }


    @Override
    @Transactional
    public Long saveDraft(Long id, ArticleCreateDTO dto) {
        // 从上下文获取用户信息
        User currentUser = UserContext.getCurrentUser();
        try {
            if (id == null) {
                log.info("创建新草稿, 用户ID: {}", currentUser.getId());
                // 创建新草稿
                Article article = buildArticle(dto, currentUser);
                article.setStatus(ArticleStatus.DRAFT);
                article.setPublishTime(null);
                articleMapper.insert(article);

                // 处理标签关联
                handleArticleTags(article.getId(), dto.getTagIds());

                log.info("新草稿创建成功, 草稿ID: {}", article.getId());
                return article.getId();
            } else {
                log.info("更新草稿, 草稿ID: {}, 用户ID: {}", id, currentUser.getId());
                // 更新现有草稿
                Article existingArticle = articleMapper.selectById(id);
                if (existingArticle == null || !existingArticle.getUserId().equals(currentUser.getId())) {
                    log.warn("更新草稿失败, 草稿不存在或无权限, 草稿ID: {}, 用户ID: {}", id, currentUser.getId());
                    return null;
                }

                Article article = buildArticle(dto, currentUser);
                article.setId(id);
                article.setStatus(ArticleStatus.DRAFT);
                article.setPublishTime(null);
                article.setCreateTime(existingArticle.getCreateTime());
                article.setReadCount(existingArticle.getReadCount());
                article.setLikeCount(existingArticle.getLikeCount());
                article.setCommentCount(existingArticle.getCommentCount());
                article.setCollectionCount(existingArticle.getCollectionCount());
                article.setShareCount(existingArticle.getShareCount());

                int updateResult = articleMapper.update(article);
                if (updateResult == 0) {
                    log.warn("草稿更新失败, 草稿ID: {}, 用户ID: {}", id, currentUser.getId());
                    return null;
                }

                // 处理标签关联
                handleArticleTags(id, dto.getTagIds());

                log.info("草稿更新成功, 草稿ID: {}", id);
                return id;
            }
        } catch (Exception e) {
            log.error("保存草稿失败, 草稿ID: {}, 用户ID: {}", id, currentUser.getId(), e);
            throw new BusinessException(ResponseMessage.ARTICLE_DRAFT_FAILED, e.getMessage());
        }
    }


    @Override
    @Transactional
    public boolean deleteArticle(Long id) {
        // 从上下文获取用户信息
        User currentUser = UserContext.getCurrentUser();
        try {
            log.info("开始删除文章, 文章ID: {}, 用户ID: {}", id, currentUser.getId());

            boolean deleted = articleMapper.deleteById(id, currentUser.getId()) > 0;

            if (deleted) {
                log.info("文章删除成功, 文章ID: {}", id);
            } else {
                log.warn("文章删除失败, 文章不存在或无权限, 文章ID: {}, 用户ID: {}", id, currentUser.getId());
            }

            return deleted;
        } catch (Exception e) {
            log.error("删除文章失败, 文章ID: {}, 用户ID: {}", id, currentUser.getId(), e);
            throw new BusinessException(ResponseMessage.ARTICLE_DELETE_FAILED, e.getMessage());
        }
    }

    @Override
    public PageResult<ArticleListVO> getArticles(ArticleQueryDTO queryDTO) {
        try {
            log.info("获取文章列表, 查询参数: {}", queryDTO);

            // 计算偏移量
            int offset = (queryDTO.getPage() - 1) * queryDTO.getPageSize();
            queryDTO.setOffset(offset);

            // 查询文章列表
            List<ArticleListVO> articleList = articleMapper.selectArticleList(queryDTO);
            // 确保文章相关图片正常显示
            fileService.ensureArticleImageAreFullUrl(articleList);
            // 查询总记录数
            long total = articleMapper.countArticleList(queryDTO);

            // 构建分页结果
            PageResult<ArticleListVO> pageResult = PageResult.build(
                    articleList,
                    total,
                    queryDTO.getPage(),
                    queryDTO.getPageSize()
            );

            log.info("获取文章列表成功, 总数: {}, 页码: {}, 每页大小: {}", total, queryDTO.getPage(), queryDTO.getPageSize());
            return pageResult;
        } catch (Exception e) {
            log.error("获取文章列表失败, 查询参数: {}", queryDTO, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public ArticleDetailVO getArticleDetail(Long id) {
        log.info("获取文章详情, 文章ID: {}", id);

        // 查询文章详情
        ArticleDetailVO articleDetailVO = articleMapper.selectDetailById(id);
        if (articleDetailVO == null) {
            log.warn("文章不存在, 文章ID: {}", id);
            throw new BusinessException(ResponseMessage.ARTICLE_NOT_FOUND);
        }

        // 查询文章标签
        List<Tag> tagList = tagService.getTagsByArticleId(id);
        articleDetailVO.setTags(tagList);
        fileService.ensureArticleDetailIsFullUrl(articleDetailVO);

        log.info("获取文章详情成功, 文章ID: {}", id);
        return articleDetailVO;
    }


    /**
     * 构建文章实体
     */
    private Article buildArticle(ArticleCreateDTO dto, User user) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setContentHtml(dto.getContent()); // 暂时直接使用内容, 后续需要添加 Markdown 转换

        // 处理摘要
        String summary = dto.getSummary();
        if (summary == null || summary.trim().isEmpty()) {
            // 如果没有提供摘要, 自动从内容中提取前200字
            summary = generateSummary(dto.getContent());
        }
        article.setSummary(summary);
        article.setCoverImage(dto.getCoverImage());
        article.setUserId(user.getId());
        article.setAuthorName(user.getNickname());
        article.setCategoryId(dto.getCategoryId());
        article.setStatus(dto.getStatus());
        article.setVisible(dto.getVisible());
        article.setAllowComment(dto.getAllowComment());
        article.setAllowForward(dto.getAllowForward());
        article.setOriginal(dto.getOriginal());
        article.setOriginalUrl(dto.getOriginalUrl());
        article.setLastEditTime(LocalDateTime.now());
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        article.setDeleted(DeleteStatus.NOT_DELETED);

        // 初始化统计数据
        article.setReadCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setCollectionCount(0);
        article.setShareCount(0);

        // 设置发布时间
        if (ArticleStatus.PENDING == dto.getStatus() || ArticleStatus.PUBLISHED == dto.getStatus()) {
            article.setPublishTime(LocalDateTime.now());
        }

        return article;
    }

    /**
     * 生成文章摘要
     *
     * @param content 文章内容
     * @return 摘要(最多200字)
     */
    private String generateSummary(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }

        // 移除HTML标签(如果有)
        String plainText = content.replaceAll("<[^>]*>", "");

        // 移除Markdown标记
        plainText = plainText
                .replaceAll("#+", "") // 标题
                .replaceAll("\\*\\*", "") // 加粗
                .replaceAll("_+", "") // 斜体
                .replaceAll("`+", "") // 代码
                .replaceAll(">+", "") // 引用
                .replaceAll("\\*+", "") // 列表
                .replaceAll("-+", "") // 分割线
                .trim();

        // 截取前200字
        if (plainText.length() > 200) {
            plainText = plainText.substring(0, 200) + "...";
        }

        return plainText;
    }

    /**
     * 处理文章标签关联
     */
    private void handleArticleTags(Long articleId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            // 没有标签, 清除现有关联
            tagService.deleteArticleTagsByArticleId(articleId);
            return;
        }

        // 保存文章标签关联
        tagService.saveArticleTags(articleId, tagIds);
    }
}
