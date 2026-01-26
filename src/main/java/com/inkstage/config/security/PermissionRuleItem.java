package com.inkstage.config.security;

import lombok.Data;

import java.util.Set;

/**
 * 权限规则项
 */
@Data
public class PermissionRuleItem {
    /**
     * 规则名称
     */
    private String name;

    /**
     * 路径模式，支持Ant风格通配符
     */
    private String pattern;

    /**
     * HTTP方法，多个方法用逗号分隔
     */
    private String methods;

    /**
     * 所需权限，多个权限用空格分隔
     */
    private String requiredScopes;

    /**
     * 获取HTTP方法集合
     * @return HTTP方法集合
     */
    public Set<String> getMethodsSet() {
        return Set.of(methods.split(","));
    }

    /**
     * 获取所需权限集合
     * @return 所需权限集合
     */
    public Set<String> getRequiredScopesSet() {
        if (requiredScopes == null || requiredScopes.isEmpty()) {
            return Set.of();
        }
        return Set.of(requiredScopes.split(" "));
    }
}
