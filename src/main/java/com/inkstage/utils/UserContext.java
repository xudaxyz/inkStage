package com.inkstage.utils;

import com.inkstage.entity.model.User;
import com.inkstage.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * 用户上下文工具类
 * 提供统一的用户信息获取方式
 */
@Slf4j
public class UserContext {

    /**
     * 获取当前用户ID
     *
     * @return 当前用户ID
     * @throws AccessDeniedException 如果用户未认证
     */
    public static Long getCurrentUserId() {
        log.info("从用户上下文中获取用户ID");
        UserDetailsImpl currentUserDetails = getCurrentUserDetails();
        return currentUserDetails.getUser().getId();
    }


    /**
     * 获取当前用户详情
     *
     * @return UserDetailsImpl对象
     * @throws AccessDeniedException 如果用户未认证或类型不匹配
     */
    public static UserDetailsImpl getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("用户未认证");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return (UserDetailsImpl) principal;
        }
        throw new AccessDeniedException("用户认证失败");
    }

    /**
     * 获取当前用户详情(可选)
     *
     * @return UserDetailsImpl的Optional对象
     */
    public static Optional<UserDetailsImpl> getCurrentUserDetailsOptional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            return Optional.of((UserDetailsImpl) authentication.getPrincipal());
        }
        return Optional.empty();
    }

    /**
     * 获取当前用户对象
     *
     * @return User对象
     * @throws AccessDeniedException 如果用户未认证或类型不匹配
     */
    public static User getCurrentUser() {
        try {
            log.info("从用户上下文中获取用户对象");
            UserDetailsImpl currentUserDetails = getCurrentUserDetails();
            return currentUserDetails.getUser();
        } catch (AccessDeniedException e) {
            log.warn("用户未认证或未登录 {}", e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }
    }

    /**
     * 获取当前用户对象(可选)
     *
     * @return User的Optional对象
     */
    public static Optional<User> getCurrentUserOptional() {
        log.info("从用户上下文中获取可选的用户对象");
        return getCurrentUserDetailsOptional().map(UserDetailsImpl::getUser);
    }

    /**
     * 检查用户是否已认证
     *
     * @return 如果用户已认证, 返回true；否则返回false
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
    }
}
