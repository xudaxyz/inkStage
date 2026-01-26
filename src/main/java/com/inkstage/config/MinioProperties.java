package com.inkstage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Minio配置属性类
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    /**
     * Minio服务端点
     */
    private String endpoint;

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 秘密密钥
     */
    private String secretKey;

    /**
     * 默认桶名称
     */
    private String bucketName;

    /**
     * 是否使用SSL
     */
    private boolean secure;

    /**
     * 预签名URL过期时间(秒)
     */
    private long preSignedUrlExpiry;

    /**
     * 区域
     */
    private String region;
}
