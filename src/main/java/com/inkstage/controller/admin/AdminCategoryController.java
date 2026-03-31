package com.inkstage.controller.admin;

import com.inkstage.annotation.AdminAccess;
import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.entity.model.Category;
import com.inkstage.enums.common.StatusEnum;
import com.inkstage.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员分类Controller
 */
@Slf4j
@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    /**
     * 获取所有分类（分页）
     *
     * @param keyword 关键字
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 响应结果
     */
    @GetMapping("/list")
    @AdminAccess
    public Result<PageResult<Category>> listCategories(@RequestParam(required = false) String keyword, 
                                                     @RequestParam(defaultValue = "1") Integer pageNum, 
                                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("管理员获取分类列表, 关键字: {}, 页码: {}, 页大小: {}", keyword, pageNum, pageSize);
        PageResult<Category> pageResult = categoryService.getAdminCategories(keyword, pageNum, pageSize);
        return Result.success(pageResult, ResponseMessage.CATEGORY_LIST_SUCCESS);
    }

    /**
     * 根据ID获取分类
     *
     * @param id 分类ID
     * @return 响应结果
     */
    @GetMapping("/detail/{id}")
    @AdminAccess
    public Result<Category> getCategoryDetail(@PathVariable Long id) {
        log.info("管理员获取分类详情, 分类ID: {}", id);
        Category category = categoryService.getCategoryById(id);
        return Result.success(category, ResponseMessage.CATEGORY_DETAIL_SUCCESS);
    }

    /**
     * 添加分类
     *
     * @param category 分类信息
     * @return 响应结果
     */
    @PostMapping("/add")
    @AdminAccess
    public Result<Category> addCategory(@RequestBody Category category) {
        log.info("管理员添加分类, 分类信息: {}", category);
        Category addedCategory = categoryService.addCategory(category);
        return Result.success(addedCategory, ResponseMessage.CATEGORY_CREATE_SUCCESS);
    }

    /**
     * 更新分类
     *
     * @param id 分类ID
     * @param category 分类信息
     * @return 响应结果
     */
    @PutMapping("/update/{id}")
    @AdminAccess
    public Result<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        log.info("管理员更新分类, 分类ID: {}, 分类信息: {}", id, category);
        category.setId(id);
        Category updatedCategory = categoryService.updateCategory(category);
        return Result.success(updatedCategory, ResponseMessage.CATEGORY_UPDATE_SUCCESS);
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 响应结果
     */
    @DeleteMapping("/delete/{id}")
    @AdminAccess
    public Result<Void> deleteCategory(@PathVariable Long id) {
        log.info("管理员删除分类, 分类ID: {}", id);
        categoryService.deleteCategory(id);
        return Result.success(ResponseMessage.CATEGORY_DELETE_SUCCESS);
    }

    /**
     * 更新分类状态
     *
     * @param id 分类ID
     * @param status 状态
     * @return 响应结果
     */
    @PutMapping("/status/{id}")
    @AdminAccess
    public Result<Category> updateCategoryStatus(@PathVariable Long id, @RequestParam StatusEnum status) {
        log.info("管理员更新分类状态, 分类ID: {}, 状态: {}", id, status);
        Category updatedCategory = categoryService.updateCategoryStatus(id, status);
        return Result.success(updatedCategory, ResponseMessage.CATEGORY_STATUS_UPDATE_SUCCESS);
    }

}
