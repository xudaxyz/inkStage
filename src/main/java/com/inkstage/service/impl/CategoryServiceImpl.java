package com.inkstage.service.impl;

import com.inkstage.common.ResponseMessage;
import com.inkstage.entity.model.Category;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.CategoryMapper;
import com.inkstage.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
            return categoryMapper.selectAll();
        } catch (Exception e) {
            log.error("获取所有分类失败", e);
            throw new BusinessException("获取分类列表失败", e);
        }
    }

    @Override
    public Category getCategoryById(Long id) {
        log.info("根据ID获取分类: {}", id);
        try {
            if (id == null) {
                throw new BusinessException(ResponseMessage.PARAM_ERROR);
            }
            return categoryMapper.selectById(id);
        } catch (Exception e) {
            log.error("根据ID获取分类失败", e);
            throw new BusinessException("获取分类失败", e);
        }
    }

    @Override
    public List<Category> getActiveCategories() {
        log.info("获取激活状态分类");
        try {
            return categoryMapper.selectActiveCategories();
        } catch (Exception e) {
            log.error("获取激活状态分类失败", e);
            throw new BusinessException("获取激活分类列表失败", e);
        }
    }

}