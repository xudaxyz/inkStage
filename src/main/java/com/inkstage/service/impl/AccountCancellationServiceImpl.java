package com.inkstage.service.impl;

import com.inkstage.cache.service.CacheClearService;
import com.inkstage.common.ResponseMessage;
import com.inkstage.constant.InkConstant;
import com.inkstage.entity.model.AccountCancellation;
import com.inkstage.entity.model.User;
import com.inkstage.entity.model.UserAuth;
import com.inkstage.enums.CancellationStatus;
import com.inkstage.enums.auth.AuthType;
import com.inkstage.enums.common.DeleteStatus;
import com.inkstage.enums.user.UserStatus;
import com.inkstage.exception.BusinessException;
import com.inkstage.mapper.*;
import com.inkstage.service.AccountCancellationService;
import com.inkstage.service.TokenStoreService;
import com.inkstage.service.UserProfileService;
import com.inkstage.utils.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 账号注销服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCancellationServiceImpl implements AccountCancellationService {

    private final UserProfileService userProfileService;
    private final UserAuthMapper userAuthMapper;
    private final UserMapper userMapper;
    private final AccountCancellationMapper accountCancellationMapper;
    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    private final ColumnMapper columnMapper;
    private final ArticleLikeMapper articleLikeMapper;
    private final ArticleCollectionMapper articleCollectionMapper;
    private final FollowMapper followMapper;
    private final TokenStoreService tokenStoreService;
    private final PasswordEncoder passwordEncoder;
    private final CacheClearService cacheClearService;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    @Transactional
    public void deleteAccount(Long userId, String password, Boolean cleanContent, Boolean cleanInteraction) {
        log.info("用户申请注销账号，用户ID: {}", userId);

        if (userId == null || userId <= 0) {
            throw new BusinessException(ResponseMessage.USER_NOT_FOUND);
        }
        if (password == null || password.isEmpty()) {
            throw new BusinessException(ResponseMessage.PASSWORD_REQUIRED);
        }

        User user = userProfileService.getUserById(userId);
        if (user == null) {
            throw new BusinessException(ResponseMessage.USER_NOT_FOUND);
        }

        if (user.getStatus() == UserStatus.PENDING_DELETE) {
            throw new BusinessException(ResponseMessage.ACCOUNT_ALREADY_IN_CANCELLATION);
        }

        UserAuth userAuth = userAuthMapper.findByUserIdAndType(userId, AuthType.USERNAME);
        if (userAuth == null || !passwordEncoder.matches(password, userAuth.getAuthCredential())) {
            throw new BusinessException(ResponseMessage.PASSWORD_ERROR);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduledTime = now.plusDays(InkConstant.ACCOUNT_DELETE_COOLING_DAYS);

        boolean shouldCleanContent = Boolean.TRUE.equals(cleanContent);
        boolean shouldCleanInteraction = Boolean.TRUE.equals(cleanInteraction);

        User userToUpdate = new User();
        userToUpdate.setId(userId);
        userToUpdate.setStatus(UserStatus.PENDING_DELETE);
        userToUpdate.setScheduledDeleteTime(scheduledTime);
        userToUpdate.setUpdateTime(now);
        userProfileService.updateUser(userToUpdate);

        AccountCancellation cancellation = new AccountCancellation();
        cancellation.setId(snowflakeIdGenerator.nextId());
        cancellation.setUserId(userId);
        cancellation.setCleanContent(shouldCleanContent);
        cancellation.setCleanInteraction(shouldCleanInteraction);
        cancellation.setApplyTime(now);
        cancellation.setScheduledTime(scheduledTime);
        cancellation.setStatus(CancellationStatus.PENDING);
        cancellation.setCreateTime(now);
        cancellation.setUpdateTime(now);
        cancellation.setDeleted(DeleteStatus.NOT_DELETED);
        int result = accountCancellationMapper.insert(cancellation);
        log.info("用户{}登记待注销结果: {}", userId, result);


        if (shouldCleanContent) {
            articleMapper.deleteByUserId(userId);
            columnMapper.deleteByUserId(userId);
            commentMapper.deleteByUserId(userId);
            log.info("用户注销-已软删除用户内容，用户ID: {}", userId);
        }

        if (shouldCleanInteraction) {
            articleLikeMapper.deleteByUserId(userId);
            articleCollectionMapper.deleteByUserId(userId);
            log.info("用户注销-已软删除用户互动记录，用户ID: {}", userId);
        }

        followMapper.deleteByFollowerId(userId);
        followMapper.deleteByFollowingId(userId);

        tokenStoreService.revokeAllRefreshTokens(userId);

        log.info("用户申请注销账号成功，用户ID: {}，计划删除时间: {}，清除内容: {}，清除互动: {}",
                userId, scheduledTime, shouldCleanContent, shouldCleanInteraction);
    }

    @Override
    @Transactional
    public void restoreAccount(Long userId) {
        log.info("用户恢复待删除账号，用户ID: {}", userId);

        if (userId == null || userId <= 0) {
            throw new BusinessException(ResponseMessage.USER_NOT_FOUND);
        }

        User user = userProfileService.getUserById(userId);
        if (user == null) {
            throw new BusinessException(ResponseMessage.USER_NOT_FOUND);
        }

        if (user.getStatus() != UserStatus.PENDING_DELETE) {
            throw new BusinessException(ResponseMessage.ACCOUNT_NOT_IN_CANCELLATION);
        }

        AccountCancellation cancellation = accountCancellationMapper.findByUserId(userId);
        LocalDateTime afterTime = null;
        if (cancellation != null) {
            afterTime = cancellation.getApplyTime();
        }

        User userToUpdate = new User();
        userToUpdate.setId(userId);
        userToUpdate.setStatus(UserStatus.NORMAL);
        userToUpdate.setScheduledDeleteTime(null);
        userToUpdate.setUpdateTime(LocalDateTime.now());
        userProfileService.updateUser(userToUpdate);

        if (cancellation != null && afterTime != null) {
            if (Boolean.TRUE.equals(cancellation.getCleanContent())) {
                List<Long> articleIds = articleMapper.findAfterDeletedIdsByUserId(userId, afterTime);
                articleMapper.restoreByUserIdAfterTime(userId, afterTime);
                columnMapper.restoreByUserIdAfterTime(userId, afterTime);
                commentMapper.restoreByUserIdAfterTime(userId, afterTime);
                log.info("恢复用户软删除的内容，用户ID: {}，恢复文章数: {}", userId, articleIds.size());
            }

            if (Boolean.TRUE.equals(cancellation.getCleanInteraction())) {
                articleLikeMapper.restoreByUserIdAfterTime(userId, afterTime);
                articleCollectionMapper.restoreByUserIdAfterTime(userId, afterTime);
                log.info("恢复用户软删除的互动记录，用户ID: {}", userId);
            }

            int cancelled = accountCancellationMapper.purgeByUserId(userId);
            log.info("取消用户 [{}] 注销服务结果: {}", userId, cancelled);
        }

        followMapper.restoreByFollowerIdAfterTime(userId, afterTime);
        followMapper.restoreByFollowingIdAfterTime(userId, afterTime);

        cacheClearService.clearUserCache(userId);
        cacheClearService.clearAllArticleCache();

        log.info("用户恢复待删除账号成功，用户ID: {}", userId);
    }

    @Override
    public int cleanupExpiredAccounts() {
        LocalDateTime now = LocalDateTime.now();
        List<Long> expiredUserIds = userMapper.findExpiredPendingDeleteUserIds(now);

        if (expiredUserIds.isEmpty()) {
            log.debug("没有需要清理的过期待删除账号");
            return 0;
        }

        log.info("发现 {} 个过期待删除账号，开始清理", expiredUserIds.size());
        int successCount = 0;

        for (Long userId : expiredUserIds) {
            try {
                AccountCancellation cancellation = accountCancellationMapper.findByUserId(userId);

                if (cancellation != null && Boolean.TRUE.equals(cancellation.getCleanContent())) {
                    articleMapper.purgeByUserId(userId);
                    columnMapper.purgeByUserId(userId);
                    commentMapper.purgeByUserId(userId);
                    log.info("彻底删除用户内容，用户ID: {}", userId);
                }

                if (cancellation != null && Boolean.TRUE.equals(cancellation.getCleanInteraction())) {
                    LocalDateTime applyTime = cancellation.getApplyTime();
                    List<Long> likedArticleIds = applyTime != null
                            ? articleLikeMapper.findDeletedArticleIdsByUserIdAfterTime(userId, applyTime)
                            : articleLikeMapper.findDeletedArticleIdsByUserId(userId);
                    List<Long> collectedArticleIds = applyTime != null
                            ? articleCollectionMapper.findDeletedArticleIdsByUserIdAfterTime(userId, applyTime)
                            : articleCollectionMapper.findDeletedArticleIdsByUserId(userId);

                    articleLikeMapper.purgeByUserId(userId);
                    articleCollectionMapper.purgeByUserId(userId);

                    for (Long articleId : likedArticleIds) {
                        articleMapper.updateLikeCount(articleId, -1);
                    }
                    for (Long articleId : collectedArticleIds) {
                        articleMapper.updateCollectionCount(articleId, -1);
                    }
                    log.info("彻底删除用户互动记录并修正文章计数，用户ID: {}，点赞文章数: {}，收藏文章数: {}",
                            userId, likedArticleIds.size(), collectedArticleIds.size());
                }

                followMapper.purgeByFollowerId(userId);
                followMapper.purgeByFollowingId(userId);

                tokenStoreService.revokeAllRefreshTokens(userId);
                userAuthMapper.purgeByUserId(userId);

                int cancelled = userMapper.cancelUserAccount(userId);
                log.info("注销用户 {} 账号结果: {}", userId, cancelled);

                if (cancellation != null) {
                    int completed = accountCancellationMapper.updateStatusByUserId(userId, CancellationStatus.COMPLETED);
                    log.info("完成用户[{}]账号注销, 结果:{}", userId, completed);
                }

                cacheClearService.clearUserCache(userId);
                cacheClearService.clearAllArticleCache();

                successCount++;
                log.info("清理过期待删除账号成功，用户ID: {}", userId);
            } catch (Exception e) {
                log.error("清理过期待删除账号失败，用户ID: {}", userId, e);
            }
        }

        log.info("过期待删除账号清理完成，成功: {}/{}", successCount, expiredUserIds.size());
        return successCount;
    }
}
