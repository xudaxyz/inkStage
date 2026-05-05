package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 评论点赞通知参数
 * <br/>
 * 
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：评论点赞通知                                            ==
 * ==        内容：${username} 点赞了你的评论                               ==
 * ==        跳转链接：${articleUrl}                                      ==
 * ======================================================================
 * <br/>
 * 
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：评论点赞通知                                            ==
 * ==        内容：钱七 点赞了你的评论                                       ==
 * ==        点击查看                                                      ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommentLikeParam extends NotificationParam {

    /**
     * 点赞者用户名
     */
    private String username;
    /**
     * 文章ID
     */
    private Long articleId;
    /**
     * 文章链接
     */
    private String articleUrl;
}
