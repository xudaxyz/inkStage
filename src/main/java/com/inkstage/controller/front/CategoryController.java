package com.inkstage.controller.front;

import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.entity.model.Category;
import com.inkstage.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台分类Controller
 */
@RestController
@RequestMapping("/front/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取所有分类
     *
     * @return 响应结果
     */
    @GetMapping
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        if (categories != null && !categories.isEmpty()) {
            return Result.success(categories);
        } else {
            return Result.error(ResponseMessage.CATEGORIES_ARE_EMPTY);
        }
    }

    /**
     * 获取激活状态的分类
     *
     * @return 响应结果
     */
    @GetMapping("/active")
    public Result<List<Category>> getActiveCategories() {
        List<Category> categories = categoryService.getActiveCategories();
        if (categories != null && !categories.isEmpty()) {
            return Result.success(categories);
        } else {
            return Result.error(ResponseMessage.CATEGORIES_ARE_EMPTY);
        }
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
        if (category != null) {
            return Result.success(category);
        } else {
            return Result.error(ResponseMessage.CATEGORY_NOT_FOUND);
        }
    }

}
