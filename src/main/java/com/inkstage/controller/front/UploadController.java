package com.inkstage.controller.front;

import com.inkstage.common.model.ResponseMessage;
import com.inkstage.common.model.Result;
import com.inkstage.entity.model.User;
import com.inkstage.service.FileService;
import com.inkstage.service.UserService;
import com.inkstage.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传Controller
 */
@RestController
@RequestMapping("/api/upload")
@Slf4j
@RequiredArgsConstructor
public class UploadController {

    private final FileService fileService;
    private final UserService userService;

    /**
     * 上传封面图
     *
     * @param file 上传的文件
     * @return 上传结果，包含文件URL
     */
    @PostMapping("/cover")
    @PreAuthorize("isAuthenticated()")
    public Result<String> uploadCover(@RequestParam("cover") MultipartFile file, @RequestParam(required = false) Long expiry) {
        try {
            // 从UserContext中获取当前用户ID
            String userIdStr = UserContext.getCurrentUserId();
            Long userId = Long.valueOf(userIdStr);

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
     * @return 上传结果，包含文件URL
     */
    @PostMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public Result<String> uploadAvatar(@RequestParam("avatar") MultipartFile file, @RequestParam(required = false) Long expiry) {
        try {
            // 从UserContext中获取当前用户ID
            String userIdStr = UserContext.getCurrentUserId();
            Long userId = Long.valueOf(userIdStr);

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

}
