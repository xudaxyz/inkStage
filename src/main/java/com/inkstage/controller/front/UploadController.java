package com.inkstage.controller.front;

import com.inkstage.annotation.UserAccess;
import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.entity.model.User;
import com.inkstage.service.FileService;
import com.inkstage.service.UserService;
import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传Controller
 */
@RestController
@RequestMapping("/upload")
@Slf4j
@RequiredArgsConstructor
public class UploadController {

    private final FileService fileService;
    private final UserService userService;

    /**
     * 上传用户封面图
     *
     * @param file 上传的文件
     * @return 上传结果, 包含文件URL
     */
    @PostMapping("/user/cover-image")
    @UserAccess
    public Result<String> uploadUserCoverImage(@RequestParam("image") MultipartFile file, @RequestParam(required = false) Long expiry) {
        // 从UserContext中获取当前用户ID
        Long userId = UserContext.getCurrentUserId();

        // 调用文件服务上传封面图
        long expiryTime = expiry != null ? expiry : 604800; // 默认7天
        String coverUrl = fileService.uploadCoverImage(file, userId, expiryTime);

        // 更新用户封面图URL
        User user = new User();
        user.setId(userId);
        user.setCoverImage(coverUrl);
        userService.updateUser(user);
        String fullCoverUrl = fileService.convertToFullUrl(coverUrl);

        return Result.success(fullCoverUrl, ResponseMessage.UPLOAD_SUCCESS);
    }

    /**
     * 上传头像
     *
     * @param file 上传的文件
     * @return 上传结果, 包含文件URL
     */
    @PostMapping("/user/avatar")
    @UserAccess
    public Result<String> uploadAvatar(@RequestParam("avatar") MultipartFile file, @RequestParam(required = false) Long expiry) {
        // 从UserContext中获取当前用户ID
        Long userId = UserContext.getCurrentUserId();

        // 调用文件服务上传头像
        long expiryTime = expiry != null ? expiry : 604800; // 默认7天
        String avatarUrl = fileService.uploadAvatar(file, userId, expiryTime);
        log.info("头像上传成功:{}", avatarUrl);

        // 更新用户头像URL
        User user = new User();
        user.setId(userId);
        user.setAvatar(avatarUrl);
        userService.updateUser(user);
        // 返回头像的完整URL
        String fullAvatarUrl = fileService.convertToFullUrl(avatarUrl);
        return Result.success(fullAvatarUrl, ResponseMessage.UPLOAD_SUCCESS);
    }

    /**
     * 上传文章封面图
     *
     * @param file 上传的文件
     * @return 上传结果, 包含文件URL
     */
    @PostMapping("/article/cover-image")
    @UserAccess
    public Result<String> uploadArticleCoverImage(@RequestParam("file") MultipartFile file, @RequestParam(required = false) Long expiry) {
        log.info("上传文章封面图片:{}", file.getOriginalFilename());
        // 从UserContext中获取当前用户ID
        Long userId = UserContext.getCurrentUserId();

        // 调用文件服务上传文章封面图
        long expiryTime = expiry != null ? expiry : 604800; // 默认7天
        String coverUrl = fileService.uploadArticleCoverImage(file, userId, expiryTime);
        log.info("文章封面图片上传成功:{}", coverUrl);
        return Result.success(coverUrl, ResponseMessage.UPLOAD_SUCCESS);
    }

    /**
     * 上传专栏封面图
     *
     * @param file 上传的文件
     * @return 上传结果, 包含文件URL
     */
    @PostMapping("/column/cover-image")
    @UserAccess
    public Result<String> uploadColumnCoverImage(@RequestParam("file") MultipartFile file, @RequestParam(required = false) Long expiry) {
        log.info("上传专栏封面图片:{}", file.getOriginalFilename());
        Long userId = UserContext.getCurrentUserId();

        // 上传专栏封面图
        long expiryTime = expiry != null ? expiry : 2592000; // 默认30天
        String coverUrl = fileService.uploadColumnCoverImage(file, userId, expiryTime);
        log.info("专栏封面图片上传成功:{}", coverUrl);
        return Result.success(coverUrl, ResponseMessage.UPLOAD_SUCCESS);
    }

    /**
     * 即时上传图片
     *
     * @param file 上传的文件
     * @return 上传结果, 包含文件URL
     */
    @PostMapping("/article/image")
    @UserAccess
    public Result<String> uploadArticleImage(@RequestParam("file") MultipartFile file, @RequestParam(required = false) Long expiry) {
        log.info("上传图片:{}", file.getOriginalFilename());
        // 从UserContext中获取当前用户ID
        Long userId = UserContext.getCurrentUserId();

        // 调用文件服务上传文章封面图
        long expiryTime = expiry != null ? expiry : 604800; // 默认7天
        String coverUrl = fileService.uploadArticleImage(file, userId, expiryTime);
        String fullUrl = fileService.convertToFullUrl(coverUrl);
        log.info("文章图片上传成功:{}", fullUrl);
        return Result.success(fullUrl, ResponseMessage.UPLOAD_SUCCESS);
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名称(文件路径)
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    @UserAccess
    public Result<?> deleteFile(@RequestParam("file") String fileName) {
        // 从UserContext中获取当前用户ID
        Long userId = UserContext.getCurrentUserId();

        // 验证文件是否属于当前用户(简单验证：检查路径中是否包含用户ID)
        if (!fileName.contains(String.valueOf(userId))) {
            return Result.error(ResponseMessage.FORBIDDEN);
        }

        // 调用文件服务删除文件
        fileService.deleteFile(fileName);

        return Result.success(ResponseMessage.FILE_DELETE_SUCCESS);
    }

}
