package com.inkstage.vo.admin;

import com.inkstage.enums.NotificationType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 模板预览结果VO
 */
@Data
public class AdminNotificationTemplatePreviewVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 渲染后的标题
     */
    private String title;

    /**
     * 渲染后的内容
     */
    private String content;

    /**
     * 通知类型
     */
    private NotificationType notificationType;

    /**
     * 操作链接
     */
    private String actionUrl;
}
