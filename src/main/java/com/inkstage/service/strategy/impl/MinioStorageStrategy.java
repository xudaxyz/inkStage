package com.inkstage.service.strategy.impl;

import com.inkstage.config.MinioProperties;
import com.inkstage.service.strategy.StorageStrategy;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Minio存储策略实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioStorageStrategy implements StorageStrategy {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public String uploadFile(MultipartFile file, String bucketName, String objectName) {
        try {
            // 上传文件到Minio
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 只返回相对路径, 不包含完整URL前缀
            return objectName;
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("上传文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("上传文件失败", e);
        }
    }

    @Override
    public void deleteFile(String objectName) {
        try {
            // 从MinIO删除文件
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .build()
            );
            log.info("删除文件成功: {}/{}", minioProperties.getBucketName(), objectName);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("删除文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("删除文件失败", e);
        }
    }

    @Override
    public String generateFullUrl(String objectName) {
        if (objectName == null || objectName.isEmpty()) {
            return objectName;
        }

        // 构建完整的Minio访问URL
        // 格式: <minio-endpoint>/<bucket-name>/<file-url>
        return minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + objectName;
    }
}
