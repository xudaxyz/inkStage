package com.inkstage.service.impl;


import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.constant.RedisKeyConstants;
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
import com.inkstage.utils.RedisUtil;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private final RedisUtil redisUtil;

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
                // 清除文章详情缓存
                String detailCacheKey = RedisKeyConstants.buildCacheKey("article:detail", id.toString());
                redisUtil.delete(detailCacheKey);
                log.info("清除文章详情缓存, 缓存键: {}", detailCacheKey);

                // 清除文章列表缓存
                redisUtil.deletePattern("cache:article:list:*");
                log.info("清除文章列表缓存");

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

            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "article:list",
                    queryDTO.getPage() + ":" + queryDTO.getPageSize() + ":" +
                            (queryDTO.getCategoryId() != null ? queryDTO.getCategoryId() : "null") + ":" +
                            (queryDTO.getTagId() != null ? queryDTO.getTagId() : "null")
            );

            // 尝试从缓存获取
            PageResult<ArticleListVO> pageResult = redisUtil.get(cacheKey, new TypeReference<>() {
            });
            if (pageResult != null) {
                log.info("从缓存获取文章列表成功, 缓存键: {}", cacheKey);
                return pageResult;
            }

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
            pageResult = PageResult.build(
                    articleList,
                    total,
                    queryDTO.getPage(),
                    queryDTO.getPageSize()
            );

            // 更新缓存
            redisUtil.set(cacheKey, pageResult, 30, TimeUnit.MINUTES);
            log.info("更新文章列表缓存, 缓存键: {}", cacheKey);

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

        // 生成缓存键
        String cacheKey = RedisKeyConstants.buildCacheKey("article:detail", id.toString());

        // 尝试从缓存获取
        ArticleDetailVO articleDetailVO = redisUtil.get(cacheKey, ArticleDetailVO.class);
        if (articleDetailVO != null) {
            log.info("从缓存获取文章详情成功, 缓存键: {}", cacheKey);
            return articleDetailVO;
        }

        // 查询文章详情
        articleDetailVO = articleMapper.selectDetailById(id);
        if (articleDetailVO == null) {
            log.warn("文章不存在, 文章ID: {}", id);
            throw new BusinessException(ResponseMessage.ARTICLE_NOT_FOUND);
        }

        // 查询文章标签
        List<Tag> tagList = tagService.getTagsByArticleId(id);
        articleDetailVO.setTags(tagList);
        fileService.ensureArticleDetailIsFullUrl(articleDetailVO);

        // 更新缓存
        redisUtil.set(cacheKey, articleDetailVO, 30, TimeUnit.MINUTES);
        log.info("更新文章详情缓存, 缓存键: {}", cacheKey);

        log.info("获取文章详情成功, 文章ID: {}", id);
        return articleDetailVO;
    }

    @Override
    @Transactional
    public boolean updateArticle(Long articleId, ArticleCreateDTO articleCreateDTO) {
        // 从上下文获取用户信息
        User currentUser = UserContext.getCurrentUser();
        try {
            log.info("更新文章, 文章ID: {}, 用户ID: {}", articleId, currentUser.getId());

            // 检查文章是否存在且属于当前用户
            Article existingArticle = articleMapper.selectById(articleId);
            if (existingArticle == null || !existingArticle.getUserId().equals(currentUser.getId())) {
                log.warn("更新文章失败, 文章不存在或无权限, 文章ID: {}, 用户ID: {}", articleId, currentUser.getId());
                return false;
            }

            // 构建更新后的文章实体
            existingArticle.setTitle(articleCreateDTO.getTitle());
            existingArticle.setContent(articleCreateDTO.getContent());
            existingArticle.setSummary(articleCreateDTO.getSummary());
            existingArticle.setCategoryId(articleCreateDTO.getCategoryId());
            existingArticle.setAllowComment(articleCreateDTO.getAllowComment());
            existingArticle.setOriginal(articleCreateDTO.getOriginal());
            existingArticle.setOriginalUrl(articleCreateDTO.getOriginalUrl());
            existingArticle.setLastEditTime(LocalDateTime.now());
            existingArticle.setVisible(articleCreateDTO.getVisible());
            existingArticle.setAllowForward(articleCreateDTO.getAllowForward());
            existingArticle.setTop(articleCreateDTO.getTop());
            existingArticle.setUpdateTime(LocalDateTime.now());

            // 更新文章
            int updateResult = articleMapper.update(existingArticle);
            if (updateResult <= 0) {
                log.warn("文章更新失败, 文章ID: {}, 用户ID: {}", articleId, currentUser.getId());
                return false;
            }

            // 处理标签关联
            handleArticleTags(articleId, articleCreateDTO.getTagIds());

            // 清除文章详情缓存
            String detailCacheKey = RedisKeyConstants.buildCacheKey("article:detail", articleId.toString());
            redisUtil.delete(detailCacheKey);

            // 清除文章列表缓存
            redisUtil.deletePattern("cache:article:list:*");

            log.info("文章更新成功, 文章ID: {}", articleId);
            return true;
        } catch (Exception e) {
            log.error("更新文章失败, 文章ID: {}, 用户ID: {}, 标题: {}", articleId, currentUser.getId(), articleCreateDTO.getTitle(), e);
            throw new BusinessException(ResponseMessage.ARTICLE_UPDATE_FAILED, e.getMessage());
        }
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

    /**
     * 增加文章阅读数
     *
     * @param articleId 文章ID
     * @return 增加后的阅读数
     */
    public Long incrementArticleReadCount(Long articleId) {
        try {
            // 生成Redis计数器键
            String readCountKey = RedisKeyConstants.buildHotDataKey("article", articleId + ":read");

            // 增加Redis计数器
            Long count = redisUtil.increment(readCountKey);
            if (count != null) {
                log.info("文章阅读数增加成功, 文章ID: {}, 阅读数: {}", articleId, count);

                // 异步更新数据库（这里简化处理，实际项目中应使用@Async或消息队列）
                // articleMapper.updateReadCount(articleId, count);

                return count;
            }
            return null;
        } catch (Exception e) {
            log.error("增加文章阅读数失败, 文章ID: {}", articleId, e);
            return null;
        }
    }

    /**
     * 增加文章点赞数
     *
     * @param articleId 文章ID
     * @return 增加后的点赞数
     */
    public Long incrementArticleLikeCount(Long articleId) {
        return incrementArticleCount(articleId, "like");
    }

    /**
     * 增加文章评论数
     *
     * @param articleId 文章ID
     * @return 增加后的评论数
     */
    public Long incrementArticleCommentCount(Long articleId) {
        return incrementArticleCount(articleId, "comment");
    }

    /**
     * 增加文章收藏数
     *
     * @param articleId 文章ID
     * @return 增加后的收藏数
     */
    public Long incrementArticleCollectionCount(Long articleId) {
        return incrementArticleCount(articleId, "collection");
    }

    /**
     * 增加文章分享数
     *
     * @param articleId 文章ID
     * @return 增加后的分享数
     */
    public Long incrementArticleShareCount(Long articleId) {
        return incrementArticleCount(articleId, "share");
    }

    /**
     * 增加文章计数的通用方法
     *
     * @param articleId 文章ID
     * @param countType 计数类型
     * @return 增加后的计数值
     */
    private Long incrementArticleCount(Long articleId, String countType) {
        try {
            // 生成Redis计数器键
            String countKey = RedisKeyConstants.buildHotDataKey("article", articleId + ":" + countType);

            // 增加Redis计数器
            Long count = redisUtil.increment(countKey);
            if (count != null) {
                log.info("文章{}数增加成功, 文章ID: {}, 计数: {}", countType, articleId, count);

                // 异步更新数据库（这里简化处理，实际项目中应使用@Async或消息队列）
                // 根据countType调用不同的更新方法

                return count;
            }
            return null;
        } catch (Exception e) {
            log.error("增加文章{}数失败, 文章ID: {}", countType, articleId, e);
            return null;
        }
    }

    /**
     * 获取文章计数
     *
     * @param articleId 文章ID
     * @param countType 计数类型
     * @return 计数值
     */
    public Long getArticleCount(Long articleId, String countType) {
        try {
            // 生成Redis计数器键
            String countKey = RedisKeyConstants.buildHotDataKey("article", articleId + ":" + countType);

            // 获取Redis计数器值
            Object value = redisUtil.get(countKey);
            if (value != null) {
                if (value instanceof Long) {
                    return (Long) value;
                } else if (value instanceof String) {
                    try {
                        return Long.parseLong((String) value);
                    } catch (NumberFormatException e) {
                        log.error("解析文章计数值失败, 文章ID: {}, 计数类型: {}", articleId, countType, e);
                    }
                }
            }

            // 如果Redis中不存在，从数据库获取并更新到Redis
            // 这里简化处理，实际项目中应从数据库查询
            return 0L;
        } catch (Exception e) {
            log.error("获取文章{}数失败, 文章ID: {}", countType, articleId, e);
            return 0L;
        }
    }

    @Override
    public List<ArticleListVO> getHotArticles(Integer limit, String timeRange) {
        try {
            log.info("获取热门文章, limit: {}, timeRange: {}", limit, timeRange);

            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "article:hot",
                    limit + ":" + timeRange
            );

            // 尝试从缓存获取
            List<ArticleListVO> hotArticles = redisUtil.get(cacheKey, new TypeReference<>() {
            });
            if (hotArticles != null) {
                log.info("从缓存获取热门文章成功, 缓存键: {}", cacheKey);
                return hotArticles;
            }

            // 查询热门文章
            // 这里简化处理，实际项目中应根据时间范围和热度算法查询
            // 暂时从数据库查询已发布的文章，并按阅读数排序
            hotArticles = articleMapper.selectHotArticles(limit);
            fileService.ensureArticleImageAreFullUrl(hotArticles);

            // 更新缓存
            redisUtil.set(cacheKey, hotArticles, 30, TimeUnit.MINUTES);
            log.info("更新热门文章缓存, 缓存键: {}", cacheKey);

            log.info("获取热门文章成功, 数量: {}", hotArticles.size());
            return hotArticles;
        } catch (Exception e) {
            log.error("获取热门文章失败, limit: {}, timeRange: {}", limit, timeRange, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public List<ArticleListVO> getLatestArticles(Integer limit) {
        try {
            log.info("获取最新文章, limit: {}", limit);

            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "article:latest",
                    limit.toString()
            );

            // 尝试从缓存获取
            List<ArticleListVO> latestArticles = redisUtil.get(cacheKey, new TypeReference<>() {
            });
            if (latestArticles != null) {
                log.info("从缓存获取最新文章成功, 缓存键: {}", cacheKey);
                return latestArticles;
            }

            // 查询最新文章
            latestArticles = articleMapper.selectLatestArticles(limit);
            fileService.ensureArticleImageAreFullUrl(latestArticles);

            // 更新缓存
            redisUtil.set(cacheKey, latestArticles, 30, TimeUnit.MINUTES);
            log.info("更新最新文章缓存, 缓存键: {}", cacheKey);

            log.info("获取最新文章成功, 数量: {}", latestArticles.size());
            return latestArticles;
        } catch (Exception e) {
            log.error("获取最新文章失败, limit: {}", limit, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public List<ArticleListVO> getBannerArticles(Integer limit) {
        try {
            log.info("获取轮播图文章, limit: {}", limit);

            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "article:banner",
                    limit.toString()
            );

            // 尝试从缓存获取
            List<ArticleListVO> bannerArticles = redisUtil.get(cacheKey, new TypeReference<>() {
            });
            if (bannerArticles != null) {
                log.info("从缓存获取轮播图文章成功, 缓存键: {}", cacheKey);
                return bannerArticles;
            }

            // 查询轮播图文章
            bannerArticles = articleMapper.selectBannerArticles(limit);
            fileService.ensureArticleImageAreFullUrl(bannerArticles);

            // 更新缓存
            redisUtil.set(cacheKey, bannerArticles, 30, TimeUnit.MINUTES);
            log.info("更新轮播图文章缓存, 缓存键: {}", cacheKey);

            log.info("获取轮播图文章成功, 数量: {}", bannerArticles.size());
            return bannerArticles;
        } catch (Exception e) {
            log.error("获取轮播图文章失败, limit: {}", limit, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public PageResult<ArticleListVO> getUserArticles(Long userId, Integer page, Integer size) {
        try {
            log.info("获取用户文章列表, 用户ID: {}, 页码: {}, 每页大小: {}", userId, page, size);

            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "article:user",
                    userId + ":" + page + ":" + size
            );

            // 尝试从缓存获取
            PageResult<ArticleListVO> pageResult = redisUtil.get(cacheKey, new TypeReference<>() {
            });
            if (pageResult != null) {
                log.info("从缓存获取用户文章列表成功, 缓存键: {}", cacheKey);
                return pageResult;
            }

            // 计算偏移量
            int offset = (page - 1) * size;

            // 查询用户文章列表
            List<ArticleListVO> articleList = articleMapper.selectUserArticles(userId, offset, size);
            // 确保文章相关图片正常显示
            fileService.ensureArticleImageAreFullUrl(articleList);
            // 查询总记录数
            long total = articleMapper.countUserArticles(userId);

            // 构建分页结果
            pageResult = PageResult.build(
                    articleList,
                    total,
                    page,
                    size
            );

            // 更新缓存
            redisUtil.set(cacheKey, pageResult, 30, TimeUnit.MINUTES);
            log.info("更新用户文章列表缓存, 缓存键: {}", cacheKey);

            log.info("获取用户文章列表成功, 总数: {}, 页码: {}, 每页大小: {}", total, page, size);
            return pageResult;
        } catch (Exception e) {
            log.error("获取用户文章列表失败, 用户ID: {}, 页码: {}, 每页大小: {}", userId, page, size, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public List<ArticleListVO> getAuthorRelatedArticles(Long userId, Long excludeArticleId, Integer limit) {
        try {
            log.info("获取作者相关文章, 用户ID: {}, 排除文章ID: {}, 限制数量: {}", userId, excludeArticleId, limit);

            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "article:author:related",
                    userId + ":" + excludeArticleId + ":" + limit
            );

            // 尝试从缓存获取
            List<ArticleListVO> relatedArticles = redisUtil.get(cacheKey, new TypeReference<>() {
            });
            if (relatedArticles != null) {
                log.info("从缓存获取作者相关文章成功, 缓存键: {}", cacheKey);
                return relatedArticles;
            }

            // 查询作者相关文章
            relatedArticles = articleMapper.selectAuthorRelatedArticles(userId, excludeArticleId, limit);
            // 确保文章相关图片正常显示
            fileService.ensureArticleImageAreFullUrl(relatedArticles);

            // 更新缓存
            redisUtil.set(cacheKey, relatedArticles, 30, TimeUnit.MINUTES);
            log.info("更新作者相关文章缓存, 缓存键: {}", cacheKey);

            log.info("获取作者相关文章成功, 数量: {}", relatedArticles.size());
            return relatedArticles;
        } catch (Exception e) {
            log.error("获取作者相关文章失败, 用户ID: {}, 排除文章ID: {}, 限制数量: {}", userId, excludeArticleId, limit, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }
}
