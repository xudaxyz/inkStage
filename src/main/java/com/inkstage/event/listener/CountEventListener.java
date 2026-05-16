package com.inkstage.event.listener;

import com.inkstage.event.CountEvent;
import com.inkstage.service.CountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 计数事件监听器
 * <p>
 * 监听 CountEvent 和 BatchCountEvent，统一处理所有计数更新逻辑。
 * 业务层只需发布事件，所有计数更新在此集中管理，确保不遗漏。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CountEventListener {

    private final CountService countService;

    /**
     * 处理单条计数事件
     */
    @EventListener
    @Async("countTaskExecutor")
    public void handleCountEvent(CountEvent event) {
        try {
            countService.updateCount(event.getCountType(), event.getTargetId(), event.getDelta());
        } catch (Exception e) {
            log.error("处理计数事件失败, 计数类型: {}, 目标ID: {}, 增量: {}",
                    event.getCountType(), event.getTargetId(), event.getDelta(), e);
        }
    }

    /**
     * 处理批量计数事件
     */
    @EventListener
    @Async("countTaskExecutor")
    public void handleBatchCountEvent(CountEvent.BatchCountEvent event) {
        List<CountEvent> events = event.getEvents();
        for (CountEvent countEvent : events) {
            try {
                countService.updateCount(countEvent.getCountType(), countEvent.getTargetId(), countEvent.getDelta());
            } catch (Exception e) {
                log.error("处理批量计数事件失败, 计数类型: {}, 目标ID: {}, 增量: {}",
                        countEvent.getCountType(), countEvent.getTargetId(), countEvent.getDelta(), e);
            }
        }
    }
}
