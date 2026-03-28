package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.constant.RedisKeyConstants;
import com.inkstage.dto.admin.AdminArticleQueryDTO;
import com.inkstage.dto.front.ArticleQueryDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.entity.model.Tag;
import com.inkstage.entity.model.User;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleMapper;
import com.inkstage.service.*;
import com.inkstage.utils.RedisUtil;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.admin.AdminArticleVO;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;
import com.inkstage.vo.front.MyArticleListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import tools.jackson.core.type.TypeReference;

/**
 * 文章查询服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleQueryServiceImpl implements ArticleQueryService {

    private final ArticleMapper articleMapper;
    private final ArticleTagService articleTagService;
    private final FileService fileService;
    private final RedisUtil redisUtil;
    private final ArticleLikeService articleLikeService;
    private final ArticleCollectionService articleCollectionService;
    private final CountService countService;

    /**
     * 获取文章列表
     */
    public PageResult<ArticleListVO> getArticles(ArticleQueryDTO queryDTO) {
        try {
            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "article:list:",
                    queryDTO.getPageNum() + ":" + queryDTO.getPageSize() + ":" +
                            (queryDTO.getCategoryId() != null ? queryDTO.getCategoryId() : "null") + ":" +
                            (queryDTO.getTagId() != null ? queryDTO.getTagId() : "null")
            );

            // 尝试从缓存获取
            PageResult<ArticleListVO> pageResult = redisUtil.getWithType(cacheKey, new TypeReference<>() {
            });
            if (pageResult != null) {
                return pageResult;
            }

            // 计算偏移量
            int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
            queryDTO.setOffset(offset);

            // 查询文章列表
            List<ArticleListVO> articleList = articleMapper.findArticleList(queryDTO);
            // 确保文章相关图片正常显示
            fileService.ensureArticleImageAreFullUrl(articleList);
            // 查询总记录数
            long total = articleMapper.countArticleList(queryDTO);

            // 构建分页结果
            pageResult = PageResult.build(
                    articleList,
                    total,
                    queryDTO.getPageNum(),
                    queryDTO.getPageSize()
            );

            // 更新缓存
            redisUtil.set(cacheKey, pageResult, 30, TimeUnit.MINUTES);

            log.info("获取文章列表成功, 总数: {}, 页码: {}, 每页大小: {}", total, queryDTO.getPageNum(), queryDTO.getPageSize());
            return pageResult;
        } catch (Exception e) {
            log.error("获取文章列表失败, 查询参数: {}", queryDTO, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    /**
     * 获取文章详情
     */
    public ArticleDetailVO getArticleDetail(Long id) {
        // 生成缓存键（不包含用户信息，因为缓存的是通用信息）
        String cacheKey = RedisKeyConstants.buildCacheKey("article:detail:", id.toString());

        // 尝试从缓存获取通用信息
        ArticleDetailVO articleDetailVO = redisUtil.get(cacheKey, ArticleDetailVO.class);
        if (articleDetailVO == null) {
            // 查询文章详情
            articleDetailVO = articleMapper.findDetailById(id);
            if (articleDetailVO == null) {
                log.warn("文章: {} 不存在", id);
                throw new BusinessException(ResponseMessage.ARTICLE_NOT_FOUND);
            }

            // 查询文章标签
            List<Tag> tagList = articleTagService.getTagsByArticleId(id);
            articleDetailVO.setTags(tagList);
            fileService.ensureArticleDetailIsFullUrl(articleDetailVO);

            // 更新缓存（只缓存通用信息）
            redisUtil.set(cacheKey, articleDetailVO, 30, TimeUnit.MINUTES);
        }

        // 获取当前用户的点赞和收藏状态(不缓存, 每次都从数据库或Redis获取)
        Optional<User> currentUser = UserContext.getCurrentUserOptional();
        if (currentUser.isPresent()) {
            // 检查点赞状态
            boolean isLiked = articleLikeService.isArticleLiked(id);
            articleDetailVO.setIsLiked(isLiked);


            // 检查收藏状态
            boolean isCollected = articleCollectionService.isArticleCollected(id);
            articleDetailVO.setIsCollected(isCollected);

        } else {
            // 用户未登录, 设置为false
            articleDetailVO.setIsLiked(false);
            articleDetailVO.setIsCollected(false);
        }

        // 更新点赞数
        Long likeCount = countService.getArticleLikeCount(id);
        articleDetailVO.setLikeCount(Math.toIntExact(likeCount));

        // 更新收藏数
        Long collectionCount = countService.getArticleCollectionCount(id);
        articleDetailVO.setCollectionCount(Math.toIntExact(collectionCount));

        return articleDetailVO;
    }

    /**
     * 获取热门文章
     */
    public List<ArticleListVO> getHotArticles(Integer limit, String timeRange) {
        try {
            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "article:hot:",
                    limit + ":" + timeRange
            );

            // 尝试从缓存获取
            List<ArticleListVO> hotArticles = redisUtil.getWithType(cacheKey, new TypeReference<>() {
            });
            if (hotArticles != null) {
                return hotArticles;
            }

            // 查询热门文章
            // 这里简化处理，实际项目中应根据时间范围和热度算法查询
            // 暂时从数据库查询已发布的文章，并按阅读数排序
            hotArticles = articleMapper.findHotArticles(limit);
            fileService.ensureArticleImageAreFullUrl(hotArticles);

            // 更新缓存
            redisUtil.set(cacheKey, hotArticles, 30, TimeUnit.MINUTES);

            return hotArticles;
        } catch (Exception e) {
            log.error("获取热门文章失败, limit: {}, timeRange: {}", limit, timeRange, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    /**
     * 获取最新文章
     */
    public List<ArticleListVO> getLatestArticles(Integer limit) {
        try {
            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "article:latest:",
                    limit.toString()
            );

            // 尝试从缓存获取
            List<ArticleListVO> latestArticles = redisUtil.getWithType(cacheKey, new TypeReference<>() {
            });
            if (latestArticles != null) {
                return latestArticles;
            }

            // 查询最新文章
            latestArticles = articleMapper.findLatestArticles(limit);
            fileService.ensureArticleImageAreFullUrl(latestArticles);

            // 更新缓存
            redisUtil.set(cacheKey, latestArticles, 30, TimeUnit.MINUTES);

            return latestArticles;
        } catch (Exception e) {
            log.error("获取最新文章失败, limit: {}", limit, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    /**
     * 获取轮播图文章
     */
    public List<ArticleListVO> getBannerArticles(Integer limit) {
        try {
            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "article:banner:",
                    limit.toString()
            );

            // 尝试从缓存获取
            List<ArticleListVO> bannerArticles = redisUtil.getWithType(cacheKey, new TypeReference<>() {
            });
            if (bannerArticles != null) {
                return bannerArticles;
            }

            // 查询轮播图文章
            bannerArticles = articleMapper.findBannerArticles(limit);
            fileService.ensureArticleImageAreFullUrl(bannerArticles);

            // 更新缓存
            redisUtil.set(cacheKey, bannerArticles, 30, TimeUnit.MINUTES);

            return bannerArticles;
        } catch (Exception e) {
            log.error("获取轮播图文章失败, limit: {}", limit, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    /**
     * 获取用户文章列表
     */
    public PageResult<ArticleListVO> getUserArticles(Long userId, Integer pageNum, Integer pageSize) {
        try {
            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "article:user:",
                    userId + ":" + pageNum + ":" + pageSize
            );

            // 尝试从缓存获取
            PageResult<ArticleListVO> pageResult = redisUtil.getWithType(cacheKey, new TypeReference<>() {
            });
            if (pageResult != null) {
                return pageResult;
            }

            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;

            // 查询用户文章列表
            List<ArticleListVO> articleList = articleMapper.findUserArticles(userId, offset, pageSize);
            // 确保文章相关图片正常显示
            fileService.ensureArticleImageAreFullUrl(articleList);
            // 查询总记录数
            long total = articleMapper.countUserArticles(userId);

            // 构建分页结果
            pageResult = PageResult.build(
                    articleList,
                    total,
                    pageNum,
                    pageSize
            );

            // 更新缓存
            redisUtil.set(cacheKey, pageResult, 30, TimeUnit.MINUTES);

            return pageResult;
        } catch (Exception e) {
            log.error("获取用户文章列表失败, 用户ID: {}, 页码: {}, 每页大小: {}", userId, pageNum, pageSize, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    /**
     * 获取作者相关文章
     */
    public List<ArticleListVO> getUserRelatedArticles(Long userId, Long excludeArticleId, Integer limit) {
        try {
            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "article:user:related:",
                    userId + ":" + excludeArticleId + ":" + limit
            );

            // 尝试从缓存获取
            List<ArticleListVO> relatedArticles = redisUtil.getWithType(cacheKey, new TypeReference<>() {
            });
            if (relatedArticles != null) {
                return relatedArticles;
            }

            // 查询作者相关文章
            relatedArticles = articleMapper.findUserRelatedArticles(userId, excludeArticleId, limit);
            // 确保文章相关图片正常显示
            fileService.ensureArticleImageAreFullUrl(relatedArticles);

            // 更新缓存
            redisUtil.set(cacheKey, relatedArticles, 30, TimeUnit.MINUTES);

            return relatedArticles;
        } catch (Exception e) {
            log.error("获取作者相关文章失败, 用户ID: {}, 排除文章ID: {}, 限制数量: {}", userId, excludeArticleId, limit, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    /**
     * 获取当前用户文章列表
     */
    public PageResult<MyArticleListVO> getMyArticles(ArticleStatus articleStatus, String keyword, Integer page, Integer size) {
        try {
            // 从上下文获取用户信息
            User currentUser = UserContext.getCurrentUser();

            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "article:my",
                    currentUser.getId() + ":" + articleStatus.getCode() + ":" + (keyword != null ? keyword : "null") + ":" + page + ":" + size
            );

            // 尝试从缓存获取
            PageResult<MyArticleListVO> pageResult = redisUtil.getWithType(cacheKey, new TypeReference<>() {
            });
            if (pageResult != null) {
                return pageResult;
            }

            // 计算偏移量
            int offset = (page - 1) * size;

            // 查询当前用户文章列表
            List<MyArticleListVO> myArticleList = articleMapper.findMyArticles(currentUser.getId(), articleStatus, keyword, offset, size);
            // 查询总记录数
            long total = articleMapper.countMyArticles(currentUser.getId(), articleStatus, keyword);

            // 构建分页结果
            pageResult = PageResult.build(
                    myArticleList,
                    total,
                    page,
                    size
            );

            // 更新缓存
            redisUtil.set(cacheKey, pageResult, 30, TimeUnit.MINUTES);

            return pageResult;
        } catch (Exception e) {
            log.error("获取当前用户文章列表失败, 状态: {}, 关键词: {}, 页码: {}, 每页大小: {}", articleStatus.getDesc(), keyword, page, size, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    /**
     * 搜索文章
     */
    public PageResult<ArticleListVO> searchArticles(String keyword, String sortBy, Integer pageNum, Integer pageSize) {
        try {
            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "article:search",
                    keyword + ":" + sortBy + ":" + pageNum + ":" + pageSize
            );

            // 尝试从缓存获取
            PageResult<ArticleListVO> pageResult = redisUtil.getWithType(cacheKey, new TypeReference<>() {
            });
            if (pageResult != null) {
                return pageResult;
            }

            // 计算偏移量
            int offset = (pageNum - 1) * pageSize;

            // 查询搜索结果
            List<ArticleListVO> articleList = articleMapper.searchArticles(keyword, sortBy, offset, pageSize);
            // 确保文章相关图片正常显示
            fileService.ensureArticleImageAreFullUrl(articleList);
            // 查询总记录数
            long total = articleMapper.countSearchArticles(keyword);

            // 构建分页结果
            pageResult = PageResult.build(
                    articleList,
                    total,
                    pageNum,
                    pageSize
            );

            // 只缓存有结果的搜索
            if (total > 0) {
                // 更新缓存
                redisUtil.set(cacheKey, pageResult, 30, TimeUnit.MINUTES);
            }

            return pageResult;
        } catch (Exception e) {
            log.error("搜索文章失败, 关键词: {}, 排序方式: {}", keyword, sortBy, e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    /**
     * 管理员获取文章列表
     */
    public PageResult<Article> getArticlesByPage(AdminArticleQueryDTO queryDTO) {
        try {
            // 计算偏移量
            int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
            queryDTO.setOffset(offset);

            // 查询文章列表
            List<Article> articleList = articleMapper.findByPage(queryDTO);
            // 查询总记录数
            long total = articleMapper.countByPage(queryDTO);

            // 构建分页结果
            return PageResult.build(
                    articleList,
                    total,
                    queryDTO.getPageNum(),
                    queryDTO.getPageSize()
            );
        } catch (Exception e) {
            log.error("管理员获取文章列表失败, 页码: {}, 每页大小: {}", queryDTO.getPageNum(), queryDTO.getPageSize(), e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }

    /**
     * 管理员获取文章详情
     */
    public Article getArticleById(Long id) {
        try {
            Article article = articleMapper.findById(id);
            if (article == null) {
                log.warn("文章不存在, 文章ID: {}", id);
                throw new BusinessException(ResponseMessage.ARTICLE_NOT_FOUND);
            }
            return article;
        } catch (Exception e) {
            log.error("管理员获取文章详情失败, 文章ID: {}", id, e);
            throw new BusinessException(ResponseMessage.ARTICLE_NOT_FOUND, e.getMessage());
        }
    }

    /**
     * 管理员获取文章列表（VO）
     */
    public PageResult<AdminArticleVO> getAdminArticlesByPage(AdminArticleQueryDTO queryDTO) {
        try {
            // 计算偏移量
            int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
            queryDTO.setOffset(offset);

            // 查询文章列表
            List<AdminArticleVO> articleList = articleMapper.findAdminArticlesByPage(queryDTO);
            // 查询总记录数
            long total = articleMapper.countByPage(queryDTO);

            // 构建分页结果
            return PageResult.build(
                    articleList,
                    total,
                    queryDTO.getPageNum(),
                    queryDTO.getPageSize()
            );
        } catch (Exception e) {
            log.error("管理员获取文章列表失败, 页码: {}, 每页大小: {}", queryDTO.getPageNum(), queryDTO.getPageSize(), e);
            throw new BusinessException(ResponseMessage.ARTICLE_LIST_NOT_FOUND, e.getMessage());
        }
    }
}
