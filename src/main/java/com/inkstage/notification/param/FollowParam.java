package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 关注通知参数
 * <br/>
 *
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：新增关注                                               ==
 * ==        内容：${username} 关注了你                                   ==
 * ==        点击查看: /user/{{userId}}                                  ==
 * ======================================================================
 * <br/>
 *
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：新增关注                                               ==
 * ==        内容：周九 关注了你                                           ==
 * ==        点击查看                                                    ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FollowParam extends NotificationParam {

    /**
     * 关注者用户名
     */
    private Long followerId;
    /**
     * 关注者用户名
     */
    private String username;
}
