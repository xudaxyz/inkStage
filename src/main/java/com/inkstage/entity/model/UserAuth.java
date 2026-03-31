package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import com.inkstage.enums.common.DefaultStatus;
import com.inkstage.enums.common.StatusEnum;
import com.inkstage.enums.auth.AuthType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 用户认证实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAuth extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 认证类型(username,email,phone,GitHub,qq,WeChat等)
     */
    private AuthType authType;

    /**
     * 认证标识(用户名,邮箱,手机号,第三方用户ID)
     */
    private String authIdentifier;

    /**
     * 认证凭证(密码哈希,第三方access_token)
     */
    private String authCredential;

    /**
     * 凭证过期时间(如第三方access_token过期时间)
     */
    private LocalDateTime credentialExpiredAt;

    /**
     * 是否为主认证方式(0:否,1:是)
     */
    private DefaultStatus primaryAuth;

    /**
     * 是否启用该认证方式(0:禁用,1:启用)
     */
    private StatusEnum enabled;

    /**
     * 最后认证时间
     */
    private LocalDateTime lastAuthTime;
}