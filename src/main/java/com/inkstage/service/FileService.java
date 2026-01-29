package com.inkstage.service;

import com.inkstage.entity.model.User;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface FileService {


    /**
     * 将单个文件URL转换为完整的预签名URL
     *
     * @param fileUrl 文件URL(可能是短路径或完整URL)
     * @return 完整的预签名URL
     */
    String convertToFullUrl(String fileUrl);

    /**
     * 上传文件到Minio
     *
     * @param file       上传的文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称（文件路径）
     * @param expiry     URL有效期（秒）
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String bucketName, String objectName, long expiry);

    /**
     * 获取文件的预签名URL
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称（文件路径）
     * @param expiry     URL有效期（秒）
     * @return 预签名URL
     */
    String getPresignedUrl(String bucketName, String objectName, long expiry);

    /**
     * 上传用户封面图
     *
     * @param file   上传的文件
     * @param userId 用户ID
     * @param expiry URL有效期（秒）
     * @return 文件访问URL
     */
    String uploadCoverImage(MultipartFile file, Long userId, long expiry);

    /**
     * 上传用户头像
     *
     * @param file   上传的文件
     * @param userId 用户ID
     * @param expiry URL有效期（秒）
     * @return 文件访问URL
     */
    String uploadAvatar(MultipartFile file, Long userId, long expiry);

    /**
     * 确保User对象中的头像、封面图等字段是完整的预签名URL
     *
     * @param user 用户对象
     */
    void ensureUserImgIsFullUrl(User user);

}
