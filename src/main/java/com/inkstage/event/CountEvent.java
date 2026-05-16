package com.inkstage.event;

import com.inkstage.enums.CountType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 计数事件
 * <p>
 * 用于解耦业务操作与计数更新的依赖关系。
 * 业务层发布 CountEvent 后，由 CountEventListener 统一处理计数更新逻辑。
 */
@Getter
public class CountEvent extends ApplicationEvent {

    private final CountType countType;

    private final Long targetId;

    private final int delta;

    public CountEvent(Object source, CountType countType, Long targetId, int delta) {
        super(source);
        this.countType = countType;
        this.targetId = targetId;
        this.delta = delta;
    }

    /**
     * 创建单条计数事件
     */
    public static CountEvent of(Object source, CountType countType, Long targetId, int delta) {
        return new CountEvent(source, countType, targetId, delta);
    }

    /**
     * 批量计数事件
     * <p>
     * 一次业务操作可能触发多个计数更新（如发布文章需同时更新用户文章数、分类文章数等），
     * 使用 BatchCountEvent 确保这些更新在同一个事件中完成，避免遗漏。
     */
    @Getter
    public static class BatchCountEvent extends ApplicationEvent {

        private final List<CountEvent> events;

        public BatchCountEvent(Object source, List<CountEvent> events) {
            super(source);
            this.events = events;
        }
    }
}
