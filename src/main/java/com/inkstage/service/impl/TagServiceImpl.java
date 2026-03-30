package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.entity.model.Tag;
import com.inkstage.entity.model.User;
import com.inkstage.enums.StatusEnum;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.TagMapper;
import com.inkstage.service.TagService;
import com.inkstage.service.NotificationService;
import com.inkstage.utils.UserContext;
import com.inkstage.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 标签服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {


    private final TagMapper tagMapper;
    private final NotificationService notificationService;

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
            tag.setUserId(UserContext.getCurrentUserId());
            tag.setTagVersion(1); // 新标签版本号设为1
            tag.setCreateTime(LocalDateTime.now());
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
            // 获取现有标签信息
            Tag existingTag = tagMapper.findById(tag.getId());
            if (existingTag == null) {
                throw new BusinessException("标签不存在");
            }
            // 递增版本号
            tag.setTagVersion(existingTag.getTagVersion() + 1);
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

            // 获取标签信息
            Tag tag = tagMapper.findById(id);
            if (tag == null) {
                throw new BusinessException("标签不存在");
            }

            // 获取使用该标签的所有用户ID
            List<Long> userIds = tagMapper.findUserIdsByTagId(id);

            // 发送通知
            for (Long userId : userIds) {
                String message;
                if (tag.getUserId() != null && tag.getUserId().equals(userId)) {
                    // 对创建者的通知
                    message = "您创建的标签" + tag.getName() + "已被删除";
                } else {
                    // 对使用者的通知
                    message = "您使用的标签" + tag.getName() + "已被删除";
                }

                notificationService.sendNotificationWithTemplate(
                        userId,
                        NotificationType.TAG_DELETE,
                        id,
                        0L, // 系统发送
                        message,
                        "标签不符合平台规范"
                );
            }

            // 执行删除操作
            tagMapper.deleteById(id);
            log.info("删除标签并发送通知成功, 标签ID: {}", id);
        } catch (BusinessException e) {
            throw e;
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

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Long createTagIfNotExists(Tag tag) {
        log.info("创建标签（如果不存在）: {}", tag.getName());
        try {
            // 首先尝试根据名称查找标签
            Tag existingTag = tagMapper.findByName(tag.getName());
            if (existingTag != null) {
                log.info("标签已存在: {}", tag.getName());
                // 标签已存在，更新文章数
                existingTag.setArticleCount(existingTag.getArticleCount() + 1);
                tagMapper.update(existingTag);
                return existingTag.getId();
            }

            // 标签不存在，创建新标签
            tag.setStatus(StatusEnum.ENABLED);
            tag.setArticleCount(1); // 新标签，文章数设为1
            tag.setUsageCount(1); // 使用次数设置为1

            // 设置当前用户ID
            User currentUser = UserContext.getCurrentUser();
            if (currentUser != null) {
                tag.setUserId(currentUser.getId());
            }

            tagMapper.insert(tag);
            log.info("创建新标签成功: {}", tag.getName());
            return tag.getId();
        } catch (Exception e) {
            log.error("创建标签失败", e);
            throw new BusinessException("创建标签失败", e);
        }
    }

}