package com.inkstage.controller.front;

import com.inkstage.common.PageResult;
import com.inkstage.common.Result;
import com.inkstage.dto.front.CollectArticleDTO;
import com.inkstage.entity.model.CollectionFolder;
import com.inkstage.service.ArticleCollectionService;
import com.inkstage.service.CollectionFolderService;
import com.inkstage.vo.front.CollectionArticleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台文章收藏相关Controller
 */
@Slf4j
@RestController
@RequestMapping("/front/article")
@RequiredArgsConstructor
public class ArticleCollectionController {

    private final ArticleCollectionService articleCollectionService;
    private final CollectionFolderService collectionFolderService;

    /**
     * 收藏文章
     *
     * @param collectArticleDTO 收藏文章DTO
     * @return 响应结果
     */
    @PostMapping("/collect")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> collectArticle(@RequestBody CollectArticleDTO collectArticleDTO) {
        log.info("收藏文章: {}", collectArticleDTO);
        boolean success = articleCollectionService.collectArticle(collectArticleDTO);
        return success ? Result.success(true, "收藏成功") : Result.error("收藏失败");
    }

    /**
     * 取消收藏
     *
     * @param articleId 文章ID
     * @return 响应结果
     */
    @PostMapping("/un-collect/{articleId}")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> unCollectArticle(@PathVariable Long articleId) {
        log.info("取消收藏文章ID: {}", articleId);
        boolean success = articleCollectionService.unCollectArticle(articleId);
        return success ? Result.success(true, "取消收藏成功") : Result.error("取消收藏失败");
    }

    /**
     * 检查文章是否已收藏
     *
     * @param articleId 文章ID
     * @return 响应结果
     */
    @GetMapping("/collect/{articleId}/status")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> checkArticleCollectStatus(@PathVariable Long articleId) {
        log.info("检查用户文章收藏状态, 文章ID: {}", articleId);
        boolean isCollected = articleCollectionService.isArticleCollected(articleId);
        return Result.success(isCollected, "获取收藏状态成功");
    }

    /**
     * 获取当前用户收藏的文章列表
     *
     * @param folderId  文件夹ID，0表示默认文件夹
     * @param keyword   搜索关键词
     * @param page      页码
     * @param size      每页大小
     * @param sortBy    排序字段
     * @param sortOrder 排序方向
     * @return 收藏文章列表分页结果
     */
    @GetMapping("/collections")
    @PreAuthorize("isAuthenticated()")
    public Result<PageResult<CollectionArticleVO>> getCollectionArticles(
            @RequestParam(required = false) Long folderId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "collectTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        log.info("获取用户收藏文章列表, 文件夹ID: {}, 关键词: {}, 页码: {}, 每页大小: {}, 排序字段: {}, 排序方向: {}",
                folderId, keyword, page, size, sortBy, sortOrder);
        PageResult<CollectionArticleVO> pageResult = articleCollectionService.getCollectionArticles(
                folderId, page, size, sortBy, sortOrder, keyword);
        return Result.success(pageResult);
    }

    /**
     * 获取用户的收藏文件夹列表
     *
     * @return 收藏文件夹列表
     */
    @GetMapping("/collections/folders")
    @PreAuthorize("isAuthenticated()")
    public Result<List<CollectionFolder>> getCollectionFolders() {
        log.info("获取用户收藏文件夹列表");
        List<CollectionFolder> folders = articleCollectionService.getCollectionFolders();
        return Result.success(folders);
    }

    /**
     * 获取用户的总收藏数
     *
     * @return 总收藏数
     */
    @GetMapping("/collections/total")
    @PreAuthorize("isAuthenticated()")
    public Result<Long> getTotalCollectionCount() {
        log.info("获取用户总收藏数");
        long totalCount = articleCollectionService.getTotalCollectionCount();
        return Result.success(totalCount);
    }

    /**
     * 移动收藏文章到其他文件夹
     *
     * @param collectArticleDTO 移动收藏请求
     * @return 响应结果
     */
    @PutMapping("/collections/move")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> moveCollectionArticle(@RequestBody CollectArticleDTO collectArticleDTO) {
        log.info("移动收藏文章到其他文件夹, 文章ID: {}, 目标文件夹ID: {}", collectArticleDTO.getArticleId(), collectArticleDTO.getFolderId());
        boolean success = articleCollectionService.moveCollectionArticle(collectArticleDTO.getArticleId(), collectArticleDTO.getFolderId());
        return success ? Result.success(true, "移动成功") : Result.error("移动失败");
    }


    /**
     * 创建收藏文件夹
     *
     * @param collectArticleDTO 收藏文件夹信息
     * @return 响应结果
     */
    @PostMapping("/collections/folders")
    @PreAuthorize("isAuthenticated()")
    public Result<Long> createCollectionFolder(@RequestBody CollectArticleDTO collectArticleDTO) {
        log.info("创建收藏文件夹: {}", collectArticleDTO.getFolderName());
        Long folderId = collectionFolderService.createCollectionFolder(collectArticleDTO);
        return Result.success(folderId, "创建收藏文件夹成功");
    }

    /**
     * 更新收藏文件夹
     *
     * @param folderId    文件夹ID
     * @param name        文件夹名称
     * @param description 文件夹描述
     * @return 响应结果
     */
    @PutMapping("/collections/folders/{folderId}")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> updateCollectionFolder(
            @PathVariable Long folderId,
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        log.info("更新收藏文件夹, 文件夹ID: {}, 名称: {}, 描述: {}", folderId, name, description);
        boolean success = collectionFolderService.updateCollectionFolder(folderId, name, description);
        return success ? Result.success(true, "更新收藏文件夹成功") : Result.error("更新收藏文件夹失败");
    }

    /**
     * 删除收藏文件夹
     *
     * @param folderId 文件夹ID
     * @return 响应结果
     */
    @DeleteMapping("/collections/folders/{folderId}")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> deleteCollectionFolder(@PathVariable Long folderId) {
        log.info("删除收藏文件夹, 文件夹ID: {}", folderId);
        boolean success = collectionFolderService.deleteCollectionFolder(folderId);
        return success ? Result.success(true, "删除收藏文件夹成功") : Result.error("删除收藏文件夹失败");
    }

}
