package com.inkstage.service;

import com.inkstage.entity.model.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {

    /**
     * 获取所有分类
     * @return 分类列表
     */
    List<Category> getAllCategories();

    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类对象
     */
    Category getCategoryById(Long id);

    /**
     * 获取所有激活状态的分类
     * @return 激活状态的分类列表
     */
    List<Category> getActiveCategories();

}