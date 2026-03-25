package com.inkstage.vo;

import com.inkstage.enums.NotificationType;
import lombok.Data;

/**
 * 模板预览结果VO
 */
@Data
public class TemplatePreviewVO {

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
    private NotificationType type;

    /**
     * 操作链接
     */
    private String actionUrl;
}
