package com.inkstage.config.oauth2;

import com.inkstage.service.RoleService;
import com.inkstage.service.UserRoleService;
import com.inkstage.cache.service.UserCacheService;
import com.inkstage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

/**
 * OAuth2资源服务器配置类
 */
@Configuration
@RequiredArgsConstructor
public class ResourceServerConfig {

    private final UserService userService;
    private final UserCacheService userCacheService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;


    /**
     * JWT解码器
     * 使用本地RSA公钥
     *
     * @param rsaKeyPair RSA密钥对
     * @return JwtDecoder实例
     */
    @Bean
    public JwtDecoder jwtDecoder(KeyPair rsaKeyPair) {
        // 使用本地公钥直接创建JwtDecoder
        RSAPublicKey publicKey = (RSAPublicKey) rsaKeyPair.getPublic();
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    /**
     * JWT认证转换器
     *
     * @return Converter<Jwt, AbstractAuthenticationToken>实例
     */
    @Bean
    public Converter<@NotNull Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return new CustomJwtAuthenticationConverter(userService, userCacheService, roleService, userRoleService);
    }
}