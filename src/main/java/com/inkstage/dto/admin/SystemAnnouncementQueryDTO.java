package com.inkstage.dto.admin;

import com.inkstage.common.PageRequest;
import com.inkstage.enums.AnnouncementType;
import com.inkstage.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统公告查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemAnnouncementQueryDTO extends PageRequest {

    /**
     * 公告类型
     */
    private AnnouncementType type;

    /**
     * 状态
     */
    private StatusEnum status;

    /**
     * 关键词(搜索标题或内容)
     */
    private String keyword;
}
