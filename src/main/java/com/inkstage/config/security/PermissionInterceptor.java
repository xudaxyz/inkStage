package com.inkstage.config.security;

import com.inkstage.exception.BusinessException;
import com.inkstage.common.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 权限拦截器
 * 实现细粒度的API权限控制
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor, InitializingBean {

    // 权限规则配置
    private final PermissionRuleConfig permissionRuleConfig;

    // 权限规则缓存,提高性能
    private final Map<String, PermissionRule> permissionRulesCache = new ConcurrentHashMap<>();

    /**
     * 初始化权限规则,从配置文件中读取
     */
    @Override
    public void afterPropertiesSet() {
        // 从配置文件中加载权限规则
        if (permissionRuleConfig != null && permissionRuleConfig.getPermissionRules() != null) {
            List<PermissionRuleItem> permissionRules = permissionRuleConfig.getPermissionRules();
            for (PermissionRuleItem item : permissionRules) {
                PermissionRule rule = new PermissionRule(
                        item.getPattern(),
                        item.getMethodsSet(),
                        item.getRequiredScopesSet()
                );
                permissionRulesCache.put(item.getName(), rule);
                log.debug("加载权限规则: {}, 路径: {}, 方法: {}, 所需权限: {}",
                        item.getName(), item.getPattern(), item.getMethods(), item.getRequiredScopesSet());
            }
        }
        log.info("权限规则加载完成,共加载 {} 条规则", permissionRulesCache.size());
    }

    /**
     * 请求处理前的拦截
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param handler  处理程序
     * @return 是否继续执行
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String requestPath = request.getRequestURI();
        String httpMethod = request.getMethod();

        log.info("权限检查 - 请求路径: {}, HTTP方法: {}", requestPath, httpMethod);

        // 1. 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. 如果认证信息为null,说明未认证
        if (authentication == null) {
            log.warn("权限检查 - 未认证请求,路径: {}", requestPath);
            return true; // 交给Spring Security处理
        }

        // 3. 检查认证类型是否为JWT认证
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            log.warn("权限检查 - 非JWT认证,路径: {}", requestPath);
            return true; // 交给其他认证机制处理
        }

        // 4. 获取JWT令牌和用户信息
        Jwt jwt = jwtAuthenticationToken.getToken();
        String userId = jwt.getSubject();
        String username = jwt.getClaimAsString("username");
        String scope = jwt.getClaimAsString("scope");

        log.info("权限检查 - 用户ID: {}, 用户名: {}, 权限范围: {}", userId, username, scope);

        // 5. 解析用户权限
        Set<String> userScopes = parseScope(scope);

        // 6. 遍历权限规则,检查是否匹配
        for (Map.Entry<String, PermissionRule> entry : permissionRulesCache.entrySet()) {
            PermissionRule rule = entry.getValue();

            // 检查路径和方法是否匹配
            if (rule.matches(requestPath, httpMethod)) {
                log.debug("权限检查 - 匹配规则: {}, 所需权限: {}", entry.getKey(), rule.getRequiredScopes());

                // 检查是否需要权限
                if (rule.getRequiredScopes().isEmpty()) {
                    log.info("权限检查 - 路径: {} 无需权限,允许访问", requestPath);
                    return true; // 公开API,无需权限
                }

                // 检查用户是否拥有所需权限
                boolean hasPermission = userScopes.stream()
                        .anyMatch(rule.getRequiredScopes()::contains);

                if (hasPermission) {
                    log.info("权限检查 - 用户: {} 拥有权限: {},允许访问路径: {}", username, userScopes, requestPath);
                    return true;
                } else {
                    log.warn("权限检查 - 用户: {} 缺少所需权限: {},禁止访问路径: {}", username, rule.getRequiredScopes(), requestPath);
                    throw new BusinessException(ResponseCode.FORBIDDEN,
                            String.format("权限不足,需要权限: %s,当前用户权限: %s",
                                    rule.getRequiredScopes(), userScopes));
                }
            }
        }

        // 7. 如果没有匹配的规则,默认允许访问
        log.info("权限检查 - 未找到匹配规则,默认允许访问路径: {}", requestPath);
        return true;
    }

    /**
     * 解析权限范围字符串为集合
     *
     * @param scope 权限范围字符串,格式: "read write admin"
     * @return 权限集合
     */
    private Set<String> parseScope(String scope) {
        if (scope == null || scope.isEmpty()) {
            return Set.of();
        }
        return Set.of(scope.split(" "));
    }
}