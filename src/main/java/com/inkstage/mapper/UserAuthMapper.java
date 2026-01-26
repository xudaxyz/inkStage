package com.inkstage.mapper;

import com.inkstage.entity.model.UserAuth;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户认证Mapper接口
 */
@Mapper
public interface UserAuthMapper {


    /**
     * 新增用户认证
     * @param userAuth 用户认证对象
     */
    void insert(UserAuth userAuth);
}