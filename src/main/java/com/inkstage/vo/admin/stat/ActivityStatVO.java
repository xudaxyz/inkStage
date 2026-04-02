package com.inkstage.vo.admin.stat;

import lombok.Data;

/**
 * 活动数据VO
 */
@Data
public class ActivityStatVO {
    private long id;
    private long userId;
    private String userName;
    private String action;
    private String target;
    private String status;
    private String avatar;
    private String time;
}
