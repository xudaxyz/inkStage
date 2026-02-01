package com.inkstage.service.impl;

import com.inkstage.config.MinioProperties;
import com.inkstage.constant.InkConstant;
import com.inkstage.entity.model.User;
import com.inkstage.service.FileService;
import com.inkstage.vo.front.ArticleDetailVO;
import com.inkstage.vo.front.ArticleListVO;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    /**
     * 允许的图片文件类型
     */
    private static final String[] ALLOWED_IMAGE_TYPES = {
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp"
    };

    /**
     * 最大文件大小(5MB)
     */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Override
    public String convertToFullUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return fileUrl;
        }

        // 如果已经是完整URL, 直接返回
        if (fileUrl.startsWith(InkConstant.PREFIX_URL) || fileUrl.startsWith(InkConstant.PREFIX_URLS)) {
            return fileUrl;
        }

        // 构建完整的Minio访问URL
        // 格式: <minio-endpoint>/<bucket-name>/<file-url>
        return minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + fileUrl;
    }

    @Override
    public void ensureUserImgIsFullUrl(User user) {
        if (user == null) {
            return;
        }

        String fullAvatarUrl = convertToFullUrl(user.getAvatar());
        String fullCoverImageUrl = convertToFullUrl(user.getCoverImage());
        user.setAvatar(fullAvatarUrl);
        user.setCoverImage(fullCoverImageUrl);
    }

    @Override
    public void ensureImageAreFullUrl(List<ArticleListVO> articleList) {
        if (articleList == null || articleList.isEmpty()) {
            return;
        }
        for (ArticleListVO articleListVO : articleList) {
            String fullCoverImageUrl = convertToFullUrl(articleListVO.getCoverImage());
            articleListVO.setCoverImage(fullCoverImageUrl);
            String fullAvatarUrl = convertToFullUrl(articleListVO.getAvatar());
            articleListVO.setAvatar(fullAvatarUrl);
        }
    }

    @Override
    public void ensureArticleDetailIsFullUrl(ArticleDetailVO articleDetailVO) {
        if (articleDetailVO == null) {
            return;
        }
        String fullCoverImageUrl = convertToFullUrl(articleDetailVO.getCoverImage());
        articleDetailVO.setCoverImage(fullCoverImageUrl);
        String fullAvatarUrl = convertToFullUrl(articleDetailVO.getAvatar());
        articleDetailVO.setAvatar(fullAvatarUrl);
    }

    @Override
    public String uploadFile(MultipartFile file, String bucketName, String objectName, long expiry) {
        try {
            // 验证文件类型
            validateFileType(file);

            // 验证文件大小
            validateFileSize(file);

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
    public String uploadFile(MultipartFile file, String objectName, long expiry) {
        return uploadFile(file, minioProperties.getBucketName(), objectName, expiry);
    }


    @Override
    public String uploadCoverImage(MultipartFile file, Long userId, long expiry) {
        String bucketName = minioProperties.getBucketName();
        String objectName = generateObjectName("user-covers", userId, file.getOriginalFilename());
        return uploadFile(file, bucketName, objectName, expiry);
    }

    @Override
    public String uploadAvatar(MultipartFile file, Long userId, long expiry) {
        String bucketName = minioProperties.getBucketName();
        String objectName = generateObjectName("user-avatars", userId, file.getOriginalFilename());
        return uploadFile(file, bucketName, objectName, expiry);
    }

    @Override
    public String uploadArticleCoverImage(MultipartFile file, Long userId, long expiry) {
        String bucketName = minioProperties.getBucketName();
        String objectName = generateObjectName("article-covers", userId, file.getOriginalFilename());
        return uploadFile(file, bucketName, objectName, expiry);
    }

    /**
     * 生成唯一的对象名称
     *
     * @param prefix           前缀(covers/avatars)
     * @param userId           用户ID
     * @param originalFilename 原始文件名
     * @return 生成的对象名称
     */
    private String generateObjectName(String prefix, Long userId, String originalFilename) {
        // 生成UUID
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 获取文件扩展名
        String extension = getFileExtension(originalFilename);

        // 构建对象名称, 直接使用用户名、前缀和uuid生成的图片名称
        return String.format("%d/%s/%s%s",
                userId, prefix, uuid, extension);
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 文件扩展名(包含.)
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 验证文件类型
     *
     * @param file 上传的文件
     */
    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        boolean isAllowed = false;

        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equals(contentType)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new IllegalArgumentException("只允许上传图片文件(jpg, jpeg, png, gif, webp)");
        }
    }

    /**
     * 验证文件大小
     *
     * @param file 上传的文件
     */
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }
    }

    @Override
    public void deleteFile(String objectName) {
        try {
            // 从MinIO删除文件
            minioClient.removeObject(
                    io.minio.RemoveObjectArgs.builder()
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

}
