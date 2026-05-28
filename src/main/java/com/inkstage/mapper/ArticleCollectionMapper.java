package com.inkstage.mapper;

import com.inkstage.entity.model.ArticleCollection;
import com.inkstage.vo.front.CollectionArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
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
    int purgeByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 根据用户ID和文件夹ID查询收藏记录列表
     *
     * @param userId 用户ID
     * @param folderId 文件夹ID
     * @return 收藏记录列表
     */
    List<ArticleCollection> findByUserIdAndFolderId(@Param("userId") Long userId, @Param("folderId") Long folderId);

    // ==================== 统计（Count） ====================
    
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

    /**
     * 软删除用户所有收藏
     *
     * @param userId 用户ID
     */
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 恢复指定时间之后被软删除的用户收藏
     *
     * @param userId    用户ID
     * @param afterTime 时间节点，恢复此时间之后被删除的收藏
     */
    void restoreByUserIdAfterTime(@Param("userId") Long userId, @Param("afterTime") java.time.LocalDateTime afterTime);

    /**
     * 彻底删除用户所有收藏
     *
     * @param userId 用户ID
     */
    void purgeByUserId(@Param("userId") Long userId);

    /**
     * 查询指定用户被软删除的收藏记录对应的文章ID列表
     *
     * @param userId 用户ID
     * @return 被软删除的收藏记录对应的文章ID列表
     */
    List<Long> findDeletedArticleIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询指定用户在指定时间之后被软删除的收藏记录对应的文章ID列表
     *
     * @param userId    用户ID
     * @param afterTime 时间节点
     * @return 被软删除的收藏记录对应的文章ID列表
     */
    List<Long> findDeletedArticleIdsByUserIdAfterTime(@Param("userId") Long userId, @Param("afterTime") LocalDateTime afterTime);

}