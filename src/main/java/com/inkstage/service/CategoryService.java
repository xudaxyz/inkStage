package com.inkstage.service;

import com.inkstage.common.PageRequest;
import com.inkstage.common.PageResult;
import com.inkstage.entity.model.Category;
import com.inkstage.enums.StatusEnum;

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

    /**
     * 添加分类
     * @param category 分类对象
     * @return 添加后的分类对象
     */
    Category addCategory(Category category);

    /**
     * 更新分类
     * @param category 分类对象
     * @return 更新后的分类对象
     */
    Category updateCategory(Category category);

    /**
     * 删除分类
     * @param id 分类ID
     */
    void deleteCategory(Long id);

    /**
     * 更新分类状态
     * @param id 分类ID
     * @param status 状态
     * @return 更新后的分类对象
     */
    Category updateCategoryStatus(Long id, StatusEnum status);

    /**
     * 获取所有分类（分页）
     * @param keyword 关键字
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    PageResult<Category> getAdminCategories(String keyword, Integer pageNum, Integer pageSize);
}