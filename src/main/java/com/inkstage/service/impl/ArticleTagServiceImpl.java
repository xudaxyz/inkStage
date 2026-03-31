package com.inkstage.service.impl;

import com.inkstage.entity.model.ArticleTag;
import com.inkstage.entity.model.Tag;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleTagMapper;
import com.inkstage.mapper.TagMapper;
import com.inkstage.service.ArticleTagService;
import com.inkstage.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 文章标签关联服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleTagServiceImpl implements ArticleTagService {

    private final ArticleTagMapper articleTagMapper;
    private final TagMapper tagMapper;
    private final TagService tagService;

    @Override
    public List<Tag> getTagsByArticleId(Long articleId) {
        log.info("根据文章ID获取标签: {}", articleId);
        try {
            if (articleId == null) {
                throw new IllegalArgumentException("文章ID不能为空");
            }
            return tagMapper.findByArticleId(articleId);
        } catch (Exception e) {
            log.error("根据文章ID获取标签失败", e);
            throw new BusinessException("获取文章标签失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveArticleTags(Long articleId, List<Long> tagIds) {
        try {
            log.info("保存文章标签关联, 文章ID：{}, 标签ID列表：{}", articleId, tagIds);
            if (articleId == null) {
                throw new IllegalArgumentException("文章ID不能为空");
            }
            // 删除文章现有的标签关联
            articleTagMapper.deleteByArticleId(articleId);

            // 添加新的标签关联
            if (tagIds != null && !tagIds.isEmpty()) {
                for (Long tagId : tagIds) {
                    ArticleTag articleTag = new ArticleTag();
                    articleTag.setArticleId(articleId);
                    articleTag.setTagId(tagId);
                    articleTag.setCreateTime(LocalDateTime.now());
                    articleTag.setDeleted(DeleteStatus.NOT_DELETED);
                    articleTagMapper.insert(articleTag);
                }
            }
        } catch (Exception e) {
            log.error("保存文章标签关联失败", e);
            throw new BusinessException("保存文章标签失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticleTagsByArticleId(Long articleId) {
        if (articleId == null) {
            throw new IllegalArgumentException("文章ID不能为空");
        }

        articleTagMapper.deleteByArticleId(articleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleArticleTags(Long articleId, List<Tag> tags) {
        if (articleId == null) {
            log.warn("处理文章标签关联失败, 文章ID为空");
            return;
        }

        // 收集所有标签ID
        List<Long> allTagIds = new ArrayList<>();

        // 处理标签列表
        if (tags == null || tags.isEmpty()) {
            // 没有标签, 清除现有关联
            deleteArticleTagsByArticleId(articleId);
            return;
        }
        for (Tag tag : tags) {
            if (tag == null || tag.getName() == null || tag.getName().trim().isEmpty()) {
                continue;
            }

            if (tag.getId() != null && tag.getId() > 0) {
                // 已存在的标签，直接使用其ID
                allTagIds.add(tag.getId());
            } else {
                // 新标签，需要创建
                try {
                    Long tagId = tagService.createTagIfNotExists(tag);
                    if (tagId != null) {
                        allTagIds.add(tagId);
                    }
                } catch (Exception e) {
                    // 标签创建失败，记录日志但不影响文章处理
                    log.error("创建标签失败, 标签名称: {}", tag.getName(), e);
                }
            }
        }

        if (allTagIds.isEmpty()) {
            // 没有标签, 清除现有关联
            deleteArticleTagsByArticleId(articleId);
            return;
        }

        // 保存文章标签关联
        saveArticleTags(articleId, allTagIds);
    }
}
