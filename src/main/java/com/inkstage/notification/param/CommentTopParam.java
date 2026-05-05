package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 评论置顶通知参数
 * <br/>
 * 
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：评论置顶通知                                            ==
 * ==        内容：你的评论已被置顶                                         ==
 * ==        跳转链接：${articleUrl}                                     ==
 * ======================================================================
 * <br/>
 * 
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：评论置顶通知                                            ==
 * ==        内容：你的评论已被置顶                                         ==
 * ==        点击查看                                                     ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommentTopParam extends NotificationParam {

    /**
     * 文章ID
     */
    private Long articleId;
    /**
     * 文章链接
     */
    private String articleUrl;
}
