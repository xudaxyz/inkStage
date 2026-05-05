package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 文章评论通知参数
 * <br/>
 * 
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：文章评论通知                                            ==
 * ==        内容：${username} 评论了你的文章《${articleTitle}》            ==
 * ==               ${commentContent}                                   ==
 * ==        跳转链接：${articleUrl}                                      ==
 * ======================================================================
 * <br/>
 * 
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：文章评论通知                                            ==
 * ==        内容：王五 评论了你的文章《微服务架构》                          ==
 * ==               这篇文章讲得很详细，收藏了！                            ==
 * ==        点击查看                                                      ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArticleCommentParam extends NotificationParam {

    /**
     * 评论者用户名
     */
    private String username;
    /**
     * 文章标题
     */
    private String articleTitle;
    /**
     * 评论内容
     */
    private String commentContent;
    /**
     * 文章ID
     */
    private Long articleId;
    /**
     * 文章链接
     */
    private String articleUrl;
}
