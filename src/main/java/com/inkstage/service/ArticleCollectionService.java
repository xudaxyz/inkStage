package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.dto.front.CollectArticleDTO;
import com.inkstage.entity.model.CollectionFolder;
import com.inkstage.vo.front.CollectionArticleVO;

import java.util.List;

/**
 * 文章收藏服务接口
 */
public interface ArticleCollectionService {

    /**
     * 收藏文章
     *
     * @param collectArticleDTO 收藏文章DTO
     * @return 是否收藏成功
     */
    boolean collectArticle(CollectArticleDTO collectArticleDTO);

    /**
     * 取消收藏
     *
     * @param articleId 文章ID
     * @return 是否取消成功
     */
    boolean unCollectArticle(Long articleId);

    /**
     * 检查用户是否已收藏文章
     *
     * @param articleId 文章ID
     * @return 是否已收藏
     */
    boolean isArticleCollected(Long articleId);

    /**
     * 获取用户收藏的文章列表
     *
     * @param folderId 文件夹ID，0表示默认文件夹
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @param keyword 搜索关键词
     * @return 收藏文章列表
     */
    PageResult<CollectionArticleVO> getCollectionArticles(Long folderId, Integer page, Integer size, String sortBy, String sortOrder, String keyword);

    /**
     * 获取用户的收藏文件夹列表
     *
     * @return 收藏文件夹列表
     */
    List<CollectionFolder> getCollectionFolders();

    /**
     * 获取用户的总收藏数
     *
     * @return 总收藏数
     */
    long getTotalCollectionCount();

    /**
     * 移动收藏文章到其他文件夹
     *
     * @param articleId 文章ID
     * @param targetFolderId 目标文件夹ID
     * @return 是否移动成功
     */
    boolean moveCollectionArticle(Long articleId, Long targetFolderId);

}