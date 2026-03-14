package com.inkstage.service.strategy;

import com.inkstage.service.strategy.impl.MinioStorageStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 存储策略工厂
 */
@Component
@RequiredArgsConstructor
public class StorageStrategyFactory {

    private final MinioStorageStrategy minioStorageStrategy;

    /**
     * 获取存储策略
     *
     * @param strategyType 策略类型
     * @return 存储策略
     */
    public StorageStrategy getStorageStrategy(String strategyType) {
        // 目前只支持Minio存储策略
        // 未来可以根据需要添加其他存储策略，如本地存储、AWS S3等
        return minioStorageStrategy;
    }

    /**
     * 获取默认存储策略
     *
     * @return 默认存储策略
     */
    public StorageStrategy getDefaultStorageStrategy() {
        return minioStorageStrategy;
    }
}
