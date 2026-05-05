package com.inkstage.notification.param;

import com.inkstage.notification.NotificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 标签删除通知参数
 * <br/>
 * 
 * --- 通知展示格式 ---
 * ======================================================================
 * ==        标题：标签删除通知                                           ==
 * ==        内容：标签《${tagName}》已被删除                              ==
 * ======================================================================
 * <br/>
 * 
 * --- 参考示例 ---
 * ======================================================================
 * ==        标题：标签删除通知                                           ==
 * ==        内容：标签《旧标签》已被删除                                   ==
 * ======================================================================
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TagDeleteParam extends NotificationParam {

    /**
     * 标签名称
     */
    private String tagName;
    /**
     * 标签ID
     */
    private Long tagId;
}
