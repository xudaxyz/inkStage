package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.entity.model.ArticleTag;
import com.inkstage.entity.model.Tag;
import com.inkstage.enums.StatusEnum;
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
        log.info("获取所有标签");
        try {
            return tagMapper.findAll();
        } catch (Exception e) {
            log.error("获取所有标签失败", e);
            throw new BusinessException("获取标签列表失败", e);
        }
    }

    @Override
    public PageResult<Tag> getAdminTags(String keyword, Integer pageNum, Integer pageSize) {
        log.info("分页获取标签，页码：{}，每页大小：{}", pageNum, pageSize);
        try {
            // 关键词转换为小写
            if (keyword != null && !keyword.isEmpty()) {
                keyword = keyword.toLowerCase();
            }
            
            // 获取总记录数
            Long total = tagMapper.countByKeyword(keyword);

            Integer offset = (pageNum - 1) * pageSize;
            
            // 获取分页数据
            List<Tag> tags = tagMapper.findByKeyword(keyword, offset, pageSize);
            
            // 构建分页结果
            return PageResult.build(tags, total, pageNum, pageSize);
        } catch (Exception e) {
            log.error("分页获取标签失败", e);
            throw new BusinessException("分页获取标签失败", e);
        }
    }

    @Override
    public Tag getTagById(Long id) {
        log.info("根据ID获取标签: {}", id);
        try {
            if (id == null) {
                throw new BusinessException(ResponseMessage.PARAM_ERROR);
            }
            return tagMapper.findById(id);
        } catch (Exception e) {
            log.error("根据ID获取标签失败", e);
            throw new BusinessException("获取标签失败", e);
        }
    }

    @Override
    public List<Tag> getActiveTags() {
        log.info("获取激活状态标签列表");
        try {
            return tagMapper.findActiveTags();
        } catch (Exception e) {
            log.error("获取激活状态标签失败", e);
            throw new BusinessException("获取激活标签列表失败", e);
        }
    }

    @Override
    public List<Tag> getTagsByArticleId(Long articleId) {
        log.info("根据文章ID获取标签: {}", articleId);
        try {
            if (articleId == null) {
                throw new BusinessException(ResponseMessage.PARAM_ERROR);
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
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticleTagsByArticleId(Long articleId) {
        if (articleId == null) {
            throw new IllegalArgumentException("文章ID不能为空");
        }

        tagMapper.deleteArticleTagsByArticleId(articleId);
    }

    @Override
    public Tag addTag(Tag tag) {
        log.info("添加标签: {}", tag.getName());
        try {
            // 设置默认值
            if (tag.getArticleCount() == null) {
                tag.setArticleCount(0);
            }
            if (tag.getStatus() == null) {
                tag.setStatus(StatusEnum.ENABLED);
            }
            if (tag.getUsageCount() == null) {
                tag.setUsageCount(0);
            }
            // 将slug转换为小写
            if (tag.getSlug() != null && !tag.getSlug().isEmpty()) {
                tag.setSlug(tag.getSlug().toLowerCase());
            }
            tagMapper.insert(tag);
            return tag;
        } catch (Exception e) {
            log.error("添加标签失败", e);
            throw new BusinessException("添加标签失败", e);
        }
    }

    @Override
    public Tag updateTag(Tag tag) {
        log.info("更新标签: {}", tag.getId());
        try {
            if (tag.getId() == null) {
                throw new BusinessException(ResponseMessage.PARAM_ERROR);
            }
            // 将slug转换为小写
            if (tag.getSlug() != null && !tag.getSlug().isEmpty()) {
                tag.setSlug(tag.getSlug().toLowerCase());
            }
            tagMapper.update(tag);
            return tag;
        } catch (Exception e) {
            log.error("更新标签失败", e);
            throw new BusinessException("更新标签失败", e);
        }
    }

    @Override
    public void deleteTag(Long id) {
        log.info("删除标签: {}", id);
        try {
            if (id == null) {
                throw new BusinessException(ResponseMessage.PARAM_ERROR);
            }
            tagMapper.deleteById(id);
        } catch (Exception e) {
            log.error("删除标签失败", e);
            throw new BusinessException("删除标签失败", e);
        }
    }

    @Override
    public Tag updateTagStatus(Long id, StatusEnum status) {
        log.info("更新标签状态: {}, {}", id, status);
        try {
            if (id == null || status == null) {
                throw new BusinessException(ResponseMessage.PARAM_ERROR);
            }
            tagMapper.updateStatus(id, status);
            return tagMapper.findById(id);
        } catch (Exception e) {
            log.error("更新标签状态失败", e);
            throw new BusinessException("更新标签状态失败", e);
        }
    }

}