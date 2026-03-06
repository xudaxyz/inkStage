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

    /**
     * 插入收藏记录
     *
     * @param articleCollection 收藏实体
     * @return 影响行数
     */
    int insert(ArticleCollection articleCollection);

    /**
     * 根据文章ID和用户ID删除收藏记录
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 根据文章ID和用户ID查询收藏记录数
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 记录数
     */
    int countByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 根据文章ID和用户ID查询收藏记录
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 收藏记录
     */
    ArticleCollection selectByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

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
    List<CollectionArticleVO> selectCollectionArticles(
            @Param("userId") Long userId,
            @Param("folderId") Long folderId,
            @Param("keyword") String keyword,
            @Param("sortBy") String sortBy,
            @Param("sortOrder") String sortOrder,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );

    /**
     * 查询用户收藏的文章总数
     *
     * @param userId 用户ID
     * @param folderId 文件夹ID
     * @param keyword 搜索关键词
     * @return 总数
     */
    Long countCollectionArticles(
            @Param("userId") Long userId,
            @Param("folderId") Long folderId,
            @Param("keyword") String keyword
    );

    /**
     * 更新收藏记录
     *
     * @param articleCollection 收藏实体
     * @return 影响行数
     */
    int update(ArticleCollection articleCollection);

}