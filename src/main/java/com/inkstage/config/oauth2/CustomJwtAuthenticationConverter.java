package com.inkstage.config.oauth2;

import com.inkstage.entity.model.User;
import com.inkstage.security.UserDetailsImpl;
import com.inkstage.service.UserCacheService;
import com.inkstage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * 自定义JWT认证转换器
 * 完善JWT认证转换, 确保正确处理用户信息
 */
@Slf4j
public class CustomJwtAuthenticationConverter implements Converter<@NotNull Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter;
    private final UserService userService;
    private final UserCacheService userCacheService;

    public CustomJwtAuthenticationConverter(UserService userService, UserCacheService userCacheService) {
        this.userService = userService;
        this.userCacheService = userCacheService;
        this.grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 配置权限前缀
        this.grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        // 配置权限声明名称
        this.grantedAuthoritiesConverter.setAuthoritiesClaimName("scope");
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // 从JWT中提取用户信息
        Map<String, Object> claims = jwt.getClaims();

        // 获取用户ID
        String userId;
        if (claims.containsKey("user_id")) {
            userId = claims.get("user_id").toString();
        } else {
            // 尝试从subject中获取
            userId = jwt.getSubject();
        }

        // 获取用户名
        String username = null;
        if (claims.containsKey("username")) {
            username = claims.get("username").toString();
        }

        // 加载用户信息
        User user = loadUser(userId, username);

        // 构建UserDetailsImpl
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // 获取权限信息
        Collection<? extends GrantedAuthority> authorities = this.grantedAuthoritiesConverter.convert(jwt);

        // 创建UsernamePasswordAuthenticationToken，使用UserDetailsImpl作为principal
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "[PROTECTED]",
                authorities
        );
    }

    /**
     * 加载用户信息
     * 优先从缓存中获取，缓存不存在则从数据库获取
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return 用户对象
     */
    private User loadUser(String userId, String username) {
        // 尝试从缓存中获取
        Optional<User> cachedUser = userCacheService.getUserFromCache(userId);
        if (cachedUser.isPresent()) {
            log.debug("从缓存中获取用户, id: {}", userId);
            return cachedUser.get();
        }

        // 从数据库中获取
        User user = null;
        try {
            // 尝试根据ID获取
            if (userId != null) {
                try {
                    Long id = Long.parseLong(userId);
                    user = userService.getUserById(id);
                } catch (NumberFormatException e) {
                    log.warn("无效的用户id: {}", userId);
                }
            }

            // 如果根据ID获取失败，尝试根据用户名获取
            if (user == null && username != null) {
                user = userService.getUserByUsername(username);
            }

            if (user == null) {
                log.error("用户未找到: userId={}, username={}", userId, username);
                throw new IllegalArgumentException("User not found");
            }

            // 缓存用户信息
            userCacheService.cacheUser(user);
            log.debug("从数据库中获取用户并缓存: {}", userId);
        } catch (Exception e) {
            log.error("加载用户失败: {}", e.getMessage(), e);
            throw new IllegalArgumentException("获取用户信息失败", e);
        }

        return user;
    }
}

