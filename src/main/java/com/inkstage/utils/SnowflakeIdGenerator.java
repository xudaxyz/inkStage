package com.inkstage.utils;

import com.inkstage.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 雪花算法ID生成器
 * <p>
 * 基于 Twitter Snowflake 算法，生成全局唯一、趋势递增的 64 位整数 ID。
 * ID 结构（共 64 位）：
 * <pre>
 * | 1位符号位 | 41位时间戳 | 10位机器标识 | 12位序列号 |
 * </pre>
 * <ul>
 *   <li>1 位符号位：始终为 0，保证 ID 为正数</li>
 *   <li>41 位时间戳：相对于起始纪元（EPOCH）的毫秒数，可用约 69 年</li>
 *   <li>10 位机器标识：由 5 位数据中心 ID 和 5 位工作节点 ID 组成，最多支持 1024 个节点</li>
 *   <li>12 位序列号：同一毫秒内的自增序列，每毫秒最多生成 4096 个 ID</li>
 * </ul>
 */
@Slf4j
@Component
public class SnowflakeIdGenerator {

    /**
     * 起始纪元时间戳（2024-01-01 00:00:00 UTC+8）
     */
    private static final long EPOCH = 1704067200000L;

    /**
     * 工作节点 ID 占用位数
     */
    private static final long WORKER_ID_BITS = 5L;

    /**
     * 数据中心 ID 占用位数
     */
    private static final long DATACENTER_ID_BITS = 5L;

    /**
     * 工作节点 ID 最大值：31
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /**
     * 数据中心 ID 最大值：31
     */
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

    /**
     * 序列号占用位数
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * 工作节点 ID 左移位数：12
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据中心 ID 左移位数：17
     */
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 时间戳左移位数：22
     */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    /**
     * 序列号掩码：4095，用于限制序列号范围在 0~4095 之间
     */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /**
     * 工作节点 ID（0~31）
     */
    private final long workerId;

    /**
     * 数据中心 ID（0~31）
     */
    private final long datacenterId;

    /**
     * 当前毫秒内的序列号
     */
    private long sequence = 0L;

    /**
     * 上次生成 ID 的时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 创建雪花算法 ID 生成器
     *
     * @param workerId     工作节点 ID，范围 0~31
     * @param datacenterId 数据中心 ID，范围 0~31
     */
    public SnowflakeIdGenerator(
            @Value("${snowflake.worker-id:1}") long workerId,
            @Value("${snowflake.datacenter-id:1}") long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new BusinessException("工作节点ID无效，允许范围：0~" + MAX_WORKER_ID);
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new BusinessException("数据中心ID无效，允许范围：0~" + MAX_DATACENTER_ID);
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        log.info("雪花算法ID生成器初始化完成，workerId={}, datacenterId={}", workerId, datacenterId);
    }

    /**
     * 生成下一个全局唯一 ID
     * <p>
     * 线程安全，使用 synchronized 保证同一时刻只有一个线程可以生成 ID。
     * 内置时钟回拨保护机制：5ms 以内的回拨会等待恢复，超过 5ms 则抛出异常。
     *
     * @return 全局唯一的 64 位整数 ID
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = System.currentTimeMillis();
                    if (timestamp < lastTimestamp) {
                        throw new BusinessException("时钟回拨，无法生成ID");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new BusinessException("时钟回拨，无法生成ID", e);
                }
            } else {
                throw new BusinessException("时钟回拨 " + offset + " 毫秒，无法生成ID");
            }
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 自旋等待直到获得下一个毫秒时间戳
     *
     * @param lastTimestamp 上次生成 ID 的时间戳
     * @return 下一个毫秒时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
