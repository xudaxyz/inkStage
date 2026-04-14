package com.inkstage.dto.front;

import com.inkstage.enums.ReportTargetType;
import com.inkstage.enums.ReportTypeEnum;
import com.inkstage.enums.common.DefaultStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 举报创建DTO
 */
@Data
public class ReportCreateDTO {

    /**
     * 被举报对象类型
     */
    @NotNull(message = "被举报对象类型不能为空")
    private ReportTargetType reportedType;

    /**
     * 被举报对象ID
     */
    @NotNull(message = "被举报对象ID不能为空")
    private Long reportedId;

    /**
     * 被举报对象用户名
     */
    private String reportedName;

    /**
     * 关联ID
     */
    @NotNull(message = "关联ID不能为空")
    private Long relatedId;

    /**
     * 被举报内容
     */
    private String reportedContent;

    /**
     * 举报类型
     */
    @NotNull(message = "举报类型不能为空")
    private ReportTypeEnum reportType;

    /**
     * 举报理由
     */
    @NotBlank(message = "举报理由不能为空")
    private String reason;

    /**
     * 举报证据(JSON格式, 包含图片、视频等链接)
     */
    private String evidence;

    /**
     * 是否匿名举报(0:否,1:是)
     */
    private DefaultStatus anonymous;
}
