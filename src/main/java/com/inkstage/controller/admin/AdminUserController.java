package com.inkstage.controller.admin;

import com.inkstage.annotation.AdminAccess;
import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.dto.admin.AdminUserQueryDTO;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.enums.user.UserStatus;
import com.inkstage.service.UserRoleService;
import com.inkstage.service.UserService;
import com.inkstage.utils.UserContext;
import com.inkstage.vo.UserInfo;
import com.inkstage.vo.admin.AdminUserDetailVO;
import com.inkstage.vo.admin.AdminUserListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 后台用户Controller
 */
@Slf4j
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final UserRoleService userRoleService;

    /**
     * 分页获取用户
     *
     * @param userQueryDTO 分页请求
     * @return 响应结果
     */
    @PostMapping("/list")
    @AdminAccess
    public Result<PageResult<AdminUserListVO>> getUsersByPage(@RequestBody AdminUserQueryDTO userQueryDTO) {
        log.info("查询用户参数: {}", userQueryDTO);
        PageResult<AdminUserListVO> pageResult = userService.getUsersByPage(userQueryDTO);
        return Result.success(pageResult, ResponseMessage.USER_LIST_SUCCESS);
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 响应结果
     */
    @GetMapping("/detail/{id}")
    @AdminAccess
    public Result<AdminUserDetailVO> getUserById(@PathVariable Long id) {
        log.info("获取用户详情, 用户ID: {}", id);
        AdminUserDetailVO userDetailVO = userService.getUserDetailById(id);
        return Result.success(userDetailVO, ResponseMessage.USER_DETAIL_SUCCESS);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 响应结果
     */
    @DeleteMapping("/delete/{id}")
    @AdminAccess
    public Result<Void> deleteUser(@PathVariable Long id) {
        log.info("删除用户, 用户ID: {}", id);
        userService.deleteUser(id);
        return Result.success(ResponseMessage.USER_DELETE_SUCCESS);
    }

    /**
     * 更新用户
     *
     * @param id           用户ID
     * @param userDetailVO 用户详情
     * @return 响应结果
     */
    @PutMapping("/update/{id}")
    @AdminAccess
    public Result<AdminUserDetailVO> updateUser(@PathVariable Long id, @RequestBody AdminUserDetailVO userDetailVO) {
        log.info("更新用户, 用户ID: {}, 用户详情: {}", id, userDetailVO);
        userService.updateUserDetail(id, userDetailVO);
        AdminUserDetailVO updatedUser = userService.getUserDetailById(id);
        return Result.success(updatedUser, ResponseMessage.USER_UPDATE_SUCCESS);
    }

    /**
     * 更新用户状态
     *
     * @param id         用户ID
     * @param userStatus 用户状态
     * @return 响应结果
     */
    @PutMapping("/update-status/{id}")
    @AdminAccess
    public Result<?> updateUserStatus(@PathVariable Long id, @RequestBody UserStatus userStatus) {
        log.info("更新用户状态, 用户ID: {}, 状态: {}", id, userStatus);
        Boolean result = userService.updateUserStatus(id, userStatus);
        if (result) {
            return Result.success(ResponseMessage.USER_STATUS_UPDATE_SUCCESS);
        } else {
            return Result.error(ResponseMessage.UPDATE_FAILED);
        }
    }

    /**
     * 更新用户角色
     *
     * @param id       用户ID
     * @param userRole 用户角色
     * @return 响应结果
     */
    @PutMapping("/update-role/{id}")
    @AdminAccess
    public Result<?> updateUserRole(@PathVariable Long id, @RequestBody UserRoleEnum userRole) {
        log.info("更新用户角色, 用户ID: {}, 角色: {}", id, userRole);
        Boolean result = userRoleService.updateUserRole(id, userRole);
        if (result) {
            return Result.success(ResponseMessage.USER_ROLE_UPDATE_SUCCESS);
        } else {
            return Result.error(ResponseMessage.UPDATE_FAILED);
        }
    }

    /**
     * 获取当前管理员个人资料
     *
     * @return 管理员个人资料
     */
    @GetMapping("/profile")
    @AdminAccess
    public Result<UserInfo> getAdminProfile() {
        log.info("管理员获取个人资料");
        Long userId = UserContext.getCurrentUserId();
        UserInfo userInfo = userService.getUserProfile(userId);
        return Result.success(userInfo, ResponseMessage.SUCCESS);
    }

}