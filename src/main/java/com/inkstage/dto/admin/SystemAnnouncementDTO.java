package com.inkstage.dto.admin;

import com.inkstage.enums.AnnouncementType;
import com.inkstage.enums.StatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统公告创建DTO
 */
@Data
public class SystemAnnouncementDTO {

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告类型
     */
    private AnnouncementType type;

    /**
     * 状态
     */
    private StatusEnum status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
}
