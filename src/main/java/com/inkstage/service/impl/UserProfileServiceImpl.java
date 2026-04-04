package com.inkstage.service.impl;

import com.inkstage.cache.service.CacheClearService;
import com.inkstage.entity.model.User;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.UserMapper;
import com.inkstage.service.FileService;
import com.inkstage.cache.service.UserCacheService;
import com.inkstage.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户资料服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserMapper userMapper;
    private final FileService fileService;
    private final UserCacheService userCacheService;
    private final CacheClearService cacheClearService;

    @Override
    public User getUserById(Long id) {
        try {
            log.debug("根据ID获取用户, 用户ID: {}", id);
            var user = userMapper.findById(id);
            if (user == null) {
                log.warn("用户不存在, 用户ID: {}", id);
                throw new BusinessException("用户不存在");
            }
            log.info("根据ID获取用户成功, 用户ID: {}", id);
            return user;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据ID获取用户失败, 用户ID: {}", id, e);
            throw new BusinessException("获取用户信息失败");
        }
    }

    @Override
    public User updateUser(User user) {
        try {
            log.debug("更新用户信息, 用户ID: {}", user.getId());
            // 检查用户是否存在
            User existingUser = userMapper.findById(user.getId());
            if (existingUser == null) {
                log.warn("用户ID: {}不存在!", user.getId());
                throw new BusinessException("用户不存在");
            }
            // 递增版本号
            user.setUserVersion(existingUser.getUserVersion() + 1);
            
            // 执行更新
            int result = userMapper.updateByPrimaryKeySelective(user);
            if (result == 0) {
                log.warn("更新用户信息失败, 用户ID: {}", user.getId());
                throw new BusinessException("更新用户信息失败");
            }
            // 重新查询更新后的用户
            User updatedUser = userMapper.findById(user.getId());
            fileService.ensureUserImgIsFullUrl(updatedUser);
            // 更新缓存
            userCacheService.updateUserCache(updatedUser);
            // 清除文章列表相关缓存，因为文章列表包含用户信息
            if (user.getAvatar() != null) {
                cacheClearService.clearArticleListCache();
                cacheClearService.clearHotArticleCache();
                cacheClearService.clearLatestArticleCache();
                cacheClearService.clearBannerArticleCache();
                cacheClearService.clearUserArticleCache(user.getId());
            }
            log.info("更新用户信息成功, 用户ID: {}", user.getId());
            return updatedUser;
        } catch (Exception e) {
            log.error("更新用户信息失败, 用户ID: {}", user.getId(), e);
            throw new BusinessException("更新用户信息失败");
        }
    }

    @Override
    public User getUserByUsername(String username) {
        try {
            log.debug("根据用户名获取用户, 用户名: {}", username);
            var user = userMapper.findByUsername(username);
            log.info("根据用户名获取用户成功, 用户名: {}", username);
            return user;
        } catch (Exception e) {
            log.error("根据用户名获取用户失败, 用户名: {}", username, e);
            throw new BusinessException("获取用户信息失败");
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            log.debug("根据邮箱获取用户, 邮箱: {}", email);
            var user = userMapper.findByEmail(email);
            log.info("根据邮箱获取用户成功, 邮箱: {}", email);
            return user;
        } catch (Exception e) {
            log.error("根据邮箱获取用户失败, 邮箱: {}", email, e);
            throw new BusinessException("获取用户信息失败");
        }
    }

    @Override
    public User getUserByPhone(String phone) {
        try {
            log.debug("根据手机号获取用户, 手机号: {}", phone);
            var user = userMapper.findByPhone(phone);
            log.info("根据手机号获取用户成功, 手机号: {}", phone);
            return user;
        } catch (Exception e) {
            log.error("根据手机号获取用户失败, 手机号: {}", phone, e);
            throw new BusinessException("获取用户信息失败");
        }
    }

    @Override
    public User getUserProfile(Long id) {
        try {
            log.debug("获取用户资料, 用户ID: {}", id);
            User user = userMapper.findById(id);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            // 确保用户头像和封面图的URL是完整的
            fileService.ensureUserImgIsFullUrl(user);
            log.info("获取用户资料成功, 用户ID: {}", id);
            return user;
        } catch (Exception e) {
            log.error("获取用户资料失败, 用户ID: {}", id, e);
            throw new BusinessException("获取用户资料失败");
        }
    }
}