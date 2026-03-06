package com.inkstage.mapper;

import com.inkstage.entity.model.ReadingHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 阅读历史Mapper
 */
@Mapper
public interface ReadingHistoryMapper {

    /**
     * 保存或更新阅读历史
     * @param readingHistory 阅读历史
     * @return 影响行数
     */
    int saveOrUpdate(ReadingHistory readingHistory);

    /**
     * 根据用户ID和文章ID查询阅读历史
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 阅读历史
     */
    ReadingHistory findByUserIdAndArticleId(@Param("userId") Long userId, @Param("articleId") Long articleId);

    /**
     * 根据用户ID查询阅读历史列表
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数
     * @return 阅读历史列表
     */
    List<ReadingHistory> findByUserId(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 根据用户ID删除阅读历史
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 影响行数
     */
    int deleteByUserIdAndArticleId(@Param("userId") Long userId, @Param("articleId") Long articleId);

    /**
     * 根据用户ID清空阅读历史
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询阅读历史总数
     * @param userId 用户ID
     * @return 总数
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 批量查询用户的阅读历史
     * @param userId 用户ID
     * @param articleIds 文章ID列表
     * @return 阅读历史列表
     */
    List<ReadingHistory> findByUserIdAndArticleIds(@Param("userId") Long userId, @Param("articleIds") List<Long> articleIds);

    /**
     * 根据用户ID查询阅读历史列表（带文章和用户信息）
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数
     * @return 阅读历史VO列表
     */
    List<com.inkstage.vo.front.ReadingHistoryVO> findByUserIdWithDetails(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 根据用户ID和文章ID查询阅读历史（带文章和用户信息）
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 阅读历史VO
     */
    com.inkstage.vo.front.ReadingHistoryVO findByUserIdAndArticleIdWithDetails(@Param("userId") Long userId, @Param("articleId") Long articleId);

    /**
     * 批量查询用户的阅读历史（带文章和用户信息）
     * @param userId 用户ID
     * @param articleIds 文章ID列表
     * @return 阅读历史VO列表
     */
    List<com.inkstage.vo.front.ReadingHistoryVO> findByUserIdAndArticleIdsWithDetails(@Param("userId") Long userId, @Param("articleIds") List<Long> articleIds);
}