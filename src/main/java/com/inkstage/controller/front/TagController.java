package com.inkstage.controller.front;

import com.inkstage.common.ResponseMessage;
import com.inkstage.common.Result;
import com.inkstage.entity.model.Tag;
import com.inkstage.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台标签控制器
 */
@Slf4j
@RestController
@RequestMapping("/front/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * 获取所有激活状态的标签
     *
     * @return 标签列表
     */
    @GetMapping("/active")
    public Result<List<Tag>> getActiveTags() {
        log.info("获取激活状态的标签列表");
        List<Tag> tags = tagService.getActiveTags();
        if (tags != null && !tags.isEmpty()) {
            return Result.success(tags);
        } else {
            return Result.error(ResponseMessage.TAGS_ARE_EMPTY);
        }
    }

}
