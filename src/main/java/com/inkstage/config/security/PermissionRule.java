package com.inkstage.config.security;

import lombok.Data;
import org.springframework.util.AntPathMatcher;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * 权限规则类
 * 封装权限检查的规则
 */
@Data
public class PermissionRule {
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 路径模式, 支持Ant风格通配符
     */
    private final String pattern;
    /**
     * HTTP方法集合
     */
    private final Set<String> methods;
    /**
     * 所需权限集合
     */
    private final Set<String> requiredScopes;
    /**
     * 编译后的正则表达式, 提高匹配性能
     */
    private final Pattern patternRegex;

    /**
     * 构造方法
     *
     * @param pattern        路径模式
     * @param methods        HTTP方法集合
     * @param requiredScopes 所需权限集合
     */
    public PermissionRule(String pattern, Set<String> methods, Set<String> requiredScopes) {
        this.pattern = pattern;
        this.methods = Set.copyOf(methods);
        this.requiredScopes = Set.copyOf(requiredScopes);
        // 将Ant风格路径转换为正则表达式
        this.patternRegex = Pattern.compile(
                pattern.replace("**", ".*")
                        .replace("*", "[^/]*")
                        .replace("?", ".")
        );
    }

    /**
     * 检查请求是否匹配该规则
     *
     * @param requestPath 请求路径
     * @param httpMethod  HTTP方法
     * @return 是否匹配
     */
    public boolean matches(String requestPath, String httpMethod) {
        // 检查HTTP方法是否匹配
        if (!methods.contains(httpMethod)) {
            return false;
        }

        // 检查路径是否匹配（同时支持Ant风格和正则表达式）
        return pathMatcher.match(pattern, requestPath) ||
                patternRegex.matcher(requestPath).matches();
    }
}