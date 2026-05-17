package com.inkstage.config.redis;

public class RedisStreamConstants {

    /**
     * 通知消息Stream的Key
     */
    public static final String NOTIFICATION_STREAM = "inkstage:stream:notification";

    /**
     * 通知消息消费者组名称
     */
    public static final String NOTIFICATION_CONSUMER_GROUP = "notification-consumer-group";

    /**
     * 通知消息消费者名称（同一消费者组内的消费者标识）
     */
    public static final String NOTIFICATION_CONSUMER_NAME = "notification-consumer-1";

    /**
     * 通知消息死信队列Stream的Key（超过最大重试次数的消息转入此队列）
     */
    public static final String NOTIFICATION_DLQ_STREAM = "inkstage:stream:notification:dlq";

    /**
     * 死信队列消费者组名称
     */
    public static final String NOTIFICATION_DLQ_CONSUMER_GROUP = "notification-dlq-consumer-group";

    /**
     * 消息最大重试次数（超过此次数后转入死信队列）
     */
    public static final int MAX_RETRY_COUNT = 3;

    /**
     * 待重试消息的最小空闲时间（毫秒），超过此时间未确认的消息才会被重新消费
     */
    public static final long PENDING_MESSAGE_MIN_IDLE_TIME_MS = 60_000;

    /**
     * 计数消息Stream的Key
     */
    public static final String COUNT_STREAM = "inkstage:stream:count";

    /**
     * 计数消息消费者组名称
     */
    public static final String COUNT_CONSUMER_GROUP = "count-consumer-group";

    /**
     * 计数消息消费者名称（同一消费者组内的消费者标识）
     */
    public static final String COUNT_CONSUMER_NAME = "count-consumer-1";

    /**
     * 计数消息死信队列Stream的Key（超过最大重试次数的消息转入此队列）
     */
    public static final String COUNT_DLQ_STREAM = "inkstage:stream:count:dlq";

    /**
     * 计数死信队列消费者组名称
     */
    public static final String COUNT_DLQ_CONSUMER_GROUP = "count-dlq-consumer-group";

    /**
     * 计数消息幂等Key前缀（用于防止重复消费导致计数重复递增）
     */
    public static final String COUNT_IDEMPOTENT_KEY_PREFIX = "inkstage:idempotent:count:";

    /**
     * 幂等Key过期时间（分钟），防止Key无限堆积
     */
    public static final int IDEMPOTENT_KEY_TTL_MINUTES = 5;

    private RedisStreamConstants() {
    }
}
