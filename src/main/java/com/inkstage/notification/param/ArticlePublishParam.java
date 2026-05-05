package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 文章发布通知参数
 * <br/>
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：文章发布通知                                            ==
 * ==        内容：${username}发布了新文章《${articleTitle}》，快去看看吧！   ==
 * ==        跳转链接：${articleUrl}                                     ==
 * ======================================================================
 * <br/>
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：文章发布通知                                           ==
 * ==        内容：张三发布了新文章《Spring Boot 入门指南》，快去看看吧！       ==
 * ==        点击查看                                                    ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArticlePublishParam extends NotificationParam {

    /**
     * 作者用户名
     */
    private String username;
    /**
     * 文章标题
     */
    private String articleTitle;
    /**
     * 文章ID
     */
    private Long articleId;
    /**
     * 文章链接
     */
    private String articleUrl;
}
