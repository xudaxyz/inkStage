package com.inkstage.service.impl;

import com.inkstage.cache.constant.RedisKeyConstants;
import com.inkstage.cache.service.CacheClearService;
import com.inkstage.entity.model.Column;
import com.inkstage.entity.model.ColumnSubscription;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.ColumnMapper;
import com.inkstage.mapper.ColumnSubscriptionMapper;
import com.inkstage.service.ColumnSubscriptionService;
import com.inkstage.service.FileService;
import com.inkstage.service.NotificationService;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.front.MyColumnSubscriptionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean subscribeColumn(Long columnId) {
        Long userId = UserContext.getCurrentUserId();

        // 检查专栏是否存在
        Column column = columnMapper.findById(columnId);
        if (column == null) {
            throw new BusinessException("专栏不存在");
        }

        // 不能订阅自己的专栏
        if (column.getUserId().equals(userId)) {
            throw new BusinessException("不能订阅自己的专栏");
        }

        // 检查是否已经订阅
        int subscribed = columnSubscriptionMapper.checkSubscriptionStatus(userId, columnId);
        if (subscribed != 0) {
            log.info("用户 {} 已经订阅了专栏 {}", userId, columnId);
            return true;
        }

        // 创建订阅关系
        ColumnSubscription subscription = new ColumnSubscription();
        subscription.setUserId(userId);
        subscription.setColumnId(columnId);
        LocalDateTime now = LocalDateTime.now();
        subscription.setSubscriptionTime(now);
        subscription.setCreateTime(now);
        subscription.setUpdateTime(now);
        subscription.setDeleted(DeleteStatus.NOT_DELETED);

        int result = columnSubscriptionMapper.insert(subscription);
        if (result > 0) {
            cacheClearService.clearColumnSubscriptionCache(columnId, userId);
            columnMapper.updateSubscriptionCount(columnId, 1);
        }

        log.info("用户 {} 订阅专栏 {} 结果: {}", userId, columnId, result > 0);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unsubscribeColumn(Long columnId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        int result = columnSubscriptionMapper.delete(userId, columnId);
        if (result > 0) {
            cacheClearService.clearColumnSubscriptionCache(columnId, userId);
            columnMapper.updateSubscriptionCount(columnId, -1);
        }

        log.info("用户 {} 取消订阅专栏 {} 结果: {}", userId, columnId, result > 0);
        return result > 0;
    }

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_COLUMN_SUBSCRIPTION_STATUS,
            key = "#columnId",
            unless = "#result == null")
    public boolean isSubscribed(Long columnId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return false;
        }

        int result = columnSubscriptionMapper.checkSubscriptionStatus(userId, columnId);
        return result > 0;
    }

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_COLUMN_SUBSCRIPTION_LIST,
            key = "'my:' + #offset + ':' + #limit",
            unless = "#result == null or #result.isEmpty()")
    public List<MyColumnSubscriptionVO> getMySubscriptions(Integer offset, Integer limit) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        List<MyColumnSubscriptionVO> mySubscriptions = columnSubscriptionMapper.findMySubscriptions(userId, offset, limit);
        fileService.ensureImageFullUrl(mySubscriptions);
        return mySubscriptions;
    }

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_COLUMN_SUBSCRIPTION_LIST,
            key = "'count:my'",
            unless = "#result == null")
    public long countMySubscriptions() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        return columnSubscriptionMapper.countSubscriptions(userId);
    }

    @Override
    public List<Long> getSubscriberIds(Long columnId) {
        return columnSubscriptionMapper.findSubscriberIds(columnId);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.CACHE_COLUMN_SUBSCRIPTION_LIST,
            key = "'count:column:' + #columnId",
            unless = "#result == null")
    public long countSubscribers(Long columnId) {
        return columnSubscriptionMapper.countSubscribers(columnId);
    }

    @Override
    public void notifySubscribers(Long columnId, NotificationType notificationType, Map<String, Object> params) {
        log.info("向专栏订阅者发送通知: columnId={}, type={}", columnId, notificationType);

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

        params.put("columnId", columnId);
        params.put("columnName", column.getName());
        params.put("columnDescription", column.getDescription());
        params.put("columnCreatorName", column.getUserId());

        for (Long subscriberId : subscriberIds) {
            if (subscriberId.equals(column.getUserId())) {
                continue;
            }
            notificationService.sendNotificationWithTemplate(subscriberId, notificationType, params);
        }

        log.info("已向 {} 个订阅者发送专栏通知: type={}", subscriberIds.size(), notificationType);
    }
}
