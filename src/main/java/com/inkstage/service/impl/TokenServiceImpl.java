package com.inkstage.service.impl;

import com.inkstage.common.ResponseCode;
import com.inkstage.common.ResponseMessage;
import com.inkstage.dto.AuthDTO;
import com.inkstage.entity.model.User;
import com.inkstage.entity.model.UserRole;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.exception.BusinessException;
import com.inkstage.service.FileService;
import com.inkstage.service.TokenService;
import com.inkstage.service.TokenStoreService;
import com.inkstage.service.UserRoleService;
import com.inkstage.service.UserService;
import com.inkstage.vo.TokenResponse;
import com.inkstage.vo.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OAuth2令牌服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final FileService fileService;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserRoleService userRoleService;
    private final UserService userService;
    private final TokenStoreService tokenStoreService;

    @Override
    public TokenResponse generateTokenForUser(User user, AuthDTO authDTO) {
        log.info("生成用户: {} 令牌", user.getUsername());

        // 解析权限范围
        Set<String> scopes = resolveScopes(authDTO.getScope());

        // 获取用户角色
        List<UserRole> userRoles = userRoleService.getUserRoles(user.getId());
        // 设置用户权限
        List<GrantedAuthority> authorities = userRoles.stream()
                .map(role -> {
                    UserRoleEnum roleEnum = UserRoleEnum.fromCode(role.getRoleId());
                    return new SimpleGrantedAuthority("ROLE_" + roleEnum.name());
                })
                .collect(Collectors.toList());

        // 如果用户没有角色，默认设置为普通用户
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // 生成访问令牌
        Instant now = Instant.now();
        Instant expiresAt = now.plus(3600, ChronoUnit.SECONDS);

        // 生成刷新令牌
        Instant refreshExpiresAt;
        if (authDTO.getRememberMe() != null && authDTO.getRememberMe()) {
            // 记住我：刷新令牌7天过期
            refreshExpiresAt = now.plus(7, ChronoUnit.DAYS);
        } else {
            // 不记住我：刷新令牌1天过期
            refreshExpiresAt = now.plus(1, ChronoUnit.DAYS);
        }

        JwtClaimsSet accessTokenClaims = JwtClaimsSet.builder()
                .issuer("inkstage-2026-access") // 令牌颁发者
                .subject(user.getId().toString()) // 用户ID
                .audience(Collections.singletonList(authDTO.getClientId())) // 客户端ID
                .issuedAt(now) // 颁发时间
                .expiresAt(expiresAt) // 过期时间
                .notBefore(now) // 生效时间
                .id(UUID.randomUUID().toString()) // 令牌ID
                .claim("username", user.getUsername()) // 用户名
                .claim("roles", authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())) // 角色
                .claim("scope", String.join(" ", scopes)) // 权限范围
                .build();

        // 使用JwtEncoder生成访问令牌
        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();
        JwtClaimsSet refreshTokenClaims = JwtClaimsSet.builder()
                .issuer("inkstage-2026-refresh")
                .subject(user.getId().toString())
                .audience(Collections.singletonList(authDTO.getClientId()))
                .issuedAt(now)
                .expiresAt(refreshExpiresAt)
                .notBefore(now)
                .id(UUID.randomUUID().toString())
                .claim("type", "refresh")
                .build();

        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();

        // 存储刷新令牌到Redis
        tokenStoreService.storeRefreshToken(user.getId(), refreshToken,
                Duration.ofSeconds(refreshExpiresAt.getEpochSecond() - now.getEpochSecond()));

        // 构建响应
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccess_token(accessToken);
        tokenResponse.setToken_type("Bearer");
        tokenResponse.setRefresh_token(refreshToken);
        tokenResponse.setExpires_in(Math.toIntExact(expiresAt.getEpochSecond() - now.getEpochSecond()));
        tokenResponse.setScope(String.join(" ", scopes));

        // 确保用户相关图片URL完整
        fileService.ensureImageFullUrl(user);
        // 设置用户信息
        UserInfo userInfo = assembleUserInfo(user, userRoles);
        tokenResponse.setUserInfo(userInfo);

        log.info("用户: {} 令牌生成完成", user.getUsername());
        return tokenResponse;
    }

    private UserInfo assembleUserInfo(User user, List<UserRole> userRoles) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setNickname(user.getNickname());
        userInfo.setCoverImage(user.getCoverImage());
        userInfo.setSignature(user.getSignature());
        userInfo.setGender(user.getGender());
        userInfo.setBirthDate(user.getBirthDate());
        userInfo.setLocation(user.getLocation());

        // 设置用户角色
        if (!userRoles.isEmpty()) {
            UserRoleEnum primaryRole = UserRoleEnum.fromCode(userRoles.getFirst().getRoleId());
            userInfo.setRole(primaryRole);
        } else {
            userInfo.setRole(UserRoleEnum.USER);
        }

        return userInfo;
    }

    /**
     * 解析权限范围
     *
     * @param scope 请求中的权限范围字符串
     * @return 权限范围集合
     */
    private Set<String> resolveScopes(String scope) {
        if (scope == null || scope.isEmpty()) {
            return new HashSet<>(Arrays.asList("read", "write"));
        }
        return Arrays.stream(scope.split(" "))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        log.info("刷新令牌");
        try {
            // 解析刷新令牌
            Jwt jwt = jwtDecoder.decode(refreshToken);

            // 从令牌中获取用户ID
            String userIdStr = jwt.getSubject();
            if (userIdStr == null) {
                throw new BusinessException(ResponseCode.TOKEN_INVALID, ResponseMessage.REFRESH_TOKEN_INVALID);
            }

            Long userId = Long.parseLong(userIdStr);

            // 验证刷新令牌是否在Redis中有效
            if (!tokenStoreService.validateRefreshToken(userId, refreshToken)) {
                throw new BusinessException(ResponseCode.TOKEN_REVOKED, ResponseMessage.REFRESH_TOKEN_NOT_FOUND);
            }

            // 获取用户信息
            User user = userService.getUserById(userId);
            if (user == null) {
                // 撤销所有刷新令牌
                tokenStoreService.revokeAllRefreshTokens(userId);
                throw new BusinessException(ResponseCode.USER_NOT_FOUND, ResponseMessage.USER_NOT_FOUND);
            }

            // 获取客户端id
            List<String> audience = jwt.getAudience();
            if (audience == null || audience.isEmpty()) {
                throw new BusinessException(ResponseCode.TOKEN_INVALID, ResponseMessage.INVALID_AUDIENCE);
            }
            String clientId = jwt.getAudience().getFirst();

            String scope = jwt.getClaimAsString("scope");
            if (scope == null || scope.isEmpty()) {
                scope = "read write";
            }

            Instant expiresAt = jwt.getExpiresAt();
            long daysUnitExpiry = ChronoUnit.DAYS.between(Instant.now(), expiresAt);

            AuthDTO authDTO = new AuthDTO();
            authDTO.setClientId(clientId);
            authDTO.setScope(scope);
            // 如果刷新令牌过期时间超过3天，认为用户选择了记住我
            authDTO.setRememberMe(daysUnitExpiry >= 3);

            // 撤销旧的刷新令牌
            tokenStoreService.revokeRefreshToken(userId, refreshToken);

            // 生成新的令牌
            return generateTokenForUser(user, authDTO);
        } catch (Exception e) {
            log.error("刷新令牌失败: {}", e.getMessage());
            throw new BusinessException(ResponseCode.TOKEN_REFRESH_FAILED, ResponseMessage.TOKEN_REFRESH_FAILED);
        }
    }
}