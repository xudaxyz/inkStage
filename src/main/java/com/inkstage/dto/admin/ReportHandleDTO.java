package com.inkstage.dto.admin;

import com.inkstage.enums.HandleResultEnum;
import com.inkstage.enums.ReportStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 举报处理DTO
 */
@Data
public class ReportHandleDTO {

    /**
     * 举报状态
     */
    @NotNull(message = "举报状态不能为空")
    private ReportStatus reportStatus;

    /**
     * 处理结果
     */
    private HandleResultEnum handleResult;

    /**
     * 处理理由
     */
    @NotBlank(message = "处理理由不能为空")
    private String handleReason;
}
