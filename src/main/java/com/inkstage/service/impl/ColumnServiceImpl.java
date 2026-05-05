package com.inkstage.service.impl;

import com.inkstage.cache.service.ArticleCacheService;
import com.inkstage.common.PageResult;
import com.inkstage.constant.InkConstant;
import com.inkstage.dto.front.ColumnCreateDTO;
import com.inkstage.dto.front.ColumnQueryDTO;
import com.inkstage.entity.model.ArticleColumn;
import com.inkstage.entity.model.Column;
import com.inkstage.entity.model.User;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.enums.common.StatusEnum;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ArticleColumnMapper;
import com.inkstage.mapper.ColumnMapper;
import com.inkstage.notification.param.ColumnArticlePublishParam;
import com.inkstage.notification.param.ColumnDisabledParam;
import com.inkstage.notification.param.ColumnRestoredParam;
import com.inkstage.service.ColumnService;
import com.inkstage.service.ColumnSubscriptionService;
import com.inkstage.service.FileService;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ColumnServiceImpl implements ColumnService {

    private final ColumnMapper columnMapper;
    private final ArticleColumnMapper articleColumnMapper;
    private final FileService fileService;
    private final ColumnSubscriptionService columnSubscriptionService;
    private final ArticleCacheService articleCacheService;

    @Override
    @Transactional
    public Long createColumn(ColumnCreateDTO dto) {
        log.info("创建专栏: {}", dto.getName());
        try {
            Long userId = UserContext.getCurrentUserId();

            Column existColumn = columnMapper.findByNameAndUserId(dto.getName(), userId);
            if (existColumn != null) {
                throw new BusinessException("您已创建过同名专栏");
            }

            Column column = new Column();
            column.setUserId(userId);
            column.setName(dto.getName());
            column.setSlug(dto.getSlug());
            column.setDescription(dto.getDescription());
            column.setCoverImage(dto.getCoverImage());
            column.setArticleCount(0);
            column.setSubscriptionCount(0);
            column.setReadCount(0);
            column.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
            column.setStatus(StatusEnum.ENABLED);
            column.setCreateTime(LocalDateTime.now());
            column.setUpdateTime(LocalDateTime.now());
            column.setDeleted(DeleteStatus.NOT_DELETED);

            int result = columnMapper.insert(column);
            if (result > 0) {
                return column.getId();
            }
            return null;
        } catch (Exception e) {
            log.error("创建专栏失败", e);
            throw new BusinessException("创建专栏失败", e);
        }
    }

    @Override
    @Transactional
    public boolean updateColumn(Long columnId, ColumnCreateDTO dto) {
        log.info("更新专栏: id={}, name={}", columnId, dto.getName());
        try {
            Long userId = UserContext.getCurrentUserId();
            Column column = columnMapper.findById(columnId);

            if (column == null) {
                throw new BusinessException("专栏不存在");
            }

            if (!column.getUserId().equals(userId)) {
                throw new BusinessException("无权操作此专栏");
            }

            if (dto.getName() != null && !dto.getName().equals(column.getName())) {
                Column existColumn = columnMapper.findByNameAndUserId(dto.getName(), userId);
                if (existColumn != null && !existColumn.getId().equals(columnId)) {
                    throw new BusinessException("您已创建过同名专栏");
                }
            }

            column.setName(dto.getName());
            if (dto.getSlug() != null && !dto.getSlug().isEmpty()) {
                column.setSlug(dto.getSlug());
            }
            column.setDescription(dto.getDescription());
            column.setCoverImage(dto.getCoverImage());
            if (dto.getSortOrder() != null) {
                column.setSortOrder(dto.getSortOrder());
            }
            column.setUpdateTime(LocalDateTime.now());

            return columnMapper.update(column) > 0;
        } catch (Exception e) {
            log.error("更新专栏失败", e);
            throw new BusinessException("更新专栏失败", e);
        }
    }

    @Override
    @Transactional
    public boolean deleteColumn(Long columnId) {
        log.info("删除专栏: id={}", columnId);
        try {
            User user = UserContext.getCurrentUser();
            checkColumnIsMine(columnId, user);

            articleColumnMapper.deleteByColumnId(columnId);
            return columnMapper.deleteById(columnId, user.getId()) > 0;
        } catch (Exception e) {
            log.error("删除专栏失败", e);
            throw new BusinessException("删除专栏失败", e);
        }
    }

    /**
     * 检查专栏是否存在，以及是否有权限删除该专栏
     *
     * @param columnId 专栏ID
     * @param user     用户
     */
    private void checkColumnIsMine(Long columnId, User user) {
        Column column = columnMapper.findById(columnId);

        if (column == null) {
            throw new BusinessException("专栏不存在");
        }

        if (!column.getUserId().equals(user.getId()) && UserRoleEnum.ADMIN != UserRoleEnum.fromCode(user.getRoleId())) {
            throw new BusinessException("您无权操作此专栏");
        }
    }

    @Override
    public PageResult<ColumnListVO> getColumns(ColumnQueryDTO queryDTO) {
        log.info("获取专栏列表, pageNum={}, pageSize={}", queryDTO.getPageNum(), queryDTO.getPageSize());
        try {
            int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
            long total = columnMapper.countColumnList(queryDTO);
            List<ColumnListVO> columnListVOList = columnMapper.findColumnList(queryDTO, offset, queryDTO.getPageSize());

            fileService.ensureImageFullUrl(columnListVOList);

            return PageResult.build(columnListVOList, total, queryDTO.getPageNum(), queryDTO.getPageSize());
        } catch (Exception e) {
            log.error("获取专栏列表失败", e);
            throw new BusinessException("获取专栏列表失败", e);
        }
    }

    @Override
    public ColumnDetailVO getColumnDetail(Long columnId) {
        log.info("获取专栏详情: id={}", columnId);
        try {
            ColumnDetailVO columnDetail = columnMapper.findDetailById(columnId);
            fileService.ensureImageFullUrl(columnDetail);
            if (columnDetail != null) {
                List<ArticleListVO> articles = articleColumnMapper.findArticlesByColumnId(columnId);
                fileService.ensureImageFullUrl(articles);
                columnDetail.setArticles(articles);
            }
            return columnDetail;
        } catch (Exception e) {
            log.error("获取专栏详情失败", e);
            throw new BusinessException("获取专栏详情失败", e);
        }
    }

    @Override
    public List<ColumnListVO> getHotColumns(Integer limit) {
        log.info("获取热门专栏, limit={}", limit);
        try {
            List<ColumnListVO> hotColumns = columnMapper.findHotColumns(limit);

            fileService.ensureImageFullUrl(hotColumns);

            return hotColumns;
        } catch (Exception e) {
            log.error("获取热门专栏失败", e);
            throw new BusinessException("获取热门专栏失败", e);
        }
    }

    @Override
    public List<MyColumnVO> getMyColumns() {
        log.info("获取我的专栏");
        try {
            Long userId = UserContext.getCurrentUserId();
            List<MyColumnVO> myColumns = columnMapper.findMyColumns(userId);

            fileService.ensureImageFullUrl(myColumns);

            return myColumns;
        } catch (Exception e) {
            log.error("获取我的专栏失败", e);
            throw new BusinessException("获取我的专栏失败", e);
        }
    }

    @Override
    @Transactional
    public boolean addArticleToColumn(Long columnId, Long articleId, Integer sortOrder) {
        log.info("添加文章到专栏: columnId={}, articleId={}", columnId, articleId);
        try {
            User user = UserContext.getCurrentUser();
            checkColumnIsMine(columnId, user);

            ArticleColumn existArticleColumn = articleColumnMapper.findByArticleId(articleId);
            if (existArticleColumn != null) {
                if (existArticleColumn.getColumnId().equals(columnId)) {
                    throw new BusinessException("文章已在此专栏中");
                } else {
                    throw new BusinessException("文章已在其他专栏中");
                }
            }

            if (sortOrder == null) {
                int count = articleColumnMapper.countByColumnId(columnId);
                sortOrder = count + 1;
            }

            ArticleColumn articleColumn = new ArticleColumn();
            articleColumn.setArticleId(articleId);
            articleColumn.setColumnId(columnId);
            articleColumn.setSortOrder(sortOrder);
            articleColumn.setCreateTime(LocalDateTime.now());
            articleColumn.setUpdateTime(LocalDateTime.now());
            articleColumn.setDeleted(DeleteStatus.NOT_DELETED);

            boolean result = articleColumnMapper.insert(articleColumn) > 0;
            if (result) {
                columnMapper.updateArticleCount(columnId, 1);
                // 异步发送通知给订阅者
                try {
                    // 使用缓存获取文章详情
                    ArticleDetailVO article = articleCacheService.getArticleDetail(articleId);
                    sendColumnArticleUpdateNotification(columnId, articleId, article.getTitle());
                } catch (Exception e) {
                    log.error("发送专栏文章更新通知失败", e);
                }
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("添加文章到专栏失败", e);
            throw new BusinessException("添加文章到专栏失败", e);
        }
    }

    @Override
    @Transactional
    public boolean removeArticleFromColumn(Long columnId, Long articleId) {
        log.info("从专栏移除文章: columnId={}, articleId={}", columnId, articleId);
        try {
            User user = UserContext.getCurrentUser();
            checkColumnIsMine(columnId, user);

            ArticleColumn articleColumn = articleColumnMapper.findByArticleAndColumn(articleId, columnId);
            if (articleColumn == null) {
                throw new BusinessException("文章不在此专栏中");
            }

            int result = articleColumnMapper.deleteById(articleColumn.getId());
            if (result > 0) {
                columnMapper.updateArticleCount(columnId, -1);
            }
            return result > 0;
        } catch (Exception e) {
            log.error("从专栏移除文章失败", e);
            throw new BusinessException("从专栏移除文章失败", e);
        }
    }

    @Override
    @Transactional
    public boolean updateArticleSort(Long columnId, Long articleId, Integer sortOrder) {
        log.info("更新专栏文章排序: columnId={}, articleId={}, sortOrder={}", columnId, articleId, sortOrder);
        try {
            User user = UserContext.getCurrentUser();
            checkColumnIsMine(columnId, user);

            ArticleColumn articleColumn = articleColumnMapper.findByArticleAndColumn(articleId, columnId);
            if (articleColumn == null) {
                throw new BusinessException("文章不在此专栏中");
            }

            articleColumn.setSortOrder(sortOrder);
            articleColumn.setUpdateTime(LocalDateTime.now());

            return articleColumnMapper.update(articleColumn) > 0;
        } catch (Exception e) {
            log.error("更新专栏文章排序失败", e);
            throw new BusinessException("更新专栏文章排序失败", e);
        }
    }

    @Override
    public boolean isArticleInColumn(Long columnId, Long articleId) {
        log.info("检查文章是否在专栏中: columnId={}, articleId={}", columnId, articleId);
        try {
            ArticleColumn articleColumn = articleColumnMapper.findByArticleAndColumn(articleId, columnId);
            return articleColumn != null;
        } catch (Exception e) {
            log.error("检查文章是否在专栏中失败", e);
            throw new BusinessException("检查文章是否在专栏中失败", e);
        }
    }

    @Override
    public ArticleColumn getArticleColumn(Long articleId) {
        log.info("获取文章所属专栏: articleId={}", articleId);
        try {
            return articleColumnMapper.findByArticleId(articleId);
        } catch (Exception e) {
            log.error("获取文章所属专栏失败", e);
            throw new BusinessException("获取文章所属专栏失败", e);
        }
    }

    @Override
    @Transactional
    public void incrementColumnReadCount(Long columnId) {
        log.info("增加专栏阅读量: columnId={}", columnId);
        try {
            columnMapper.updateReadCount(columnId, 1);
        } catch (Exception e) {
            log.error("增加专栏阅读量失败", e);
            throw new BusinessException("增加专栏阅读量失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveArticleToColumn(Long articleId, Long newColumnId, Integer sortOrder) {
        log.info("移动文章到专栏: articleId={}, newColumnId={}", articleId, newColumnId);
        try {
            User user = UserContext.getCurrentUser();

            ArticleColumn existingRelation = articleColumnMapper.findByArticleId(articleId);
            Long oldColumnId = existingRelation != null ? existingRelation.getColumnId() : null;

            if (oldColumnId != null && oldColumnId.equals(newColumnId)) {
                log.info("文章已在目标专栏中，无需移动: articleId={}, columnId={}", articleId, newColumnId);
                return;
            }

            if (newColumnId != null) {
                checkColumnIsMine(newColumnId, user);
            }

            if (oldColumnId != null) {
                articleColumnMapper.deleteByArticleId(articleId);
                columnMapper.updateArticleCount(oldColumnId, -1);
                log.info("从旧专栏移除文章: articleId={}, oldColumnId={}", articleId, oldColumnId);
            }

            if (newColumnId != null) {
                if (sortOrder == null) {
                    int count = articleColumnMapper.countByColumnId(newColumnId);
                    sortOrder = count + 1;
                }

                ArticleColumn articleColumn = new ArticleColumn();
                articleColumn.setArticleId(articleId);
                articleColumn.setSortOrder(sortOrder);
                articleColumn.setColumnId(newColumnId);
                articleColumn.setCreateTime(LocalDateTime.now());
                articleColumn.setUpdateTime(LocalDateTime.now());
                articleColumn.setDeleted(DeleteStatus.NOT_DELETED);

                boolean result = articleColumnMapper.insert(articleColumn) > 0;
                if (result) {
                    columnMapper.updateArticleCount(newColumnId, 1);
                    log.info("文章移动到新专栏成功: articleId={}, newColumnId={}", articleId, newColumnId);
                }
            }
        } catch (Exception e) {
            log.error("移动文章到专栏失败", e);
            throw new BusinessException("移动文章到专栏失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeArticleColumnRelation(Long articleId) {
        log.info("解除文章与专栏的关联: articleId={}", articleId);
        try {
            ArticleColumn existingRelation = articleColumnMapper.findByArticleId(articleId);
            if (existingRelation != null) {
                articleColumnMapper.deleteByArticleId(articleId);
                columnMapper.updateArticleCount(existingRelation.getColumnId(), -1);
                log.info("文章与专栏关联已解除: articleId={}, columnId={}", articleId, existingRelation.getColumnId());
            }
        } catch (Exception e) {
            log.error("解除文章与专栏关联失败", e);
            throw new BusinessException("解除文章与专栏关联失败", e);
        }
    }

    @Override
    public void sendColumnArticleUpdateNotification(Long columnId, Long articleId, String articleTitle) {
        log.info("发送专栏文章更新通知: columnId={}, articleId={}, articleTitle={}", columnId, articleId, articleTitle);

        Column column = columnMapper.findById(columnId);
        if (column == null) {
            return;
        }

        ColumnArticlePublishParam param = new ColumnArticlePublishParam();
        param.setColumnId(columnId);
        param.setColumnName(column.getName());
        param.setArticleId(articleId);
        param.setArticleTitle(articleTitle);
        param.setArticleUrl(InkConstant.ARTICLE_URL + articleId);
        param.setSenderId(column.getUserId());
        param.setNotificationType(NotificationType.COLUMN_ARTICLE_PUBLISH);
        columnSubscriptionService.notifySubscribers(columnId, param);
    }

    @Override
    public void sendColumnDisabledNotification(Long columnId) {
        log.info("发送专栏下线通知: columnId={}", columnId);

        Column column = columnMapper.findById(columnId);
        if (column == null) {
            return;
        }

        ColumnDisabledParam param = new ColumnDisabledParam();
        param.setColumnId(columnId);
        param.setColumnName(column.getName());
        param.setReason("专栏已下线");
        param.setSenderId(column.getUserId());
        param.setNotificationType(NotificationType.COLUMN_DISABLED);
        columnSubscriptionService.notifySubscribers(columnId, param);
    }

    @Override
    public void sendColumnRestoredNotification(Long columnId) {
        log.info("发送专栏恢复通知: columnId={}", columnId);

        Column column = columnMapper.findById(columnId);
        if (column == null) {
            return;
        }

        ColumnRestoredParam param = new ColumnRestoredParam();
        param.setColumnId(columnId);
        param.setColumnName(column.getName());
        param.setActionUrl(InkConstant.COLUMN_URL + columnId);
        param.setSenderId(column.getUserId());
        param.setNotificationType(NotificationType.COLUMN_RESTORED);

        columnSubscriptionService.notifySubscribers(columnId, param);
    }
}
