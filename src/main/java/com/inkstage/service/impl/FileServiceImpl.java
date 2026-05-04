package com.inkstage.service.impl;

import com.inkstage.config.MinioProperties;
import com.inkstage.constant.InkConstant;
import com.inkstage.exception.BusinessException;
import com.inkstage.service.FileService;
import com.inkstage.service.strategy.StorageStrategy;
import com.inkstage.service.strategy.StorageStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioProperties minioProperties;
    private final StorageStrategyFactory storageStrategyFactory;

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
     * 最大文件大小(10MB)
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 支持的图片字段名
     */
    private static final Set<String> IMAGE_FIELDS = Set.of(
            "coverImage",
            "avatar",
            "repliedUserAvatar"
    );

    /**
     * 字段缓存：Class -> 该类中需要处理的图片字段列表
     */
    private final Map<Class<?>, List<Field>> fieldCache = new ConcurrentHashMap<>();

    @Override
    public String convertToFullUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return fileUrl;
        }

        // 如果已经是完整URL, 直接返回
        if (fileUrl.startsWith(InkConstant.PREFIX_URL) || fileUrl.startsWith(InkConstant.PREFIX_URLS)) {
            return fileUrl;
        }

        // 使用存储策略生成完整URL
        StorageStrategy storageStrategy = storageStrategyFactory.getDefaultStorageStrategy();
        return storageStrategy.generateFullUrl(fileUrl);
    }



    @Override
    public String uploadFile(MultipartFile file, String bucketName, String objectName, long expiry) {
        // 验证文件类型
        validateFileType(file);

        // 验证文件大小
        validateFileSize(file);

        // 使用存储策略上传文件
        StorageStrategy storageStrategy = storageStrategyFactory.getDefaultStorageStrategy();
        return storageStrategy.uploadFile(file, bucketName, objectName);
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

    @Override
    public String uploadColumnCoverImage(MultipartFile file, Long userId, long expiry) {
        String bucketName = minioProperties.getBucketName();
        String objectName = generateObjectName("column-covers", userId, file.getOriginalFilename());
        return uploadFile(file, bucketName, objectName, expiry);
    }

    @Override
    public String uploadArticleImage(MultipartFile file, Long userId, long expiry) {
        String bucketName = minioProperties.getBucketName();
        String objectName = generateObjectName("articles", userId, file.getOriginalFilename());
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
            throw new BusinessException("文件大小不能超过10MB");
        }
    }

    @Override
    public void deleteFile(String objectName) {
        // 使用存储策略删除文件
        StorageStrategy storageStrategy = storageStrategyFactory.getDefaultStorageStrategy();
        storageStrategy.deleteFile(objectName);
    }

    @Override
    public void ensureImageFullUrl(Object obj) {
        if (obj == null) {
            return;
        }

        if (obj instanceof Collection<?> collection) {
            for (Object item : collection) {
                ensureImageFullUrl(item);
            }
            return;
        }

        Class<?> clazz = obj.getClass();
        List<Field> imageFields = getImageFields(clazz);

        for (Field field : imageFields) {
            try {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (value instanceof String url && !url.isEmpty()) {
                    field.set(obj, convertToFullUrl(url));
                }
            } catch (IllegalAccessException e) {
                log.warn("处理字段 {} 时出错", field.getName(), e);
            }
        }
    }

    private List<Field> getImageFields(Class<?> clazz) {
        return fieldCache.computeIfAbsent(clazz, cls ->
                Arrays.stream(cls.getDeclaredFields())
                        .filter(f -> IMAGE_FIELDS.contains(f.getName()))
                        .toList()
        );
    }

}
