package com.inkstage.mapper;

import com.inkstage.entity.model.Category;
import org.apache.ibatis.annotations.Mapper;

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

}