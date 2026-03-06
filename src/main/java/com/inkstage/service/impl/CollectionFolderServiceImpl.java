package com.inkstage.service.impl;

import com.inkstage.common.ResponseMessage;
import com.inkstage.dto.front.CollectArticleDTO;
import com.inkstage.entity.model.CollectionFolder;
import com.inkstage.enums.DefaultStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.CollectionFolderMapper;
import com.inkstage.service.CollectionFolderService;
import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CollectionFolderServiceImpl implements CollectionFolderService {

    private final CollectionFolderMapper collectionFolderMapper;

    @Override
    public Long createCollectionFolder(CollectArticleDTO collectArticleDTO) {
        String folderName = collectArticleDTO.getFolderName();
        if (folderName == null || folderName.isEmpty()) {
            log.info("收藏夹名称为空");
            throw new BusinessException("收藏夹名称不能为空");
        }
        Long userId = UserContext.getCurrentUserId();
        log.info("用户: {} 创建收藏文件夹: {}", userId, folderName);

        CollectionFolder collectionFolder = collectionFolderMapper.selectByUserIdAndName(userId, folderName);
        if (collectionFolder != null) {
            throw new BusinessException(ResponseMessage.COLLECTION_FOLDER_EXIST, folderName);
        }
        collectionFolder = new CollectionFolder();
        collectionFolder.setUserId(userId);
        collectionFolder.setName(folderName);
        collectionFolder.setDescription(collectArticleDTO.getFolderDescription());
        collectionFolder.setArticleCount(0);
        collectionFolder.setSortOrder(0);
        collectionFolder.setDefaultFolder(collectArticleDTO.getDefaultFolder());
        collectionFolder.setCreateTime(LocalDateTime.now());
        collectionFolder.setUpdateTime(LocalDateTime.now());
        int result = collectionFolderMapper.insert(collectionFolder);
        if (result <= 0) {
            log.warn("用户: {} 创建收藏文件夹: {} 失败", userId, folderName);
            throw new BusinessException("创建收藏文件夹失败");
        }

        return collectionFolder.getId();
    }

    @Override
    public void createDefaultFolder(Long userId) {
        // 检查是否已存在默认收藏夹
        List<CollectionFolder> folders = collectionFolderMapper.selectByUserId(userId);
        boolean hasDefaultFolder = folders.stream().anyMatch(folder -> DefaultStatus.YES.equals(folder.getDefaultFolder()));
        if (hasDefaultFolder) {
            return;
        }
        // 创建默认收藏夹
        CollectionFolder collectionFolder = new CollectionFolder();
        collectionFolder.setUserId(userId);
        collectionFolder.setName("默认收藏夹");
        collectionFolder.setDescription("这是默认收藏夹");
        collectionFolder.setArticleCount(0);
        collectionFolder.setSortOrder(0);
        collectionFolder.setDefaultFolder(DefaultStatus.YES);
        collectionFolder.setCreateTime(LocalDateTime.now());
        collectionFolder.setUpdateTime(LocalDateTime.now());
        collectionFolderMapper.insert(collectionFolder);
    }

    @Override
    public List<CollectionFolder> getCollectionFoldersByUserId(Long userId) {
        log.info("获取用户收藏文件夹列表, 用户ID: {}", userId);
        List<CollectionFolder> folders = collectionFolderMapper.selectByUserId(userId);
        // 如果没有文件夹，创建默认收藏夹
        if (folders == null || folders.isEmpty()) {
            createDefaultFolder(userId);
            folders = collectionFolderMapper.selectByUserId(userId);
        }
        return folders;
    }

    @Override
    public boolean updateCollectionFolder(Long folderId, String name, String description) {
        log.info("更新收藏文件夹, 文件夹ID: {}, 名称: {}, 描述: {}", folderId, name, description);
        Long userId = UserContext.getCurrentUserId();

        // 检查文件夹是否存在且属于当前用户
        CollectionFolder folder = collectionFolderMapper.selectById(folderId);
        if (folder == null || !folder.getUserId().equals(userId)) {
            throw new BusinessException("收藏文件夹不存在或无权限");
        }

        // 检查名称是否重复
        if (name != null && !name.equals(folder.getName())) {
            CollectionFolder existingFolder = collectionFolderMapper.selectByUserIdAndName(userId, name);
            if (existingFolder != null) {
                throw new BusinessException(ResponseMessage.COLLECTION_FOLDER_EXIST, name);
            }
        }

        // 更新文件夹信息
        folder.setName(name != null ? name : folder.getName());
        folder.setDescription(description != null ? description : folder.getDescription());
        folder.setUpdateTime(LocalDateTime.now());

        int result = collectionFolderMapper.update(folder);
        return result > 0;
    }

    @Override
    public boolean deleteCollectionFolder(Long folderId) {
        log.info("删除收藏文件夹, 文件夹ID: {}", folderId);
        Long userId = UserContext.getCurrentUserId();

        // 检查文件夹是否存在且属于当前用户
        CollectionFolder folder = collectionFolderMapper.selectById(folderId);
        if (folder == null || !folder.getUserId().equals(userId)) {
            throw new BusinessException("收藏文件夹不存在或无权限");
        }

        // 检查是否是默认收藏夹
        if (DefaultStatus.YES.equals(folder.getDefaultFolder())) {
            throw new BusinessException("默认收藏夹不能删除");
        }

        // 检查文件夹是否为空
        if (folder.getArticleCount() > 0) {
            throw new BusinessException("收藏文件夹不为空，无法删除");
        }

        int result = collectionFolderMapper.deleteById(folderId);
        return result > 0;
    }

    @Override
    public CollectionFolder getDefaultFolder(Long userId) {
        log.info("获取用户默认收藏文件夹, 用户ID: {}", userId);
        List<CollectionFolder> folders = collectionFolderMapper.selectByUserId(userId);
        CollectionFolder defaultFolder = folders.stream()
                .filter(folder -> DefaultStatus.YES.equals(folder.getDefaultFolder()))
                .findFirst()
                .orElse(null);

        // 如果没有默认收藏夹，创建一个
        if (defaultFolder == null) {
            createDefaultFolder(userId);
            folders = collectionFolderMapper.selectByUserId(userId);
            defaultFolder = folders.stream()
                    .filter(folder -> DefaultStatus.YES.equals(folder.getDefaultFolder()))
                    .findFirst()
                    .orElse(null);
        }

        return defaultFolder;
    }

    @Override
    public CollectionFolder getCollectionFolderById(Long folderId) {
        log.info("获取收藏文件夹, 文件夹ID: {}", folderId);
        Long userId = UserContext.getCurrentUserId();
        CollectionFolder folder = collectionFolderMapper.selectById(folderId);
        if (folder == null || !folder.getUserId().equals(userId)) {
            throw new BusinessException("收藏文件夹不存在或无权限");
        }
        return folder;
    }

    @Override
    public CollectionFolder getCollectionFolderByName(String folderName) {
        log.info("获取收藏文件夹, 文件夹名称: {}", folderName);
        Long userId = UserContext.getCurrentUserId();
        CollectionFolder folder = collectionFolderMapper.selectByUserIdAndName(userId, folderName);
        if (folder == null) {
            throw new BusinessException("收藏文件夹不存在");
        }
        return folder;
    }
}
