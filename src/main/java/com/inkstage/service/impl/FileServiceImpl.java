package com.inkstage.service.impl;

import com.inkstage.config.MinioProperties;
import com.inkstage.constant.InkConstant;
import com.inkstage.entity.model.User;
import com.inkstage.service.FileService;
import com.inkstage.service.strategy.StorageStrategy;
import com.inkstage.service.strategy.StorageStrategyFactory;
import com.inkstage.vo.admin.AdminArticleDetailVO;
import com.inkstage.vo.front.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

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

        // 使用存储策略生成完整URL
        StorageStrategy storageStrategy = storageStrategyFactory.getDefaultStorageStrategy();
        return storageStrategy.generateFullUrl(fileUrl);
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
    public void ensureArticleImageAreFullUrl(List<ArticleListVO> articleList) {
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
    public void ensureCommentImageAreFullUrl(List<ArticleCommentVO> commentVOs) {
        if (commentVOs == null || commentVOs.isEmpty()) {
            return;
        }
        for (ArticleCommentVO commentVO : commentVOs) {
            // 顶层评论头像
            String topAvatarUrl = convertToFullUrl(commentVO.getAvatar());
            commentVO.setAvatar(topAvatarUrl);
            // 子评论头像
            List<ArticleCommentVO> replies = commentVO.getReplies();
            if (replies != null && !replies.isEmpty()) {
                for (ArticleCommentVO reply : replies) {
                    String fullAvatarUrl = convertToFullUrl(reply.getAvatar());
                    reply.setAvatar(fullAvatarUrl);
                }
            }

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
    public void ensureHotUserImgAreFullUrl(List<HotUserVO> hotUsers) {
        if (hotUsers == null || hotUsers.isEmpty()) {
            return;
        }
        for (HotUserVO hotUser : hotUsers) {
            String fullAvatarUrl = convertToFullUrl(hotUser.getAvatar());
            hotUser.setAvatar(fullAvatarUrl);
        }
    }

    @Override
    public void ensureCollectionArticleImgAreFullUrl(List<CollectionArticleVO> collectionArticlesVO) {
        if (collectionArticlesVO == null || collectionArticlesVO.isEmpty()) {
            return;
        }
        // 确保封面图和用户头像的URL是完整的
        for (CollectionArticleVO collectionArticleVO : collectionArticlesVO) {
            String fullCoverImageUrl = convertToFullUrl(collectionArticleVO.getCoverImage());
            collectionArticleVO.setCoverImage(fullCoverImageUrl);
            String fullAvatarUrl = convertToFullUrl(collectionArticleVO.getAvatar());
            collectionArticleVO.setAvatar(fullAvatarUrl);
        }
    }

    @Override
    public void ensureAdminArticleDetailIsFullUrl(AdminArticleDetailVO adminArticleDetailVO) {
        if (adminArticleDetailVO == null) {
            return;
        }
        String fullCoverImageUrl = convertToFullUrl(adminArticleDetailVO.getCoverImage());
        adminArticleDetailVO.setCoverImage(fullCoverImageUrl);
    }

    @Override
    public String getFullUrl(String image) {
        if (image == null) {
            log.info("图片信息不存在，无法生成完整URL");
            return null;
        }
        return convertToFullUrl(image);
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
        // 使用存储策略删除文件
        StorageStrategy storageStrategy = storageStrategyFactory.getDefaultStorageStrategy();
        storageStrategy.deleteFile(objectName);
    }

}
