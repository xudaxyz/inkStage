package com.inkstage.controller.front;

import com.inkstage.common.PageResult;
import com.inkstage.common.Result;
import com.inkstage.dto.front.ArticleQueryDTO;
import com.inkstage.service.ArticleService;
import com.inkstage.service.UserService;
import com.inkstage.vo.front.ArticleListVO;
import com.inkstage.vo.front.HotUserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/index")
@RequiredArgsConstructor
public class IndexController {

    private final ArticleService articleService;
    private final UserService userService;

    /**
     * 获取文章列表
     *
     * @return 响应结果
     */
    @PostMapping("/articles")
    public Result<?> getIndexArticleList(@RequestBody ArticleQueryDTO queryDTO) {
        log.info("获取首页文章列表");
        PageResult<ArticleListVO> pageResult = articleService.getArticles(queryDTO);
        log.info("获取首页文章列表结果: {}", pageResult);
        return Result.success(pageResult);
    }


    /**
     * 获取轮播图文章
     *
     * @param limit 限制数量
     * @return 响应结果
     */
    @GetMapping("/banner")
    public Result<List<?>> getBannerArticles(@RequestParam(defaultValue = "3") Integer limit) {
        // 这里简化处理，实际应该调用服务层方法获取轮播图文章
        // 暂时返回空列表，后续需要实现具体逻辑
        return Result.success(List.of());
    }

    /**
     * 获取热门文章
     *
     * @param limit 限制数量
     * @param timeRange 时间范围：day, week, month
     * @return 响应结果
     */
    @GetMapping("/hot-articles")
    public Result<List<ArticleListVO>> getHotArticles(
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "week") String timeRange) {
        log.info("获取热门文章，limit: {}, timeRange: {}", limit, timeRange);
        List<ArticleListVO> hotArticles = articleService.getHotArticles(limit, timeRange);
        log.info("获取热门文章结果: {}", hotArticles);
        return Result.success(hotArticles);
    }

    /**
     * 获取最新文章
     *
     * @param limit 限制数量
     * @return 响应结果
     */
    @GetMapping("/latest-articles")
    public Result<List<ArticleListVO>> getLatestArticles(@RequestParam(defaultValue = "5") Integer limit) {
        log.info("获取最新文章，limit: {}", limit);
        List<ArticleListVO> latestArticles = articleService.getLatestArticles(limit);
        log.info("获取最新文章结果: {}", latestArticles);
        return Result.success(latestArticles);
    }

    /**
     * 获取热门用户
     *
     * @param limit 限制数量
     * @return 响应结果
     */
    @GetMapping("/hot-users")
    public Result<List<HotUserVO>> getHotUsers(@RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取热门用户, limit: {}", limit);
        List<HotUserVO> hotUsers = userService.getHotUsers(limit);
        log.info("获取热门用户结果: {}", hotUsers);
        return Result.success(hotUsers);
    }

}
