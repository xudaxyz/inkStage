package com.inkstage.config.oauth2;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

/**
 * OAuth2授权服务器配置类
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {

    /**
     * 注册客户端仓库
     *
     * @param passwordEncoder 密码编码器
     * @return RegisteredClientRepository实例
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("inkstage-client")
                .clientSecret(passwordEncoder.encode("inkstage-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                // 启用password授权类型，用于用户名密码注册后直接获取令牌
                .authorizationGrantType(new AuthorizationGrantType("password"))
                .redirectUri("http://localhost:8081/login/oauth2/code/inkstage-client")
                .postLogoutRedirectUri("http://localhost:8081")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("read")
                .scope("write")
                .scope("admin")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1))  // 访问令牌有效期1小时
                        .refreshTokenTimeToLive(Duration.ofDays(7))  // 刷新令牌有效期7天
                        .reuseRefreshTokens(true)  // 允许刷新令牌重用
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }


    /**
     * 授权服务器安全过滤器链, 用于保护授权服务器端点
     */
    @Bean
    @Order(0)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) {
        try {
            // 只处理授权服务器相关请求
            http.securityMatcher("/oauth2/**", "/.well-known/oauth-authorization-server")
                    // 启用CORS支持
                    .cors(cors -> cors.configurationSource(request -> {
                        CorsConfiguration config = new CorsConfiguration();
                        // 动态允许请求来源
                        String origin = request.getHeader("Origin");
                        config.addAllowedOriginPattern(Objects.requireNonNullElse(origin, "*"));
                        config.addAllowedMethod("*");
                        config.addAllowedHeader("*");
                        config.setAllowCredentials(true);
                        config.setMaxAge(3600L);
                        return config;
                    }))
                    // 允许所有授权服务器端点公开访问
                    .authorizeHttpRequests(authorize -> authorize
                            // 允许所有授权服务器端点
                            .anyRequest().permitAll()
                    )
                    // 禁用CSRF保护（适用于密码授权类型）
                    .csrf(csrf -> csrf.ignoringRequestMatchers("/oauth2/**"))
                    // 无状态会话
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

            return http.build();

        } catch (Exception e) {
            log.error("授权服务器安全过滤器链配置失败", e);
            throw e;
        }

    }

    /**
     * JWK源
     * 使用统一密钥管理的KeyPair，避免每次生成新密钥
     *
     * @param keyPair 统一密钥管理的KeyPair实例
     * @return JWKSource实例
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource(KeyPair keyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * 授权服务器设置
     *
     * @return AuthorizationServerSettings实例
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("Inkstage-2026")
                .build();
    }
}