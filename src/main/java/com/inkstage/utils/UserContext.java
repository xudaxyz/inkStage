package com.inkstage.utils;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * 用户上下文工具类
 * 提供统一的用户信息获取方式
 */
public class UserContext {

    /**
     * 获取当前用户ID
     *
     * @return 当前用户ID
     * @throws AccessDeniedException 如果用户未认证
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("User not authenticated");
        }
        return authentication.getName();
    }

    /**
     * 获取当前用户ID（可选）
     *
     * @return 当前用户ID的Optional对象
     */
    public static Optional<String> getCurrentUserIdOptional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return Optional.of(authentication.getName());
    }

    /**
     * 检查用户是否已认证
     *
     * @return 如果用户已认证，返回true；否则返回false
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
    }
}
