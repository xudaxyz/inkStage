package com.inkstage.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 手动发送通知DTO
 */
@Data
public class ManualNotificationDTO {

    /**
     * 模板编码
     */
    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    /**
     * 接收用户类型：all(所有用户), specific(指定用户), role(指定角色)
     */
    @NotBlank(message = "接收用户类型不能为空")
    private String userType;

    /**
     * 指定用户ID列表（当userType为specific时必填）
     */
    private List<Long> userIds;

    /**
     * 指定角色代码（当userType为role时必填）
     */
    private String roleCode;

    /**
     * 模板变量
     */
    private String variables;

    /**
     * 关联ID
     */
    private Long relatedId;

    /**
     * 发送者ID
     */
    private Long senderId;
}
