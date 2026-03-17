package com.inkstage.controller.front;

import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.entity.model.Article;
import com.inkstage.service.impl.ArticleShareServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 文章分享控制器
 */
@Slf4j
@RestController
@RequestMapping("/front/article")
@RequiredArgsConstructor
public class ArticleShareController {

    private final ArticleShareServiceImpl articleShareService;

    /**
     * 生成文章分享链接
     *
     * @param articleId 文章ID
     * @return 分享链接
     */
    @PostMapping("/generate-share")
    public Result<String> generateShareLink(@RequestParam Long articleId) {
        log.info("生成文章分享链接，文章ID: {}", articleId);
        String shareLink = articleShareService.generateShareLink(articleId);
        return Result.success(shareLink, ResponseMessage.SUCCESS);
    }

    /**
     * 通过分享链接获取文章
     *
     * @param shareToken 分享令牌
     * @return 文章详情
     */
    @GetMapping("/share/{shareToken}")
    public Result<Article> getArticleByShareToken(@PathVariable String shareToken) {
        log.info("通过分享链接获取文章，令牌: {}", shareToken);

        Article article = articleShareService.getArticleByShareToken(shareToken);
        if (article == null) {
            return Result.error(ResponseMessage.ARTICLE_NOT_FOUND);
        }
        return Result.success(article, ResponseMessage.SUCCESS);
    }
}
