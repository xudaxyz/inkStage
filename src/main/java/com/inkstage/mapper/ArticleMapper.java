package com.inkstage.mapper;

import com.inkstage.entity.model.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章Mapper接口
 */
@Mapper
public interface ArticleMapper {

    /**
     * 插入文章
     *
     * @param article 文章实体
     * @return 影响行数
     */
    int insert(Article article);

    /**
     * 更新文章
     *
     * @param article 文章实体
     * @return 影响行数
     */
    int update(Article article);

    /**
     * 根据ID删除文章
     *
     * @param id     文章ID
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 根据ID查询文章
     *
     * @param id 文章ID
     * @return 文章实体
     */
    Article selectById(Long id);

}
