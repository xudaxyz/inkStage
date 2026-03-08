package com.inkstage.service.impl;

import com.inkstage.dto.AuthDTO;
import com.inkstage.entity.model.User;
import com.inkstage.entity.model.UserRole;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.service.FileService;
import com.inkstage.service.TokenService;
import com.inkstage.service.UserRoleService;
import com.inkstage.vo.TokenResponse;
import com.inkstage.vo.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

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
    private final UserRoleService userRoleService;

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

        // 生成刷新令牌
        Instant refreshExpiresAt = now.plus(7, ChronoUnit.DAYS);
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

        // 构建响应
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccess_token(accessToken);
        tokenResponse.setToken_type("Bearer");
        tokenResponse.setRefresh_token(refreshToken);
        tokenResponse.setExpires_in(Math.toIntExact(expiresAt.getEpochSecond() - now.getEpochSecond()));
        tokenResponse.setScope(String.join(" ", scopes));

        // 确保用户相关图片URL完整
        fileService.ensureUserImgIsFullUrl(user);
        // 设置用户信息
        UserInfo userInfo = assembleUserInfo(user, userRoles);
        tokenResponse.setUserInfo(userInfo);

        log.info("用户: {} 令牌生成完成", user.getUsername());
        return tokenResponse;
    }

    private UserInfo assembleUserInfo(User user, List<UserRole> userRoles) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setName(user.getUsername());
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
            userInfo.setRole(primaryRole.name());
        } else {
            userInfo.setRole(UserRoleEnum.USER.name());
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
}