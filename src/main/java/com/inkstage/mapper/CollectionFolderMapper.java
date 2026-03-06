package com.inkstage.mapper;

import com.inkstage.entity.model.CollectionFolder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收藏文件夹Mapper
 */
@Mapper
public interface CollectionFolderMapper {

    /**
     * 根据ID查询收藏文件夹
     *
     * @param id 文件夹ID
     * @return 收藏文件夹
     */
    CollectionFolder selectById(Long id);

    /**
     * 根据用户ID和文件夹名称查询收藏文件夹
     *
     * @param userId 用户ID
     * @param name   文件夹名称
     * @return 收藏文件夹
     */
    CollectionFolder selectByUserIdAndName(@Param("userId") Long userId, @Param("name") String name);

    /**
     * 插入收藏文件夹
     *
     * @param folder 收藏文件夹
     * @return 影响行数
     */
    int insert(CollectionFolder folder);

    /**
     * 更新收藏文件夹
     *
     * @param folder 收藏文件夹
     * @return 影响行数
     */
    int update(CollectionFolder folder);

    /**
     * 更新文件夹文章数量
     *
     * @param folderId 文件夹ID
     * @param count    变更数量
     * @return 影响行数
     */
    int updateArticleCount(@Param("folderId") Long folderId, @Param("count") Integer count);

    /**
     * 删除收藏文件夹
     *
     * @param id 文件夹ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据用户ID查询收藏文件夹列表
     *
     * @param userId 用户ID
     * @return 收藏文件夹列表
     */
    List<CollectionFolder> selectByUserId(Long userId);

    /**
     * 根据用户ID和文件夹ID查询收藏文件夹
     * @param userId 用户ID
     * @param folderId 文件夹ID
     * @return 收藏文件夹
     */
    CollectionFolder selectByUserIdAndFolderId(@Param("userId") Long userId, @Param("folderId") Long folderId);

}
