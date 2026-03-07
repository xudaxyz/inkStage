package com.inkstage.controller.front;

import com.inkstage.common.PageRequest;
import com.inkstage.common.PageResult;
import com.inkstage.common.Result;
import com.inkstage.service.ArticleService;
import com.inkstage.vo.front.ArticleListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台搜索功能Controller
 */
@RestController
@RequestMapping("/front/search")
@RequiredArgsConstructor
public class SearchController {

    private final ArticleService articleService;

    /**
     * 搜索文章
     */
    @GetMapping("/articles")
    public Result<PageResult<ArticleListVO>> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "relevance") String sortBy) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(page);
        pageRequest.setPageSize(size);
        PageResult<ArticleListVO> result = articleService.searchArticles(keyword, sortBy, pageRequest);
        return Result.success(result);
    }

    /**
     * 获取热门搜索词
     */
    @GetMapping("/hot-words")
    public Result<?> getHotWords() {
        // 暂时返回空数据，后续可以从数据库查询
        return Result.success();
    }

    /**
     * 获取搜索历史
     */
    @GetMapping("/history")
    public Result<?> getSearchHistory() {
        // 暂时返回空数据，后续可以从数据库查询
        return Result.success();
    }

    /**
     * 删除搜索历史
     */
    @DeleteMapping("/history/delete/{id}")
    public Result<?> deleteSearchHistory(@PathVariable Long id) {
        // 暂时返回成功，后续可以从数据库删除
        return Result.success();
    }

    /**
     * 清空搜索历史
     */
    @DeleteMapping("/history/clear")
    public Result<?> clearSearchHistory() {
        // 暂时返回成功，后续可以从数据库清空
        return Result.success();
    }
}
