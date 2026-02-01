package com.inkstage.controller.front;

import com.inkstage.common.PageResult;
import com.inkstage.common.Result;
import com.inkstage.dto.front.ArticleQueryDTO;
import com.inkstage.service.ArticleService;
import com.inkstage.vo.front.ArticleListVO;
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
     * 获取最新文章
     *
     * @param limit 限制数量
     * @return 响应结果
     */
    @GetMapping("/latest-article")
    public Result<List<?>> getLatestArticles(@RequestParam(defaultValue = "5") Integer limit) {
        // 这里简化处理，实际应该调用服务层方法获取最新文章
        // 暂时返回空列表，后续需要实现具体逻辑
        return Result.success(List.of());
    }
}
