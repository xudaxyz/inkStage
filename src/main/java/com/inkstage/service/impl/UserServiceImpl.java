package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.dto.admin.AdminUserQueryDTO;
import com.inkstage.entity.model.User;
import com.inkstage.enums.user.UserStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.service.UserService;
import com.inkstage.service.UserAdminService;
import com.inkstage.service.UserProfileService;
import com.inkstage.service.UserRegistrationService;
import com.inkstage.service.UserStatsService;
import com.inkstage.vo.admin.AdminUserDetailVO;
import com.inkstage.vo.admin.AdminUserListVO;
import com.inkstage.vo.front.HotUserVO;
import com.inkstage.vo.front.UserPublicProfileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRegistrationService userRegistrationService;
    private final UserProfileService userProfileService;
    private final UserAdminService userAdminService;
    private final UserStatsService userStatsService;

    @Override
    public boolean isUsernameExists(String username) {
        if (username == null || username.isEmpty()) {
            log.warn("检查用户名是否存在参数为空");
            return false;
        }
        return userRegistrationService.isUsernameExists(username);
    }

    @Override
    public boolean isEmailExists(String email) {
        if (email == null || email.isEmpty()) {
            log.warn("检查邮箱是否存在参数为空");
            return false;
        }
        return userRegistrationService.isEmailExists(email);
    }

    @Override
    public boolean isPhoneExists(String phone) {
        if (phone == null || phone.isEmpty()) {
            log.warn("检查手机号是否存在参数为空");
            return false;
        }
        return userRegistrationService.isPhoneExists(phone);
    }

    @Override
    public User createUser(User user) {
        if (user == null) {
            log.warn("创建用户参数为空");
            return null;
        }
        return userRegistrationService.createUser(user);
    }
    
    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            log.warn("根据用户名获取用户参数为空");
            return null;
        }
        return userProfileService.getUserByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            log.warn("根据邮箱获取用户参数为空");
            return null;
        }
        return userProfileService.getUserByEmail(email);
    }

    @Override
    public User getUserByPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            log.warn("根据手机号获取用户参数为空");
            return null;
        }
        return userProfileService.getUserByPhone(phone);
    }

    @Override
    public User updateUser(User user) {
        if (user == null || user.getId() == null) {
            log.warn("更新用户参数为空或用户ID为空");
            return null;
        }
        return userProfileService.updateUser(user);
    }

    @Override
    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            log.warn("根据ID获取用户参数无效, 用户ID: {}", id);
            return null;
        }
        return userProfileService.getUserById(id);
    }

    @Override
    public AdminUserDetailVO getUserDetailById(Long id) {
        if (id == null || id <= 0) {
            log.warn("根据ID获取用户详情参数无效, 用户ID: {}", id);
            return null;
        }
        return userAdminService.getUserDetailById(id);
    }

    @Override
    public List<HotUserVO> getHotUsers(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return userStatsService.getHotUsers(limit);
    }

    @Override
    public PageResult<AdminUserListVO> getUsersByPage(AdminUserQueryDTO userQueryDTO) {
        return userAdminService.getUsersByPage(userQueryDTO);
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            log.warn("删除用户参数无效, 用户ID: {}", id);
            return;
        }
        userAdminService.deleteUser(id);
    }

    @Override
    public void updateUserDetail(Long id, AdminUserDetailVO userDetailVO) {
        if (id == null || id <= 0 || userDetailVO == null) {
            log.warn("更新用户详情参数无效, 用户ID: {}", id);
            return;
        }
        userAdminService.updateUserDetail(id, userDetailVO);
    }

    @Override
    public Boolean updateUserStatus(Long id, UserStatus userStatus) {
        if (id == null || id <= 0 || userStatus == null) {
            log.warn("更新用户状态参数无效, 用户ID: {}, 状态: {}", id, userStatus);
            return false;
        }
        return userAdminService.updateUserStatus(id, userStatus);
    }

    @Override
    public User getUserProfile(Long id) {
        if (id == null || id <= 0) {
            log.warn("获取用户资料参数无效, 用户ID: {}", id);
            return null;
        }
        return userProfileService.getUserProfile(id);
    }

    @Override
    public UserPublicProfileVO getUserPublicProfile(Long id) {
        if (id == null || id <= 0) {
            log.warn("获取用户公开资料参数无效, 用户ID: {}", id);
            return null;
        }
        User user = userProfileService.getUserProfile(id);
        if (user == null) {
            log.warn("用户不存在, ID: {}", id);
            return null;
        }
        
        // 构建用户公开资料VO
        UserPublicProfileVO publicProfileVO = new UserPublicProfileVO();
        publicProfileVO.setId(user.getId());
        publicProfileVO.setNickname(user.getNickname());
        publicProfileVO.setAvatar(user.getAvatar());
        publicProfileVO.setCoverImage(user.getCoverImage());
        publicProfileVO.setSignature(user.getSignature());
        publicProfileVO.setGender(user.getGender());
        publicProfileVO.setLocation(user.getLocation());
        publicProfileVO.setWebsite(user.getWebsite());
        publicProfileVO.setRegisterTime(user.getRegisterTime());
        publicProfileVO.setArticleCount(user.getArticleCount());
        publicProfileVO.setCommentCount(user.getCommentCount());
        publicProfileVO.setLikeCount(user.getLikeCount());
        publicProfileVO.setFollowCount(user.getFollowCount());
        publicProfileVO.setFollowerCount(user.getFollowerCount());
        publicProfileVO.setStatus(user.getStatus());
        
        return publicProfileVO;
    }

    @Override
    public User updateUsername(Long userId, String newUsername) {
        if (userId == null || userId <= 0) {
            log.warn("修改用户名参数无效, 用户ID: {}", userId);
            throw new BusinessException("用户不存在");
        }
        if (newUsername == null || newUsername.isEmpty()) {
            log.warn("修改用户名参数无效, 新用户名为空");
            throw new BusinessException("用户名不能为空");
        }

        // 检查用户是否在一个月内已修改过用户名
        long timeLeft = getUsernameModificationTimeLeft(userId);
        if (timeLeft > 0) {
            log.warn("用户在冷却期内, 用户ID: {}", userId);
            throw new BusinessException("一个月内只能修改一次用户名");
        }

        newUsername = newUsername.trim();
        // 检查新用户名是否已存在
        if (isUsernameExists(newUsername)) {
            log.warn("用户名已存在, 用户名: {}", newUsername);
            throw new BusinessException("用户名已存在");
        }

        // 更新用户名和修改时间
        User user = new User();
        user.setId(userId);
        user.setUsername(newUsername);
        user.setUsernameLastModifiedTime(LocalDateTime.now());

        return userProfileService.updateUser(user);
    }

    @Override
    public long getUsernameModificationTimeLeft(Long userId) {
        if (userId == null || userId <= 0) {
            log.warn("获取修改时间剩余参数无效, 用户ID: {}", userId);
            return -1;
        }

        User user = userProfileService.getUserById(userId);
        if (user == null) {
            log.warn("用户不存在, 用户ID: {}", userId);
            return -1;
        }

        // 如果用户从未修改过用户名，允许修改
        if (user.getUsernameLastModifiedTime() == null) {
            return -1;
        }

        // 计算距离上次修改的时间差（毫秒）
        LocalDateTime lastModified = user.getUsernameLastModifiedTime();
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(lastModified, now);
        long milliseconds = duration.toMillis();

        // 一个月的毫秒数（30天）
        long oneMonth = 30L * 24L * 60L * 60L * 1000L;

        // 如果距离上次修改不足一个月，返回剩余时间
        if (milliseconds < oneMonth) {
            return oneMonth - milliseconds;
        }

        // 否则允许修改
        return -1;
    }
}