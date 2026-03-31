package com.inkstage.mapper;

import com.inkstage.entity.model.Category;
import com.inkstage.enums.common.StatusEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类Mapper接口
 */
@Mapper
public interface CategoryMapper {

    // ==================== 查询（Read） ====================
    
    /**
     * 获取所有分类
     * @return 分类列表
     */
    List<Category> findAll();

    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类对象
     */
    Category findById(Long id);

    /**
     * 根据分类ID查询分类版本号
     * @param id 分类ID
     * @return 分类版本号
     */
    Integer findCategoryVersionById(Long id);

    /**
     * 获取所有激活状态的分类
     * @return 激活状态的分类列表
     */
    List<Category> findActiveCategories();

    /**
     * 根据关键字分页获取分类
     * @param keyword 关键字
     * @param offset 页码
     * @param pageSize 每页大小
     * @return 分类列表
     */
    List<Category> findByKeyword(@Param("keyword") String keyword, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /**
     * 批量查询分类
     * @param ids 分类ID列表
     * @return 分类列表
     */
    List<Category> findByIds(@Param("ids") List<Long> ids);

    // ==================== 新增（Create） ====================
    
    /**
     * 添加分类
     * @param category 分类对象
     * @return 影响行数
     */
    int insert(Category category);

    // ==================== 更新（Update） ====================
    
    /**
     * 更新分类
     * @param category 分类对象
     * @return 影响行数
     */
    int update(Category category);

    /**
     * 更新分类状态
     * @param id 分类ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") StatusEnum status);

    // ==================== 删除（Delete） ====================
    
    /**
     * 删除分类
     * @param id 分类ID
     * @return 影响行数
     */
    int deleteById(Long id);

    // ==================== 统计（Count） ====================
    
    /**
     * 根据关键字统计分类数量
     * @param keyword 关键字
     * @return 数量
     */
    long countByKeyword(@Param("keyword") String keyword);

    /**
     * 更新分类文章数量
     * @param categoryId 分类ID
     * @param increment 增量值
     * @return 影响行数
     */
    int updateArticleCount(@Param("categoryId") Long categoryId, @Param("increment") int increment);

}