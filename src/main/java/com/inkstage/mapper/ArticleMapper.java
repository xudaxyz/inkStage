package com.inkstage.mapper;

import com.inkstage.dto.front.ArticleQueryDTO;
import com.inkstage.entity.model.Article;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 根据ID查询文章详情VO对象
     *
     * @param id 文章ID
     * @return 文章详情VO对象
     */
    ArticleDetailVO selectDetailById(Long id);

    /**
     * 查询文章列表
     *
     * @param queryDTO 查询条件
     * @return 文章列表
     */
    List<ArticleListVO> selectArticleList(@Param("query") ArticleQueryDTO queryDTO);

    /**
     * 查询文章总数
     *
     * @param queryDTO 查询条件
     * @return 文章总数
     */
    long countArticleList(@Param("query") ArticleQueryDTO queryDTO);

    /**
     * 查询热门文章
     *
     * @param limit 限制数量
     * @return 热门文章列表
     */
    List<ArticleListVO> selectHotArticles(@Param("limit") Integer limit);

    /**
     * 查询最新文章
     *
     * @param limit 限制数量
     * @return 最新文章列表
     */
    List<ArticleListVO> selectLatestArticles(@Param("limit") Integer limit);

    /**
     * 查询轮播图文章
     *
     * @param limit 限制数量
     * @return 轮播图文章列表
     */
    List<ArticleListVO> selectBannerArticles(@Param("limit") Integer limit);

    /**
     * 查询指定用户的文章列表
     *
     * @param userId 用户ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 文章列表
     */
    List<ArticleListVO> selectUserArticles(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("size") Integer size);

    /**
     * 查询指定用户的文章总数
     *
     * @param userId 用户ID
     * @return 文章总数
     */
    long countUserArticles(@Param("userId") Long userId);

    /**
     * 查询作者相关文章（排除当前文章）
     *
     * @param userId 用户ID
     * @param excludeArticleId 排除的文章ID
     * @param limit 限制数量
     * @return 相关文章列表
     */
    List<ArticleListVO> selectAuthorRelatedArticles(@Param("userId") Long userId, @Param("excludeArticleId") Long excludeArticleId, @Param("limit") Integer limit);

}
