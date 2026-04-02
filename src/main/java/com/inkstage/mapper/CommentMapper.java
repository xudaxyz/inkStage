package com.inkstage.mapper;

import com.inkstage.dto.admin.AdminCommentQueryDTO;
import com.inkstage.dto.front.CommentQueryDTO;
import com.inkstage.entity.model.Comment;
import com.inkstage.vo.front.ArticleCommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论Mapper接口
 */
@Mapper
public interface CommentMapper {

    // ==================== 查询（Read） ====================
    
    /**
     * 根据ID查询评论
     *
     * @param id 评论ID
     * @return 评论实体
     */
    Comment findById(Long id);

    /**
     * 根据文章ID查询评论列表
     *
     * @param queryDTO 查询条件
     * @return 评论列表
     */
    List<ArticleCommentVO> findCommentsByArticleId(@Param("query") CommentQueryDTO queryDTO);

    /**
     * 管理员分页查询评论列表
     *
     * @param query 分页查询条件
     * @return 评论列表
     */
    List<ArticleCommentVO> findCommentsByPage(@Param("query") AdminCommentQueryDTO query);

    /**
     * 根据用户ID查询评论
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 评论列表
     */
    List<ArticleCommentVO> findByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 批量查询评论
     *
     * @param ids 评论ID列表
     * @return 评论列表
     */
    List<Comment> findByIds(@Param("ids") List<Long> ids);

    /**
     * 查询文章的最大楼层号
     *
     * @param articleId 文章ID
     * @return 最大楼层号
     */
    Integer findMaxFloorByArticleId(Long articleId);

    // ==================== 新增（Create） ====================
    
    /**
     * 插入评论
     *
     * @param comment 评论实体
     * @return 影响行数
     */
    int insert(Comment comment);

    // ==================== 更新（Update） ====================
    
    /**
     * 根据ID更新评论
     *
     * @param comment 评论实体
     * @return 影响行数
     */
    int updateById(Comment comment);

    /**
     * 更新评论状态
     *
     * @param id 评论ID
     * @param status 状态
     * @param reviewUserId 审核用户ID
     * @param reviewReason 审核原因
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, 
                          @Param("reviewUserId") Long reviewUserId, @Param("reviewReason") String reviewReason);

    /**
     * 更新评论置顶状态
     *
     * @param id 评论ID
     * @param top 置顶状态
     * @param topOrder 置顶顺序
     * @return 影响行数
     */
    int updateTop(@Param("id") Long id, @Param("top") Integer top, @Param("topOrder") Integer topOrder);

    /**
     * 更新评论的回复数
     *
     * @param id 评论ID
     * @param replyCount 回复数
     * @return 影响行数
     */
    int updateReplyCount(@Param("id") Long id, @Param("replyCount") Integer replyCount);

    // ==================== 删除（Delete） ====================
    
    /**
     * 根据ID删除评论
     *
     * @param id 评论ID
     * @return 影响行数
     */
    int deleteById(Long id);

    // ==================== 统计（Count） ====================
    
    /**
     * 根据文章ID查询评论总数
     *
     * @param queryDTO 查询条件
     * @return 评论总数
     */
    long countCommentsByArticleId(@Param("query") CommentQueryDTO queryDTO);

    /**
     * 管理员查询评论总数
     *
     * @param query 分页查询条件
     * @return 评论总数
     */
    long countCommentsByPage(@Param("query") AdminCommentQueryDTO query);

    /**
     * 统计用户评论数量
     *
     * @param userId 用户ID
     * @return 评论数量
     */
    long countByUserId(@Param("userId") Long userId);

    /**
     * 查询文章的置顶评论
     *
     * @param articleId 文章ID
     * @return 置顶评论
     */
    Comment findTopCommentByArticleId(@Param("articleId") Long articleId);

    /**
     * 根据父评论ID查询子评论列表
     *
     * @param parentId 父评论ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @param sortBy 排序方式
     * @return 子评论列表
     */
    List<ArticleCommentVO> findRepliesByParentId(@Param("parentId") Long parentId, 
                                               @Param("offset") int offset, 
                                               @Param("limit") int limit, 
                                               @Param("sortBy") String sortBy);

    /**
     * 根据父评论ID统计子评论总数
     *
     * @param parentId 父评论ID
     * @return 子评论总数
     */
    long countRepliesByParentId(@Param("parentId") Long parentId);

    /**
     * 查询文章的最新评论版本号
     *
     * @param articleId 文章ID
     * @return 最新评论版本号
     */
    Integer findMaxCommentVersionByArticleId(@Param("articleId") Long articleId);

    /**
     * 统计所有评论总数
     *
     * @return 评论总数
     */
    long countAll();

    /**
     * 统计待审核评论数量
     *
     * @return 待审核评论数量
     */
    long countPendingReviews();

}
