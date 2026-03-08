package com.inkstage.mapper;

import com.inkstage.entity.model.Category;
import com.inkstage.enums.StatusEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类Mapper接口
 */
@Mapper
public interface CategoryMapper {

    /**
     * 获取所有分类
     * @return 分类列表
     */
    List<Category> selectAll();

    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类对象
     */
    Category selectById(Long id);

    /**
     * 获取所有激活状态的分类
     * @return 激活状态的分类列表
     */
    List<Category> selectActiveCategories();

    /**
     * 添加分类
     * @param category 分类对象
     * @return 影响行数
     */
    int insert(Category category);

    /**
     * 更新分类
     * @param category 分类对象
     * @return 影响行数
     */
    int update(Category category);

    /**
     * 删除分类
     * @param id 分类ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 更新分类状态
     * @param id 分类ID
     * @param status 状态
     */
    void updateStatus(@Param("id") Long id, @Param("status") StatusEnum status);

}