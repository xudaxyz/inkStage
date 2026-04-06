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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            log.info("保存文章标签关联, 文章ID：{}, 标签ID数量：{}", articleId, tagIds != null ? tagIds.size() : 0);
            if (articleId == null) {
                throw new IllegalArgumentException("文章ID不能为空");
            }

            // 获取当前文章关联的标签
            List<Tag> currentTags = tagMapper.findByArticleId(articleId);
            Set<Long> currentTagIds = currentTags.stream()
                    .map(Tag::getId)
                    .collect(Collectors.toSet());

            // 转换新标签ID为集合
            Set<Long> newTagIds = tagIds != null ? new HashSet<>(tagIds) : new HashSet<>();

            // 计算需要删除和添加的标签
            Set<Long> tagsToRemove = new HashSet<>(currentTagIds);
            tagsToRemove.removeAll(newTagIds);

            Set<Long> tagsToAdd = new HashSet<>(newTagIds);
            tagsToAdd.removeAll(currentTagIds);

            // 删除文章现有的标签关联
            articleTagMapper.deleteByArticleId(articleId);

            // 批量添加新的标签关联
            if (tagIds != null && !tagIds.isEmpty()) {
                List<ArticleTag> articleTags = new ArrayList<>(tagIds.size());
                LocalDateTime now = LocalDateTime.now();

                for (Long tagId : tagIds) {
                    ArticleTag articleTag = new ArticleTag();
                    articleTag.setArticleId(articleId);
                    articleTag.setTagId(tagId);
                    articleTag.setCreateTime(now);
                    articleTag.setDeleted(DeleteStatus.NOT_DELETED);
                    articleTags.add(articleTag);
                }

                // 批量插入
                articleTagMapper.batchInsert(articleTags);
            }

            // 更新标签统计数据
            updateTagStatistics(tagsToRemove, tagsToAdd);
        } catch (Exception e) {
            log.error("保存文章标签关联失败", e);
            throw new BusinessException("保存文章标签失败", e);
        }
    }

    /**
     * 更新标签统计数据
     *
     * @param tagsToRemove 需要减少统计数据的标签ID集合
     * @param tagsToAdd    需要增加统计数据的标签ID集合
     */
    private void updateTagStatistics(Set<Long> tagsToRemove, Set<Long> tagsToAdd) {
        // 减少被移除标签的统计数据
        for (Long tagId : tagsToRemove) {
            tagMapper.updateTagStats(tagId, -1, -1);
        }

        // 增加新添加标签的统计数据
        for (Long tagId : tagsToAdd) {
            tagMapper.updateTagStats(tagId, 1, 1);
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
        try {
            log.info("处理文章标签关联, 文章ID：{}, 标签数量：{}", articleId, tags != null ? tags.size() : 0);

            // 参数校验
            if (articleId == null) {
                log.warn("处理文章标签关联失败, 文章ID为空");
                return;
            }

            // 处理标签列表，获取所有标签ID
            List<Long> tagIds = processTagList(tags);

            // 保存文章标签关联
            saveArticleTags(articleId, tagIds);
        } catch (Exception e) {
            log.error("处理文章标签关联失败, 文章ID：{}", articleId, e);
            throw new BusinessException("处理文章标签失败", e);
        }
    }

    /**
     * 处理标签列表，返回所有标签ID
     * 对于已存在的标签直接使用其ID，对于新标签进行创建
     */
    private List<Long> processTagList(List<Tag> tags) {
        List<Long> tagIds = new ArrayList<>();

        if (tags == null || tags.isEmpty()) {
            return tagIds;
        }

        for (Tag tag : tags) {
            if (tag == null || tag.getName() == null || tag.getName().trim().isEmpty()) {
                continue;
            }

            if (tag.getId() != null && tag.getId() > 0) {
                // 已存在的标签，直接使用其ID
                tagIds.add(tag.getId());
            } else {
                // 新标签，需要创建
                try {
                    Long tagId = tagService.createTagIfNotExists(tag);
                    if (tagId != null) {
                        tagIds.add(tagId);
                    }
                } catch (Exception e) {
                    // 标签创建失败，记录日志但不影响文章处理
                    log.error("创建标签失败, 标签名称: {}", tag.getName(), e);
                }
            }
        }

        return tagIds;
    }
}
