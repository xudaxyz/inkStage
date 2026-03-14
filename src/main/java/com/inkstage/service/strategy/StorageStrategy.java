package com.inkstage.service.strategy;

import org.springframework.web.multipart.MultipartFile;

/**
 * 存储策略接口
 */
public interface StorageStrategy {

    /**
     * 上传文件
     *
     * @param file       上传的文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称(文件路径)
     * @return 文件访问路径
     */
    String uploadFile(MultipartFile file, String bucketName, String objectName);

    /**
     * 删除文件
     *
     * @param objectName 对象名称(文件路径)
     */
    void deleteFile(String objectName);

    /**
     * 生成完整的文件URL
     *
     * @param objectName 对象名称(文件路径)
     * @return 完整的文件URL
     */
    String generateFullUrl(String objectName);
}
