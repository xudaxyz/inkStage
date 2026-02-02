package com.inkstage.controller.front;

import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.entity.model.User;
import com.inkstage.service.FileService;
import com.inkstage.service.UserService;
import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
     * 上传封面图
     *
     * @param file 上传的文件
     * @return 上传结果, 包含文件URL
     */
    @PostMapping("/user/cover-image")
    @PreAuthorize("isAuthenticated()")
    public Result<String> uploadCover(@RequestParam("image") MultipartFile file, @RequestParam(required = false) Long expiry) {
        try {
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

            return Result.success(coverUrl, ResponseMessage.UPLOAD_SUCCESS);
        } catch (Exception e) {
            log.error("上传封面图失败: {}", e.getMessage(), e);
            return Result.error(ResponseMessage.UPLOAD_FAILED);
        }
    }

    /**
     * 上传头像
     *
     * @param file 上传的文件
     * @return 上传结果, 包含文件URL
     */
    @PostMapping("/user/avatar")
    @PreAuthorize("isAuthenticated()")
    public Result<String> uploadAvatar(@RequestParam("avatar") MultipartFile file, @RequestParam(required = false) Long expiry) {
        try {
            // 从UserContext中获取当前用户ID
            Long userId = UserContext.getCurrentUserId();

            // 调用文件服务上传头像
            long expiryTime = expiry != null ? expiry : 604800; // 默认7天
            String avatarUrl = fileService.uploadAvatar(file, userId, expiryTime);

            // 更新用户头像URL
            User user = new User();
            user.setId(userId);
            user.setAvatar(avatarUrl);
            userService.updateUser(user);

            return Result.success(avatarUrl, ResponseMessage.UPLOAD_SUCCESS);
        } catch (Exception e) {
            log.error("上传头像失败: {}", e.getMessage(), e);
            return Result.error(ResponseMessage.UPLOAD_FAILED);
        }
    }

    /**
     * 上传文章封面图
     *
     * @param file 上传的文件
     * @return 上传结果, 包含文件URL
     */
    @PostMapping("/article/cover-image")
    @PreAuthorize("isAuthenticated()")
    public Result<String> uploadArticleCover(@RequestParam("file") MultipartFile file, @RequestParam(required = false) Long expiry) {
        try {
            log.info("上传文章封面图片:{}", file.getOriginalFilename());
            // 从UserContext中获取当前用户ID
            Long userId = UserContext.getCurrentUserId();

            // 调用文件服务上传文章封面图
            long expiryTime = expiry != null ? expiry : 604800; // 默认7天
            String coverUrl = fileService.uploadArticleCoverImage(file, userId, expiryTime);
            log.info("文章封面图片上传成功:{}", coverUrl);
            return Result.success(coverUrl, ResponseMessage.UPLOAD_SUCCESS);
        } catch (Exception e) {
            log.error("上传文章封面图失败: {}", e.getMessage(), e);
            return Result.error(ResponseMessage.UPLOAD_FAILED);
        }
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名称(文件路径)
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public Result<Boolean> deleteFile(@RequestParam("file") String fileName) {
        try {
            // 从UserContext中获取当前用户ID
            Long userId = UserContext.getCurrentUserId();

            // 验证文件是否属于当前用户(简单验证：检查路径中是否包含用户ID)
            if (!fileName.contains(String.valueOf(userId))) {
                return Result.error("无权删除此文件");
            }

            // 调用文件服务删除文件
            fileService.deleteFile(fileName);

            return Result.success(true, "文件删除成功");
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage(), e);
            return Result.error("文件删除失败");
        }
    }

}
