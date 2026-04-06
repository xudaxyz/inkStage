package com.inkstage.mapper;

import com.inkstage.entity.model.Tag;
import com.inkstage.enums.common.StatusEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签Mapper接口
 */
@Mapper
public interface TagMapper {

    // ==================== 查询（Read） ====================

    /**
     * 获取所有标签
     *
     * @return 标签列表
     */
    List<Tag> findAll();

    /**
     * 根据ID获取标签
     *
     * @param id 标签ID
     * @return 标签对象
     */
    Tag findById(Long id);

    /**
     * 根据标签ID查询标签版本号
     *
     * @param id 标签ID
     * @return 标签版本号
     */
    Integer findTagVersionById(Long id);

    /**
     * 获取所有激活状态的标签
     *
     * @return 激活状态的标签列表
     */
    List<Tag> findActiveTags();

    /**
     * 根据文章ID获取关联的标签
     *
     * @param articleId 文章ID
     * @return 标签列表
     */
    List<Tag> findByArticleId(Long articleId);

    /**
     * 根据关键字分页获取标签
     *
     * @param keyword  关键字
     * @param offset   分页偏移量
     * @param pageSize 每页大小
     * @return 标签列表
     */
    List<Tag> findByKeyword(@Param("keyword") String keyword, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /**
     * 批量查询标签
     *
     * @param ids 标签ID列表
     * @return 标签列表
     */
    List<Tag> findByIds(@Param("ids") List<Long> ids);

    /**
     * 根据用户ID查询标签
     *
     * @param userId 用户ID
     * @return 标签列表
     */
    List<Tag> findByUserId(@Param("userId") Long userId);

    /**
     * 根据名称查询标签
     *
     * @param name 标签名称
     * @return 标签对象
     */
    Tag findByName(@Param("name") String name);

    /**
     * 根据slug查询标签
     *
     * @param slug 标签slug
     * @return 标签对象
     */
    Tag findBySlug(@Param("slug") String slug);

    // ==================== 新增（Create） ====================

    /**
     * 插入标签
     *
     * @param tag 标签对象
     * @return 影响行数
     */
    int insert(Tag tag);


    // ==================== 更新（Update） ====================

    /**
     * 更新标签
     *
     * @param tag 标签对象
     * @return 影响行数
     */
    int update(Tag tag);

    /**
     * 更新标签状态
     *
     * @param id     标签ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") StatusEnum status);

    // ==================== 删除（Delete） ====================

    /**
     * 根据ID删除标签
     *
     * @param id 标签ID
     * @return 影响行数
     */
    int deleteById(Long id);


    // ==================== 统计（Count） ====================

    /**
     * 根据关键字统计标签总数
     *
     * @param keyword 关键字
     * @return 总数
     */
    long countByKeyword(@Param("keyword") String keyword);

    /**
     * 根据标签ID获取使用该标签的所有用户ID
     *
     * @param tagId 标签ID
     * @return 用户ID列表
     */
    List<Long> findUserIdsByTagId(@Param("tagId") Long tagId);

    /**
     * 统计所有标签总数
     *
     * @return 标签总数
     */
    long countAll();

    /**
     * 统计待审核标签数量
     *
     * @return 待审核标签数量
     */
    long countPendingReviews();

    /**
     * 更新标签统计数据
     *
     * @param tagId              标签ID
     * @param articleCountChange 文章数量变化值
     * @param usageCountChange   使用次数变化值
     * @return 影响行数
     */
    int updateTagStats(@Param("tagId") Long tagId,
                       @Param("articleCountChange") int articleCountChange,
                       @Param("usageCountChange") int usageCountChange);

}