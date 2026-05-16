package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 文章收藏通知参数
 * <br/>
 * 
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：文章收藏通知                                            ==
 * ==        内容：${collectorName} 收藏了你的文章《${articleTitle}》       ==
 * ==        跳转链接：${articleUrl}                                      ==
 * ======================================================================
 * <br/>
 * 
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：文章收藏通知                                            ==
 * ==        内容：李四 收藏了你的文章《Java核心技术》                        ==
 * ==        点击查看                                                     ==
 * ======================================================================
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArticleCollectionParam extends NotificationParam {

    /**
     * 收藏者用户名
     */
    private String collectorName;
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
