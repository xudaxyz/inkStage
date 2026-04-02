package com.inkstage.vo.admin.stat;

import lombok.Data;

/**
 * 活动数据VO
 */
@Data
public class ActivityStatVO {
    /**
     * id
     */
    private long id;
    /**
     * 用户id
     */
    private long userId;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户行为
     */
    private String action;
    /**
     * 目标
     */
    private String target;
    /**
     * 状态
     */
    private String status;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 时间
     */
    private String time;
}
