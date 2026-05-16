package com.inkstage.mapper;

import com.inkstage.entity.model.ArticleColumn;
import com.inkstage.vo.front.ArticleListVO;
import com.inkstage.vo.front.NeighborArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章专栏关联数据访问接口
 * 提供文章与专栏关联关系的数据库操作方法
 */
@Mapper
public interface ArticleColumnMapper {

    /**
     * 根据ID查询文章专栏关联记录
     *
     * @param id 关联记录ID
     * @return 文章专栏关联实体，如果不存在返回null
     */
    ArticleColumn findById(Long id);

    /**
     * 根据专栏ID分页查询文章列表
     *
     * @param columnId 专栏ID
     * @param offset   偏移量
     * @param pageSize 每页大小
     * @param sortBy   排序方式
     * @return 专栏内的文章列表（按排序顺序排列）
     */
    List<ArticleListVO> findArticlesByColumnId(@Param("columnId") Long columnId, @Param("offset") int offset, @Param("pageSize") int pageSize, @Param("sortBy") String sortBy);

    /**
     * 统计专栏内文章数量
     *
     * @param columnId 专栏ID
     * @return 专栏内文章总数
     */
    long countArticlesByColumnId(@Param("columnId") Long columnId);

    /**
     * 根据专栏ID和关键词搜索文章列表
     *
     * @param columnId 专栏ID
     * @param keyword  搜索关键词
     * @param offset   偏移量
     * @param pageSize 每页大小
     * @return 专栏内匹配的文章列表
     */
    List<ArticleListVO> searchArticlesByColumnId(@Param("columnId") Long columnId, @Param("keyword") String keyword, @Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 统计专栏内匹配关键词的文章数量
     *
     * @param columnId 专栏ID
     * @param keyword  搜索关键词
     * @return 匹配的文章数量
     */
    long countSearchArticlesByColumnId(@Param("columnId") Long columnId, @Param("keyword") String keyword);

    /**
     * 根据文章ID查询关联的专栏信息
     * 用于判断文章是否已加入其他专栏
     *
     * @param articleId 文章ID
     * @return 文章专栏关联实体，如果文章未加入任何专栏返回null
     */
    ArticleColumn findByArticleId(@Param("articleId") Long articleId);

    /**
     * 根据文章ID和专栏ID查询关联记录
     *
     * @param articleId 文章ID
     * @param columnId  专栏ID
     * @return 文章专栏关联实体，如果不存在返回null
     */
    ArticleColumn findByArticleAndColumn(@Param("articleId") Long articleId, @Param("columnId") Long columnId);

    /**
     * 插入文章专栏关联记录
     *
     * @param articleColumn 文章专栏关联实体
     * @return 影响的行数
     */
    int insert(ArticleColumn articleColumn);

    /**
     * 更新文章专栏关联信息
     *
     * @param articleColumn 文章专栏关联实体（包含要更新的字段）
     * @return 影响的行数
     */
    int update(ArticleColumn articleColumn);

    /**
     * 获取专栏内的最大排序值
     *
     * @param columnId 专栏ID
     * @return 最大排序值，如果专栏无文章返回0
     */
    Integer getMaxSortOrder(@Param("columnId") Long columnId);

    /**
     * 批量更新专栏文章排序
     * 使用 INNER JOIN + UNION ALL 方式一次性更新多条记录
     *
     * @param columnId 专栏ID
     * @param list     包含 articleId 和 sortOrder 的对象列表
     * @return 影响的行数
     */
    int batchUpdateSortOrder(@Param("columnId") Long columnId, @Param("list") List<ArticleColumn> list);

    /**
     * 删除文章专栏关联记录
     *
     * @param id 关联记录ID
     * @return 影响的行数
     */
    int deleteById(Long id);

    /**
     * 根据文章ID删除所有关联记录 - 用于文章删除时清理关联关系
     *
     * @param articleId 文章ID
     */
    void deleteByArticleId(@Param("articleId") Long articleId);

    /**
     * 根据专栏ID删除所有关联记录
     * 用于专栏删除时清理关联关系
     *
     * @param columnId 专栏ID
     */
    void deleteByColumnId(@Param("columnId") Long columnId);

    /**
     * 统计专栏内的文章数量
     *
     * @param columnId 专栏ID
     * @return 专栏内的文章数量
     */
    int countByColumnId(@Param("columnId") Long columnId);

    /**
     * 查找专栏内当前文章的上一篇文章
     * 按 sort_order ASC 排序，找到比当前文章排序靠前的文章
     *
     * @param columnId     专栏ID
     * @param articleId  文章ID
     * @param sortOrder  当前文章的排序值
     * @return 上一篇文章信息，如果不存在返回null
     */
    NeighborArticleVO findPrevArticleInColumn(@Param("columnId") Long columnId, @Param("articleId") Long articleId, @Param("sortOrder") Integer sortOrder);

    /**
     * 查找专栏内当前文章的下一篇文章
     * 按 sort_order ASC 排序，找到比当前文章排序靠后的文章
     *
     * @param columnId     专栏ID
     * @param articleId  文章ID
     * @param sortOrder  当前文章的排序值
     * @return 下一篇文章信息，如果不存在返回null
     */
    NeighborArticleVO findNextArticleInColumn(@Param("columnId") Long columnId, @Param("articleId") Long articleId, @Param("sortOrder") Integer sortOrder);
}
