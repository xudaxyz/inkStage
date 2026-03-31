package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.common.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 用户角色关联实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserRole extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 分配角色的用户ID
     */
    private Long assignedBy;

    /**
     * 角色分配时间
     */
    private LocalDateTime assignedAt;

    /**
     * 角色过期时间(NULL表示永久有效)
     */
    private LocalDateTime expiresAt;

    /**
     * 状态 (0:禁用, 1:启用)
     */
    private StatusEnum status;
}