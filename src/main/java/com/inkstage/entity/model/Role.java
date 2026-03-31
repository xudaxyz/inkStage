package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.common.DefaultStatus;
import com.inkstage.enums.common.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 是否为系统角色(0:否,1:是, 系统角色不可删除)
     */
    private DefaultStatus systemRole;

    /**
     * 角色等级(数值越大, 权限越高)
     */
    private Integer level;

    /**
     * 状态(0:禁用,1:正常)
     */
    private StatusEnum status;
}