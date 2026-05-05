package com.inkstage.service;

import com.inkstage.notification.NotificationParam;
import com.inkstage.vo.front.MyColumnSubscriptionVO;

import java.util.List;

/**
 * 专栏订阅服务接口
 * 提供专栏订阅、取消订阅、查询订阅状态等功能
 */
public interface ColumnSubscriptionService {

    /**
     * 订阅专栏
     *
     * @param columnId 专栏ID
     * @return 订阅成功返回true，失败返回false
     */
    boolean subscribeColumn(Long columnId);

    /**
     * 取消订阅专栏
     *
     * @param columnId 专栏ID
     * @return 取消订阅成功返回true，失败返回false
     */
    boolean unsubscribeColumn(Long columnId);

    /**
     * 检查当前用户是否已订阅指定专栏
     *
     * @param columnId 专栏ID
     * @return 已订阅返回true，未订阅返回false
     */
    boolean isSubscribed(Long columnId);

    /**
     * 获取当前用户的订阅专栏列表
     *
     * @param offset 偏移量
     * @param limit  限制数量
     * @return 订阅专栏列表
     */
    List<MyColumnSubscriptionVO> getMySubscriptions(Integer offset, Integer limit);

    /**
     * 获取当前用户的订阅专栏总数
     *
     * @return 订阅总数
     */
    long countMySubscriptions();

    /**
     * 获取专栏的订阅用户ID列表
     *
     * @param columnId 专栏ID
     * @return 订阅用户ID列表
     */
    List<Long> getSubscriberIds(Long columnId);

    /**
     * 获取专栏的订阅数
     *
     * @param columnId 专栏ID
     * @return 订阅数
     */
    long countSubscribers(Long columnId);

    /**
     * 向专栏的所有订阅者发送通知
     *
     * @param columnId 专栏ID
     * @param param    通知参数对象
     */
    void notifySubscribers(Long columnId, NotificationParam param);
}
