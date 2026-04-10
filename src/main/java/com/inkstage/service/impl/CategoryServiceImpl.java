package com.inkstage.service.impl;

import com.inkstage.cache.constant.RedisKeyConstants;
import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.entity.model.Category;
import com.inkstage.enums.common.StatusEnum;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.CategoryMapper;
import com.inkstage.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;


    @Override
    public List<Category> getAllCategories() {
        log.info("获取所有分类");
        try {
            return categoryMapper.findAll();
        } catch (Exception e) {
            log.error("获取所有分类失败", e);
            throw new BusinessException("获取分类列表失败", e);
        }
    }

    @Override
    public PageResult<Category> getAdminCategories(String keyword, Integer pageNum, Integer pageSize) {
        log.info("分页获取分类，页码：{}，每页大小：{}", pageNum, pageSize);
        try {
            // 关键词转换为小写
            if (keyword != null && !keyword.isEmpty()) {
                keyword = keyword.toLowerCase();
            }

            // 获取总记录数
            Long total = categoryMapper.countByKeyword(keyword);

            int offset = (pageNum - 1) * pageSize;

            // 获取分页数据
            List<Category> categories = categoryMapper.findByKeyword(keyword, offset, pageSize);

            // 构建分页结果
            return PageResult.build(categories, total, pageNum, pageSize);
        } catch (Exception e) {
            log.error("分页获取分类失败", e);
            throw new BusinessException("分页获取分类失败", e);
        }
    }

    @Override
    public Category getCategoryById(Long id) {
        log.info("根据ID获取分类: {}", id);
        try {
            if (id == null) {
                throw new BusinessException(ResponseMessage.PARAM_ERROR);
            }
            return categoryMapper.findById(id);
        } catch (Exception e) {
            log.error("根据ID获取分类失败", e);
            throw new BusinessException("获取分类失败", e);
        }
    }

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_CATEGORIES, key = "'active'")
    public List<Category> getActiveCategories() {
        log.info("获取激活状态分类");
        try {
            return categoryMapper.findActiveCategories();
        } catch (Exception e) {
            log.error("获取激活状态分类失败", e);
            throw new BusinessException("获取激活分类列表失败", e);
        }
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.CACHE_CATEGORIES, allEntries = true)
    public Category addCategory(Category category) {
        log.info("添加分类: {}", category.getName());
        try {
            // 设置默认值
            if (category.getParentId() == null) {
                category.setParentId(0L);
            }
            if (category.getSortOrder() == null) {
                category.setSortOrder(100);
            }
            if (category.getArticleCount() == null) {
                category.setArticleCount(0);
            }
            if (category.getStatus() == null) {
                category.setStatus(StatusEnum.ENABLED);
            }
            // 将slug转换为小写
            if (category.getSlug() != null && !category.getSlug().isEmpty()) {
                category.setSlug(category.getSlug().toLowerCase());
            }
            category.setCategoryVersion(1); // 设置初始版本号为1
            categoryMapper.insert(category);
            return category;
        } catch (Exception e) {
            log.error("添加分类失败", e);
            throw new BusinessException("添加分类失败", e);
        }
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.CACHE_CATEGORIES, allEntries = true)
    public Category updateCategory(Category category) {
        log.info("更新分类: {}", category.getId());
        try {
            if (category.getId() == null) {
                log.warn("更新分类失败, 分类ID为空");
                throw new BusinessException(ResponseMessage.PARAM_ERROR);
            }
            // 获取现有分类信息
            Category existingCategory = categoryMapper.findById(category.getId());
            if (existingCategory == null) {
                throw new BusinessException("分类不存在");
            }
            // 递增版本号
            category.setCategoryVersion(existingCategory.getCategoryVersion() + 1);
            // 将slug转换为小写
            if (category.getSlug() != null && !category.getSlug().isEmpty()) {
                category.setSlug(category.getSlug().toLowerCase());
            }
            category.setUpdateTime(LocalDateTime.now());
            categoryMapper.update(category);
            return category;
        } catch (Exception e) {
            log.error("更新分类失败", e);
            throw new BusinessException("更新分类失败", e);
        }
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.CACHE_CATEGORIES, allEntries = true)
    public void deleteCategory(Long id) {
        log.info("删除分类: {}", id);
        try {
            if (id == null) {
                throw new BusinessException(ResponseMessage.PARAM_ERROR);
            }
            categoryMapper.deleteById(id);
        } catch (Exception e) {
            log.error("删除分类失败", e);
            throw new BusinessException("删除分类失败", e);
        }
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.CACHE_CATEGORIES, allEntries = true)
    public Category updateCategoryStatus(Long id, StatusEnum status) {
        log.info("更新分类状态: {}, {}", id, status);
        try {
            if (id == null || status == null) {
                throw new BusinessException(ResponseMessage.PARAM_ERROR);
            }
            categoryMapper.updateStatus(id, status);
            return categoryMapper.findById(id);
        } catch (Exception e) {
            log.error("更新分类状态失败", e);
            throw new BusinessException("更新分类状态失败", e);
        }
    }

    @Override
    public void updateArticleCount(Long categoryId, int increment) {
        log.info("更新分类文章数量: {}, 增量: {}", categoryId, increment);
        try {
            if (categoryId == null) {
                log.warn("更新分类文章数量失败, 分类ID为空");
                return;
            }
            categoryMapper.updateArticleCount(categoryId, increment);
        } catch (Exception e) {
            log.error("更新分类文章数量失败", e);
        }
    }

}