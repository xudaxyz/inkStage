package com.inkstage.entity.model;

import com.inkstage.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 仪表盘统计数据实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DashboardStats extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 统计键（如 total_users、views_2026-04-02）
     */
    private String statKey;

    /**
     * 统计值（支持不同类型的数据）
     */
    private String statValue;

    /**
     * 数据类型（如 counter、trend、distribution）
     */
    private String dataType;

    /**
     * 时间值（如具体日期 2026-04-02 或月份 2026-04，可为空）
     */
    private String timeValue;
}
