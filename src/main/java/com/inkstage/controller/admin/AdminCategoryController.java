package com.inkstage.controller.admin;

import com.inkstage.common.Result;
import com.inkstage.entity.model.Category;
import com.inkstage.enums.StatusEnum;
import com.inkstage.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员分类Controller
 */
@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    /**
     * 获取所有分类
     *
     * @return 响应结果
     */
    @GetMapping
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 根据ID获取分类
     *
     * @param id 分类ID
     * @return 响应结果
     */
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return Result.success(category);
    }

    /**
     * 添加分类
     *
     * @param category 分类信息
     * @return 响应结果
     */
    @PostMapping
    public Result<Category> addCategory(@RequestBody Category category) {
        Category addedCategory = categoryService.addCategory(category);
        return Result.success(addedCategory);
    }

    /**
     * 更新分类
     *
     * @param id       分类ID
     * @param category 分类信息
     * @return 响应结果
     */
    @PutMapping("/{id}")
    public Result<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        Category updatedCategory = categoryService.updateCategory(category);
        return Result.success(updatedCategory);
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 响应结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    /**
     * 更新分类状态
     *
     * @param id     分类ID
     * @param status 状态
     * @return 响应结果
     */
    @PutMapping("/{id}/status")
    public Result<Category> updateCategoryStatus(@PathVariable Long id, @RequestParam StatusEnum status) {
        Category updatedCategory = categoryService.updateCategoryStatus(id, status);
        return Result.success(updatedCategory);
    }

}
