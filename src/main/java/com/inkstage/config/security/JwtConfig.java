package com.inkstage.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * JWT密钥管理配置类
 * 实现统一的密钥管理，支持多种密钥配置方式
 */
@Slf4j
@Configuration
public class JwtConfig {

    private final ResourceLoader resourceLoader;
    private final JwtKeyConfig jwtKeyConfig;

    public JwtConfig(ResourceLoader resourceLoader, JwtKeyConfig jwtKeyConfig) {
        this.resourceLoader = resourceLoader;
        this.jwtKeyConfig = jwtKeyConfig;
    }

    /**
     * 创建KeyPair Bean，支持多种密钥配置方式：
     * 1. 优先使用配置文件中的Base64编码密钥
     * 2. 其次使用密钥文件
     * 3. 最后动态生成密钥对
     *
     * @return KeyPair实例
     */
    @Bean
    public KeyPair rsaKeyPair() throws Exception {
        try {
            // 优先使用配置的Base64密钥
            if (jwtKeyConfig.getPrivateKey() != null && jwtKeyConfig.getPublicKey() != null) {
                log.info("Loading RSA key pair from Base64 configuration");
                PrivateKey privateKey = loadPrivateKeyFromBase64(jwtKeyConfig.getPrivateKey());
                PublicKey publicKey = loadPublicKeyFromBase64(jwtKeyConfig.getPublicKey());
                return new KeyPair(publicKey, privateKey);
            }

            // 其次使用密钥文件
            if (jwtKeyConfig.getPrivateKeyFile() != null && jwtKeyConfig.getPublicKeyFile() != null) {
                log.info("Loading RSA key pair from files: private={}, public={}",
                        jwtKeyConfig.getPrivateKeyFile(), jwtKeyConfig.getPublicKeyFile());
                PrivateKey privateKey = loadPrivateKeyFromFile(jwtKeyConfig.getPrivateKeyFile());
                PublicKey publicKey = loadPublicKeyFromFile(jwtKeyConfig.getPublicKeyFile());
                return new KeyPair(publicKey, privateKey);
            }

            // 动态生成密钥对作为备选方案
            log.warn("No RSA key configuration found, generating new key pair dynamically");
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                    jwtKeyConfig.getAlgorithm() != null ? jwtKeyConfig.getAlgorithm() : "RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            log.error("Failed to load RSA key pair. {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 从Base64字符串加载私钥
     *
     * @param privateKeyBase64 Base64编码的私钥
     * @return PrivateKey实例
     */
    private PrivateKey loadPrivateKeyFromBase64(String privateKeyBase64) throws Exception {
        String algorithm = jwtKeyConfig.getAlgorithm() != null ? jwtKeyConfig.getAlgorithm() : "RSA";
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePrivate(spec);
    }

    /**
     * 从文件加载私钥
     *
     * @param filePath 文件路径
     * @return PrivateKey实例
     */
    private PrivateKey loadPrivateKeyFromFile(String filePath) throws Exception {
        String privateKeyContent = loadKeyFile(filePath);
        String privateKeyPem = privateKeyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        return loadPrivateKeyFromBase64(privateKeyPem);
    }

    /**
     * 从Base64字符串加载公钥
     *
     * @param publicKeyBase64 Base64编码的公钥
     * @return PublicKey实例
     */
    private PublicKey loadPublicKeyFromBase64(String publicKeyBase64) throws Exception {
        String algorithm = jwtKeyConfig.getAlgorithm() != null ? jwtKeyConfig.getAlgorithm() : "RSA";
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(spec);
    }


    /**
     * 从文件加载公钥
     *
     * @param filePath 文件路径
     * @return PublicKey实例
     */
    private PublicKey loadPublicKeyFromFile(String filePath) throws Exception {
        String publicKeyContent = loadKeyFile(filePath);
        String publicKeyPem = publicKeyContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        return loadPublicKeyFromBase64(publicKeyPem);
    }

    /**
     * 加载密钥文件内容
     *
     * @param filePath 文件路径
     * @return 文件内容
     */
    private String loadKeyFile(String filePath) throws IOException {
        Resource resource = resourceLoader.getResource(filePath);
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }
}