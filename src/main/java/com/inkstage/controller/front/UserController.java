package com.inkstage.controller.front;

import com.inkstage.annotation.UserAccess;
import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.dto.front.UserProfileDTO;
import com.inkstage.entity.model.User;
import com.inkstage.service.UserService;
import com.inkstage.utils.UserContext;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 获取当前用户个人资料
     *
     * @return 用户个人资料
     */
    @GetMapping("/profile")
    @UserAccess
    public Result<User> getProfile() {
        // 从UserContext中获取当前用户ID
        Long userId = UserContext.getCurrentUserId();
        User user = userService.getUserProfile(userId);
        return Result.success(user, ResponseMessage.SUCCESS);
    }

    /**
     * 更新当前用户个人资料
     *
     * @param userProfileDTO 用户个人资料DTO
     * @return 更新结果
     */
    @PutMapping("/profile")
    @UserAccess
    public Result<User> updateProfile(@RequestBody @Valid UserProfileDTO userProfileDTO) {
        log.info("更新个人资料DTO: {}", userProfileDTO);
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
        // 重新获取包含完整URL的用户信息
        return Result.success(updatedUser, ResponseMessage.UPDATE_SUCCESS);
    }

    /**
     * 获取指定用户的详细信息（公开信息）
     *
     * @param userId 用户ID
     * @return 用户详细信息
     */
    @GetMapping("/profile/{userId}")
    public Result<User> getUserProfile(@PathVariable Long userId) {
        User user = userService.getUserProfile(userId);
        return Result.success(user, ResponseMessage.SUCCESS);
    }

    /**
     * 修改用户名
     *
     * @param newUsername 新用户名
     * @return 更新结果
     */
    @PutMapping("/username")
    @UserAccess
    public Result<User> updateUsername(@RequestParam @Valid String newUsername) {
        log.info("修改用户名, 新用户名: {}", newUsername);
        Long userId = UserContext.getCurrentUserId();
        User updatedUser = userService.updateUsername(userId, newUsername);
        return Result.success(updatedUser, ResponseMessage.UPDATE_SUCCESS);
    }

    /**
     * 获取修改用户名的剩余时间
     *
     * @return 剩余时间（毫秒），-1表示可以修改
     */
    @GetMapping("/username/modification-time-left")
    @UserAccess
    public Result<Long> getUsernameModificationTimeLeft() {
        Long userId = UserContext.getCurrentUserId();
        long timeLeft = userService.getUsernameModificationTimeLeft(userId);
        return Result.success(timeLeft, ResponseMessage.SUCCESS);
    }

}
