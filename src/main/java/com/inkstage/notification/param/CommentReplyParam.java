package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 评论回复通知参数
 * <br/>
 * 
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：评论回复通知                                            ==
 * ==        内容：${username} 回复了你的评论                              ==
 * ==               ${commentContent}                                  ==
 * ==        跳转链接：${articleUrl}                                     ==
 * ======================================================================
 * <br/>
 * 
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：评论回复通知                                            ==
 * ==        内容：孙八 回复了你的评论                                      ==
 * ==               同意楼上观点，确实讲得很详细                             ==
 * ==        点击查看                                                    ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommentReplyParam extends NotificationParam {

    /**
     * 回复者用户名
     */
    private String username;
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
