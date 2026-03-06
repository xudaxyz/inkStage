package com.inkstage.service;

import com.inkstage.dto.front.ReadingHistoryDTO;
import com.inkstage.vo.front.ReadingHistoryVO;
import com.inkstage.common.PageResult;

import java.util.List;

/**
 * 阅读历史服务接口
 */
public interface ReadingHistoryService {

    /**
     * 保存或更新阅读历史
     * @param dto 阅读历史DTO
     * @return 是否成功
     */
    boolean saveOrUpdateReadingHistory(ReadingHistoryDTO dto);

    /**
     * 获取用户的阅读历史列表
     * @param page 页码
     * @param size 每页大小
     * @return 阅读历史列表
     */
    PageResult<ReadingHistoryVO> getReadingHistoryList(Integer page, Integer size);

    /**
     * 删除单条阅读历史
     * @param articleId 文章ID
     * @return 是否成功
     */
    boolean deleteReadingHistory(Long articleId);

    /**
     * 清空阅读历史
     * @return 是否成功
     */
    boolean clearReadingHistory();

    /**
     * 获取单篇文章的阅读历史
     * @param articleId 文章ID
     * @return 阅读历史VO
     */
    ReadingHistoryVO getReadingHistoryByArticleId(Long articleId);

    /**
     * 批量获取阅读历史
     * @param articleIds 文章ID列表
     * @return 阅读历史列表
     */
    List<ReadingHistoryVO> getReadingHistoriesByArticleIds(List<Long> articleIds);

    /**
     * 获取用户的阅读历史列表（带详细信息）
     * @param page 页码
     * @param size 每页大小
     * @return 阅读历史列表
     */
    PageResult<ReadingHistoryVO> getReadingHistoryListWithDetails(Integer page, Integer size);

}