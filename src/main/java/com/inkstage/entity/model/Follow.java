package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 关注关系实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Follow extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 粉丝ID
     */
    private Long followerId;

    /**
     * 关注对象ID
     */
    private Long followingId;
}