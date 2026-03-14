package com.inkstage.controller.front;

import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.entity.model.Category;
import com.inkstage.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台分类Controller
 */
@Slf4j
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
    @GetMapping("/list")
    public Result<List<Category>> getAllCategories() {
        log.info("获取所有分类");
        List<Category> categories = categoryService.getAllCategories();
        if (categories != null && !categories.isEmpty()) {
            return Result.success(categories, "获取分类列表成功");
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
        log.info("获取激活状态的分类");
        List<Category> categories = categoryService.getActiveCategories();
        if (categories != null && !categories.isEmpty()) {
            return Result.success(categories, "获取激活分类成功");
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
    @GetMapping("/detail/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        log.info("根据ID获取分类, 分类ID: {}", id);
        Category category = categoryService.getCategoryById(id);
        if (category != null) {
            return Result.success(category, "获取分类详情成功");
        } else {
            return Result.error(ResponseMessage.CATEGORY_NOT_FOUND);
        }
    }

}
