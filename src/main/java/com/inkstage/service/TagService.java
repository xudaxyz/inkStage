package com.inkstage.service;

import com.inkstage.common.PageRequest;
import com.inkstage.common.PageResult;
import com.inkstage.entity.model.Tag;
import com.inkstage.enums.StatusEnum;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService {

    /**
     * 获取所有标签
     *
     * @return 标签列表
     */
    List<Tag> getAllTags();

    /**
     * 根据ID获取标签
     *
     * @param id 标签ID
     * @return 标签对象
     */
    Tag getTagById(Long id);

    /**
     * 获取所有激活状态的标签
     *
     * @return 激活状态的标签列表
     */
    List<Tag> getActiveTags();

    /**
     * 根据文章ID获取关联的标签
     *
     * @param articleId 文章ID
     * @return 标签列表
     */
    List<Tag> getTagsByArticleId(Long articleId);

    /**
     * 保存文章与标签的关联(通过标签ID)
     *
     * @param articleId 文章ID
     * @param tagIds    标签ID列表
     */
    void saveArticleTags(Long articleId, List<Long> tagIds);

    /**
     * 删除文章的所有标签关联
     *
     * @param articleId 文章ID
     */
    void deleteArticleTagsByArticleId(Long articleId);

    /**
     * 添加标签
     *
     * @param tag 标签信息
     * @return 标签对象
     */
    Tag addTag(Tag tag);

    /**
     * 更新标签
     *
     * @param tag 标签信息
     * @return 标签对象
     */
    Tag updateTag(Tag tag);

    /**
     * 删除标签
     *
     * @param id 标签ID
     */
    void deleteTag(Long id);

    /**
     * 更新标签状态
     *
     * @param id     标签ID
     * @param status 状态
     * @return 标签对象
     */
    Tag updateTagStatus(Long id, StatusEnum status);

    /**
     * 管理员获取标签列表
     * @param keyword 关键字
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 响应结果
     */
    PageResult<Tag> getAdminTags(String keyword, Integer pageNum, Integer pageSize);
}