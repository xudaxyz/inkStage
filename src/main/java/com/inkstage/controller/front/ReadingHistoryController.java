package com.inkstage.controller.front;

import com.inkstage.dto.front.ReadingHistoryDTO;
import com.inkstage.service.ReadingHistoryService;
import com.inkstage.vo.front.ReadingHistoryVO;
import com.inkstage.common.PageResult;
import com.inkstage.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 阅读历史Controller
 */
@RestController
@RequestMapping("/front/reading-history")
@RequiredArgsConstructor
@Slf4j
public class ReadingHistoryController {

    private final ReadingHistoryService readingHistoryService;

    /**
     * 保存或更新阅读历史
     *
     * @param dto 阅读历史DTO
     * @return 操作结果
     */
    @PostMapping("/save")
    public Result<?> saveOrUpdateReadingHistory(@RequestBody ReadingHistoryDTO dto) {
        try {
            boolean result = readingHistoryService.saveOrUpdateReadingHistory(dto);
            return result ? Result.success("保存阅读历史成功") : Result.error("保存阅读历史失败");
        } catch (Exception e) {
            log.error("保存阅读历史失败", e);
            return Result.error("保存阅读历史失败");
        }
    }

    /**
     * 获取阅读历史列表
     *
     * @param page 页码
     * @param size 每页大小
     * @return 阅读历史列表
     */
    @GetMapping("/get")
    public Result<PageResult<ReadingHistoryVO>> getReadingHistoryList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            PageResult<ReadingHistoryVO> result = readingHistoryService.getReadingHistoryList(page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取阅读历史列表失败", e);
            return Result.error("获取阅读历史列表失败");
        }
    }

    /**
     * 删除单条阅读历史
     *
     * @param articleId 文章ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{articleId}")
    public Result<?> deleteReadingHistory(@PathVariable Long articleId) {
        try {
            boolean result = readingHistoryService.deleteReadingHistory(articleId);
            return result ? Result.success("删除阅读历史成功") : Result.error("删除阅读历史失败");
        } catch (Exception e) {
            log.error("删除阅读历史失败", e);
            return Result.error("删除阅读历史失败");
        }
    }

    /**
     * 清空阅读历史
     *
     * @return 操作结果
     */
    @DeleteMapping("/delete/all")
    public Result<?> clearReadingHistory() {
        try {
            boolean result = readingHistoryService.clearReadingHistory();
            return result ? Result.success("清空阅读历史成功") : Result.error("清空阅读历史失败");
        } catch (Exception e) {
            log.error("清空阅读历史失败", e);
            return Result.error("清空阅读历史失败");
        }
    }

    /**
     * 获取单篇文章的阅读历史
     *
     * @param articleId 文章ID
     * @return 阅读历史
     */
    @GetMapping("/{articleId}")
    public Result<ReadingHistoryVO> getReadingHistoryByArticleId(@PathVariable Long articleId) {
        try {
            ReadingHistoryVO result = readingHistoryService.getReadingHistoryByArticleId(articleId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取阅读历史失败", e);
            return Result.error("获取阅读历史失败");
        }
    }

    /**
     * 批量获取阅读历史
     *
     * @param articleIds 文章ID列表
     * @return 阅读历史列表
     */
    @PostMapping("/batch")
    public Result<List<ReadingHistoryVO>> getReadingHistoriesByArticleIds(@RequestBody List<Long> articleIds) {
        try {
            List<ReadingHistoryVO> result = readingHistoryService.getReadingHistoriesByArticleIds(articleIds);
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量获取阅读历史失败", e);
            return Result.error("批量获取阅读历史失败");
        }
    }

}