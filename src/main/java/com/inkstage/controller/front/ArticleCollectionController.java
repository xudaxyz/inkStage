package com.inkstage.controller.front;

import com.inkstage.annotation.UserAccess;
import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.constant.InkConstant;
import com.inkstage.dto.front.CollectArticleDTO;
import com.inkstage.dto.front.CollectionFolderDTO;
import com.inkstage.dto.front.MoveCollectionDTO;
import com.inkstage.entity.model.CollectionFolder;
import com.inkstage.service.ArticleCollectionService;
import com.inkstage.service.CollectionFolderService;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.CollectionArticleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @UserAccess
    public Result<Boolean> collectArticle(@RequestBody CollectArticleDTO collectArticleDTO) {
        log.info("收藏文章: {}", collectArticleDTO);
        boolean success = articleCollectionService.collectArticle(collectArticleDTO);
        return success ? Result.success(true, ResponseMessage.ARTICLE_COLLECT_SUCCESS) : Result.error(ResponseMessage.ARTICLE_COLLECT_FAILED);
    }

    /**
     * 取消收藏
     *
     * @param articleId 文章ID
     * @return 响应结果
     */
    @PostMapping("/un-collect/{articleId}")
    @UserAccess
    public Result<Boolean> unCollectArticle(@PathVariable Long articleId) {
        log.info("取消收藏文章ID: {}", articleId);
        boolean success = articleCollectionService.unCollectArticle(articleId);
        return success ? Result.success(true, ResponseMessage.ARTICLE_UN_COLLECT_SUCCESS) : Result.error(ResponseMessage.ARTICLE_UN_COLLECT_FAILED);
    }

    /**
     * 检查文章是否已收藏
     *
     * @param articleId 文章ID
     * @return 响应结果
     */
    @GetMapping("/collect/{articleId}/status")
    @UserAccess
    public Result<Boolean> checkArticleCollectStatus(@PathVariable Long articleId) {
        log.info("检查用户文章收藏状态, 文章ID: {}", articleId);
        boolean isCollected = articleCollectionService.isArticleCollected(articleId);
        return Result.success(isCollected, ResponseMessage.SUCCESS);
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
    @UserAccess
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
    @UserAccess
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
    @UserAccess
    public Result<Long> getTotalCollectionCount() {
        log.info("获取用户总收藏数");
        long totalCount = articleCollectionService.getTotalCollectionCount();
        return Result.success(totalCount);
    }

    /**
     * 移动收藏文章到其他文件夹
     *
     * @param moveCollectionDTO 移动收藏请求
     * @return 响应结果
     */
    @PutMapping("/collections/move")
    @UserAccess
    public Result<Boolean> moveCollectionArticle(@Valid @RequestBody MoveCollectionDTO moveCollectionDTO) {
        log.info("移动收藏文章到其他文件夹, 文章ID: {}, 目标文件夹ID: {}", moveCollectionDTO.getArticleId(), moveCollectionDTO.getTargetFolderId());
        boolean success = articleCollectionService.moveCollectionArticle(moveCollectionDTO);
        return success ? Result.success(true, ResponseMessage.COLLECTION_ARTICLE_MOVE_SUCCESS) : Result.error(ResponseMessage.ERROR);
    }


    /**
     * 创建收藏文件夹
     *
     * @param collectionFolderDTO 收藏文件夹信息
     * @return 响应结果
     */
    @PostMapping("/collections/folders")
    @UserAccess
    public Result<Long> createCollectionFolder(@Valid @RequestBody CollectionFolderDTO collectionFolderDTO) {
        log.info("创建收藏文件夹: {}", collectionFolderDTO.getName());
        Long folderId = collectionFolderService.createCollectionFolder(collectionFolderDTO);
        return Result.success(folderId, ResponseMessage.COLLECTION_FOLDER_CREATE_SUCCESS);
    }

    /**
     * 更新收藏文件夹
     *
     * @param folderId            文件夹ID
     * @param collectionFolderDTO 收藏文件夹DTO
     * @return 响应结果
     */
    @PutMapping("/collections/folders/{folderId}")
    @UserAccess
    public Result<Boolean> updateCollectionFolder(
            @PathVariable Long folderId,
            @Valid @RequestBody CollectionFolderDTO collectionFolderDTO) {
        log.info("更新收藏文件夹, 文件夹ID: {}, 名称: {}, 描述: {}, 状态: {}",
                folderId, collectionFolderDTO.getName(), collectionFolderDTO.getDescription(), collectionFolderDTO.getStatus());
        boolean success = collectionFolderService.updateCollectionFolder(folderId, collectionFolderDTO);
        return success ? Result.success(true, ResponseMessage.COLLECTION_FOLDER_UPDATE_SUCCESS) : Result.error(ResponseMessage.UPDATE_FAILED);
    }

    /**
     * 删除收藏文件夹
     *
     * @param folderId 文件夹ID
     * @param strategy 删除策略（MOVE_TO_DEFAULT: 移至默认收藏夹, DELETE_COLLECTIONS: 同时取消收藏）
     */
    @DeleteMapping("/collections/folders/{folderId}")
    @UserAccess
    public Result<Boolean> deleteCollectionFolder(
            @PathVariable Long folderId,
            @RequestParam(defaultValue = InkConstant.COLLECT_DELETE_STRATEGY_MOVE) String strategy) {
        log.info("删除收藏文件夹, 文件夹ID: {}, 策略: {}", folderId, strategy);
        boolean success = collectionFolderService.deleteCollectionFolder(folderId, strategy);
        return success ? Result.success(true, ResponseMessage.COLLECTION_FOLDER_DELETE_SUCCESS) : Result.error(ResponseMessage.ERROR);
    }

    /**
     * 批量更新收藏文件夹排序
     */
    @PutMapping("/collections/folders/sort")
    @UserAccess
    public Result<Boolean> batchUpdateFolderSort(@RequestBody List<Long> folderIds) {
        log.info("批量更新收藏文件夹排序, 文件夹数量: {}", folderIds.size());
        Long userId = UserContext.getCurrentUserId();
        boolean success = collectionFolderService.batchUpdateFolderSort(userId, folderIds);
        return success ? Result.success(true, "排序更新成功") : Result.error("排序更新失败");
    }

}
