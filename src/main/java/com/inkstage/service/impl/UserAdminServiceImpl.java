package com.inkstage.service.impl;

import com.inkstage.common.PageResult;
import com.inkstage.dto.admin.AdminUserQueryDTO;
import com.inkstage.entity.model.User;
import com.inkstage.enums.user.UserStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.UserMapper;
import com.inkstage.service.FileService;
import com.inkstage.service.NotificationService;
import com.inkstage.service.UserAdminService;
import com.inkstage.enums.NotificationType;
import com.inkstage.vo.admin.AdminUserArticleVO;
import com.inkstage.vo.admin.AdminUserCommentVO;
import com.inkstage.vo.admin.AdminUserDetailVO;
import com.inkstage.vo.admin.AdminUserListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户管理服务实现类（管理员）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {

    private final UserMapper userMapper;
    private final FileService fileService;
    private final NotificationService notificationService;

    @Override
    public AdminUserDetailVO getUserDetailById(Long id) {
        try {
            log.debug("管理员根据ID获取用户详情, 用户ID: {}", id);
            AdminUserDetailVO userDetail = userMapper.findAdminUserDetailById(id);
            if (userDetail == null) {
                log.warn("用户不存在: {}", id);
                throw new BusinessException("用户不存在");
            }
            log.info("管理员根据ID获取用户详情成功, 用户ID: {}", id);
            fileService.ensureAdminUserDetailIsFullUrl(userDetail);
            // 获取用户最近发表的文章
            List<AdminUserArticleVO> recentArticles = userMapper.findRecentArticles(id, 5);
            userDetail.setRecentArticles(recentArticles);
            // 获取用户最近发表的评论
            List<AdminUserCommentVO> recentComments = userMapper.findRecentComments(id, 5);
            userDetail.setRecentComments(recentComments);

            return userDetail;
        } catch (Exception e) {
            log.error("管理员根据ID获取用户详情失败, 用户ID: {}", id, e);
            throw new BusinessException("获取用户详情失败");
        }
    }

    @Override
    public PageResult<AdminUserListVO> getUsersByPage(AdminUserQueryDTO pageRequest) {
        try {
            log.debug("管理员分页获取用户, 页码: {}, 每页大小: {}, 关键词: {}", 
                    pageRequest.getPageNum(), pageRequest.getPageSize(), pageRequest.getKeyword());

            // 计算偏移量
            int offset = (pageRequest.getPageNum() - 1) * pageRequest.getPageSize();
            pageRequest.setOffset(offset);

            // 查询用户列表
            List<AdminUserListVO> userList = userMapper.findAdminUserList(pageRequest);
            // 查询总记录数
            long total = userMapper.countAdminUserList(pageRequest);

            // 构建分页结果
            var pageResult = PageResult.build(
                    userList,
                    total,
                    pageRequest.getPageNum(),
                    pageRequest.getPageSize()
            );

            log.info("管理员分页获取用户成功, 总数: {}, 页码: {}, 每页大小: {}", total, pageRequest.getPageNum(), pageRequest.getPageSize());
            return pageResult;
        } catch (Exception e) {
            log.error("管理员分页获取用户失败, 页码: {}, 每页大小: {}", pageRequest.getPageNum(), pageRequest.getPageSize(), e);
            throw new BusinessException("获取用户列表失败");
        }
    }

    @Override
    public void deleteUser(Long id) {
        try {
            log.debug("管理员删除用户, 用户ID: {}", id);
            // 检查用户是否存在
            var user = userMapper.findById(id);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            // 执行删除
            int result = userMapper.deleteById(id);
            if (result == 0) {
                log.warn("删除用户失败, 用户ID: {}", id);
                throw new BusinessException("删除用户失败");
            }
            log.info("管理员删除用户成功, 用户ID: {}", id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("管理员删除用户失败, 用户ID: {}", id, e);
            throw new BusinessException("删除用户失败");
        }
    }

    @Override
    public void updateUserDetail(Long id, AdminUserDetailVO userDetailVO) {
        try {
            log.debug("管理员更新用户详情, 用户ID: {}", id);
            // 检查用户是否存在
            var user = userMapper.findById(id);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            // 执行更新
            int result = userMapper.updateAdminUserDetail(userDetailVO);
            if (result == 0) {
                log.warn("更新用户详情失败, 用户ID: {}", id);
                throw new BusinessException("更新用户详情失败");
            }
            log.info("管理员更新用户详情成功, 用户ID: {}", id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("管理员更新用户详情失败, 用户ID: {}", id, e);
            throw new BusinessException("更新用户详情失败");
        }
    }

    @Override
    public Boolean updateUserStatus(Long id, UserStatus userStatus) {
        try {
            log.debug("管理员更新用户状态, 用户ID: {}, 状态: {}", id, userStatus.getDesc());
            // 检查用户是否存在
            var user = userMapper.findById(id);
            if (user == null) {
                log.warn("用户不存在, 用户ID: {}", id);
                throw new BusinessException("用户不存在");
            }
            // 执行更新
            int result = userMapper.updateUserStatus(id, userStatus);
            boolean success = result > 0;
            log.info("管理员更新用户状态{}, 用户ID: {}, 新状态: {}", success ? "成功" : "失败", id, userStatus.getDesc());
            return success;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("管理员更新用户状态失败, 用户ID: {}, 状态: {}", id, userStatus.getDesc(), e);
            throw new BusinessException("更新用户状态失败");
        }
    }

    @Override
    public Boolean updateUserStatusWithNotification(Long id, UserStatus userStatus, String reason) {
        try {
            log.debug("管理员更新用户状态并发送通知, 用户ID: {}, 状态: {}, 原因: {}", id, userStatus.getDesc(), reason);
            // 检查用户是否存在
            User user = userMapper.findById(id);
            if (user == null) {
                log.warn("用户不存在, 用户ID: {}", id);
                throw new BusinessException("用户不存在");
            }
            // 执行更新
            int result = userMapper.updateUserStatus(id, userStatus);
            boolean success = result > 0;
            if (success) {
                // 发送通知
                notificationService.sendNotificationWithTemplate(
                        id,
                        NotificationType.USER_STATUS_CHANGE,
                        null,
                        0L, // 系统发送
                        userStatus.getDesc(),
                        reason
                );
                log.info("管理员更新用户状态并发送通知成功, 用户ID: {}, 新状态: {}", id, userStatus.getDesc());
            } else {
                log.warn("更新用户状态失败, 未发送通知, 用户ID: {}", id);
            }
            return success;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("管理员更新用户状态并发送通知失败, 用户ID: {}, 状态: {}", id, userStatus.getDesc(), e);
            throw new BusinessException("更新用户状态失败");
        }
    }

    @Override
    public List<Long> getUserIdsByRoleCode(String roleCode) {
        return userMapper.findUserIdsByRoleCode(roleCode);
    }

    @Override
    public List<Long> getAllUserIds() {
        return userMapper.findAllUserIds();
    }
}