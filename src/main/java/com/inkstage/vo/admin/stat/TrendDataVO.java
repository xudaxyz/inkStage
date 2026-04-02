package com.inkstage.vo.admin.stat;

import lombok.Data;

/**
 * 趋势数据VO
 */
@Data
public class TrendDataVO {
    /**
     * 时间值
     */
    private String timeValue;
    /**
     * 值
     */
    private long value;
}
