package com.inkstage.enums.article;

import com.inkstage.enums.EnumCode;
import lombok.Getter;

@Getter
public enum RecommendStatus implements EnumCode {
    RECOMMENDED(1, "已推荐"),
    NOT_RECOMMENDED(0, "未推荐");

    private final Integer code;
    private final String desc;

    RecommendStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
