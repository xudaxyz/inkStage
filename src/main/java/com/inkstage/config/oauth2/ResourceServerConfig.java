package com.inkstage.config.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

/**
 * OAuth2资源服务器配置类
 */
@Configuration
public class ResourceServerConfig {

    /**
     * JWT解码器
     * 使用本地RSA公钥，解决JWKS端点401错误
     *
     * @param rsaKeyPair RSA密钥对
     * @return JwtDecoder实例
     */
    @Bean
    public JwtDecoder jwtDecoder(KeyPair rsaKeyPair) {
        // 使用本地公钥直接创建JwtDecoder，避免访问远程JWKS端点
        RSAPublicKey publicKey = (RSAPublicKey) rsaKeyPair.getPublic();
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    /**
     * JWT认证转换器
     *
     * @return JwtAuthenticationConverter实例
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 配置权限前缀
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        // 配置权限声明名称
        grantedAuthoritiesConverter.setAuthoritiesClaimName("scope");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}