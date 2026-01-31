package com.inkstage.service.impl;

import com.inkstage.common.ResponseMessage;
import com.inkstage.entity.model.ArticleTag;
import com.inkstage.entity.model.Tag;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.TagMapper;
import com.inkstage.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {


    private final TagMapper tagMapper;

    @Override
    public List<Tag> getAllTags() {
        return tagMapper.selectAll();
    }

    @Override
    public Tag getTagById(Long id) {
        if (id == null) {
            log.error("标签ID为空");
            throw new BusinessException(ResponseMessage.PARAM_ERROR);
        }
        return tagMapper.selectById(id);

    }

    @Override
    public List<Tag> getActiveTags() {
        log.info("获取激活状态标签列表");
        return tagMapper.selectActiveTags();
    }

    @Override
    public List<Tag> getTagsByArticleId(Long articleId) {
        if (articleId == null) {
            log.error("文章ID为空");
            throw new BusinessException(ResponseMessage.PARAM_ERROR);
        }
        return tagMapper.selectByArticleId(articleId);
    }

    @Override
    @Transactional
    public void saveArticleTags(Long articleId, List<Long> tagIds) {
        try {
            log.info("保存文章标签关联, 文章ID：{}, 标签ID列表：{}", articleId, tagIds);
            if (articleId == null) {
                throw new IllegalArgumentException("文章ID不能为空");
            }
            // 删除文章现有的标签关联
            tagMapper.deleteArticleTagsByArticleId(articleId);

            // 添加新的标签关联
            if (tagIds != null && !tagIds.isEmpty()) {
                for (Long tagId : tagIds) {
                    ArticleTag articleTag = new ArticleTag();
                    articleTag.setArticleId(articleId);
                    articleTag.setTagId(tagId);
                    tagMapper.insertArticleTag(articleTag);
                }
            }
        } catch (Exception e) {
            log.error("保存文章标签关联失败", e);
            throw new BusinessException("保存文章标签失败", e);
        }
    }

    @Override
    @Transactional
    public void deleteArticleTagsByArticleId(Long articleId) {
        if (articleId == null) {
            throw new IllegalArgumentException("文章ID不能为空");
        }

        tagMapper.deleteArticleTagsByArticleId(articleId);
    }

}