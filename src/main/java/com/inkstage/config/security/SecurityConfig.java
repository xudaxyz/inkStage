package com.inkstage.config.security;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Objects;

/**
 * Spring Security基本配置类
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;

    private final Converter<@NotNull Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter;

    /**
     * 安全过滤器链
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain实例
     */
    @Bean
    @Order(100)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // 禁用CSRF保护, 适用于前后端分离架构
        http
                // 只处理非OAuth2授权服务器的请求
                .securityMatcher("/front/**", "/index/**", "/ws/**", "/admin/**", "/upload/**")
                // 启用CORS支持
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    // 动态允许请求来源
                    String origin = request.getHeader("Origin");
                    config.addAllowedOriginPattern(Objects.requireNonNullElse(origin, "*"));
                    config.addAllowedHeader("*");
                    config.addAllowedMethod("*");
                    config.setAllowCredentials(true);
                    config.setMaxAge(3600L);
                    return config;
                }))
                .authorizeHttpRequests(authorize -> authorize
                        // 允许公开API请求
                        .requestMatchers("/index/**").permitAll()
                        .requestMatchers("/front/ranking/**").permitAll()
                        .requestMatchers("/front/column/**").permitAll()
                        .requestMatchers("/front/user/**").permitAll()
                        .requestMatchers("/front/article/**", "/front/comment/**").permitAll()
                        .requestMatchers("/front/tag/**", "/front/category/**").permitAll()
                        .requestMatchers("/front/search/**").permitAll()
                        .requestMatchers("/front/notification/**").permitAll()
                        .requestMatchers("/auth/*").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable
                )
                .sessionManagement(session -> session
                        // 无状态会话, 适用于前后端分离架构
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 启用OAuth2资源服务器支持
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                );

        return http.build();
    }

    /**
     * 认证管理器
     *
     * @param userDetailsService 用户详情服务
     * @return AuthenticationManager实例
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

}