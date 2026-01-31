package com.inkstage.mapper;

import com.inkstage.entity.model.ArticleTag;
import com.inkstage.entity.model.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 标签Mapper接口
 */
@Mapper
public interface TagMapper {

    /**
     * 获取所有标签
     *
     * @return 标签列表
     */
    List<Tag> selectAll();

    /**
     * 根据ID获取标签
     *
     * @param id 标签ID
     * @return 标签对象
     */
    Tag selectById(Long id);

    /**
     * 获取所有激活状态的标签
     *
     * @return 激活状态的标签列表
     */
    List<Tag> selectActiveTags();

    /**
     * 根据文章ID获取关联的标签
     *
     * @param articleId 文章ID
     * @return 标签列表
     */
    List<Tag> selectByArticleId(Long articleId);

    /**
     * 插入文章与标签的关联
     *
     * @param articleTag 文章标签关联对象
     * @return 影响行数
     */
    int insertArticleTag(ArticleTag articleTag);

    /**
     * 删除文章的所有标签关联
     *
     * @param articleId 文章ID
     * @return 影响行数
     */
    int deleteArticleTagsByArticleId(Long articleId);

}