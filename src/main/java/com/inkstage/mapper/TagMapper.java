package com.inkstage.mapper;

import com.inkstage.entity.model.ArticleTag;
import com.inkstage.entity.model.Tag;
import com.inkstage.enums.StatusEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签Mapper接口
 */
@Mapper
public interface TagMapper {

    // ==================== 查询（Read） ====================
    
    /**
     * 获取所有标签
     *
     * @return 标签列表
     */
    List<Tag> findAll();

    /**
     * 根据ID获取标签
     *
     * @param id 标签ID
     * @return 标签对象
     */
    Tag findById(Long id);

    /**
     * 获取所有激活状态的标签
     *
     * @return 激活状态的标签列表
     */
    List<Tag> findActiveTags();

    /**
     * 根据文章ID获取关联的标签
     *
     * @param articleId 文章ID
     * @return 标签列表
     */
    List<Tag> findByArticleId(Long articleId);

    /**
     * 根据关键字分页获取标签
     *
     * @param keyword 关键字
     * @param offset 分页偏移量
     * @param pageSize 每页大小
     * @return 标签列表
     */
    List<Tag> findByKeyword(@Param("keyword") String keyword, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /**
     * 批量查询标签
     *
     * @param ids 标签ID列表
     * @return 标签列表
     */
    List<Tag> findByIds(@Param("ids") List<Long> ids);

    // ==================== 新增（Create） ====================
    
    /**
     * 插入标签
     *
     * @param tag 标签对象
     * @return 影响行数
     */
    int insert(Tag tag);

    /**
     * 插入文章与标签的关联
     *
     * @param articleTag 文章标签关联对象
     * @return 影响行数
     */
    int insertArticleTag(ArticleTag articleTag);

    /**
     * 批量插入文章标签关联
     *
     * @param articleTags 文章标签关联列表
     * @return 影响行数
     */
    int batchInsertArticleTags(@Param("articleTags") List<ArticleTag> articleTags);

    // ==================== 更新（Update） ====================
    
    /**
     * 更新标签
     *
     * @param tag 标签对象
     * @return 影响行数
     */
    int update(Tag tag);

    /**
     * 更新标签状态
     *
     * @param id     标签ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") StatusEnum status);

    // ==================== 删除（Delete） ====================
    
    /**
     * 根据ID删除标签
     *
     * @param id 标签ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 删除文章的所有标签关联
     *
     * @param articleId 文章ID
     * @return 影响行数
     */
    int deleteArticleTagsByArticleId(Long articleId);

    // ==================== 统计（Count） ====================
    
    /**
     * 根据关键字统计标签总数
     *
     * @param keyword 关键字
     * @return 总数
     */
    long countByKeyword(@Param("keyword") String keyword);



}