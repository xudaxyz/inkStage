package com.inkstage.mapper;

import com.inkstage.dto.admin.AdminArticleQueryDTO;
import com.inkstage.dto.front.ArticleQueryDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.vo.admin.AdminArticleVO;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;
import com.inkstage.vo.front.MyArticleListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章Mapper接口
 */
@Mapper
public interface ArticleMapper {

    // ==================== 查询（Read） ====================
    
    /**
     * 根据ID查询文章
     *
     * @param id 文章ID
     * @return 文章实体
     */
    Article findById(Long id);

    /**
     * 根据ID查询文章详情VO对象
     *
     * @param id 文章ID
     * @return 文章详情VO对象
     */
    ArticleDetailVO findDetailById(Long id);

    /**
     * 查询文章列表
     *
     * @param queryDTO 查询条件
     * @return 文章列表
     */
    List<ArticleListVO> findArticleList(@Param("query") ArticleQueryDTO queryDTO);

    /**
     * 查询热门文章
     *
     * @param limit 限制数量
     * @return 热门文章列表
     */
    List<ArticleListVO> findHotArticles(@Param("limit") Integer limit);

    /**
     * 查询最新文章
     *
     * @param limit 限制数量
     * @return 最新文章列表
     */
    List<ArticleListVO> findLatestArticles(@Param("limit") Integer limit);

    /**
     * 查询轮播图文章
     *
     * @param limit 限制数量
     * @return 轮播图文章列表
     */
    List<ArticleListVO> findBannerArticles(@Param("limit") Integer limit);

    /**
     * 查询指定用户的文章列表
     *
     * @param userId 用户ID
     * @param offset 偏移量
     * @param size   每页大小
     * @return 文章列表
     */
    List<ArticleListVO> findUserArticles(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("size") Integer size);

    /**
     * 查询作者相关文章（排除当前文章）
     *
     * @param userId           用户ID
     * @param excludeArticleId 排除的文章ID
     * @param limit            限制数量
     * @return 相关文章列表
     */
    List<ArticleListVO> findUserRelatedArticles(@Param("userId") Long userId, @Param("excludeArticleId") Long excludeArticleId, @Param("limit") Integer limit);

    /**
     * 查询当前用户文章列表
     *
     * @param userId        用户ID
     * @param articleStatus 文章状态
     * @param keyword       搜索关键词
     * @param offset        偏移量
     * @param limit         限制数量
     * @return 文章列表
     */
    List<MyArticleListVO> findMyArticles(@Param("userId") Long userId, @Param("articleStatus") ArticleStatus articleStatus, @Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 搜索文章
     *
     * @param keyword 搜索关键词
     * @param sortBy 排序方式
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 文章列表
     */
    List<ArticleListVO> searchArticles(@Param("keyword") String keyword, @Param("sortBy") String sortBy, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 分页查询所有文章（管理员）
     *
     * @param offset 偏移量
     * @param pageSize 每页大小
     * @return 文章列表
     */
    List<Article> findByPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /**
     * 分页查询所有文章（管理员）
     *
     * @param queryDTO 查询条件
     * @return 文章列表
     */
    List<Article> findByPage(@Param("query") AdminArticleQueryDTO queryDTO);

    /**
     * 分页查询所有文章（管理员）
     *
     * @param queryDTO 查询条件
     * @return 文章列表
     */
    List<AdminArticleVO> findAdminArticlesByPage(@Param("query") AdminArticleQueryDTO queryDTO);

    /**
     * 批量查询文章
     *
     * @param ids 文章ID列表
     * @return 文章列表
     */
    List<Article> findByIds(@Param("ids") List<Long> ids);

    /**
     * 根据分类查询文章
     *
     * @param categoryId 分类ID
     * @param limit 限制数量
     * @return 文章列表
     */
    List<ArticleListVO> findByCategory(@Param("categoryId") Long categoryId, @Param("limit") Integer limit);

    /**
     * 根据标签查询文章
     *
     * @param tagId 标签ID
     * @param limit 限制数量
     * @return 文章列表
     */
    List<ArticleListVO> findByTag(@Param("tagId") Long tagId, @Param("limit") Integer limit);

    /**
     * 根据文章ID获取阅读数
     *
     * @param id 文章ID
     * @return 阅读数
     */
    Long getReadCount(@Param("id") Long id);

    /**
     * 根据文章ID获取点赞数
     *
     * @param id 文章ID
     * @return 点赞数
     */
    Long getLikeCount(@Param("id") Long id);

    /**
     * 根据文章ID获取评论数
     *
     * @param id 文章ID
     * @return 评论数
     */
    Long getCommentCount(@Param("id") Long id);

    /**
     * 根据文章ID获取收藏数
     *
     * @param id 文章ID
     * @return 收藏数
     */
    Long getCollectionCount(@Param("id") Long id);

    /**
     * 根据文章ID获取分享数
     *
     * @param id 文章ID
     * @return 分享数
     */
    Long getShareCount(@Param("id") Long id);

    // ==================== 新增（Create） ====================
    
    /**
     * 插入文章
     *
     * @param article 文章实体
     * @return 影响行数
     */
    int insert(Article article);

    // ==================== 更新（Update） ====================
    
    /**
     * 更新文章
     *
     * @param article 文章实体
     * @return 影响行数
     */
    int update(Article article);

    /**
     * 更新文章状态
     *
     * @param id 文章ID
     * @param status 文章状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") ArticleStatus status);

    /**
     * 更新文章阅读数
     *
     * @param id 文章ID
     * @param increment 增加/减少的阅读数
     * @return 影响行数
     */
    int updateReadCount(@Param("id") Long id, @Param("increment") int increment);

    /**
     * 更新文章点赞数
     *
     * @param id 文章ID
     * @param offset 增加/减少的点赞数
     * @return 影响行数
     */
    int updateLikeCount(@Param("id") Long id, @Param("offset") int offset);

    /**
     * 更新文章评论数
     *
     * @param id 文章ID
     * @param offset 增加/减少的评论数
     * @return 影响行数
     */
    int updateCommentCount(@Param("id") Long id, @Param("offset") int offset);

    /**
     * 更新文章收藏数
     *
     * @param id 文章ID
     * @param offset 增加/减少的收藏数
     * @return 影响行数
     */
    int updateCollectionCount(@Param("id") Long id, @Param("offset") int offset);

    /**
     * 更新文章分享数
     *
     * @param id 文章ID
     * @param offset 增加/减少的分享数
     * @return 影响行数
     */
    int updateShareCount(@Param("id") Long id, @Param("offset") int offset);

    // ==================== 删除（Delete） ====================
    
    /**
     * 根据ID删除文章
     *
     * @param id     文章ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 根据文章ID永久删除文章
     *
     * @param id     文章ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int permanentDeleteById(@Param("id") Long id, @Param("userId") Long userId);

    // ==================== 统计（Count） ====================
    
    /**
     * 查询文章总数
     *
     * @param queryDTO 查询条件
     * @return 文章总数
     */
    long countArticleList(@Param("query") ArticleQueryDTO queryDTO);

    /**
     * 查询指定用户的文章总数
     *
     * @param userId 用户ID
     * @return 文章总数
     */
    long countUserArticles(@Param("userId") Long userId);

    /**
     * 统计当前用户文章数量
     *
     * @param userId        用户ID
     * @param articleStatus 文章状态
     * @param keyword       搜索关键词
     * @return 文章数量
     */
    long countMyArticles(@Param("userId") Long userId, @Param("articleStatus") ArticleStatus articleStatus, @Param("keyword") String keyword);

    /**
     * 统计搜索文章数量
     *
     * @param keyword 搜索关键词
     * @return 文章数量
     */
    long countSearchArticles(@Param("keyword") String keyword);

    /**
     * 统计所有文章总数（管理员）
     *
     * @return 文章总数
     */
    long countAll();

    /**
     * 统计所有文章总数（管理员）
     *
     * @param queryDTO 查询条件
     * @return 文章总数
     */
    long countByPage(@Param("query") AdminArticleQueryDTO queryDTO);



}
