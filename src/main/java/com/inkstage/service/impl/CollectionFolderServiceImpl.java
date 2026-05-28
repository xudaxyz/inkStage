package com.inkstage.service.impl;

import com.inkstage.common.ResponseMessage;
import com.inkstage.constant.InkConstant;
import com.inkstage.dto.front.CollectionFolderDTO;
import com.inkstage.entity.model.ArticleCollection;
import com.inkstage.entity.model.CollectionFolder;
import com.inkstage.enums.CollectionStatus;
import com.inkstage.enums.CountType;
import com.inkstage.enums.common.DefaultStatus;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleCollectionMapper;
import com.inkstage.mapper.CollectionFolderMapper;
import com.inkstage.service.CollectionFolderService;
import com.inkstage.utils.SnowflakeIdGenerator;
import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CollectionFolderServiceImpl implements CollectionFolderService {

    private final CollectionFolderMapper collectionFolderMapper;
    private final ArticleCollectionMapper articleCollectionMapper;
    private final CountProducer countProducer;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public Long createCollectionFolder(CollectionFolderDTO collectionFolderDTO) {
        String folderName = collectionFolderDTO.getName();
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
        collectionFolder.setDescription(collectionFolderDTO.getDescription());
        collectionFolder.setArticleCount(0);
        collectionFolder.setSortOrder(0);
        collectionFolder.setDefaultFolder(DefaultStatus.NO);
        collectionFolder.setStatus(collectionFolderDTO.getStatus() != null ? collectionFolderDTO.getStatus() : CollectionStatus.PUBLIC);
        collectionFolder.setCreateTime(LocalDateTime.now());
        collectionFolder.setUpdateTime(LocalDateTime.now());
        collectionFolder.setId(snowflakeIdGenerator.nextId());
        collectionFolder.setDeleted(DeleteStatus.NOT_DELETED);
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
        collectionFolder.setStatus(CollectionStatus.PUBLIC);
        collectionFolder.setCreateTime(LocalDateTime.now());
        collectionFolder.setUpdateTime(LocalDateTime.now());
        collectionFolder.setId(snowflakeIdGenerator.nextId());
        collectionFolderMapper.insert(collectionFolder);
    }

    @Override
    public List<CollectionFolder> getCollectionFoldersByUserId(Long userId) {
        log.info("获取用户收藏文件夹列表, 用户ID: {}", userId);
        List<CollectionFolder> folders = collectionFolderMapper.selectByUserId(userId);
        if (folders == null || folders.isEmpty()) {
            createDefaultFolder(userId);
            folders = collectionFolderMapper.selectByUserId(userId);
        }
        return folders;
    }

    @Override
    public boolean updateCollectionFolder(Long folderId, CollectionFolderDTO collectionFolderDTO) {
        log.info("更新收藏文件夹, 文件夹ID: {}, 名称: {}, 描述: {}, 状态: {}",
                folderId, collectionFolderDTO.getName(), collectionFolderDTO.getDescription(), collectionFolderDTO.getStatus());
        Long userId = UserContext.getCurrentUserId();

        CollectionFolder folder = collectionFolderMapper.selectById(folderId);
        if (folder == null || !folder.getUserId().equals(userId)) {
            throw new BusinessException("收藏文件夹不存在或无权限");
        }

        String name = collectionFolderDTO.getName();
        if (name != null && !name.equals(folder.getName())) {
            CollectionFolder existingFolder = collectionFolderMapper.selectByUserIdAndName(userId, name);
            if (existingFolder != null) {
                throw new BusinessException(ResponseMessage.COLLECTION_FOLDER_EXIST, name);
            }
        }

        folder.setName(name != null ? name : folder.getName());
        folder.setDescription(collectionFolderDTO.getDescription() != null ? collectionFolderDTO.getDescription() : folder.getDescription());
        folder.setStatus(collectionFolderDTO.getStatus() != null ? collectionFolderDTO.getStatus() : folder.getStatus());
        folder.setUpdateTime(LocalDateTime.now());

        int result = collectionFolderMapper.update(folder);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean deleteCollectionFolder(Long folderId, String strategy) {
        log.info("删除收藏文件夹, 文件夹ID: {}, 策略: {}", folderId, strategy);
        Long userId = UserContext.getCurrentUserId();

        CollectionFolder folder = collectionFolderMapper.selectById(folderId);
        if (folder == null || !folder.getUserId().equals(userId)) {
            throw new BusinessException("收藏文件夹不存在或无权限");
        }

        if (DefaultStatus.YES.equals(folder.getDefaultFolder())) {
            throw new BusinessException("默认收藏夹不能删除");
        }

        // 处理非空文件夹
        if (folder.getArticleCount() != null && folder.getArticleCount() > 0) {
            CollectionFolder defaultFolder = getDefaultFolder(userId);
            List<ArticleCollection> collections = articleCollectionMapper.findByUserIdAndFolderId(userId, folderId);
            if (InkConstant.COLLECT_DELETE_STRATEGY_DELETE.equals(strategy)) {
                // 策略：同时取消该文件夹下所有收藏
                for (ArticleCollection collection : collections) {
                    articleCollectionMapper.deleteByArticleIdAndUserId(collection.getArticleId(), userId);
                    // 减少文章收藏数
                    countProducer.sendCountMessage(CountType.ARTICLE_COLLECTION, collection.getArticleId(), -1);
                }
                // 更新默认收藏夹文章数量(无需增加，因为文章已被取消收藏)
                log.info("删除收藏夹 {} 下的 {} 条收藏记录", folderId, collections.size());
            } else {
                // 默认策略：移至默认收藏夹
                for (ArticleCollection collection : collections) {
                    collection.setFolderId(defaultFolder.getId());
                    collection.setUpdateTime(LocalDateTime.now());
                    articleCollectionMapper.update(collection);
                }
                // 增加默认收藏夹文章数量
                if (!collections.isEmpty()) {
                    countProducer.sendCountMessage(CountType.FOLDER_ARTICLE, defaultFolder.getId(), collections.size());
                    log.info("将 {} 篇文章移至默认收藏夹", collections.size());
                }
            }
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

    @Override
    @Transactional
    public boolean batchUpdateFolderSort(Long userId, List<Long> folderIds) {
        log.info("批量更新收藏文件夹排序: userId={}, folderCount={}", userId, folderIds.size());
        try {
            List<CollectionFolder> list = new ArrayList<>();
            for (int i = 0; i < folderIds.size(); i++) {
                CollectionFolder folder = new CollectionFolder();
                folder.setId(folderIds.get(i));
                folder.setSortOrder((i + 1) * 100);
                list.add(folder);
            }
            int updatedCount = collectionFolderMapper.batchUpdateSortOrder(userId, list);
            log.info("批量更新收藏文件夹排序完成: 更新了{}条记录", updatedCount);
            return updatedCount > 0;
        } catch (Exception e) {
            log.error("批量更新收藏文件夹排序失败", e);
            throw new BusinessException("批量更新收藏文件夹排序失败");
        }
    }
}
