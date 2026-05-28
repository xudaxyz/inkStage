package com.inkstage.constant;

public class InkConstant {

    /**
     * 用户默认头像URL
     */
    public static final String DEFAULT_AVATAR_URL = "https://example.com/default-avatar.jpg";

    /**
     * URL前缀
     */
    public static final String PREFIX_URL = "http://";

    /**
     * URL前缀
     */
    public static final String PREFIX_URLS = "https://";

    /**
     * 文章url路径
     */
    public static final String ARTICLE_URL = "/article/";

    /**
     * 专栏url路径
     */
    public static final String COLUMN_URL = "/column/";

    /**
     * 删除收藏夹时收藏夹内容移除策略 -- 移至默认文件夹
     */
    public static final String COLLECT_DELETE_STRATEGY_MOVE = "MOVE_TO_DEFAULT";

    /**
     * 删除收藏夹时收藏夹内容移除策略 -- 同步删除收藏夹中的内容(取消收藏)
     */
    public static final String COLLECT_DELETE_STRATEGY_DELETE = "DELETE_COLLECTIONS";

    /**
     * 账号注销冷静期 - 30天
     */
    public static final int ACCOUNT_DELETE_COOLING_DAYS = 30;
}
