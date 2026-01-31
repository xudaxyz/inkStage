package com.inkstage.config.security;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/**
 * JWT配置类
 * 配置JwtEncoder, 用于生成JWT令牌
 * 用于读取application.yml中的JWT配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtKeyConfig {

    /**
     * 直接配置Base64编码的私钥
     */
    private String privateKey;

    /**
     * 直接配置Base64编码的公钥
     */
    private String publicKey;

    /**
     * 签名算法
     */
    private String algorithm;

    /**
     * 私钥文件路径
     */
    private String privateKeyFile;

    /**
     * 公钥文件路径
     */
    private String publicKeyFile;

    /**
     * Token过期时间(秒)
     */
    private Long expiration = 3600L;

    /**
     * Refresh Token过期时间(秒)
     */
    private Long refreshExpiration = 2592000L;

    /**
     * 配置JwtEncoder Bean, 用于生成JWT令牌
     * 依赖于AuthorizationServerConfig提供的JWKSource
     *
     * @param jwkSource JWKSource实例
     * @return JwtEncoder实例
     */
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }
}