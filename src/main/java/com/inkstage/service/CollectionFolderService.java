package com.inkstage.service;

import com.inkstage.dto.front.CollectArticleDTO;
import com.inkstage.entity.model.CollectionFolder;

import java.util.List;

public interface CollectionFolderService {

    /**
     * 创建文章收藏夹
     * @param collectArticleDTO 收藏夹信息
     * @return 创建的收藏夹id
     */
    Long createCollectionFolder(CollectArticleDTO collectArticleDTO);

    /**
     * 创建默认收藏夹
     * @param userId 用户id
     */
    void createDefaultFolder(Long userId);

    /**
     * 获取用户的收藏文件夹列表
     * @param userId 用户id
     * @return 收藏文件夹列表
     */
    List<CollectionFolder> getCollectionFoldersByUserId(Long userId);

    /**
     * 更新收藏文件夹
     * @param folderId 文件夹id
     * @param name 文件夹名称
     * @param description 文件夹描述
     * @return 是否更新成功
     */
    boolean updateCollectionFolder(Long folderId, String name, String description);

    /**
     * 删除收藏文件夹
     * @param folderId 文件夹id
     * @return 是否删除成功
     */
    boolean deleteCollectionFolder(Long folderId);

    /**
     * 获取默认收藏文件夹
     * @param userId 用户id
     * @return 默认收藏文件夹
     */
    CollectionFolder getDefaultFolder(Long userId);

    /**
     * 获取收藏文件夹
     * @param folderId 文件夹id
     * @return 收藏文件夹
     */
    CollectionFolder getCollectionFolderById(Long folderId);

    /**
     * 获取收藏文件夹
     * @param folderName 文件夹名称
     * @return 收藏文件夹
     */
    CollectionFolder getCollectionFolderByName(String folderName);
}
