package com.inkstage.dto.admin;

import com.inkstage.common.PageRequest;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.enums.user.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 后台用户管理查询DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AdminUserQueryDTO extends PageRequest {

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 角色
     */
    private UserRoleEnum userRole;

    /**
     * 用户状态
     */
    private UserStatus status;

    /**
     * 开始日期
     */
    private LocalDateTime startDate;

    /**
     * 结束日期
     */
    private LocalDateTime endDate;

}
