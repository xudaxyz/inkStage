-- InkStage 数据库建表语句
-- 创建时间：2026-01-21
-- 版本：v1.0

-- 设置字符集和排序规则
SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 用户相关表
-- ----------------------------

-- 用户表（user）
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`                          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`                    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '用户名',
    `password`                    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密后的密码, 用于身份验证',
    `nickname`                    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '昵称',
    `role_id`                     tinyint                                                       NOT NULL COMMENT '用户角色ID',
    `email`                       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
    `email_verified`              tinyint NULL DEFAULT 0 COMMENT '邮箱是否已验证（0:未验证,1:已验证）',
    `phone`                       varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
    `phone_verified`              tinyint NULL DEFAULT 0 COMMENT '手机号是否已验证（0:未验证,1:已验证）',
    `avatar`                      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
    `cover_image`                 varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人主页封面图',
    `signature`                   varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个性签名',
    `gender`                      tinyint NULL DEFAULT 0 COMMENT '性别（0:未知,1:男,2:女）',
    `birth_date`                  date NULL DEFAULT NULL COMMENT '生日',
    `location`                    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所在地区',
    `website`                     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人网站',
    `follow_count`                int NULL DEFAULT 0 COMMENT '关注数',
    `follower_count`              int NULL DEFAULT 0 COMMENT '粉丝数',
    `article_count`               int NULL DEFAULT 0 COMMENT '文章数',
    `comment_count`               int NULL DEFAULT 0 COMMENT '评论数',
    `like_count`                  int NULL DEFAULT 0 COMMENT '获赞数',
    `last_login_time`             datetime NULL DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登录IP',
    `register_ip`                 varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户注册时的IP地址',
    `privacy`                     tinyint NULL DEFAULT 1 COMMENT '隐私设置：0-公开, 1-私有, 2-仅关注者可见',
    `status`                      tinyint NULL DEFAULT 1 COMMENT '状态（0:禁用,1:正常,2:待审核）',
    `register_time`               datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户注册的时间戳',
    `create_time`                 datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`                 datetime NULL DEFAULT NULL COMMENT '更新时间',
    `username_last_modified_time` datetime NULL DEFAULT NULL COMMENT '用户名最后修改时间',
    `user_version`                int NULL DEFAULT NULL COMMENT '版本号',
    `deleted`                     tinyint NULL DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`                datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `username`(`username` ASC) USING BTREE,
    UNIQUE INDEX `email`(`email` ASC) USING BTREE,
    UNIQUE INDEX `phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- 用户认证表（user_auth）
DROP TABLE IF EXISTS `user_auth`;
CREATE TABLE `user_auth`
(
    `id`                    bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`               bigint                                                        NOT NULL COMMENT '用户ID',
    `auth_type`             tinyint                                                       NOT NULL COMMENT '认证类型（username,email,phone,github,qq,wechat）',
    `auth_identifier`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '认证标识（用户名,邮箱,手机号,第三方用户ID）',
    `auth_credential`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '认证凭证（密码哈希,第三方access_token）',
    `credential_expired_at` datetime NULL DEFAULT NULL COMMENT '凭证过期时间（如第三方access_token过期时间）',
    `primary_auth`          tinyint NULL DEFAULT 0 COMMENT '是否为主认证方式（0:否,1:是）',
    `enabled`               tinyint NULL DEFAULT 1 COMMENT '是否启用该认证方式（0:禁用,1:启用）',
    `last_auth_time`        datetime NULL DEFAULT NULL COMMENT '最后认证时间',
    `create_time`           datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           datetime NULL DEFAULT NULL COMMENT '更新时间',
    `deleted`               tinyint NULL DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`          datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX                   `idx_user_id`(`user_id` ASC) USING BTREE,
    INDEX                   `idx_auth_type`(`auth_type` ASC) USING BTREE,
    INDEX                   `idx_auth_identifier`(`auth_identifier` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户认证表' ROW_FORMAT = Dynamic;

-- 角色表（role）
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`           bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
    `code`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
    `description`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '角色描述',
    `system_role`  tinyint NULL DEFAULT 0 COMMENT '是否为系统角色（0:否,1:是，系统角色不可删除）',
    `level`        int NULL DEFAULT 0 COMMENT '角色等级（数值越大，权限越高）',
    `status`       tinyint NULL DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
    `create_time`  datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      tinyint NULL DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `name`(`name` ASC) USING BTREE,
    UNIQUE INDEX `code`(`code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表';

-- 用户角色关联表（user_role）
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`
(
    `id`           bigint   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`      bigint   NOT NULL COMMENT '用户ID',
    `role_id`      bigint   NOT NULL COMMENT '角色ID',
    `assigned_by`  bigint NULL DEFAULT NULL COMMENT '分配角色的用户ID',
    `assigned_at`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '角色分配时间',
    `expires_at`   datetime NULL DEFAULT NULL COMMENT '角色过期时间（NULL表示永久有效）',
    `status`       tinyint NULL DEFAULT 1 COMMENT '状态（0:无效,1:有效）',
    `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime NULL DEFAULT NULL COMMENT '更新时间',
    `deleted`      tinyint NULL DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
    INDEX          `idx_user_id`(`user_id` ASC) USING BTREE,
    INDEX          `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 2. 文章相关表
-- ----------------------------

-- 文章表（article）
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`
(
    `id`                     bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`                bigint                                                        NOT NULL COMMENT '作者ID',
    `title`                  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章标题',
    `summary`                text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文章摘要',
    `content`                longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章内容',
    `content_html`           longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文章HTML内容（Markdown转换结果）',
    `cover_image`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章封面图URL',
    `category_id`            bigint NULL DEFAULT NULL COMMENT '分类ID',
    `article_status`         tinyint NULL DEFAULT 1 COMMENT '文章状态(1:草稿,2:待发布,3:已发布,4:已下架,5:回收站)',
    `review_status`          tinyint NULL DEFAULT NULL COMMENT '审核状态(0:待审核,1:审核通过,2:审核拒绝,3:申诉中,4:禁用)',
    `publish_time`           datetime NULL DEFAULT NULL COMMENT '发布时间',
    `read_count`             int NULL DEFAULT 0 COMMENT '阅读量',
    `like_count`             int NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count`          int NULL DEFAULT 0 COMMENT '评论数',
    `collection_count`       int NULL DEFAULT 0 COMMENT '收藏数',
    `share_count`            int NULL DEFAULT 0 COMMENT '分享数',
    `top`                    tinyint NULL DEFAULT 0 COMMENT '是否置顶（0:否,1:是）',
    `recommended`            tinyint NULL DEFAULT 0 COMMENT '是否推荐（0:否,1:是）',
    `visible`                tinyint NULL DEFAULT 1 COMMENT '可见性状态：0-私有, 1-公开, 2-仅关注者可见',
    `allow_comment`          tinyint NULL DEFAULT 1 COMMENT '允许评论状态：0-不允许, 1-允许',
    `allow_forward`          tinyint NULL DEFAULT 1 COMMENT '允许转发状态：0-不允许, 1-允许',
    `original`               tinyint NULL DEFAULT 1 COMMENT '是否原创（0:转载,1:原创）',
    `original_url`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '转载来源URL',
    `views_ip`               json NULL COMMENT '阅读IP记录（用于去重）',
    `last_edit_time`         datetime NULL DEFAULT NULL COMMENT '最后一次编辑的时间',
    `create_time`            datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`            datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                tinyint NULL DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`           datetime NULL DEFAULT NULL COMMENT '删除时间',
    `meta_title`             varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SEO标题',
    `meta_description`       text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'SEO描述',
    `meta_keywords`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SEO关键词',
    `scheduled_publish_time` datetime NULL DEFAULT NULL COMMENT '定时发布时间',
    `share_token`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
    `article_version`        int NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX                    `idx_user_id`(`user_id` ASC) USING BTREE,
    INDEX                    `idx_category_id`(`category_id` ASC) USING BTREE,
    INDEX                    `idx_status`(`article_status` ASC) USING BTREE,
    INDEX                    `idx_top`(`top` ASC) USING BTREE,
    INDEX                    `idx_recommended`(`recommended` ASC) USING BTREE,
    INDEX                    `idx_visible`(`visible` ASC) USING BTREE,
    INDEX                    `idx_original`(`original` ASC) USING BTREE
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章表';

-- 分类表（category）
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`
(
    `id`               bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
    `slug`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类别名（URL友好）',
    `parent_id`        bigint NULL DEFAULT 0 COMMENT '父分类ID（0表示顶级分类）',
    `sort_order`       int NULL DEFAULT 0 COMMENT '排序顺序',
    `description`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '分类描述',
    `article_count`    int NULL DEFAULT 0 COMMENT '分类下文章数量',
    `status`           tinyint NULL DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
    `create_time`      datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `category_version` int NULL DEFAULT NULL COMMENT '版本号',
    `deleted`          tinyint NULL DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`     datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `slug`(`slug` ASC) USING BTREE,
    INDEX              `idx_parent_id`(`parent_id` ASC) USING BTREE,
    INDEX              `idx_sort_order`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '分类表' ROW_FORMAT = Dynamic;

-- 标签表（tag）
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`
(
    `id`            bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
    `slug`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签别名（URL友好）',
    `description`   text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '标签描述',
    `article_count` int NULL DEFAULT 0 COMMENT '标签下文章数量',
    `user_id`       bigint NULL DEFAULT NULL COMMENT '创建者ID',
    `usage_count`   int NULL DEFAULT 0 COMMENT '标签使用次数',
    `status`        tinyint NULL DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
    `create_time`   datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime NULL DEFAULT NULL COMMENT '更新时间',
    `tag_version`   int NULL DEFAULT NULL COMMENT '版本号',
    `deleted`       tinyint                                                      NOT NULL DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`  datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- 文章标签关联表（article_tag）
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag`
(
    `id`           bigint   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id`   bigint   NOT NULL COMMENT '文章ID',
    `tag_id`       bigint   NOT NULL COMMENT '标签ID',
    `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      tinyint NULL DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX          `idx_article_id`(`article_id` ASC) USING BTREE,
    INDEX          `idx_tag_id`(`tag_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章标签关联表' ROW_FORMAT = Dynamic;

-- 文章点赞表（article_like）
DROP TABLE IF EXISTS `article_like`;
CREATE TABLE `article_like`
(
    `id`           bigint   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id`   bigint   NOT NULL COMMENT '文章ID',
    `user_id`      bigint   NOT NULL COMMENT '用户ID',
    `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime NULL DEFAULT NULL COMMENT '更新时间',
    `deleted`      tinyint NULL DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_article_user`(`id` ASC, `article_id` ASC, `user_id` ASC, `deleted` ASC) USING BTREE,
    INDEX          `idx_article_id`(`article_id` ASC) USING BTREE,
    INDEX          `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章点赞表' ROW_FORMAT = Dynamic;

-- 文章收藏表（article_collection）
DROP TABLE IF EXISTS `article_collection`;
CREATE TABLE `article_collection`
(
    `id`           bigint   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id`   bigint   NOT NULL COMMENT '文章ID',
    `user_id`      bigint   NOT NULL COMMENT '用户ID',
    `folder_id`    bigint   NOT NULL COMMENT '收藏文件夹ID',
    `status`       tinyint  NOT NULL COMMENT '收藏状态（0:公开,1:私密）',
    `collect_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime NULL DEFAULT NULL COMMENT '更新时间',
    `deleted`      tinyint NULL DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_article_user`(`id` ASC, `article_id` ASC, `user_id` ASC, `deleted` ASC) USING BTREE,
    INDEX          `idx_article_id`(`article_id` ASC) USING BTREE,
    INDEX          `idx_user_id`(`user_id` ASC) USING BTREE,
    INDEX          `idx_folder_id`(`folder_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章收藏表' ROW_FORMAT = Dynamic;

-- 收藏文件夹表（collection_folder）
DROP TABLE IF EXISTS `collection_folder`;
CREATE TABLE `collection_folder`
(
    `id`             bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`        bigint                                                       NOT NULL COMMENT '用户ID',
    `name`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件夹名称',
    `description`    text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文件夹描述',
    `article_count`  int NULL DEFAULT 0 COMMENT '文件夹内文章数量',
    `sort_order`     int NULL DEFAULT 0 COMMENT '排序顺序',
    `default_folder` tinyint NULL DEFAULT 0 COMMENT '是否默认文件夹（0:否,1:是）',
    `create_time`    datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        tinyint NULL DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`   datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `idx_user_id`(`user_id` ASC) USING BTREE,
    INDEX            `idx_default_folder`(`default_folder` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收藏文件夹表' ROW_FORMAT = Dynamic;

-- 专栏表（column）
DROP TABLE IF EXISTS `column`;
CREATE TABLE `column`
(
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id`       BIGINT       NOT NULL COMMENT '专栏创建者ID',
    `name`          VARCHAR(100) NOT NULL COMMENT '专栏名称',
    `slug`          VARCHAR(100) NOT NULL UNIQUE COMMENT '专栏别名（URL友好）',
    `description`   TEXT COMMENT '专栏描述',
    `cover_image`   VARCHAR(255) COMMENT '专栏封面图URL',
    `article_count` INT                   DEFAULT 0 COMMENT '专栏内文章数量',
    `read_count`    INT                   DEFAULT 0 COMMENT '专栏总阅读量',
    `sort_order`    INT                   DEFAULT 0 COMMENT '排序顺序',
    `status`        TINYINT               DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT               DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`  DATETIME COMMENT '删除时间',
    KEY             `idx_user_id` (`user_id`),
    KEY             `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='专栏表';

-- 文章专栏关联表（article_column）
DROP TABLE IF EXISTS `article_column`;
CREATE TABLE `article_column`
(
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `article_id`   BIGINT   NOT NULL COMMENT '文章ID',
    `column_id`    BIGINT   NOT NULL COMMENT '专栏ID',
    `sort_order`   INT               DEFAULT 0 COMMENT '文章在专栏内的排序顺序',
    `create_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT           DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time` DATETIME COMMENT '删除时间',
    UNIQUE KEY `uk_article_column` (`article_id`, `column_id`),
    KEY            `idx_article_id` (`article_id`),
    KEY            `idx_column_id` (`column_id`),
    KEY            `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章专栏关联表';

-- 文章阅读统计表（article_read_stat）
DROP TABLE IF EXISTS `article_read_stat`;
CREATE TABLE `article_read_stat`
(
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `article_id`    BIGINT      NOT NULL COMMENT '文章ID',
    `user_id`       BIGINT COMMENT '用户ID（未登录用户为NULL）',
    `ip_address`    VARCHAR(50) NOT NULL COMMENT '阅读IP地址',
    `user_agent`    TEXT COMMENT '用户代理信息',
    `view_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
    `read_duration` INT COMMENT '阅读时长（秒）',
    `complete`      TINYINT              DEFAULT 0 COMMENT '是否完整阅读（0:否,1:是）',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT              DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`  DATETIME COMMENT '删除时间',
    KEY             `idx_article_id` (`article_id`),
    KEY             `idx_user_id` (`user_id`),
    KEY             `idx_ip_address` (`ip_address`),
    KEY             `idx_view_time` (`view_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章阅读统计表';

-- ----------------------------
-- 3. 评论相关表
-- ----------------------------

-- 评论表（comment）
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`
(
    `id`               bigint   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id`       bigint   NOT NULL COMMENT '文章ID',
    `user_id`          bigint   NOT NULL COMMENT '用户ID',
    `parent_id`        bigint NULL DEFAULT NULL COMMENT '父评论ID（NULL表示主评论）',
    `content`          text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
    `floor`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论楼层号',
    `like_count`       int NULL DEFAULT 0 COMMENT '点赞数',
    `reply_count`      int NULL DEFAULT 0 COMMENT '回复数',
    `status`           tinyint NULL DEFAULT 1 COMMENT '状态（0:待审核,1:已通过,2:已拒绝）',
    `top`              tinyint NULL DEFAULT 0 COMMENT '是否置顶（0:否,1:是）',
    `top_order`        int NULL DEFAULT 0 COMMENT '置顶顺序（数值越大，优先级越高）',
    `review_user_id`   bigint NULL DEFAULT NULL COMMENT '审核人ID',
    `review_time`      datetime NULL DEFAULT NULL COMMENT '审核时间',
    `review_reason`    varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核拒绝原因',
    `ip_address`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论IP地址',
    `user_agent`       text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户代理信息',
    `mention_user_ids` json NULL COMMENT '@提及的用户ID列表',
    `report_count`     int NULL DEFAULT 0 COMMENT '被举报次数',
    `comment_version`  int NULL DEFAULT NULL COMMENT '评论版本号',
    `create_time`      datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          tinyint NULL DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`     datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX              `idx_article_id`(`article_id` ASC) USING BTREE,
    INDEX              `idx_user_id`(`user_id` ASC) USING BTREE,
    INDEX              `idx_parent_id`(`parent_id` ASC) USING BTREE,
    INDEX              `idx_status`(`status` ASC) USING BTREE,
    INDEX              `idx_top`(`top` ASC) USING BTREE,
    INDEX              `idx_top_order`(`top_order` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- 评论点赞表（comment_like）
DROP TABLE IF EXISTS `comment_like`;
CREATE TABLE `comment_like`
(
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `comment_id`   BIGINT   NOT NULL COMMENT '评论ID',
    `user_id`      BIGINT   NOT NULL COMMENT '用户ID',
    `create_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT           DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time` DATETIME COMMENT '删除时间',
    UNIQUE KEY `uk_comment_user` (`comment_id`, `user_id`),
    KEY            `idx_comment_id` (`comment_id`),
    KEY            `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论点赞表';

-- ----------------------------
-- 4. 社交相关表
-- ----------------------------

-- 关注表（follow）
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow`
(
    `id`           bigint   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `follower_id`  bigint   NOT NULL COMMENT '粉丝ID',
    `following_id` bigint   NOT NULL COMMENT '关注对象ID',
    `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    `update_time`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      tinyint NULL DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX          `idx_follower_id`(`follower_id` ASC) USING BTREE,
    INDEX          `idx_following_id`(`following_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '关注表' ROW_FORMAT = Dynamic;

-- 私信表（message）
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`
(
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `sender_id`       BIGINT      NOT NULL COMMENT '发送者ID',
    `receiver_id`     BIGINT      NOT NULL COMMENT '接收者ID',
    `content`         TEXT        NOT NULL COMMENT '私信内容',
    `read`            TINYINT              DEFAULT 0 COMMENT '是否已读（0:未读,1:已读）',
    `read_time`       DATETIME COMMENT '已读时间',
    `type`            TINYINT              DEFAULT 0 COMMENT '消息类型（0:文本,1:图片,2:文件,3:系统通知）',
    `attachment_url`  VARCHAR(255) COMMENT '附件URL（图片或文件）',
    `conversation_id` VARCHAR(64) NOT NULL COMMENT '对话ID，由sender_id和receiver_id生成的唯一标识',
    `sequence_id`     BIGINT      NOT NULL COMMENT '对话内消息序号，用于保证消息顺序',
    `create_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT              DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`    DATETIME COMMENT '删除时间',
    KEY               `idx_sender_id` (`sender_id`),
    KEY               `idx_receiver_id` (`receiver_id`),
    KEY               `idx_conversation_id` (`conversation_id`),
    KEY               `idx_sequence_id` (`sequence_id`),
    KEY               `idx_read` (`read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='私信表';

-- 通知表（notification）
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`
(
    `id`                    bigint   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`               bigint   NOT NULL COMMENT '接收通知的用户ID',
    `notification_type`     int      NOT NULL COMMENT '通知类型',
    `notification_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知分类',
    `title`                 text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
    `content`               text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知内容',
    `read_status`           tinyint NULL DEFAULT 0 COMMENT '是否已读（0:未读,1:已读）',
    `read_time`             datetime NULL DEFAULT NULL COMMENT '已读时间',
    `related_id`            bigint NULL DEFAULT NULL COMMENT '关联ID（如文章ID、评论ID、私信ID等）',
    `related_type`          tinyint NULL DEFAULT NULL COMMENT '关联类型（0:文章,1:评论,2:私信,3:用户,4:活动等）',
    `sender_id`             bigint NULL DEFAULT NULL COMMENT '发送通知的用户ID（系统通知为0）',
    `pushed_status`         tinyint NULL DEFAULT 0 COMMENT '是否已推送（0:未推送,1:已推送）',
    `push_time`             datetime NULL DEFAULT NULL COMMENT '推送时间',
    `priority`              tinyint NULL DEFAULT 0 COMMENT '通知优先级（0:普通,1:重要,2:紧急）',
    `action_url`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作链接（用于直接跳转到相关内容）',
    `extra_data`            json NULL COMMENT '额外数据（用于存储通知相关的扩展信息）',
    `create_time`           datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           datetime NULL DEFAULT NULL COMMENT '更新时间',
    `deleted`               tinyint NULL DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`          datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX                   `idx_user_id`(`user_id` ASC) USING BTREE,
    INDEX                   `idx_type`(`notification_type` ASC) USING BTREE,
    INDEX                   `idx_priority`(`priority` ASC) USING BTREE,
    INDEX                   `idx_related_id`(`related_id` ASC) USING BTREE,
    INDEX                   `idx_sender_id`(`sender_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知表' ROW_FORMAT = Dynamic;


-- 通知模板表（notification_template）
DROP TABLE IF EXISTS `notification_template`;
CREATE TABLE `notification_template`
(
    `id`                   bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '模板ID',
    `code`                 varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '模板编码(唯一标识)',
    `template_name`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
    `notification_type`    int                                                           NOT NULL COMMENT '通知类型(对应NotificationType的code)',
    `notification_channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL DEFAULT 'site' COMMENT '通知渠道(site:站内信, email:邮件, sms:短信, push:推送)',
    `title_template`       varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知标题模板(支持占位符如: {{username}})',
    `content_template`     text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知内容模板(支持占位符)',
    `action_url_template`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作链接模板',
    `description`          varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模板描述',
    `priority`             tinyint                                                       NOT NULL DEFAULT 0 COMMENT '优先级(0:普通, 1:重要, 2:紧急)',
    `status`               tinyint                                                       NOT NULL DEFAULT 1 COMMENT '状态(0:禁用, 1:启用)',
    `extra_data`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '额外数据',
    `create_user_id`       bigint                                                        NOT NULL COMMENT '创建人ID',
    `update_user_id`       bigint NULL DEFAULT NULL COMMENT '更新人ID',
    `template_version`     int NULL DEFAULT NULL COMMENT '模板版本',
    `create_username`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建模版作者',
    `update_username`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新模版作者',
    `create_time`          datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`          datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`              tinyint                                                       NOT NULL DEFAULT 0 COMMENT '是否已删除(0:未删除, 1:已删除)',
    `deleted_time`         datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `code`(`code` ASC) USING BTREE,
    INDEX                  `idx_code`(`code` ASC) USING BTREE,
    INDEX                  `idx_type`(`notification_type` ASC) USING BTREE,
    INDEX                  `idx_channel`(`notification_channel` ASC) USING BTREE,
    INDEX                  `idx_status`(`status` ASC) USING BTREE,
    INDEX                  `idx_priority`(`priority` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 5. 举报和反馈表
-- ----------------------------

-- 举报表（report）
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `reporter_id`   bigint       NOT NULL COMMENT '举报人ID',
    `reporter_name` varchar(50)  NOT NULL COMMENT '举报人昵称',
    `reported_type` tinyint      NOT NULL COMMENT '被举报对象类型',
    `related_id`    bigint                DEFAULT NULL COMMENT '相关对象ID(文章ID、评论ID、用户ID等)',
    `reported_id`   bigint       NOT NULL COMMENT '被举报对象ID',
    `reported_name` varchar(50)  NOT NULL COMMENT '被举报对象用户名',
    `report_type`   tinyint      NOT NULL COMMENT '举报类型(枚举存储字符串)',
    `reason`        varchar(500) NOT NULL COMMENT '举报理由',
    `evidence`      text COMMENT '举报证据(JSON格式,包含图片、视频等链接)',
    `anonymous`     tinyint NULL DEFAULT 0 COMMENT '是否匿名举报(0:否,1:是)',
    `report_status` tinyint      NOT NULL COMMENT '举报状态(枚举存储字符串)',
    `handle_result` tinyint               DEFAULT NULL COMMENT '处理结果(枚举存储字符串)',
    `handle_reason` varchar(500)          DEFAULT NULL COMMENT '处理理由',
    `handler_id`    bigint                DEFAULT NULL COMMENT '处理人ID',
    `handler_name`  varchar(50)           DEFAULT NULL COMMENT '处理人昵称',
    `handle_time`   datetime              DEFAULT NULL COMMENT '处理时间',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       tinyint      NOT NULL DEFAULT 0 COMMENT '删除标识(0:未删除,1:已删除)',
    `deleted_time`  datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`),
    KEY             `idx_reporter_id` (`reporter_id`),
    KEY             `idx_reported_id` (`reported_id`),
    KEY             `idx_report_status` (`report_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报表';

-- 举报类型表（report_type）
DROP TABLE IF EXISTS `report_type`;
CREATE TABLE `report_type`
(
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `name`         VARCHAR(50) NOT NULL COMMENT '举报类型名称',
    `code`         VARCHAR(50) NOT NULL UNIQUE COMMENT '举报类型编码',
    `description`  TEXT COMMENT '举报类型描述',
    `target_types` JSON        NOT NULL COMMENT '适用对象类型（0:文章,1:评论,2:用户,3:私信）',
    `priority`     INT                  DEFAULT 0 COMMENT '优先级（数值越大，优先级越高）',
    `status`       TINYINT              DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
    `create_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT              DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time` DATETIME COMMENT '删除时间',
    KEY            `idx_code` (`code`),
    KEY            `idx_priority` (`priority`),
    KEY            `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='举报类型表';

-- 反馈表（feedback）
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`
(
    `id`               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id`          BIGINT       NOT NULL COMMENT '反馈人ID',
    `feedback_type_id` BIGINT       NOT NULL COMMENT '反馈类型ID',
    `title`            VARCHAR(200) NOT NULL COMMENT '反馈标题',
    `content`          TEXT         NOT NULL COMMENT '反馈内容',
    `contact_info`     VARCHAR(100) COMMENT '联系方式',
    `evidence_urls`    JSON COMMENT '反馈证据URL列表',
    `status`           TINYINT               DEFAULT 0 COMMENT '反馈状态（0:待处理,1:处理中,2:已解决,3:已关闭）',
    `handle_user_id`   BIGINT COMMENT '处理人ID',
    `handle_time`      DATETIME COMMENT '处理时间',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          TINYINT               DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`     DATETIME COMMENT '删除时间',
    KEY                `idx_user_id` (`user_id`),
    KEY                `idx_feedback_type_id` (`feedback_type_id`),
    KEY                `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='反馈表';

-- 反馈类型表（feedback_type）
DROP TABLE IF EXISTS `feedback_type`;
CREATE TABLE `feedback_type`
(
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `name`         VARCHAR(50) NOT NULL COMMENT '反馈类型名称',
    `code`         VARCHAR(50) NOT NULL UNIQUE COMMENT '反馈类型编码',
    `description`  TEXT COMMENT '反馈类型描述',
    `status`       TINYINT              DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
    `create_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT              DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time` DATETIME COMMENT '删除时间',
    KEY            `idx_code` (`code`),
    KEY            `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='反馈类型表';

-- 反馈回复表（feedback_reply）
DROP TABLE IF EXISTS `feedback_reply`;
CREATE TABLE `feedback_reply`
(
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `feedback_id`  BIGINT   NOT NULL COMMENT '反馈ID',
    `user_id`      BIGINT   NOT NULL COMMENT '回复人ID',
    `content`      TEXT     NOT NULL COMMENT '回复内容',
    `create_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT           DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time` DATETIME COMMENT '删除时间',
    KEY            `idx_feedback_id` (`feedback_id`),
    KEY            `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='反馈回复表';

-- ----------------------------
-- 6. 其他表
-- ----------------------------

-- 敏感词表（sensitive_word）
DROP TABLE IF EXISTS `sensitive_word`;
CREATE TABLE `sensitive_word`
(
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `word`         VARCHAR(50) NOT NULL UNIQUE COMMENT '敏感词',
    `category`     VARCHAR(20) NOT NULL COMMENT '敏感词分类',
    `level`        TINYINT     NOT NULL COMMENT '敏感级别（0:低,1:中,2:高）',
    `status`       TINYINT              DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
    `create_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT              DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time` DATETIME COMMENT '删除时间',
    KEY            `idx_word` (`word`),
    KEY            `idx_category` (`category`),
    KEY            `idx_level` (`level`),
    KEY            `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='敏感词表';

-- 系统公告表（system_announcement）
DROP TABLE IF EXISTS `system_announcement`;
CREATE TABLE `system_announcement`
(
    `id`             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `title`          VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content`        TEXT         NOT NULL COMMENT '公告内容',
    `type`           TINYINT               DEFAULT 0 COMMENT '公告类型（0:系统公告,1:活动通知,2:维护通知）',
    `status`         TINYINT               DEFAULT 0 COMMENT '状态（0:未发布,1:已发布,2:已过期）',
    `publish_time`   DATETIME COMMENT '发布时间',
    `expire_time`    DATETIME COMMENT '过期时间',
    `read_count`     INT                   DEFAULT 0 COMMENT '阅读量',
    `create_user_id` BIGINT       NOT NULL COMMENT '创建人ID',
    `update_user_id` BIGINT COMMENT '更新人ID',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT               DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`   DATETIME COMMENT '删除时间',
    KEY              `idx_type` (`type`),
    KEY              `idx_status` (`status`),
    KEY              `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统公告表';

-- 用户通知关联表（user_notification）
DROP TABLE IF EXISTS `user_notification`;
CREATE TABLE `user_notification`
(
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id`         BIGINT   NOT NULL COMMENT '用户ID',
    `notification_id` BIGINT   NOT NULL COMMENT '通知ID',
    `read_status`     TINYINT           DEFAULT 0 COMMENT '是否已读（0:未读,1:已读）',
    `read_time`       DATETIME COMMENT '已读时间',
    `create_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT           DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`    DATETIME COMMENT '删除时间',
    KEY               `idx_user_id` (`user_id`),
    KEY               `idx_notification_id` (`notification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户通知关联表';

-- 通知设置表（notification_setting）
DROP TABLE IF EXISTS `notification_setting`;
CREATE TABLE `notification_setting`
(
    `id`                              bigint   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`                         bigint   NOT NULL COMMENT '用户ID',
    `article_publish_notification`    tinyint NULL DEFAULT 1 COMMENT '是否接收文章发布通知',
    `article_like_notification`       tinyint NULL DEFAULT 1 COMMENT '是否接收文章点赞通知',
    `article_collection_notification` tinyint NULL DEFAULT 1 COMMENT '是否接收文章收藏通知',
    `article_comment_notification`    tinyint NULL DEFAULT 1 COMMENT '是否接收文章评论通知',
    `comment_reply_notification`      tinyint NULL DEFAULT 1 COMMENT '是否接收评论回复通知',
    `comment_like_notification`       tinyint NULL DEFAULT 1 COMMENT '是否接收评论点赞通知',
    `follow_notification`             tinyint NULL DEFAULT 1 COMMENT '是否接收关注通知',
    `message_notification`            tinyint NULL DEFAULT 1 COMMENT '是否接收私信通知',
    `report_notification`             tinyint NULL DEFAULT 1 COMMENT '是否接收举报处理通知',
    `feedback_notification`           tinyint NULL DEFAULT 1 COMMENT '是否接收反馈处理通知',
    `system_notification`             tinyint NULL DEFAULT 1 COMMENT '是否接收系统通知',
    `email_notification`              tinyint NULL DEFAULT 1 COMMENT '是否通过邮件接收通知',
    `site_notification`               tinyint NULL DEFAULT 1 COMMENT '是否通过站内信接收通知',
    `create_time`                     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`                     datetime NULL DEFAULT NULL COMMENT '更新时间',
    `deleted`                         tinyint  NOT NULL DEFAULT 0 COMMENT '是否已删除(0:未删除,1:已删除)',
    `deleted_time`                    datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
    INDEX                             `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知设置表' ROW_FORMAT = Dynamic;

-- 邮件模板表（email_template）
DROP TABLE IF EXISTS `email_template`;
CREATE TABLE `email_template`
(
    `id`             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `code`           VARCHAR(50)  NOT NULL UNIQUE COMMENT '模板编码',
    `name`           VARCHAR(100) NOT NULL COMMENT '模板名称',
    `subject`        VARCHAR(200) NOT NULL COMMENT '邮件主题',
    `content`        TEXT         NOT NULL COMMENT '邮件内容模板',
    `status`         TINYINT               DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
    `create_user_id` BIGINT       NOT NULL COMMENT '创建人ID',
    `update_user_id` BIGINT COMMENT '更新人ID',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT               DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`   DATETIME COMMENT '删除时间',
    KEY              `idx_code` (`code`),
    KEY              `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='邮件模板表';

-- 短信模板表（sms_template）
DROP TABLE IF EXISTS `sms_template`;
CREATE TABLE `sms_template`
(
    `id`             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `code`           VARCHAR(50)  NOT NULL UNIQUE COMMENT '模板编码',
    `name`           VARCHAR(100) NOT NULL COMMENT '模板名称',
    `content`        TEXT         NOT NULL COMMENT '短信内容模板',
    `status`         TINYINT               DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
    `create_user_id` BIGINT       NOT NULL COMMENT '创建人ID',
    `update_user_id` BIGINT COMMENT '更新人ID',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT               DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`   DATETIME COMMENT '删除时间',
    KEY              `idx_code` (`code`),
    KEY              `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='短信模板表';

-- 系统日志表（system_log）
DROP TABLE IF EXISTS `system_log`;
CREATE TABLE `system_log`
(
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `log_type`        TINYINT      NOT NULL COMMENT '日志类型（0:操作日志,1:登录日志,2:错误日志,3:性能日志）',
    `user_id`         BIGINT COMMENT '操作用户ID',
    `username`        VARCHAR(50) COMMENT '操作用户名',
    `ip_address`      VARCHAR(50) COMMENT '操作IP地址',
    `user_agent`      TEXT COMMENT '用户代理信息',
    `action`          VARCHAR(100) NOT NULL COMMENT '操作动作',
    `resource`        VARCHAR(200) NOT NULL COMMENT '操作资源',
    `request_params`  JSON COMMENT '请求参数',
    `response_result` JSON COMMENT '响应结果',
    `status`          TINYINT      NOT NULL COMMENT '操作状态（0:失败,1:成功）',
    `error_message`   TEXT COMMENT '错误信息',
    `execution_time`  INT COMMENT '执行时间（毫秒）',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT               DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`    DATETIME COMMENT '删除时间',
    KEY               `idx_log_type` (`log_type`),
    KEY               `idx_user_id` (`user_id`),
    KEY               `idx_username` (`username`),
    KEY               `idx_ip_address` (`ip_address`),
    KEY               `idx_action` (`action`),
    KEY               `idx_resource` (`resource`),
    KEY               `idx_status` (`status`),
    KEY               `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统日志表';

-- 系统配置表（system_config）
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`
(
    `id`             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `config_key`     VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    `config_value`   TEXT         NOT NULL COMMENT '配置值',
    `description`    TEXT COMMENT '配置描述',
    `type`           VARCHAR(20)  NOT NULL COMMENT '配置类型（string,number,boolean,json）',
    `status`         TINYINT               DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
    `create_user_id` BIGINT       NOT NULL COMMENT '创建人ID',
    `update_user_id` BIGINT COMMENT '更新人ID',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT               DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`   DATETIME COMMENT '删除时间',
    KEY              `idx_config_key` (`config_key`),
    KEY              `idx_type` (`type`),
    KEY              `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置表';

-- 数据统计表（statistics）
DROP TABLE IF EXISTS `statistics`;
CREATE TABLE `statistics`
(
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `stat_type`    VARCHAR(50) NOT NULL COMMENT '统计类型（daily,weekly,monthly,yearly）',
    `stat_date`    DATETIME    NOT NULL COMMENT '统计日期',
    `stat_data`    JSON        NOT NULL COMMENT '统计数据',
    `create_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT              DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time` DATETIME COMMENT '删除时间',
    UNIQUE KEY `uk_stat_type_date` (`stat_type`, `stat_date`),
    KEY            `idx_stat_type` (`stat_type`),
    KEY            `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据统计表';

-- 标签使用统计表（tag_stat）
DROP TABLE IF EXISTS `tag_stat`;
CREATE TABLE `tag_stat`
(
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `tag_id`       BIGINT   NOT NULL COMMENT '标签ID',
    `stat_date`    DATE     NOT NULL COMMENT '统计日期',
    `use_count`    INT               DEFAULT 0 COMMENT '使用次数',
    `create_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT           DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time` DATETIME COMMENT '删除时间',
    UNIQUE KEY `uk_tag_date` (`tag_id`, `stat_date`),
    KEY            `idx_tag_id` (`tag_id`),
    KEY            `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标签使用统计表';

-- 搜索历史表（search_history）
DROP TABLE IF EXISTS `search_history`;
CREATE TABLE `search_history`
(
    `id`               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id`          BIGINT       NOT NULL COMMENT '用户ID',
    `keyword`          VARCHAR(100) NOT NULL COMMENT '搜索关键词',
    `search_type`      VARCHAR(20)  NOT NULL COMMENT '搜索类型（article,user,tag）',
    `search_count`     INT                   DEFAULT 1 COMMENT '搜索次数',
    `last_search_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后搜索时间',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          TINYINT               DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`     DATETIME COMMENT '删除时间',
    KEY                `idx_user_id` (`user_id`),
    KEY                `idx_keyword` (`keyword`),
    KEY                `idx_search_type` (`search_type`),
    KEY                `idx_last_search_time` (`last_search_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='搜索历史表';

-- 搜索热词表（search_hot_word）
DROP TABLE IF EXISTS `search_hot_word`;
CREATE TABLE `search_hot_word`
(
    `id`           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `keyword`      VARCHAR(100) NOT NULL UNIQUE COMMENT '热词',
    `search_count` INT                   DEFAULT 0 COMMENT '搜索次数',
    `status`       TINYINT               DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT               DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time` DATETIME COMMENT '删除时间',
    KEY            `idx_keyword` (`keyword`),
    KEY            `idx_search_count` (`search_count`),
    KEY            `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='搜索热词表';

-- 推荐配置表（recommendation_config）
DROP TABLE IF EXISTS `recommendation_config`;
CREATE TABLE `recommendation_config`
(
    `id`             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `config_type`    VARCHAR(50)  NOT NULL COMMENT '配置类型（article,tag,user）',
    `config_key`     VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value`   TEXT         NOT NULL COMMENT '配置值',
    `description`    TEXT COMMENT '配置描述',
    `status`         TINYINT               DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
    `create_user_id` BIGINT       NOT NULL COMMENT '创建人ID',
    `update_user_id` BIGINT COMMENT '更新人ID',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT               DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`   DATETIME COMMENT '删除时间',
    UNIQUE KEY `uk_config_type_key` (`config_type`, `config_key`),
    KEY              `idx_config_type` (`config_type`),
    KEY              `idx_config_key` (`config_key`),
    KEY              `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='推荐配置表';

-- 文章推荐关系表（article_recommendation）
DROP TABLE IF EXISTS `article_recommendation`;
CREATE TABLE `article_recommendation`
(
    `id`                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `article_id`          BIGINT      NOT NULL COMMENT '文章ID',
    `related_article_id`  BIGINT      NOT NULL COMMENT '相关文章ID',
    `recommendation_type` VARCHAR(50) NOT NULL COMMENT '推荐类型（related,hot,new）',
    `score` DOUBLE DEFAULT 0 COMMENT '推荐分数',
    `create_time`         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             TINYINT              DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`        DATETIME COMMENT '删除时间',
    UNIQUE KEY `uk_article_related` (`article_id`, `related_article_id`, `recommendation_type`),
    KEY                   `idx_article_id` (`article_id`),
    KEY                   `idx_related_article_id` (`related_article_id`),
    KEY                   `idx_recommendation_type` (`recommendation_type`),
    KEY                   `idx_score` (`score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章推荐关系表';

-- 标签推荐关系表（tag_recommendation）
DROP TABLE IF EXISTS `tag_recommendation`;
CREATE TABLE `tag_recommendation`
(
    `id`             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `tag_id`         BIGINT   NOT NULL COMMENT '标签ID',
    `related_tag_id` BIGINT   NOT NULL COMMENT '相关标签ID',
    `score` DOUBLE DEFAULT 0 COMMENT '推荐分数',
    `create_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT           DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
    `deleted_time`   DATETIME COMMENT '删除时间',
    UNIQUE KEY `uk_tag_related` (`tag_id`, `related_tag_id`),
    KEY              `idx_tag_id` (`tag_id`),
    KEY              `idx_related_tag_id` (`related_tag_id`),
    KEY              `idx_score` (`score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标签推荐关系表';

-- 阅读历史表（reading_history）
DROP TABLE IF EXISTS `reading_history`;
CREATE TABLE `reading_history`
(
    `id`              bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`         bigint NOT NULL COMMENT '用户ID',
    `article_id`      bigint NOT NULL COMMENT '文章ID',
    `progress`        int NULL DEFAULT 0 COMMENT '阅读进度（百分比）',
    `duration`        int NULL DEFAULT 0 COMMENT '阅读时长（分钟）',
    `last_read_time`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后阅读时间',
    `scroll_position` int NULL DEFAULT 0 COMMENT '滚动位置',
    `create_time`     datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime NULL DEFAULT NULL COMMENT '更新时间',
    `deleted`         tinyint NULL DEFAULT 0 COMMENT '是否已删除(0:未删除,1:已删除)',
    `deleted_time`    datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_user_article`(`user_id` ASC, `article_id` ASC) USING BTREE,
    INDEX             `idx_user_id`(`user_id` ASC) USING BTREE,
    INDEX             `idx_article_id`(`article_id` ASC) USING BTREE,
    INDEX             `idx_last_read_time`(`last_read_time` ASC) USING BTREE,
    CONSTRAINT `fk_reading_history_article` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `fk_reading_history_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '阅读历史表' ROW_FORMAT = Dynamic;

-- 仪表盘统计数据表
DROP TABLE IF EXISTS `dashboard_stats`;
CREATE TABLE `dashboard_stats`
(
    `id`           bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `stat_key`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计键（如 total_users、views_2026-04-02）',
    `stat_value`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '统计值（支持不同类型的数据）',
    `data_type`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '数据类型（如 counter、trend、distribution）',
    `time_value`   varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '时间值（如具体日期 2026-04-02 或月份 2026-04，可为空）',
    `create_time`  datetime                                                      NOT NULL COMMENT '创建时间',
    `update_time`  datetime                                                      NOT NULL COMMENT '更新时间',
    `deleted`      tinyint                                                       NOT NULL DEFAULT 0 COMMENT '是否已删除(0:未删除,1:已删除)',
    `deleted_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_stat_key_time_value`(`stat_key` ASC, `time_value` ASC) USING BTREE,
    INDEX          `idx_data_type`(`data_type` ASC) USING BTREE,
    INDEX          `idx_time_value`(`time_value` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '仪表盘统计数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 7. 初始化数据
-- ----------------------------

-- 初始化标签数据
INSERT INTO `role` (`name`, `code`, `description`, `system_role`, `level`, `status`, `create_time`, `update_time`,
                    `deleted`, `deleted_time`)
VALUES ('超级管理员', 'SUPER_ADMIN', '系统最高权限，拥有所有操作权限', 1, 100, 1, '2026-01-01 00:00:00',
        '2026-01-01 00:00:00', 0, NULL);
INSERT INTO `role` (`name`, `code`, `description`, `system_role`, `level`, `status`, `create_time`, `update_time`,
                    `deleted`, `deleted_time`)
VALUES ('管理员', 'ADMIN', '拥有用户管理、内容管理等权限', 1, 50, 1, '2026-01-01 00:00:00', '2026-01-01 00:00:00', 0,
        NULL);
INSERT INTO `role` (`name`, `code`, `description`, `system_role`, `level`, `status`, `create_time`, `update_time`,
                    `deleted`, `deleted_time`)
VALUES ('普通用户', 'USER', '拥有发布文章、评论等基本权限', 1, 10, 1, '2026-01-01 00:00:00', '2026-01-01 00:00:00', 0,
        NULL);
INSERT INTO `role` (`name`, `code`, `description`, `system_role`, `level`, `status`, `create_time`, `update_time`,
                    `deleted`, `deleted_time`)
VALUES ('访客', 'GUEST', '仅拥有浏览权限，不能发布内容', 1, 1, 1, '2026-01-01 00:00:00', '2026-01-01 00:00:00', 0,
        NULL);

-- 初始化数据
INSERT INTO `category` (`name`,
                        `slug`,
                        `parent_id`,
                        `sort_order`,
                        `description`,
                        `article_count`,
                        `status`,
                        `create_time`,
                        `update_time`,
                        `category_version`,
                        `deleted`,
                        `deleted_time`)
VALUES ('技术分享', 'tech-share', 0, 1, '技术干货、经验总结、编程技巧分享', 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('前端开发', 'frontend', 0, 2, 'Vue、React、JavaScript、CSS、小程序等前端技术', 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('后端开发', 'backend', 0, 3, 'Java、SpringBoot、Go、Python、微服务等后端技术', 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('数据库', 'database', 0, 4, 'MySQL、Redis、MongoDB、PostgreSQL 等数据库技术', 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('运维部署', 'devops', 0, 5, 'Linux、Docker、Nginx、CI/CD、服务器运维', 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('工具效率', 'tools', 0, 6, '效率工具、开发插件、软件推荐、提高工作效率', 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('项目实战', 'project', 0, 7, '实战项目教程、源码解析、项目开发经验', 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('学习笔记', 'notes', 0, 8, '学习过程中的笔记、知识点总结', 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('生活随笔', 'life', 0, 9, '生活记录、感悟、日常分享', 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('资源分享', 'resources', 0, 10, '优质学习资源、教程、电子书分享', 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('面试总结', 'interview', 0, 11, '面试经验、大厂真题、技术面试总结', 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('开源推荐', 'open-source', 0, 12, '优质开源项目推荐、源码解读', 0, 1, NOW(), NOW(), 1, 0, NULL);


INSERT INTO `tag` (`name`,
                   `slug`,
                   `description`,
                   `article_count`,
                   `user_id`,
                   `usage_count`,
                   `status`,
                   `create_time`,
                   `update_time`,
                   `tag_version`,
                   `deleted`,
                   `deleted_time`)
VALUES ('Java', 'java', 'Java后端开发相关技术', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('SpringBoot', 'spring-boot', 'SpringBoot框架实战', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('MySQL', 'mysql', 'MySQL数据库优化与实战', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('Redis', 'redis', 'Redis缓存、分布式锁等使用', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('Vue', 'vue', 'Vue2/Vue3前端开发', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('React', 'react', 'React及生态技术', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('JavaScript', 'javascript', 'JS基础与进阶', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('CSS', 'css', '样式布局、动画特效', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('微服务', 'microservice', '微服务架构设计', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('Docker', 'docker', '容器化部署', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('Linux', 'linux', 'Linux运维与命令', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('Nginx', 'nginx', 'Nginx配置与代理', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('面试', 'interview', '面试题与经验分享', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('源码分析', 'source-code', '框架源码阅读与解析', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('并发编程', 'concurrent', '多线程、高并发相关', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('JVM', 'jvm', 'JVM调优与原理', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('Git', 'git', 'Git版本控制技巧', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('IDEA', 'idea', '开发工具使用技巧', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('问题排查', 'troubleshooting', '线上问题与Bug解决', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL),
       ('学习笔记', 'study-notes', '学习记录与总结', 0, 1, 0, 1, NOW(), NOW(), 1, 0, NULL);