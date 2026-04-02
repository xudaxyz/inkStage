package com.inkstage.vo.admin.stat;

import lombok.Data;

/**
 * 分布数据VO
 */
@Data
public class DistributionDataVO {
    /**
     * 名称
     */
    private String name;
    /**
     * 值
     */
    private int value;
    /**
     * 百分比
     */
    private double percentage;
}
