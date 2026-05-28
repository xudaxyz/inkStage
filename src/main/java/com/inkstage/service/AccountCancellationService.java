package com.inkstage.service;

public interface AccountCancellationService {

    /**
     * 申请注销账号
     *
     * @param userId          用户ID
     * @param password        当前密码
     * @param cleanContent    是否清除内容（文章/专栏/评论）
     * @param cleanInteraction 是否清除互动记录（点赞/收藏）
     */
    void deleteAccount(Long userId, String password, Boolean cleanContent, Boolean cleanInteraction);

    /**
     * 恢复待删除账号（冷静期内撤销注销申请）
     *
     * @param userId 用户ID
     */
    void restoreAccount(Long userId);

    /**
     * 清理过期的待删除账号（定时任务调用）
     *
     * @return 成功清理的账号数量
     */
    int cleanupExpiredAccounts();
}
