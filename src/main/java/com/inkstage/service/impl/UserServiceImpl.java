package com.inkstage.service.impl;

import com.inkstage.exception.BusinessException;
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
import com.inkstage.service.UserService;
import com.inkstage.vo.front.HotUserVO;
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
    public List<HotUserVO> getHotUsers(Integer limit) {
        try {
            log.info("获取热门用户, limit: {}", limit);

            // 生成缓存键
            String cacheKey = RedisKeyConstants.buildCacheKey(
                    "user:hot",
                    limit.toString()
            );

            // 尝试从缓存获取
            List<HotUserVO> hotUsers = redisUtil.get(cacheKey, new TypeReference<>() {
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
}