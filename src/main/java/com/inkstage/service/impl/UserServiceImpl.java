package com.inkstage.service.impl;

import com.inkstage.dto.admin.AdminUserQueryDTO;
import com.inkstage.entity.model.Role;
import com.inkstage.entity.model.UserRole;
import com.inkstage.enums.StatusEnum;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.exception.BusinessException;
import com.inkstage.common.PageResult;
import com.inkstage.common.ResponseMessage;
import com.inkstage.service.FileService;
import com.inkstage.utils.IPUtil;
import com.inkstage.utils.RedisUtil;
import com.inkstage.constant.InkConstant;
import com.inkstage.constant.RedisKeyConstants;
import com.inkstage.entity.model.User;
import com.inkstage.enums.DeleteStatus;
import com.inkstage.enums.user.Gender;
import com.inkstage.enums.user.UserStatus;
import com.inkstage.enums.VerificationStatus;
import com.inkstage.enums.VisibleStatus;
import com.inkstage.mapper.UserMapper;
import com.inkstage.mapper.UserRoleMapper;
import com.inkstage.mapper.RoleMapper;
import com.inkstage.service.UserService;
import com.inkstage.vo.front.HotUserVO;
import com.inkstage.vo.admin.AdminUserDetailVO;
import com.inkstage.vo.admin.AdminUserListVO;
import com.inkstage.vo.admin.AdminUserArticleVO;
import com.inkstage.vo.admin.AdminUserCommentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final FileService fileService;
    private final RedisUtil redisUtil;

    @Override
    public boolean isUsernameExists(String username) {
        User user = userMapper.selectByUsername(username);
        return user != null;
    }

    @Override
    public boolean isEmailExists(String email) {
        User user = userMapper.selectByEmail(email);
        return user != null;
    }

    @Override
    public boolean isPhoneExists(String phone) {
        User user = userMapper.selectByPhone(phone);
        return user != null;
    }

    @Override
    public User createUser(User user) {
        try {
            String username = user.getUsername();

            // 检查用户名是否已存在
            if (isUsernameExists(username)) {
                log.warn("用户名已存在: {}", username);
                throw new BusinessException(ResponseMessage.USERNAME_EXISTS);
            }

            // 检查邮箱是否已存在
            if (user.getEmail() != null && isEmailExists(user.getEmail())) {
                log.warn("邮箱已存在: {}", user.getEmail());
                throw new BusinessException(ResponseMessage.EMAIL_EXISTS);
            }

            // 检查手机号是否已存在
            if (user.getPhone() != null && isPhoneExists(user.getPhone())) {
                log.warn("手机号已存在: {}", user.getPhone());
                throw new BusinessException(ResponseMessage.PHONE_EXISTS);
            }

            // 设置用户默认值
            LocalDateTime now = LocalDateTime.now();
            user.setCreateTime(now);
            user.setUpdateTime(now);
            user.setAvatar(InkConstant.DEFAULT_AVATAR_URL);
            user.setGender(Gender.UNKNOWN);
            user.setNickname(user.getUsername());
            user.setEmailVerified(VerificationStatus.UNVERIFIED);
            user.setPhoneVerified(VerificationStatus.UNVERIFIED);
            user.setArticleCount(0);
            user.setCommentCount(0);
            user.setFollowCount(0);
            user.setFollowerCount(0);
            user.setLikeCount(0);
            user.setPrivacy(VisibleStatus.PUBLIC);
            user.setStatus(UserStatus.NORMAL);
            user.setLastLoginIp(IPUtil.getClientIp());
            user.setRegisterIp(IPUtil.getClientIp());
            user.setRegisterTime(now);
            user.setDeleted(DeleteStatus.NOT_DELETED);

            // 插入用户
            userMapper.insert(user);
            log.info("用户创建成功: {}", user.getUsername());
            return user;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户创建失败: {}", e.getMessage(), e);
            throw new BusinessException(ResponseMessage.REGISTER_FAILED);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    @Override
    public User getUserByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    @Override
    public User updateUser(User user) {
        try {
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateByPrimaryKeySelective(user);

            // 清除用户信息缓存
            String cacheKey = RedisKeyConstants.buildUserKey(user.getId());
            redisUtil.delete(cacheKey);
            log.info("清除用户信息缓存, 缓存键: {}", cacheKey);

            log.info("用户更新成功: {}", user.getId());
            return user;
        } catch (Exception e) {
            log.error("用户更新失败: {}", e.getMessage(), e);
            throw new BusinessException(ResponseMessage.ERROR);
        }
    }

    @Override
    public User getUserById(Long id) {
        // 生成缓存键
        String cacheKey = RedisKeyConstants.buildUserKey(id);

        // 尝试从缓存获取
        User user = redisUtil.get(cacheKey, User.class);
        if (user != null) {
            log.info("从缓存获取用户信息成功, 缓存键: {}", cacheKey);
            return user;
        }

        // 从数据库获取
        user = userMapper.selectByPrimaryKey(id);
        if (user != null) {
            // 确保用户头像和封面图的URL是完整的
            fileService.ensureUserImgIsFullUrl(user);

            // 更新缓存
            redisUtil.set(cacheKey, user, 2, TimeUnit.HOURS);
            log.info("更新用户信息缓存, 缓存键: {}", cacheKey);
        }
        return user;
    }

    @Override
    public AdminUserDetailVO getUserDetailById(Long id) {
        log.info("根据ID获取用户详情: {}", id);
        try {
            // 获取用户信息
            User user = getUserById(id);
            if (user == null) {
                throw new BusinessException(ResponseMessage.USER_NOT_FOUND);
            }

            // 转换为AdminUserDetailVO
            AdminUserDetailVO vo = new AdminUserDetailVO();
            vo.setId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setEmail(user.getEmail());
            vo.setEmailVerified(user.getEmailVerified());
            vo.setPhone(user.getPhone());
            vo.setPhoneVerified(user.getPhoneVerified());
            vo.setAvatar(fileService.convertToFullUrl(user.getAvatar()));
            vo.setSignature(user.getSignature());
            vo.setGender(user.getGender());
            vo.setLocation(user.getLocation());
            vo.setWebsite(user.getWebsite());
            vo.setFollowCount(user.getFollowCount());
            vo.setFollowerCount(user.getFollowerCount());
            vo.setArticleCount(user.getArticleCount());
            vo.setCommentCount(user.getCommentCount());
            vo.setLikeCount(user.getLikeCount());
            vo.setLastLoginTime(user.getLastLoginTime());
            vo.setLastLoginIp(user.getLastLoginIp());
            vo.setRegisterIp(user.getRegisterIp());
            vo.setRegisterTime(user.getRegisterTime());
            vo.setPrivacy(user.getPrivacy());
            vo.setStatus(user.getStatus());

            // 获取用户角色
            List<UserRole> userRoles = userRoleMapper.selectByUserId(id);
            if (!userRoles.isEmpty()) {
                vo.setRole(UserRoleEnum.fromCode(userRoles.getFirst().getRoleId()));
            }

            // 获取最近发布的文章
            List<AdminUserArticleVO> recentArticles = userMapper.selectRecentArticles(id, 5);
            vo.setRecentArticles(recentArticles);

            // 获取最近发布的评论
            List<AdminUserCommentVO> recentComments = userMapper.selectRecentComments(id, 5);
            vo.setRecentComments(recentComments);

            log.info("获取用户 {} 详情成功: {}", id, vo);
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取用户详情失败: {}", id, e);
            throw new BusinessException(ResponseMessage.ERROR, e.getMessage());
        }
    }

    @Override
    public List<HotUserVO> getHotUsers(Integer limit) {
        try {
            log.info("获取热门用户, limit: {}", limit);

            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "user:hot",
                    limit.toString()
            );

            // 尝试从缓存获取
            List<HotUserVO> hotUsers = redisUtil.getWithType(cacheKey, new TypeReference<>() {
            });
            if (hotUsers != null) {
                log.info("从缓存获取热门用户成功, 缓存键: {}", cacheKey);
                return hotUsers;
            }

            // 查询热门用户
            // 这里简化处理，实际项目中应根据粉丝数、文章数、获赞数等综合排序
            List<User> users = userMapper.selectHotUsers(limit);

            // 转换为HotUserVO
            hotUsers = users.stream().map(user -> {
                HotUserVO vo = new HotUserVO();
                vo.setId(user.getId());
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
                vo.setArticleCount(user.getArticleCount());
                vo.setFollowerCount(user.getFollowerCount());
                vo.setLikeCount(user.getLikeCount());
                return vo;
            }).toList();

            fileService.ensureHotUserImgAreFullUrl(hotUsers);

            // 更新缓存
            redisUtil.set(cacheKey, hotUsers, 30, TimeUnit.MINUTES);
            log.info("更新热门用户缓存, 缓存键: {}", cacheKey);

            log.info("获取热门用户成功, 数量: {}", hotUsers.size());
            return hotUsers;
        } catch (Exception e) {
            log.error("获取热门用户失败, limit: {}", limit, e);
            throw new BusinessException(ResponseMessage.ERROR, e.getMessage());
        }
    }

    @Override
    public PageResult<AdminUserListVO> getUsersByPage(AdminUserQueryDTO userQueryDTO) {
        log.info("分页获取用户，页码：{}，每页大小：{}，关键词：{}，角色：{}，状态：{}",
                userQueryDTO.getPageNum(), userQueryDTO.getPageSize(),
                userQueryDTO.getKeyword(), userQueryDTO.getUserRole(), userQueryDTO.getStatus());
        try {
            // 获取总记录数
            Long total = userMapper.countAll(
                    userQueryDTO.getKeyword(),
                    userQueryDTO.getUserRole(),
                    userQueryDTO.getStatus(),
                    userQueryDTO.getStartDate(),
                    userQueryDTO.getEndDate()
            );

            // 获取分页数据
            List<User> users = userMapper.selectByPage(
                    userQueryDTO.getOffset(),
                    userQueryDTO.getPageSize(),
                    userQueryDTO.getKeyword(),
                    userQueryDTO.getUserRole(),
                    userQueryDTO.getStatus(),
                    userQueryDTO.getStartDate(),
                    userQueryDTO.getEndDate()
            );

            // 转换为AdminUserListVO
            List<AdminUserListVO> userListVOs = users.stream().map(user -> {
                AdminUserListVO vo = new AdminUserListVO();
                vo.setId(user.getId());
                vo.setUsername(user.getUsername());
                vo.setNickname(user.getNickname());
                vo.setEmail(user.getEmail());
                vo.setPhone(user.getPhone());
                vo.setStatus(user.getStatus());
                vo.setRegisterTime(user.getRegisterTime());
                vo.setLastLoginTime(user.getLastLoginTime());
                vo.setArticleCount(user.getArticleCount());
                vo.setCommentCount(user.getCommentCount());

                // 获取用户角色
                List<UserRole> userRoles = userRoleMapper.selectByUserId(user.getId());
                if (!userRoles.isEmpty()) {
                    Role role = roleMapper.selectByPrimaryKey(userRoles.getFirst().getRoleId());
                    if (role != null) {
                        vo.setRole(UserRoleEnum.valueOf(role.getCode()));
                    }
                }

                return vo;
            }).toList();
            log.info("userListVOs: {}", userListVOs);

            // 构建分页结果
            return PageResult.build(userListVOs, total, userQueryDTO.getPageNum(), userQueryDTO.getPageSize());
        } catch (Exception e) {
            log.error("分页获取用户失败", e);
            throw new BusinessException("分页获取用户失败", e);
        }
    }

    @Override
    public void deleteUser(Long id) {
        log.info("删除用户: {}", id);
        try {
            if (id == null) {
                throw new BusinessException(ResponseMessage.PARAM_ERROR);
            }
            userMapper.deleteById(id);

            // 清除用户缓存
            String cacheKey = RedisKeyConstants.buildUserKey(id);
            redisUtil.delete(cacheKey);
            log.info("清除用户缓存, 缓存键: {}", cacheKey);
        } catch (Exception e) {
            log.error("删除用户失败", e);
            throw new BusinessException("删除用户失败", e);
        }
    }

    @Override
    public void updateUserDetail(Long id, AdminUserDetailVO userDetailVO) {
        log.info("更新用户详情: {}, {}", id, userDetailVO);
        try {
            // 获取原用户信息
            User user = getUserById(id);
            if (user == null) {
                throw new BusinessException(ResponseMessage.USER_NOT_FOUND);
            }

            // 更新用户基本信息
            user.setNickname(userDetailVO.getNickname());
            user.setEmail(userDetailVO.getEmail());
            user.setPhone(userDetailVO.getPhone());
            user.setSignature(userDetailVO.getSignature());
            user.setGender(userDetailVO.getGender());
            user.setLocation(userDetailVO.getLocation());
            user.setWebsite(userDetailVO.getWebsite());
            user.setPrivacy(userDetailVO.getPrivacy());
            user.setStatus(userDetailVO.getStatus());
            user.setUpdateTime(LocalDateTime.now());

            // 保存用户基本信息
            userMapper.updateByPrimaryKeySelective(user);

            // 处理角色更新
            if (userDetailVO.getRole() != null) {
                // 删除原有的用户角色关联
                userRoleMapper.deleteByUserId(id);

                // 创建新的用户角色关联
                UserRole userRole = new UserRole();
                userRole.setUserId(id);
                userRole.setRoleId(userDetailVO.getRole().getCode());
                userRole.setCreateTime(LocalDateTime.now());
                userRole.setStatus(StatusEnum.ENABLED);
                userRole.setDeleted(DeleteStatus.NOT_DELETED);
                userRoleMapper.insert(userRole);

                log.info("更新用户角色成功: {} -> {}", id, userDetailVO.getRole());
            }

            // 清除用户信息缓存
            String cacheKey = RedisKeyConstants.buildUserKey(id);
            redisUtil.delete(cacheKey);
            log.info("清除用户信息缓存, 缓存键: {}", cacheKey);

            log.info("用户详情更新成功: {}", id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新用户详情失败: {}", id, e);
            throw new BusinessException(ResponseMessage.ERROR, e.getMessage());
        }
    }

    @Override
    public Boolean updateUserStatus(Long id, UserStatus userStatus) {
        int result = userMapper.updateUserStatus(id, userStatus);
        // 更新缓存
        return result > 0;
    }

}