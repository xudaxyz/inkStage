package com.inkstage.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限规则配置类
 * 从配置文件中读取权限规则
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.security")
public class PermissionRuleConfig {

    /**
     * 权限规则列表
     */
    private List<PermissionRuleItem> permissionRules;


}