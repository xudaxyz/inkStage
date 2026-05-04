package com.inkstage.service;

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
     * @param objectName 对象名称(文件路径)
     * @param expiry     URL有效期(秒)
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String bucketName, String objectName, long expiry);

    /**
     * 上传用户封面图
     *
     * @param file   上传的文件
     * @param userId 用户ID
     * @param expiry URL有效期(秒)
     * @return 文件访问URL
     */
    String uploadCoverImage(MultipartFile file, Long userId, long expiry);

    /**
     * 上传用户头像
     *
     * @param file   上传的文件
     * @param userId 用户ID
     * @param expiry URL有效期(秒)
     * @return 文件访问URL
     */
    String uploadAvatar(MultipartFile file, Long userId, long expiry);

    /**
     * 上传文章封面图
     *
     * @param file   上传的文件
     * @param userId 用户ID
     * @param expiry URL有效期(秒)
     * @return 文件访问URL
     */
    String uploadArticleCoverImage(MultipartFile file, Long userId, long expiry);

    /**
     * 上传专栏封面图
     *
     * @param file   上传的文件
     * @param userId 用户ID
     * @param expiry URL有效期(秒)
     * @return 文件访问URL
     */
    String uploadColumnCoverImage(MultipartFile file, Long userId, long expiry);

    /**
     * 上传文章图片
     * @param file 上传的文件
     * @param userId 用户ID
     * @param expiry URL有效期(秒)
     * @return 文件访问URL
     */
    String uploadArticleImage(MultipartFile file, Long userId, long expiry);

    /**
     * 删除文件
     *
     * @param objectName 对象名称(文件路径)
     */
    void deleteFile(String objectName);

    /**
     * 自动确保对象的图片字段是完整URL
     * 支持单个对象和集合，会递归处理集合中的每个元素
     * 支持的图片字段：coverImage, avatar, repliedUserAvatar
     *
     * @param obj 对象或集合（支持 List, Set 等）
     */
    void ensureImageFullUrl(Object obj);
}
