package com.inkstage.controller.front;

import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.dto.front.UserProfileDTO;
import com.inkstage.entity.model.User;
import com.inkstage.service.FileService;
import com.inkstage.service.UserService;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.UserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 前台用户Controller
 */
@Slf4j
@RestController
@RequestMapping("/front/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FileService fileService;

    /**
     * 获取当前用户个人资料
     *
     * @return 用户个人资料
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public Result<UserInfo> getProfile() {
        try {
            // 从UserContext中获取当前用户ID
            Long userId = UserContext.getCurrentUserId();
            User user = userService.getUserById(userId);
            if (user != null) {
                // 构建UserInfo对象
                UserInfo userInfo = assembleUserInfo(user);
                return Result.success(userInfo, ResponseMessage.SUCCESS);
            } else {
                return Result.error(ResponseMessage.USER_NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("获取个人资料失败: {}", e.getMessage(), e);
            return Result.error(ResponseMessage.ERROR);
        }
    }

    /**
     * 更新当前用户个人资料
     *
     * @param userProfileDTO 用户个人资料DTO
     * @return 更新结果
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public Result<UserInfo> updateProfile(@RequestBody @Valid UserProfileDTO userProfileDTO) {
        log.info("更新个人资料DTO: {}", userProfileDTO);
        try {
            User user = new User();
            // 设置用户信息
            user.setId(UserContext.getCurrentUserId());
            user.setNickname(userProfileDTO.getNickname());
            user.setGender(userProfileDTO.getGender());

            user.setBirthDate(userProfileDTO.getBirthDate());
            user.setLocation(userProfileDTO.getLocation());
            user.setSignature(userProfileDTO.getSignature());
            user.setCoverImage(userProfileDTO.getCoverImage());

            User updatedUser = userService.updateUser(user);
            // 确保用户头像和封面图的URL是完整的
            fileService.ensureUserImgIsFullUrl(updatedUser);
            // 构建返回的UserInfo对象
            UserInfo updatedUserInfo = assembleUserInfo(updatedUser);
            return Result.success(updatedUserInfo, ResponseMessage.UPDATE_SUCCESS);
        } catch (Exception e) {
            log.error("更新个人资料失败: {}", e.getMessage(), e);
            return Result.error(ResponseMessage.UPDATE_FAILED);
        }
    }

    /**
     * 获取指定用户的详细信息（公开信息）
     *
     * @param userId 用户ID
     * @return 用户详细信息
     */
    @GetMapping("/profile/{userId}")
    public Result<UserInfo> getUserProfile(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                // 构建UserInfo对象
                UserInfo userInfo = assembleUserInfo(user);
                return Result.success(userInfo, ResponseMessage.SUCCESS);
            } else {
                return Result.error(ResponseMessage.USER_NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("获取用户资料失败: {}", e.getMessage(), e);
            return Result.error(ResponseMessage.ERROR);
        }
    }

    private UserInfo assembleUserInfo(User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setName(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setCoverImage(user.getCoverImage());
        userInfo.setSignature(user.getSignature());
        userInfo.setGender(user.getGender());
        userInfo.setBirthDate(user.getBirthDate());
        userInfo.setLocation(user.getLocation());
        userInfo.setArticleCount(user.getArticleCount());
        userInfo.setLikeCount(user.getLikeCount());
        userInfo.setCommentCount(user.getCommentCount());
        userInfo.setFollowerCount(user.getFollowerCount());
        userInfo.setFollowCount(user.getFollowCount());
        userInfo.setRegisterTime(user.getRegisterTime());
        return userInfo;
    }
}