package com.inkstage.mapper;

import com.inkstage.entity.model.AccountCancellation;
import com.inkstage.enums.CancellationStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountCancellationMapper {

    /**
     * 根据用户ID查询待注销账号
     * @param userId 用户ID
     * @return 待注销账号
     */
    AccountCancellation findByUserId(@Param("userId") Long userId);

    /**
     * 插入待注销账号数据
     * @param accountCancellation 待注销账号数据
     * @return 返回的条数
     */
    int insert(AccountCancellation accountCancellation);

    /**
     * 根据用户ID更新状态
     * @param userId 用户id
     * @param status 状态
     * @return 执行结果
     */
    int updateStatusByUserId(@Param("userId") Long userId, @Param("status") CancellationStatus status);

    /**
     * 根据用户id删除待注销账号信息
     * @param userId 用户id
     * @return 返回的条数
     */
    int purgeByUserId(@Param("userId") Long userId);
}
