package com.inkstage.service.impl;

import com.inkstage.cache.constant.CacheKey;
import com.inkstage.cache.constant.CacheTTL;
import com.inkstage.cache.service.CacheClearService;
import com.inkstage.cache.service.CacheManager;
import com.inkstage.common.PageResult;
import com.inkstage.entity.model.Column;
import com.inkstage.entity.model.ColumnSubscription;
import com.inkstage.entity.model.User;
import com.inkstage.enums.CountType;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ColumnMapper;
import com.inkstage.mapper.ColumnSubscriptionMapper;
import com.inkstage.notification.NotificationParam;
import com.inkstage.notification.param.ColumnArticlePublishParam;
import com.inkstage.notification.param.ColumnDisabledParam;
import com.inkstage.notification.param.ColumnRestoredParam;
import com.inkstage.notification.param.ColumnSubscriptionParam;
import com.inkstage.service.ColumnSubscriptionService;
import com.inkstage.service.FileService;
import com.inkstage.service.NotificationService;
import com.inkstage.utils.SnowflakeIdGenerator;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.MyColumnSubscriptionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 专栏订阅服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ColumnSubscriptionServiceImpl implements ColumnSubscriptionService {

    private final ColumnSubscriptionMapper columnSubscriptionMapper;
    private final ColumnMapper columnMapper;
    private final CacheClearService cacheClearService;
    private final NotificationService notificationService;
    private final FileService fileService;
    private final CacheManager cacheManager;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final CountProducer countProducer;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean subscribeColumn(Long columnId) {
        User user = UserContext.getCurrentUser();

        // 检查专栏是否存在
        Column column = columnMapper.findById(columnId);
        if (column == null) {
            throw new BusinessException("专栏不存在");
        }

        // 不能订阅自己的专栏
        if (column.getUserId().equals(user.getId())) {
            throw new BusinessException("不能订阅自己的专栏");
        }

        // 检查是否已经订阅
        int subscribed = columnSubscriptionMapper.checkSubscriptionStatus(user.getId(), columnId);
        if (subscribed != 0) {
            log.info("用户 {} 已经订阅了专栏 {}", user.getId(), columnId);
            return true;
        }

        // 创建订阅关系
        ColumnSubscription subscription = new ColumnSubscription();
        subscription.setUserId(user.getId());
        subscription.setColumnId(columnId);
        LocalDateTime now = LocalDateTime.now();
        subscription.setSubscriptionTime(now);
        subscription.setCreateTime(now);
        subscription.setUpdateTime(now);
        subscription.setDeleted(DeleteStatus.NOT_DELETED);
        subscription.setId(snowflakeIdGenerator.nextId());

        int result = columnSubscriptionMapper.insert(subscription);
        if (result > 0) {
            cacheClearService.clearColumnSubscriptionCache(columnId, user.getId());
            countProducer.sendCountMessage(CountType.COLUMN_SUBSCRIPTION, columnId, 1);

            ColumnSubscriptionParam param = ColumnSubscriptionParam.builder()
                    .userId(column.getUserId())
                    .senderId(user.getId())
                    .columnId(columnId)
                    .columnName(column.getName())
                    .subscriberName(user.getNickname())
                    .notificationType(NotificationType.COLUMN_SUBSCRIPTION)
                    .build();
            notificationService.send(param);
        }

        log.info("用户 {} 订阅专栏 {} 结果: {}", user.getId(), columnId, result > 0);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unsubscribeColumn(Long columnId) {
        Long userId = UserContext.getCurrentUserId();

        int result = columnSubscriptionMapper.purge(userId, columnId);
        if (result > 0) {
            cacheClearService.clearColumnSubscriptionCache(columnId, userId);
            countProducer.sendCountMessage(CountType.COLUMN_SUBSCRIPTION, columnId, -1);
        }

        log.info("用户 {} 取消订阅专栏 {} 结果: {}", userId, columnId, result > 0);
        return result > 0;
    }

    @Override
    public boolean isSubscribed(Long userId, Long columnId) {
        if (userId == null) {
            return false;
        }

        String cacheKey = CacheKey.keyForColumnSubscriptionStatus(columnId, userId);
        Boolean result = cacheManager.get(cacheKey, Boolean.class);
        if (result != null) {
            return result;
        }

        int count = columnSubscriptionMapper.checkSubscriptionStatus(userId, columnId);
        result = count > 0;
        cacheManager.set(cacheKey, result, CacheTTL.DEFAULT);
        return result;
    }

    @Override
    public PageResult<MyColumnSubscriptionVO> getMySubscriptions(Long userId, Integer pageNum, Integer pageSize, String keyword) {
        String cacheKey = CacheKey.keyForColumnSubscriptionList(userId, pageNum, pageSize, keyword);
        PageResult<MyColumnSubscriptionVO> result = cacheManager.getWithType(cacheKey, new TypeReference<>() {
        });
        if (result != null && result.getTotal() > 0) {
            return result;
        }

        int offset = (pageNum - 1) * pageSize;
        long total = columnSubscriptionMapper.countMySubscriptionsWithKeyword(userId, keyword);
        List<MyColumnSubscriptionVO> mySubscriptions = columnSubscriptionMapper.findMySubscriptionsWithKeyword(userId, keyword, offset, pageSize);
        fileService.ensureImageFullUrl(mySubscriptions);

        result = PageResult.build(mySubscriptions, total, pageNum, pageSize);
        if (result.getTotal() > 0) {
            cacheManager.set(cacheKey, result, CacheTTL.DEFAULT);
        }
        return result;
    }

    @Override
    public long countMySubscriptions(Long userId) {
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        String cacheKey = CacheKey.keyForColumnSubscriptionUserCount(userId);
        Long result = cacheManager.get(cacheKey, Long.class);
        if (result != null) {
            return result;
        }

        result = columnSubscriptionMapper.countSubscriptions(userId);
        cacheManager.set(cacheKey, result, CacheTTL.DEFAULT);
        return result;
    }

    @Override
    public List<Long> getSubscriberIds(Long columnId) {
        return columnSubscriptionMapper.findSubscriberIds(columnId);
    }

    @Override
    public long countSubscribers(Long columnId) {
        String cacheKey = CacheKey.keyForColumnSubscriptionColumnCount(columnId);
        Long result = cacheManager.get(cacheKey, Long.class);
        if (result != null) {
            return result;
        }

        result = columnSubscriptionMapper.countSubscribers(columnId);
        cacheManager.set(cacheKey, result, CacheTTL.DEFAULT);
        return result;
    }

    @Override
    public void notifySubscribers(Long columnId, NotificationParam param) {
        log.info("向专栏订阅者发送通知: columnId={}, type={}", columnId, param.getNotificationType());

        Column column = columnMapper.findById(columnId);
        if (column == null) {
            log.warn("专栏不存在，无法发送通知: columnId={}", columnId);
            return;
        }

        List<Long> subscriberIds = columnSubscriptionMapper.findSubscriberIds(columnId);
        if (subscriberIds == null || subscriberIds.isEmpty()) {
            log.info("该专栏暂无订阅者: columnId={}", columnId);
            return;
        }

        List<NotificationParam> params = subscriberIds.stream()
                .filter(subscriberId -> !subscriberId.equals(column.getUserId()))
                .map(subscriberId -> {
                    NotificationParam p = copyParam(param);
                    p.setUserId(subscriberId);
                    p.setNotificationType(param.getNotificationType());
                    return p;
                })
                .toList();

        boolean result = notificationService.sendBatch(params);
        log.info("已向 {} 个订阅者发送专栏通知: type={}, 结果: {}", params.size(), param.getNotificationType(), result);
    }

    private NotificationParam copyParam(NotificationParam param) {
        switch (param) {
            case ColumnArticlePublishParam p -> {
                return ColumnArticlePublishParam.builder()
                        .columnId(p.getColumnId())
                        .columnName(p.getColumnName())
                        .articleId(p.getArticleId())
                        .articleTitle(p.getArticleTitle())
                        .articleUrl(p.getArticleUrl())
                        .userId(p.getUserId())
                        .senderId(p.getSenderId())
                        .notificationType(p.getNotificationType())
                        .build();
            }
            case ColumnDisabledParam p -> {
                return ColumnDisabledParam.builder()
                        .columnId(p.getColumnId())
                        .columnName(p.getColumnName())
                        .reason(p.getReason())
                        .userId(p.getUserId())
                        .notificationType(p.getNotificationType())
                        .senderId(p.getSenderId())
                        .build();
            }
            case ColumnRestoredParam p -> {
                return ColumnRestoredParam.builder()
                        .columnId(p.getColumnId())
                        .columnName(p.getColumnName())
                        .actionUrl(p.getActionUrl())
                        .userId(p.getUserId())
                        .notificationType(p.getNotificationType())
                        .senderId(p.getSenderId())
                        .build();
            }
            case null, default -> {
                return param;
            }
        }
    }
}