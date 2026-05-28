package com.inkstage.service;

import com.inkstage.dto.front.CollectionFolderDTO;
import com.inkstage.entity.model.CollectionFolder;

import java.util.List;

public interface CollectionFolderService {

    /**
     * 创建文章收藏夹
     * @param collectionFolderDTO 收藏夹信息
     * @return 创建的收藏夹id
     */
    Long createCollectionFolder(CollectionFolderDTO collectionFolderDTO);

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
     * @param collectionFolderDTO 文件夹信息
     * @return 是否更新成功
     */
    boolean updateCollectionFolder(Long folderId, CollectionFolderDTO collectionFolderDTO);

    /**
     * 删除收藏文件夹
     * @param folderId 文件夹id
     * @param strategy 删除策略（MOVE_TO_DEFAULT: 移至默认收藏夹, DELETE_COLLECTIONS: 同时取消收藏）
     * @return 是否删除成功
     */
    boolean deleteCollectionFolder(Long folderId, String strategy);

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

    /**
     * 批量更新收藏文件夹排序
     * @param userId 用户id
     * @param folderIds 按新顺序排列的文件夹ID列表
     * @return 是否更新成功
     */
    boolean batchUpdateFolderSort(Long userId, List<Long> folderIds);
}
