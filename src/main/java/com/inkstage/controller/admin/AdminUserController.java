package com.inkstage.controller.admin;

import com.inkstage.common.PageResult;
import com.inkstage.common.Result;
import com.inkstage.dto.admin.AdminUserQueryDTO;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.enums.user.UserStatus;
import com.inkstage.service.UserRoleService;
import com.inkstage.service.UserService;
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
    @PostMapping("/all")
    public Result<PageResult<AdminUserListVO>> getUsersByPage(@RequestBody AdminUserQueryDTO userQueryDTO) {
        log.info("查询用户参数: {}", userQueryDTO);
        PageResult<AdminUserListVO> pageResult = userService.getUsersByPage(userQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 响应结果
     */
    @GetMapping("/{id}")
    public Result<AdminUserDetailVO> getUserById(@PathVariable Long id) {
        AdminUserDetailVO userDetailVO = userService.getUserDetailById(id);
        return Result.success(userDetailVO);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 响应结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 更新用户
     *
     * @param id           用户ID
     * @param userDetailVO 用户详情
     * @return 响应结果
     */
    @PutMapping("/{id}")
    public Result<AdminUserDetailVO> updateUser(@PathVariable Long id, @RequestBody AdminUserDetailVO userDetailVO) {
        userService.updateUserDetail(id, userDetailVO);
        AdminUserDetailVO updatedUser = userService.getUserDetailById(id);
        return Result.success(updatedUser);
    }

    /**
     * 更新用户状态
     *
     * @param id         用户ID
     * @param userStatus 用户状态
     * @return 响应结果
     */
    @PutMapping("/update-status/{id}")
    public Result<?> updateUserStatus(@PathVariable Long id, @RequestBody UserStatus userStatus) {
        Boolean result = userService.updateUserStatus(id, userStatus);
        if (result) {
            return Result.success("更新用户状态成功");
        } else {
            return Result.error("更新用户状态失败");
        }
    }

    /**
     * 更新用角色
     *
     * @param id       用户ID
     * @param userRole 用户角色
     * @return 响应结果
     */
    @PutMapping("/update-role/{id}")
    public Result<?> updateUserRole(@PathVariable Long id, @RequestBody UserRoleEnum userRole) {
        Boolean result = userRoleService.updateUserRole(id, userRole);
        if (result) {
            return Result.success("更新用户角色成功");
        } else {
            return Result.error("更新用户角色失败");
        }
    }

}