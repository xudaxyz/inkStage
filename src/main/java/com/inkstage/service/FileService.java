package com.inkstage.service;

import com.inkstage.entity.model.User;
import com.inkstage.vo.front.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
     * 上传文件到Minio(使用默认桶)
     *
     * @param file       要上传的文件
     * @param objectName 对象名称
     * @param expiry     URL有效期(秒)
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String objectName, long expiry);


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
     * 删除文件
     *
     * @param objectName 对象名称(文件路径)
     */
    void deleteFile(String objectName);

    /**
     * 确保User对象中的头像、封面图等字段是完整的预签名URL
     *
     * @param user 用户对象
     */
    void ensureUserImgIsFullUrl(User user);

    /**
     * 确保ArticleListVO对象中的图片字段是完整的预签名URL
     *
     * @param articleList 文章列表VO
     */
    void ensureArticleImageAreFullUrl(List<ArticleListVO> articleList);

    /**
     * 确保CommentVO对象中的图片字段是完整的预签名URL
     *
     * @param commentVOs 评论列表VO
     */
    void ensureCommentImageAreFullUrl(List<ArticleCommentVO> commentVOs);

    /**
     * 确保ArticleDetailVO对象中的图片字段是完整的预签名URL
     *
     * @param articleDetailVO 文章详情VO
     */
    void ensureArticleDetailIsFullUrl(ArticleDetailVO articleDetailVO);

    /**
     * 确保HotUserVO对象中的图片字段是完整的预签名URL
     *
     * @param hotUsers 热门用户列表VO
     */
    void ensureHotUserImgAreFullUrl(List<HotUserVO> hotUsers);

    /**
     * 确保CollectionArticleVO对象中的图片字段是完整的预签名URL
     * @param collectionArticlesVO 收藏文章列表VO
     */
    void ensureCollectionArticleImgAreFullUrl(List<CollectionArticleVO> collectionArticlesVO);
}
