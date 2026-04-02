package com.inkstage.mapper;

import com.inkstage.entity.model.ArticleCollection;
import com.inkstage.vo.front.CollectionArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章收藏Mapper接口
 */
@Mapper
public interface ArticleCollectionMapper {

    // ==================== 查询（Read） ====================
    
    /**
     * 根据文章ID和用户ID查询收藏记录
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 收藏记录
     */
    ArticleCollection findByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 查询用户收藏的文章列表
     *
     * @param userId 用户ID
     * @param folderId 文件夹ID
     * @param keyword 搜索关键词
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 收藏文章列表
     */
    List<CollectionArticleVO> findCollectionArticles(
            @Param("userId") Long userId,
            @Param("folderId") Long folderId,
            @Param("keyword") String keyword,
            @Param("sortBy") String sortBy,
            @Param("sortOrder") String sortOrder,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );

    /**
     * 查询用户收藏的文章ID列表
     *
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 文章ID列表
     */
    List<Long> findArticleIdsByUserId(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 批量检查用户对文章的收藏状态
     *
     * @param userId 用户ID
     * @param articleIds 文章ID列表
     * @return 收藏记录列表
     */
    List<ArticleCollection> findByUserIdAndArticleIds(@Param("userId") Long userId, @Param("articleIds") List<Long> articleIds);

    // ==================== 新增（Create） ====================
    
    /**
     * 插入收藏记录
     *
     * @param articleCollection 收藏实体
     * @return 影响行数
     */
    int insert(ArticleCollection articleCollection);

    // ==================== 更新（Update） ====================
    
    /**
     * 更新收藏记录
     *
     * @param articleCollection 收藏实体
     * @return 影响行数
     */
    int update(ArticleCollection articleCollection);

    // ==================== 删除（Delete） ====================
    
    /**
     * 根据文章ID和用户ID删除收藏记录
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    // ==================== 统计（Count） ====================
    
    /**
     * 统计文章的收藏数
     *
     * @param articleId 文章ID
     * @return 收藏数
     */
    long countByArticleId(@Param("articleId") Long articleId);

    /**
     * 统计用户的收藏数
     *
     * @param userId 用户ID
     * @return 收藏数
     */
    long countByUserId(@Param("userId") Long userId);

    /**
     * 查询用户收藏的文章总数
     *
     * @param userId 用户ID
     * @param folderId 文件夹ID
     * @param keyword 搜索关键词
     * @return 总数
     */
    long countCollectionArticles(
            @Param("userId") Long userId,
            @Param("folderId") Long folderId,
            @Param("keyword") String keyword
    );

    /**
     * 统计所有收藏总数
     *
     * @return 收藏总数
     */
    long countAll();

}