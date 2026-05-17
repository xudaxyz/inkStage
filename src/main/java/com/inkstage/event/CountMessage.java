package com.inkstage.event;

import com.inkstage.enums.CountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 计数消息（基于Redis Streams的消息载体）
 * <p>
 * 用于在业务操作与计数更新之间传递计数信息。
 * 业务层通过 CountProducer 发送 CountMessage 到 Redis Stream，
 * 由 CountConsumer 消费并执行计数更新逻辑。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountMessage {

    /**
     * 计数类型
     */
    private CountType countType;

    /**
     * 目标记录ID
     */
    private Long targetId;

    /**
     * 增量（正数增加，负数减少）
     */
    private int delta;
}
