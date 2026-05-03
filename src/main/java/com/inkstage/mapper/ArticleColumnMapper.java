package com.inkstage.mapper;

import com.inkstage.entity.model.ArticleColumn;
import com.inkstage.vo.front.ArticleListVO;
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
     * 根据专栏ID查询文章列表
     *
     * @param columnId 专栏ID
     * @return 专栏内的文章列表（按排序顺序排列）
     */
    List<ArticleListVO> findArticlesByColumnId(@Param("columnId") Long columnId);

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
     * 更新文章在专栏内的排序位置
     *
     * @param id        关联记录ID
     * @param sortOrder 新的排序位置
     * @return 影响的行数
     */
    int updateSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);

    /**
     * 软删除文章专栏关联记录
     *
     * @param id 关联记录ID
     * @return 影响的行数
     */
    int deleteById(Long id);

    /**
     * 根据文章ID软删除所有关联记录
     * 用于文章删除时清理关联关系
     *
     * @param articleId 文章ID
     * @return 影响的行数
     */
    int deleteByArticleId(@Param("articleId") Long articleId);

    /**
     * 根据专栏ID软删除所有关联记录
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
}
