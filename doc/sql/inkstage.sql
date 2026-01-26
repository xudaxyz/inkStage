-- InkStage 数据库建表语句
-- 创建时间：2026-01-21
-- 版本：v1.0

-- 设置字符集和排序规则
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 用户相关表
-- ----------------------------

-- 用户表（user）
CREATE TABLE `user` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '加密后的密码, 用于身份验证',
  `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
  `email` VARCHAR(100) UNIQUE COMMENT '邮箱',
  `email_verified` TINYINT DEFAULT 0 COMMENT '邮箱是否已验证（0:未验证,1:已验证）',
  `phone` VARCHAR(20) UNIQUE COMMENT '手机号',
  `phone_verified` TINYINT DEFAULT 0 COMMENT '手机号是否已验证（0:未验证,1:已验证）',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `cover_image` VARCHAR(255) COMMENT '个人主页封面图',
  `signature` VARCHAR(200) COMMENT '个性签名',
  `gender` TINYINT DEFAULT 0 COMMENT '性别（0:未知,1:男,2:女）',
  `birth_date` DATE COMMENT '生日',
  `location` VARCHAR(100) COMMENT '所在地区',
  `website` VARCHAR(255) COMMENT '个人网站',
  `follow_count` INT DEFAULT 0 COMMENT '关注数',
  `follower_count` INT DEFAULT 0 COMMENT '粉丝数',
  `article_count` INT DEFAULT 0 COMMENT '文章数',
  `comment_count` INT DEFAULT 0 COMMENT '评论数',
  `like_count` INT DEFAULT 0 COMMENT '获赞数',
  `last_login_time` DATETIME COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(50) COMMENT '最后登录IP',
  `register_ip` VARCHAR(50) COMMENT '用户注册时的IP地址',
  `privacy` TINYINT DEFAULT 1 COMMENT '隐私设置：0-公开, 1-私有, 2-仅关注者可见',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:禁用,1:正常,2:待审核）',
  `register_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户注册的时间戳',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- 用户认证表（user_auth）
CREATE TABLE `user_auth` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `auth_type` VARCHAR(20) NOT NULL COMMENT '认证类型（username,password,email,phone,github,qq,wechat）',
  `auth_identifier` VARCHAR(100) NOT NULL COMMENT '认证标识（用户名,邮箱,手机号,第三方用户ID）',
  `auth_credential` VARCHAR(255) COMMENT '认证凭证（密码哈希,第三方access_token）',
  `credential_expired_at` DATETIME COMMENT '凭证过期时间（如第三方access_token过期时间）',
  `primary_auth` TINYINT DEFAULT 0 COMMENT '是否为主认证方式（0:否,1:是）',
  `enabled` TINYINT DEFAULT 1 COMMENT '是否启用该认证方式（0:禁用,1:启用）',
  `last_auth_time` DATETIME COMMENT '最后认证时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_user_id` (`user_id`),
  KEY `idx_auth_type` (`auth_type`),
  KEY `idx_auth_identifier` (`auth_identifier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户认证表';

-- 角色表（role）
CREATE TABLE `role` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
  `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
  `description` TEXT COMMENT '角色描述',
  `system_role` TINYINT DEFAULT 0 COMMENT '是否为系统角色（0:否,1:是，系统角色不可删除）',
  `level` INT DEFAULT 0 COMMENT '角色等级（数值越大，权限越高）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

-- 用户角色关联表（user_role）
CREATE TABLE `user_role` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `assigned_by` BIGINT COMMENT '分配角色的用户ID',
  `assigned_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '角色分配时间',
  `expires_at` DATETIME COMMENT '角色过期时间（NULL表示永久有效）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:无效,1:有效）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';

-- ----------------------------
-- 2. 文章相关表
-- ----------------------------

-- 文章表（article）
CREATE TABLE `article` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '作者ID',
  `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
  `summary` TEXT COMMENT '文章摘要',
  `content` LONGTEXT NOT NULL COMMENT '文章内容',
  `content_html` LONGTEXT COMMENT '文章HTML内容（Markdown转换结果）',
  `cover_image` VARCHAR(255) COMMENT '文章封面图URL',
  `author_name` VARCHAR(50) COMMENT '文章作者的名称',
  `category_id` BIGINT COMMENT '分类ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:草稿,1:已发布,2:待审核,3:已下架）',
  `publish_time` DATETIME COMMENT '发布时间',
  `read_count` INT DEFAULT 0 COMMENT '阅读量',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT DEFAULT 0 COMMENT '评论数',
  `collection_count` INT DEFAULT 0 COMMENT '收藏数',
  `share_count` INT DEFAULT 0 COMMENT '分享数',
  `top` TINYINT DEFAULT 0 COMMENT '是否置顶（0:否,1:是）',
  `recommended` TINYINT DEFAULT 0 COMMENT '是否推荐（0:否,1:是）',
  `visible` TINYINT DEFAULT 1 COMMENT '可见性状态：0-私有, 1-公开, 2-仅关注者可见',
  `allow_comment` TINYINT DEFAULT 1 COMMENT '允许评论状态：0-不允许, 1-允许',
  `allow_forward` TINYINT DEFAULT 1 COMMENT '允许转发状态：0-不允许, 1-允许',
  `original` TINYINT DEFAULT 1 COMMENT '是否原创（0:转载,1:原创）',
  `original_url` VARCHAR(255) COMMENT '转载来源URL',
  `views_ip` JSON COMMENT '阅读IP记录（用于去重）',
  `last_edit_time` DATETIME COMMENT '最后一次编辑的时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_top` (`top`),
  KEY `idx_recommended` (`recommended`),
  KEY `idx_visible` (`visible`),
  KEY `idx_original` (`original`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章表';

-- 分类表（category）
CREATE TABLE `category` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `slug` VARCHAR(50) NOT NULL UNIQUE COMMENT '分类别名（URL友好）',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID（0表示顶级分类）',
  `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
  `description` TEXT COMMENT '分类描述',
  `article_count` INT DEFAULT 0 COMMENT '分类下文章数量',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='分类表';

-- 标签表（tag）
CREATE TABLE `tag` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
  `slug` VARCHAR(50) NOT NULL UNIQUE COMMENT '标签别名（URL友好）',
  `description` TEXT COMMENT '标签描述',
  `article_count` INT DEFAULT 0 COMMENT '标签下文章数量',
  `usage_count` INT DEFAULT 0 COMMENT '标签使用次数',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标签表';

-- 文章标签关联表（article_tag）
CREATE TABLE `article_tag` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章标签关联表';

-- 文章点赞表（article_like）
CREATE TABLE `article_like` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章点赞表';

-- 文章收藏表（article_collection）
CREATE TABLE `article_collection` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `folder_id` BIGINT NOT NULL COMMENT '收藏文件夹ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_folder_id` (`folder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章收藏表';

-- 收藏文件夹表（collection_folder）
CREATE TABLE `collection_folder` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '文件夹名称',
  `description` TEXT COMMENT '文件夹描述',
  `article_count` INT DEFAULT 0 COMMENT '文件夹内文章数量',
  `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
  `default_folder` TINYINT DEFAULT 0 COMMENT '是否默认文件夹（0:否,1:是）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_user_id` (`user_id`),
  KEY `idx_default_folder` (`default_folder`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收藏文件夹表';

-- 专栏表（column）
CREATE TABLE `column` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '专栏创建者ID',
  `name` VARCHAR(100) NOT NULL COMMENT '专栏名称',
  `slug` VARCHAR(100) NOT NULL UNIQUE COMMENT '专栏别名（URL友好）',
  `description` TEXT COMMENT '专栏描述',
  `cover_image` VARCHAR(255) COMMENT '专栏封面图URL',
  `article_count` INT DEFAULT 0 COMMENT '专栏内文章数量',
  `read_count` INT DEFAULT 0 COMMENT '专栏总阅读量',
  `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='专栏表';

-- 文章专栏关联表（article_column）
CREATE TABLE `article_column` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `column_id` BIGINT NOT NULL COMMENT '专栏ID',
  `sort_order` INT DEFAULT 0 COMMENT '文章在专栏内的排序顺序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  UNIQUE KEY `uk_article_column` (`article_id`, `column_id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_column_id` (`column_id`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章专栏关联表';

-- 文章阅读统计表（article_read_stat）
CREATE TABLE `article_read_stat` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `user_id` BIGINT COMMENT '用户ID（未登录用户为NULL）',
  `ip_address` VARCHAR(50) NOT NULL COMMENT '阅读IP地址',
  `user_agent` TEXT COMMENT '用户代理信息',
  `view_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  `read_duration` INT COMMENT '阅读时长（秒）',
  `complete` TINYINT DEFAULT 0 COMMENT '是否完整阅读（0:否,1:是）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_article_id` (`article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_ip_address` (`ip_address`),
  KEY `idx_view_time` (`view_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章阅读统计表';

-- ----------------------------
-- 3. 评论相关表
-- ----------------------------

-- 评论表（comment）
CREATE TABLE `comment` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID（NULL表示主评论）',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `floor` VARCHAR(50) COMMENT '评论楼层号',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `reply_count` INT DEFAULT 0 COMMENT '回复数',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:待审核,1:已通过,2:已拒绝）',
  `top` TINYINT DEFAULT 0 COMMENT '是否置顶（0:否,1:是）',
  `top_order` INT DEFAULT 0 COMMENT '置顶顺序（数值越大，优先级越高）',
  `review_user_id` BIGINT COMMENT '审核人ID',
  `review_time` DATETIME COMMENT '审核时间',
  `review_reason` VARCHAR(200) COMMENT '审核拒绝原因',
  `ip_address` VARCHAR(50) COMMENT '评论IP地址',
  `user_agent` TEXT COMMENT '用户代理信息',
  `mention_user_ids` JSON COMMENT '@提及的用户ID列表',
  `report_count` INT DEFAULT 0 COMMENT '被举报次数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_article_id` (`article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_top` (`top`),
  KEY `idx_top_order` (`top_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论表';

-- 评论点赞表（comment_like）
CREATE TABLE `comment_like` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `comment_id` BIGINT NOT NULL COMMENT '评论ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  UNIQUE KEY `uk_comment_user` (`comment_id`, `user_id`),
  KEY `idx_comment_id` (`comment_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论点赞表';

-- ----------------------------
-- 4. 社交相关表
-- ----------------------------

-- 关注表（follow）
CREATE TABLE `follow` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `follower_id` BIGINT NOT NULL COMMENT '粉丝ID',
  `following_id` BIGINT NOT NULL COMMENT '关注对象ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  UNIQUE KEY `uk_follower_following` (`follower_id`, `following_id`, `deleted`),
  KEY `idx_follower_id` (`follower_id`),
  KEY `idx_following_id` (`following_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='关注表';

-- 私信表（message）
CREATE TABLE `message` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
  `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
  `content` TEXT NOT NULL COMMENT '私信内容',
  `read` TINYINT DEFAULT 0 COMMENT '是否已读（0:未读,1:已读）',
  `read_time` DATETIME COMMENT '已读时间',
  `type` TINYINT DEFAULT 0 COMMENT '消息类型（0:文本,1:图片,2:文件,3:系统通知）',
  `attachment_url` VARCHAR(255) COMMENT '附件URL（图片或文件）',
  `conversation_id` VARCHAR(64) NOT NULL COMMENT '对话ID，由sender_id和receiver_id生成的唯一标识',
  `sequence_id` BIGINT NOT NULL COMMENT '对话内消息序号，用于保证消息顺序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_sequence_id` (`sequence_id`),
  KEY `idx_read` (`read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='私信表';

-- 通知表（notification）
CREATE TABLE `notification` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '接收通知的用户ID',
  `type` TINYINT NOT NULL COMMENT '通知类型（0:关注,1:点赞,2:评论,3:私信,4:新文章,5:系统通知,6:活动通知）',
  `content` TEXT NOT NULL COMMENT '通知内容',
  `read` TINYINT DEFAULT 0 COMMENT '是否已读（0:未读,1:已读）',
  `read_time` DATETIME COMMENT '已读时间',
  `related_id` BIGINT COMMENT '关联ID（如文章ID、评论ID、私信ID等）',
  `related_type` TINYINT COMMENT '关联类型（0:文章,1:评论,2:私信,3:用户,4:活动等）',
  `sender_id` BIGINT COMMENT '发送通知的用户ID（系统通知为0）',
  `pushed` TINYINT DEFAULT 0 COMMENT '是否已推送（0:未推送,1:已推送）',
  `push_time` DATETIME COMMENT '推送时间',
  `priority` TINYINT DEFAULT 0 COMMENT '通知优先级（0:普通,1:重要,2:紧急）',
  `action_url` VARCHAR(255) COMMENT '操作链接（用于直接跳转到相关内容）',
  `extra_data` JSON COMMENT '额外数据（用于存储通知相关的扩展信息）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_read` (`read`),
  KEY `idx_pushed` (`pushed`),
  KEY `idx_priority` (`priority`),
  KEY `idx_related_id` (`related_id`),
  KEY `idx_sender_id` (`sender_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知表';

-- ----------------------------
-- 5. 举报和反馈表
-- ----------------------------

-- 举报表（report）
CREATE TABLE `report` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '举报人ID',
  `target_type` TINYINT NOT NULL COMMENT '举报对象类型（0:文章,1:评论,2:用户,3:私信）',
  `target_id` BIGINT NOT NULL COMMENT '举报对象ID',
  `report_type_id` BIGINT NOT NULL COMMENT '举报类型ID',
  `content` TEXT NOT NULL COMMENT '举报内容',
  `evidence_urls` JSON COMMENT '举报证据URL列表',
  `status` TINYINT DEFAULT 0 COMMENT '举报状态（0:待处理,1:已处理,2:已驳回）',
  `handle_user_id` BIGINT COMMENT '处理人ID',
  `handle_time` DATETIME COMMENT '处理时间',
  `handle_result` TEXT COMMENT '处理结果',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target_type` (`target_type`),
  KEY `idx_target_id` (`target_id`),
  KEY `idx_report_type_id` (`report_type_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='举报表';

-- 举报类型表（report_type）
CREATE TABLE `report_type` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '举报类型名称',
  `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '举报类型编码',
  `description` TEXT COMMENT '举报类型描述',
  `target_types` JSON NOT NULL COMMENT '适用对象类型（0:文章,1:评论,2:用户,3:私信）',
  `priority` INT DEFAULT 0 COMMENT '优先级（数值越大，优先级越高）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_code` (`code`),
  KEY `idx_priority` (`priority`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='举报类型表';

-- 反馈表（feedback）
CREATE TABLE `feedback` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '反馈人ID',
  `feedback_type_id` BIGINT NOT NULL COMMENT '反馈类型ID',
  `title` VARCHAR(200) NOT NULL COMMENT '反馈标题',
  `content` TEXT NOT NULL COMMENT '反馈内容',
  `contact_info` VARCHAR(100) COMMENT '联系方式',
  `evidence_urls` JSON COMMENT '反馈证据URL列表',
  `status` TINYINT DEFAULT 0 COMMENT '反馈状态（0:待处理,1:处理中,2:已解决,3:已关闭）',
  `handle_user_id` BIGINT COMMENT '处理人ID',
  `handle_time` DATETIME COMMENT '处理时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_user_id` (`user_id`),
  KEY `idx_feedback_type_id` (`feedback_type_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='反馈表';

-- 反馈类型表（feedback_type）
CREATE TABLE `feedback_type` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '反馈类型名称',
  `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '反馈类型编码',
  `description` TEXT COMMENT '反馈类型描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_code` (`code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='反馈类型表';

-- 反馈回复表（feedback_reply）
CREATE TABLE `feedback_reply` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `feedback_id` BIGINT NOT NULL COMMENT '反馈ID',
  `user_id` BIGINT NOT NULL COMMENT '回复人ID',
  `content` TEXT NOT NULL COMMENT '回复内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_feedback_id` (`feedback_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='反馈回复表';

-- ----------------------------
-- 6. 其他表
-- ----------------------------

-- 敏感词表（sensitive_word）
CREATE TABLE `sensitive_word` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `word` VARCHAR(50) NOT NULL UNIQUE COMMENT '敏感词',
  `category` VARCHAR(20) NOT NULL COMMENT '敏感词分类',
  `level` TINYINT NOT NULL COMMENT '敏感级别（0:低,1:中,2:高）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_word` (`word`),
  KEY `idx_category` (`category`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='敏感词表';

-- 系统公告表（system_announcement）
CREATE TABLE `system_announcement` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
  `content` TEXT NOT NULL COMMENT '公告内容',
  `type` TINYINT DEFAULT 0 COMMENT '公告类型（0:系统公告,1:活动通知,2:维护通知）',
  `status` TINYINT DEFAULT 0 COMMENT '状态（0:未发布,1:已发布,2:已过期）',
  `publish_time` DATETIME COMMENT '发布时间',
  `expire_time` DATETIME COMMENT '过期时间',
  `read_count` INT DEFAULT 0 COMMENT '阅读量',
  `create_user_id` BIGINT NOT NULL COMMENT '创建人ID',
  `update_user_id` BIGINT COMMENT '更新人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统公告表';

-- 用户通知关联表（user_notification）
CREATE TABLE `user_notification` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `notification_id` BIGINT NOT NULL COMMENT '通知ID',
  `read` TINYINT DEFAULT 0 COMMENT '是否已读（0:未读,1:已读）',
  `read_time` DATETIME COMMENT '已读时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_user_id` (`user_id`),
  KEY `idx_notification_id` (`notification_id`),
  KEY `idx_read` (`read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户通知关联表';

-- 邮件模板表（email_template）
CREATE TABLE `email_template` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '模板编码',
  `name` VARCHAR(100) NOT NULL COMMENT '模板名称',
  `subject` VARCHAR(200) NOT NULL COMMENT '邮件主题',
  `content` TEXT NOT NULL COMMENT '邮件内容模板',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
  `create_user_id` BIGINT NOT NULL COMMENT '创建人ID',
  `update_user_id` BIGINT COMMENT '更新人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_code` (`code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='邮件模板表';

-- 短信模板表（sms_template）
CREATE TABLE `sms_template` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '模板编码',
  `name` VARCHAR(100) NOT NULL COMMENT '模板名称',
  `content` TEXT NOT NULL COMMENT '短信内容模板',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
  `create_user_id` BIGINT NOT NULL COMMENT '创建人ID',
  `update_user_id` BIGINT COMMENT '更新人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_code` (`code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='短信模板表';

-- 系统日志表（system_log）
CREATE TABLE `system_log` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `log_type` TINYINT NOT NULL COMMENT '日志类型（0:操作日志,1:登录日志,2:错误日志,3:性能日志）',
  `user_id` BIGINT COMMENT '操作用户ID',
  `username` VARCHAR(50) COMMENT '操作用户名',
  `ip_address` VARCHAR(50) COMMENT '操作IP地址',
  `user_agent` TEXT COMMENT '用户代理信息',
  `action` VARCHAR(100) NOT NULL COMMENT '操作动作',
  `resource` VARCHAR(200) NOT NULL COMMENT '操作资源',
  `request_params` JSON COMMENT '请求参数',
  `response_result` JSON COMMENT '响应结果',
  `status` TINYINT NOT NULL COMMENT '操作状态（0:失败,1:成功）',
  `error_message` TEXT COMMENT '错误信息',
  `execution_time` INT COMMENT '执行时间（毫秒）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_log_type` (`log_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_username` (`username`),
  KEY `idx_ip_address` (`ip_address`),
  KEY `idx_action` (`action`),
  KEY `idx_resource` (`resource`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统日志表';

-- 系统配置表（system_config）
CREATE TABLE `system_config` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `config_key` VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
  `config_value` TEXT NOT NULL COMMENT '配置值',
  `description` TEXT COMMENT '配置描述',
  `type` VARCHAR(20) NOT NULL COMMENT '配置类型（string,number,boolean,json）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
  `create_user_id` BIGINT NOT NULL COMMENT '创建人ID',
  `update_user_id` BIGINT COMMENT '更新人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_config_key` (`config_key`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置表';

-- 数据统计表（statistics）
CREATE TABLE `statistics` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `stat_type` VARCHAR(50) NOT NULL COMMENT '统计类型（daily,weekly,monthly,yearly）',
  `stat_date` DATETIME NOT NULL COMMENT '统计日期',
  `stat_data` JSON NOT NULL COMMENT '统计数据',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  UNIQUE KEY `uk_stat_type_date` (`stat_type`, `stat_date`),
  KEY `idx_stat_type` (`stat_type`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据统计表';

-- 标签使用统计表（tag_stat）
CREATE TABLE `tag_stat` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `use_count` INT DEFAULT 0 COMMENT '使用次数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  UNIQUE KEY `uk_tag_date` (`tag_id`, `stat_date`),
  KEY `idx_tag_id` (`tag_id`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标签使用统计表';

-- 搜索历史表（search_history）
CREATE TABLE `search_history` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `keyword` VARCHAR(100) NOT NULL COMMENT '搜索关键词',
  `search_type` VARCHAR(20) NOT NULL COMMENT '搜索类型（article,user,tag）',
  `search_count` INT DEFAULT 1 COMMENT '搜索次数',
  `last_search_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后搜索时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_user_id` (`user_id`),
  KEY `idx_keyword` (`keyword`),
  KEY `idx_search_type` (`search_type`),
  KEY `idx_last_search_time` (`last_search_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='搜索历史表';

-- 搜索热词表（search_hot_word）
CREATE TABLE `search_hot_word` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `keyword` VARCHAR(100) NOT NULL UNIQUE COMMENT '热词',
  `search_count` INT DEFAULT 0 COMMENT '搜索次数',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  KEY `idx_keyword` (`keyword`),
  KEY `idx_search_count` (`search_count`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='搜索热词表';

-- 推荐配置表（recommendation_config）
CREATE TABLE `recommendation_config` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `config_type` VARCHAR(50) NOT NULL COMMENT '配置类型（article,tag,user）',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT NOT NULL COMMENT '配置值',
  `description` TEXT COMMENT '配置描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0:禁用,1:正常）',
  `create_user_id` BIGINT NOT NULL COMMENT '创建人ID',
  `update_user_id` BIGINT COMMENT '更新人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  UNIQUE KEY `uk_config_type_key` (`config_type`, `config_key`),
  KEY `idx_config_type` (`config_type`),
  KEY `idx_config_key` (`config_key`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='推荐配置表';

-- 文章推荐关系表（article_recommendation）
CREATE TABLE `article_recommendation` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `related_article_id` BIGINT NOT NULL COMMENT '相关文章ID',
  `recommendation_type` VARCHAR(50) NOT NULL COMMENT '推荐类型（related,hot,new）',
  `score` DOUBLE DEFAULT 0 COMMENT '推荐分数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  UNIQUE KEY `uk_article_related` (`article_id`, `related_article_id`, `recommendation_type`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_related_article_id` (`related_article_id`),
  KEY `idx_recommendation_type` (`recommendation_type`),
  KEY `idx_score` (`score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章推荐关系表';

-- 标签推荐关系表（tag_recommendation）
CREATE TABLE `tag_recommendation` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  `related_tag_id` BIGINT NOT NULL COMMENT '相关标签ID',
  `score` DOUBLE DEFAULT 0 COMMENT '推荐分数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除（0:未删除,1:已删除）',
  `deleted_time` DATETIME COMMENT '删除时间',
  UNIQUE KEY `uk_tag_related` (`tag_id`, `related_tag_id`),
  KEY `idx_tag_id` (`tag_id`),
  KEY `idx_related_tag_id` (`related_tag_id`),
  KEY `idx_score` (`score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标签推荐关系表';

SET FOREIGN_KEY_CHECKS = 1;
