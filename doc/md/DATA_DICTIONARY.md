# 基础数据字典

## 1. 数据库表结构说明

### 1.1 用户相关表

#### 1.1.1 用户表（user）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| username | varchar(50) | NOT NULL | 用户名 |
| password | varchar(255) | NOT NULL | 加密后的密码, 用于身份验证 |
| nickname | varchar(50) | NOT NULL | 昵称 |
| role_id | tinyint | NOT NULL | 用户角色ID |
| email | varchar(100) | NULL | 邮箱 |
| email_verified | tinyint | NULL DEFAULT 0 | 邮箱是否已验证（0:未验证,1:已验证） |
| phone | varchar(20) | NULL | 手机号 |
| phone_verified | tinyint | NULL DEFAULT 0 | 手机号是否已验证（0:未验证,1:已验证） |
| avatar | varchar(255) | NULL | 头像URL |
| cover_image | varchar(255) | NULL | 个人主页封面图 |
| signature | varchar(200) | NULL | 个性签名 |
| gender | tinyint | NULL DEFAULT 0 | 性别（0:未知,1:男,2:女） |
| birth_date | date | NULL | 生日 |
| location | varchar(100) | NULL | 所在地区 |
| website | varchar(255) | NULL | 个人网站 |
| follow_count | int | NULL DEFAULT 0 | 关注数 |
| follower_count | int | NULL DEFAULT 0 | 粉丝数 |
| article_count | int | NULL DEFAULT 0 | 文章数 |
| comment_count | int | NULL DEFAULT 0 | 评论数 |
| like_count | int | NULL DEFAULT 0 | 获赞数 |
| last_login_time | datetime | NULL | 最后登录时间 |
| last_login_ip | varchar(50) | NULL | 最后登录IP |
| register_ip | varchar(50) | NULL | 用户注册时的IP地址 |
| privacy | tinyint | NULL DEFAULT 1 | 隐私设置：0-公开, 1-私有, 2-仅关注者可见 |
| status | tinyint | NULL DEFAULT 1 | 状态（0:禁用,1:正常,2:待审核） |
| register_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 用户注册的时间戳 |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | 更新时间 |
| username_last_modified_time | datetime | NULL | 用户名最后修改时间 |
| user_version | int | NULL | 版本号 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.1.2 用户认证表（user_auth）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| user_id | bigint | NOT NULL | 用户ID |
| auth_type | tinyint | NOT NULL | 认证类型（username,email,phone,github,qq,wechat） |
| auth_identifier | varchar(100) | NOT NULL | 认证标识（用户名,邮箱,手机号,第三方用户ID） |
| auth_credential | varchar(255) | NULL | 认证凭证（密码哈希,第三方access_token） |
| credential_expired_at | datetime | NULL | 凭证过期时间（如第三方access_token过期时间） |
| primary_auth | tinyint | NULL DEFAULT 0 | 是否为主认证方式（0:否,1:是） |
| enabled | tinyint | NULL DEFAULT 1 | 是否启用该认证方式（0:禁用,1:启用） |
| last_auth_time | datetime | NULL | 最后认证时间 |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.1.3 角色表（role）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| name | varchar(50) | NOT NULL | 角色名称 |
| code | varchar(50) | NOT NULL | 角色编码 |
| description | text | NULL | 角色描述 |
| system_role | tinyint | NULL DEFAULT 0 | 是否为系统角色（0:否,1:是，系统角色不可删除） |
| level | int | NULL DEFAULT 0 | 角色等级（数值越大，权限越高） |
| status | tinyint | NULL DEFAULT 1 | 状态（0:禁用,1:正常） |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.1.4 用户角色关联表（user_role）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| user_id | bigint | NOT NULL | 用户ID |
| role_id | bigint | NOT NULL | 角色ID |
| assigned_by | bigint | NULL | 分配角色的用户ID |
| assigned_at | datetime | NULL DEFAULT CURRENT_TIMESTAMP | 角色分配时间 |
| expires_at | datetime | NULL | 角色过期时间（NULL表示永久有效） |
| status | tinyint | NULL DEFAULT 1 | 状态（0:无效,1:有效） |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

### 1.2 文章相关表

#### 1.2.1 文章表（article）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| user_id | bigint | NOT NULL | 作者ID |
| title | varchar(200) | NOT NULL | 文章标题 |
| summary | text | NULL | 文章摘要 |
| content | longtext | NOT NULL | 文章内容 |
| content_html | longtext | NULL | 文章HTML内容（Markdown转换结果） |
| cover_image | varchar(255) | NULL | 文章封面图URL |
| category_id | bigint | NULL | 分类ID |
| article_status | tinyint | NULL DEFAULT 1 | 文章状态(1:草稿,2:待发布,3:已发布,4:已下架,5:回收站) |
| review_status | tinyint | NULL | 审核状态(0:待审核,1:审核通过,2:审核拒绝,3:申诉中,4:禁用) |
| publish_time | datetime | NULL | 发布时间 |
| read_count | int | NULL DEFAULT 0 | 阅读量 |
| like_count | int | NULL DEFAULT 0 | 点赞数 |
| comment_count | int | NULL DEFAULT 0 | 评论数 |
| collection_count | int | NULL DEFAULT 0 | 收藏数 |
| share_count | int | NULL DEFAULT 0 | 分享数 |
| top | tinyint | NULL DEFAULT 0 | 是否置顶（0:否,1:是） |
| recommended | tinyint | NULL DEFAULT 0 | 是否推荐（0:否,1:是） |
| visible | tinyint | NULL DEFAULT 1 | 可见性状态：0-私有, 1-公开, 2-仅关注者可见 |
| allow_comment | tinyint | NULL DEFAULT 1 | 允许评论状态：0-不允许, 1-允许 |
| allow_forward | tinyint | NULL DEFAULT 1 | 允许转发状态：0-不允许, 1-允许 |
| original | tinyint | NULL DEFAULT 1 | 是否原创（0:转载,1:原创） |
| original_url | varchar(255) | NULL | 转载来源URL |
| views_ip | json | NULL | 阅读IP记录（用于去重） |
| last_edit_time | datetime | NULL | 最后一次编辑的时间 |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |
| meta_title | varchar(200) | NULL | SEO标题 |
| meta_description | text | NULL | SEO描述 |
| meta_keywords | varchar(255) | NULL | SEO关键词 |
| scheduled_publish_time | datetime | NULL | 定时发布时间 |
| share_token | varchar(255) | NULL | 分享令牌 |
| article_version | int | NULL | 文章版本号 |

#### 1.2.2 分类表（category）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| name | varchar(50) | NOT NULL | 分类名称 |
| slug | varchar(50) | NOT NULL | 分类别名（URL友好） |
| parent_id | bigint | NULL DEFAULT 0 | 父分类ID（0表示顶级分类） |
| sort_order | int | NULL DEFAULT 0 | 排序顺序 |
| description | text | NULL | 分类描述 |
| article_count | int | NULL DEFAULT 0 | 分类下文章数量 |
| status | tinyint | NULL DEFAULT 1 | 状态（0:禁用,1:正常） |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| category_version | int | NULL | 版本号 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.2.3 标签表（tag）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| name | varchar(50) | NOT NULL | 标签名称 |
| slug | varchar(50) | NULL | 标签别名（URL友好） |
| description | text | NULL | 标签描述 |
| article_count | int | NULL DEFAULT 0 | 标签下文章数量 |
| user_id | bigint | NULL | 创建者ID |
| usage_count | int | NULL DEFAULT 0 | 标签使用次数 |
| status | tinyint | NULL DEFAULT 1 | 状态（0:禁用,1:正常） |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | 更新时间 |
| tag_version | int | NULL | 版本号 |
| deleted | tinyint | NOT NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.2.4 文章标签关联表（article_tag）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| article_id | bigint | NOT NULL | 文章ID |
| tag_id | bigint | NOT NULL | 标签ID |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.2.5 文章点赞表（article_like）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| article_id | bigint | NOT NULL | 文章ID |
| user_id | bigint | NOT NULL | 用户ID |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.2.6 文章收藏表（article_collection）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| article_id | bigint | NOT NULL | 文章ID |
| user_id | bigint | NOT NULL | 用户ID |
| folder_id | bigint | NOT NULL | 收藏文件夹ID |
| status | tinyint | NOT NULL | 收藏状态（0:公开,1:私密） |
| collect_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 收藏时间 |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.2.7 收藏文件夹表（collection_folder）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| user_id | bigint | NOT NULL | 用户ID |
| name | varchar(50) | NOT NULL | 文件夹名称 |
| description | text | NULL | 文件夹描述 |
| article_count | int | NULL DEFAULT 0 | 文件夹内文章数量 |
| sort_order | int | NULL DEFAULT 0 | 排序顺序 |
| default_folder | tinyint | NULL DEFAULT 0 | 是否默认文件夹（0:否,1:是） |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.2.8 专栏表（column）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| user_id | bigint | NOT NULL | 专栏创建者ID |
| name | varchar(100) | NOT NULL | 专栏名称 |
| slug | varchar(100) | NOT NULL UNIQUE | 专栏别名（URL友好） |
| description | text | NULL | 专栏描述 |
| cover_image | varchar(255) | NULL | 专栏封面图URL |
| article_count | int | NULL DEFAULT 0 | 专栏内文章数量 |
| read_count | int | NULL DEFAULT 0 | 专栏总阅读量 |
| sort_order | int | NULL DEFAULT 0 | 排序顺序 |
| status | tinyint | NULL DEFAULT 1 | 状态（0:禁用,1:正常） |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.2.9 文章专栏关联表（article_column）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| article_id | bigint | NOT NULL | 文章ID |
| column_id | bigint | NOT NULL | 专栏ID |
| sort_order | int | NULL DEFAULT 0 | 文章在专栏内的排序顺序 |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.2.10 文章阅读统计表（article_read_stat）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| article_id | bigint | NOT NULL | 文章ID |
| user_id | bigint | NULL | 用户ID（未登录用户为NULL） |
| ip_address | varchar(50) | NOT NULL | 阅读IP地址 |
| user_agent | text | NULL | 用户代理信息 |
| view_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 阅读时间 |
| read_duration | int | NULL | 阅读时长（秒） |
| complete | tinyint | NULL DEFAULT 0 | 是否完整阅读（0:否,1:是） |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

### 1.3 评论相关表

#### 1.3.1 评论表（comment）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| article_id | bigint | NOT NULL | 文章ID |
| user_id | bigint | NOT NULL | 用户ID |
| parent_id | bigint | NULL | 父评论ID（NULL表示主评论） |
| content | text | NOT NULL | 评论内容 |
| floor | varchar(50) | NULL | 评论楼层号 |
| like_count | int | NULL DEFAULT 0 | 点赞数 |
| reply_count | int | NULL DEFAULT 0 | 回复数 |
| status | tinyint | NULL DEFAULT 1 | 状态（0:待审核,1:已通过,2:已拒绝） |
| top | tinyint | NULL DEFAULT 0 | 是否置顶（0:否,1:是） |
| top_order | int | NULL DEFAULT 0 | 置顶顺序（数值越大，优先级越高） |
| review_user_id | bigint | NULL | 审核人ID |
| review_time | datetime | NULL | 审核时间 |
| review_reason | varchar(200) | NULL | 审核拒绝原因 |
| ip_address | varchar(50) | NULL | 评论IP地址 |
| user_agent | text | NULL | 用户代理信息 |
| mention_user_ids | json | NULL | @提及的用户ID列表 |
| report_count | int | NULL DEFAULT 0 | 被举报次数 |
| comment_version | int | NULL | 评论版本号 |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.3.2 评论点赞表（comment_like）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| comment_id | bigint | NOT NULL | 评论ID |
| user_id | bigint | NOT NULL | 用户ID |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

### 1.4 社交相关表

#### 1.4.1 关注表（follow）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| follower_id | bigint | NOT NULL | 粉丝ID |
| following_id | bigint | NOT NULL | 关注对象ID |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 关注时间 |
| update_time | datetime | NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.4.2 私信表（message）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| sender_id | bigint | NOT NULL | 发送者ID |
| receiver_id | bigint | NOT NULL | 接收者ID |
| content | text | NOT NULL | 私信内容 |
| read | tinyint | NULL DEFAULT 0 | 是否已读（0:未读,1:已读） |
| read_time | datetime | NULL | 已读时间 |
| type | tinyint | NULL DEFAULT 0 | 消息类型（0:文本,1:图片,2:文件,3:系统通知） |
| attachment_url | varchar(255) | NULL | 附件URL（图片或文件） |
| conversation_id | varchar(64) | NOT NULL | 对话ID，由sender_id和receiver_id生成的唯一标识 |
| sequence_id | bigint | NOT NULL | 对话内消息序号，用于保证消息顺序 |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.4.3 通知表（notification）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| user_id | bigint | NOT NULL | 接收通知的用户ID |
| notification_type | int | NOT NULL | 通知类型 |
| notification_category | varchar(255) | NULL | 通知分类 |
| title | text | NOT NULL | 通知标题 |
| content | text | NOT NULL | 通知内容 |
| read_status | tinyint | NULL DEFAULT 0 | 是否已读（0:未读,1:已读） |
| read_time | datetime | NULL | 已读时间 |
| related_id | bigint | NULL | 关联ID（如文章ID、评论ID、私信ID等） |
| related_type | tinyint | NULL | 关联类型（0:文章,1:评论,2:私信,3:用户,4:活动等） |
| sender_id | bigint | NULL | 发送通知的用户ID（系统通知为0） |
| pushed_status | tinyint | NULL DEFAULT 0 | 是否已推送（0:未推送,1:已推送） |
| push_time | datetime | NULL | 推送时间 |
| priority | tinyint | NULL DEFAULT 0 | 通知优先级（0:普通,1:重要,2:紧急） |
| action_url | varchar(255) | NULL | 操作链接（用于直接跳转到相关内容） |
| extra_data | json | NULL | 额外数据（用于存储通知相关的扩展信息） |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.4.4 通知模板表（notification_template）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 模板ID |
| code | varchar(50) | NOT NULL | 模板编码(唯一标识) |
| template_name | varchar(100) | NOT NULL | 模板名称 |
| notification_type | int | NOT NULL | 通知类型(对应NotificationType的code) |
| notification_channel | varchar(20) | NOT NULL DEFAULT 'site' | 通知渠道(site:站内信, email:邮件, sms:短信, push:推送) |
| title_template | varchar(200) | NOT NULL | 通知标题模板(支持占位符如: {{username}}) |
| content_template | text | NOT NULL | 通知内容模板(支持占位符) |
| action_url_template | varchar(255) | NULL | 操作链接模板 |
| description | varchar(500) | NULL | 模板描述 |
| priority | tinyint | NOT NULL DEFAULT 0 | 优先级(0:普通, 1:重要, 2:紧急) |
| status | tinyint | NOT NULL DEFAULT 1 | 状态(0:禁用, 1:启用) |
| extra_data | varchar(255) | NULL | 额外数据 |
| create_user_id | bigint | NOT NULL | 创建人ID |
| update_user_id | bigint | NULL | 更新人ID |
| template_version | int | NULL | 模板版本 |
| create_username | varchar(255) | NULL | 创建模版作者 |
| update_username | varchar(255) | NULL | 更新模版作者 |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | 更新时间 |
| deleted | tinyint | NOT NULL DEFAULT 0 | 是否已删除(0:未删除, 1:已删除) |
| deleted_time | datetime | NULL | 删除时间 |

### 1.5 举报和反馈表

#### 1.5.1 举报表（report）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| user_id | bigint | NOT NULL | 举报人ID |
| target_type | tinyint | NOT NULL | 举报对象类型（0:文章,1:评论,2:用户,3:私信） |
| target_id | bigint | NOT NULL | 举报对象ID |
| report_type_id | bigint | NOT NULL | 举报类型ID |
| content | text | NOT NULL | 举报内容 |
| evidence_urls | json | NULL | 举报证据URL列表 |
| status | tinyint | NULL DEFAULT 0 | 举报状态（0:待处理,1:已处理,2:已驳回） |
| handle_user_id | bigint | NULL | 处理人ID |
| handle_time | datetime | NULL | 处理时间 |
| handle_result | text | NULL | 处理结果 |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.5.2 举报类型表（report_type）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| name | varchar(50) | NOT NULL | 举报类型名称 |
| code | varchar(50) | NOT NULL UNIQUE | 举报类型编码 |
| description | text | NULL | 举报类型描述 |
| target_types | json | NOT NULL | 适用对象类型（0:文章,1:评论,2:用户,3:私信） |
| priority | int | NULL DEFAULT 0 | 优先级（数值越大，优先级越高） |
| status | tinyint | NULL DEFAULT 1 | 状态（0:禁用,1:正常） |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.5.3 反馈表（feedback）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| user_id | bigint | NOT NULL | 反馈人ID |
| feedback_type_id | bigint | NOT NULL | 反馈类型ID |
| title | varchar(200) | NOT NULL | 反馈标题 |
| content | text | NOT NULL | 反馈内容 |
| contact_info | varchar(100) | NULL | 联系方式 |
| evidence_urls | json | NULL | 反馈证据URL列表 |
| status | tinyint | NULL DEFAULT 0 | 反馈状态（0:待处理,1:处理中,2:已解决,3:已关闭） |
| handle_user_id | bigint | NULL | 处理人ID |
| handle_time | datetime | NULL | 处理时间 |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.5.4 反馈类型表（feedback_type）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| name | varchar(50) | NOT NULL | 反馈类型名称 |
| code | varchar(50) | NOT NULL UNIQUE | 反馈类型编码 |
| description | text | NULL | 反馈类型描述 |
| status | tinyint | NULL DEFAULT 1 | 状态（0:禁用,1:正常） |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.5.5 反馈回复表（feedback_reply）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| feedback_id | bigint | NOT NULL | 反馈ID |
| user_id | bigint | NOT NULL | 回复人ID |
| content | text | NOT NULL | 回复内容 |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

### 1.6 其他表

#### 1.6.1 敏感词表（sensitive_word）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| word | varchar(50) | NOT NULL UNIQUE | 敏感词 |
| category | varchar(20) | NOT NULL | 敏感词分类 |
| level | tinyint | NOT NULL | 敏感级别（0:低,1:中,2:高） |
| status | tinyint | NULL DEFAULT 1 | 状态（0:禁用,1:正常） |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.6.2 系统公告表（system_announcement）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| title | varchar(200) | NOT NULL | 公告标题 |
| content | text | NOT NULL | 公告内容 |
| type | tinyint | NULL DEFAULT 0 | 公告类型（0:系统公告,1:活动通知,2:维护通知） |
| status | tinyint | NULL DEFAULT 0 | 状态（0:未发布,1:已发布,2:已过期） |
| publish_time | datetime | NULL | 发布时间 |
| expire_time | datetime | NULL | 过期时间 |
| read_count | int | NULL DEFAULT 0 | 阅读量 |
| create_user_id | bigint | NOT NULL | 创建人ID |
| update_user_id | bigint | NULL | 更新人ID |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.6.3 用户通知关联表（user_notification）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| user_id | bigint | NOT NULL | 用户ID |
| notification_id | bigint | NOT NULL | 通知ID |
| read_status | tinyint | NULL DEFAULT 0 | 是否已读（0:未读,1:已读） |
| read_time | datetime | NULL | 已读时间 |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.6.4 通知设置表（notification_setting）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| user_id | bigint | NOT NULL | 用户ID |
| article_publish_notification | tinyint | NULL DEFAULT 1 | 是否接收文章发布通知 |
| article_like_notification | tinyint | NULL DEFAULT 1 | 是否接收文章点赞通知 |
| article_collection_notification | tinyint | NULL DEFAULT 1 | 是否接收文章收藏通知 |
| article_comment_notification | tinyint | NULL DEFAULT 1 | 是否接收文章评论通知 |
| comment_reply_notification | tinyint | NULL DEFAULT 1 | 是否接收评论回复通知 |
| comment_like_notification | tinyint | NULL DEFAULT 1 | 是否接收评论点赞通知 |
| follow_notification | tinyint | NULL DEFAULT 1 | 是否接收关注通知 |
| message_notification | tinyint | NULL DEFAULT 1 | 是否接收私信通知 |
| report_notification | tinyint | NULL DEFAULT 1 | 是否接收举报处理通知 |
| feedback_notification | tinyint | NULL DEFAULT 1 | 是否接收反馈处理通知 |
| system_notification | tinyint | NULL DEFAULT 1 | 是否接收系统通知 |
| email_notification | tinyint | NULL DEFAULT 1 | 是否通过邮件接收通知 |
| site_notification | tinyint | NULL DEFAULT 1 | 是否通过站内信接收通知 |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NULL | 更新时间 |
| deleted | tinyint | NOT NULL DEFAULT 0 | 是否已删除(0:未删除,1:已删除) |
| deleted_time | datetime | NULL | 删除时间 |

#### 1.6.5 邮件模板表（email_template）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | bigint | NOT NULL AUTO_INCREMENT | 主键ID |
| code | varchar(50) | NOT NULL UNIQUE | 模板编码 |
| name | varchar(100) | NOT NULL | 模板名称 |
| subject | varchar(200) | NOT NULL | 邮件主题 |
| content | text | NOT NULL | 邮件内容模板 |
| status | tinyint | NULL DEFAULT 1 | 状态（0:禁用,1:正常） |
| create_user_id | bigint | NOT NULL | 创建人ID |
| update_user_id | bigint | NULL | 更新人ID |
| create_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL DEFAULT CURRENT_TIMESTAMP | 更新时间 |
| deleted | tinyint | NULL DEFAULT 0 | 是否已删除（0:未删除,1:已删除） |
| deleted_time | datetime | NULL | 删除时间 |

## 2. 表之间的关联关系

### 2.1 用户相关表关联
- `user` ← `user_auth`：用户与认证方式一对多
- `user` ← `user_role`：用户与角色多对多
- `role` ← `user_role`：角色与用户多对多

### 2.2 文章相关表关联
- `user` ← `article`：用户与文章一对多
- `article` ← `article_tag`：文章与标签多对多
- `tag` ← `article_tag`：标签与文章多对多
- `category` ← `article`：分类与文章一对多
- `article` ← `article_like`：文章与点赞一对多
- `user` ← `article_like`：用户与点赞一对多
- `article` ← `article_collection`：文章与收藏一对多
- `user` ← `article_collection`：用户与收藏一对多
- `user` ← `collection_folder`：用户与收藏文件夹一对多
- `collection_folder` ← `article_collection`：收藏文件夹与收藏一对多
- `user` ← `column`：用户与专栏一对多
- `column` ← `article_column`：专栏与文章多对多
- `article` ← `article_column`：文章与专栏多对多
- `article` ← `article_read_stat`：文章与阅读统计一对多
- `user` ← `article_read_stat`：用户与阅读统计一对多

### 2.3 评论相关表关联
- `article` ← `comment`：文章与评论一对多
- `user` ← `comment`：用户与评论一对多
- `comment` ← `comment`：评论与回复一对多（自关联）
- `comment` ← `comment_like`：评论与点赞一对多
- `user` ← `comment_like`：用户与评论点赞一对多

### 2.4 社交相关表关联
- `user` ← `follow`：用户与关注一对多（follower_id）
- `user` ← `follow`：用户与被关注一对多（following_id）
- `user` ← `message`：用户与发送私信一对多（sender_id）
- `user` ← `message`：用户与接收私信一对多（receiver_id）
- `user` ← `notification`：用户与通知一对多

### 2.5 举报和反馈表关联
- `user` ← `report`：用户与举报一对多
- `report_type` ← `report`：举报类型与举报一对多
- `user` ← `feedback`：用户与反馈一对多
- `feedback_type` ← `feedback`：反馈类型与反馈一对多
- `feedback` ← `feedback_reply`：反馈与回复一对多
- `user` ← `feedback_reply`：用户与反馈回复一对多

### 2.6 其他表关联
- `user` ← `notification_setting`：用户与通知设置一对一
