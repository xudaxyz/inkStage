package com.inkstage.mapper;

import com.inkstage.entity.model.UserAuth;
import com.inkstage.enums.auth.AuthType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户认证Mapper接口
 */
@Mapper
public interface UserAuthMapper {

    // ==================== 查询（Read） ====================
    
    /**
     * 根据用户ID和认证类型查询认证信息
     * @param userId 用户ID
     * @param authType 认证类型
     * @return 认证信息
     */
    UserAuth findByUserIdAndType(@Param("userId") Long userId, @Param("authType") AuthType authType);

    /**
     * 根据用户ID查询所有认证信息
     * @param userId 用户ID
     * @return 认证信息列表
     */
    List<UserAuth> findByUserId(@Param("userId") Long userId);

    /**
     * 根据认证标识和类型查询认证信息
     * @param identifier 认证标识
     * @param authType 认证类型
     * @return 认证信息
     */
    UserAuth findByIdentifierAndType(@Param("identifier") String identifier, @Param("authType") String authType);

    // ==================== 新增（Create） ====================
    
    /**
     * 新增用户认证
     * @param userAuth 用户认证对象
     * @return 影响行数
     */
    int insert(UserAuth userAuth);

    // ==================== 更新（Update） ====================
    
    /**
     * 更新用户认证信息
     * @param userAuth 认证信息
     * @return 影响行数
     */
    int update(UserAuth userAuth);

    // ==================== 删除（Delete） ====================
    
    /**
     * 删除用户认证信息
     * @param id 认证ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据用户ID删除所有认证信息
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);

}