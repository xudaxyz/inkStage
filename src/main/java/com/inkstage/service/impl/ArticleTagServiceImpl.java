package com.inkstage.service.impl;

import com.inkstage.entity.model.ArticleTag;
import com.inkstage.entity.model.Tag;
import com.inkstage.enums.DeleteStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleTagMapper;
import com.inkstage.mapper.TagMapper;
import com.inkstage.service.ArticleTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
}
