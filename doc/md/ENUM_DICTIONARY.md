# 枚举类型字典

## 1. 通用枚举

### 1.1 基础枚举接口

#### EnumCode
- 描述：所有枚举类型的基础接口
- 方法：
  - `code()`：获取枚举值的编码
  - `desc()`：获取枚举值的描述

## 2. 文章相关枚举

### 2.1 ArticleStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| DRAFT | 1 | 草稿 |
| PENDING | 2 | 待发布 |
| PUBLISHED | 3 | 已发布 |
| OFFLINE | 4 | 已下架 |
| RECYCLE | 5 | 回收站 |

### 2.2 OriginalStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| REPRINT | 0 | 转载 |
| ORIGINAL | 1 | 原创 |

### 2.3 RecommendStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| NOT_RECOMMENDED | 0 | 不推荐 |
| RECOMMENDED | 1 | 推荐 |

### 2.4 TopStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| NOT_TOP | 0 | 不置顶 |
| TOP | 1 | 置顶 |

## 3. 认证相关枚举

### 3.1 AccountType
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| LOCAL | 1 | 本地账号 |
| GITHUB | 2 | GitHub账号 |
| QQ | 3 | QQ账号 |
| WECHAT | 4 | 微信账号 |

### 3.2 AuthOperationType
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| LOGIN | 1 | 登录 |
| REGISTER | 2 | 注册 |
| PASSWORD_RESET | 3 | 密码重置 |
| EMAIL_VERIFY | 4 | 邮箱验证 |
| PHONE_VERIFY | 5 | 手机验证 |

### 3.3 AuthType
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| USERNAME | 1 | 用户名 |
| EMAIL | 2 | 邮箱 |
| PHONE | 3 | 手机号 |
| GITHUB | 4 | GitHub |
| QQ | 5 | QQ |
| WECHAT | 6 | 微信 |

## 4. 通用状态枚举

### 4.1 AllowStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| DISALLOW | 0 | 不允许 |
| ALLOW | 1 | 允许 |

### 4.2 DefaultStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| NOT_DEFAULT | 0 | 非默认 |
| DEFAULT | 1 | 默认 |

### 4.3 DeleteStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| NOT_DELETED | 0 | 未删除 |
| DELETED | 1 | 已删除 |

### 4.4 Priority
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| LOW | 0 | 低优先级 |
| MEDIUM | 1 | 中优先级 |
| HIGH | 2 | 高优先级 |

### 4.5 StatusEnum
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| DISABLED | 0 | 禁用 |
| ENABLED | 1 | 启用 |

## 5. 通知相关枚举

### 5.1 NotificationCategory
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| SYSTEM | 1 | 系统通知 |
| ARTICLE | 2 | 文章通知 |
| COMMENT | 3 | 评论通知 |
| FOLLOW | 4 | 关注通知 |
| MESSAGE | 5 | 私信通知 |
| REPORT | 6 | 举报通知 |
| FEEDBACK | 7 | 反馈通知 |

### 5.2 NotificationChannel
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| SITE | 1 | 站内信 |
| EMAIL | 2 | 邮件 |
| SMS | 3 | 短信 |
| PUSH | 4 | 推送 |

### 5.3 NotificationStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| UNREAD | 0 | 未读 |
| READ | 1 | 已读 |

### 5.4 NotificationTemplateVariable
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| USERNAME | 1 | 用户名 |
| ARTICLE_TITLE | 2 | 文章标题 |
| COMMENT_CONTENT | 3 | 评论内容 |
| FOLLOWER_NAME | 4 | 关注者名称 |
| MESSAGE_CONTENT | 5 | 消息内容 |
| REPORT_RESULT | 6 | 举报处理结果 |
| FEEDBACK_RESULT | 7 | 反馈处理结果 |
| SYSTEM_CONTENT | 8 | 系统通知内容 |

### 5.5 NotificationType
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| SYSTEM_ANNOUNCEMENT | 1 | 系统公告 |
| ARTICLE_PUBLISH | 2 | 文章发布 |
| ARTICLE_LIKE | 3 | 文章点赞 |
| ARTICLE_COLLECTION | 4 | 文章收藏 |
| ARTICLE_COMMENT | 5 | 文章评论 |
| COMMENT_REPLY | 6 | 评论回复 |
| COMMENT_LIKE | 7 | 评论点赞 |
| FOLLOW | 8 | 关注 |
| MESSAGE | 9 | 私信 |
| REPORT_HANDLED | 10 | 举报处理 |
| FEEDBACK_HANDLED | 11 | 反馈处理 |

## 6. 用户相关枚举

### 6.1 Gender
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| UNKNOWN | 0 | 未知 |
| MALE | 1 | 男 |
| FEMALE | 2 | 女 |

### 6.2 UserRoleEnum
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| ADMIN | 1 | 管理员 |
| USER | 2 | 普通用户 |
| AUTHOR | 3 | 作者 |
| MODERATOR | 4 | 版主 |

### 6.3 UserStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| DISABLED | 0 | 禁用 |
| NORMAL | 1 | 正常 |
| PENDING | 2 | 待审核 |

## 7. 其他枚举

### 7.1 AnnouncementType
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| SYSTEM | 0 | 系统公告 |
| ACTIVITY | 1 | 活动通知 |
| MAINTENANCE | 2 | 维护通知 |

### 7.2 CollectionStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| PUBLIC | 0 | 公开 |
| PRIVATE | 1 | 私密 |

### 7.3 CommentCountType
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| ALL | 1 | 全部评论 |
| ARTICLE | 2 | 文章评论 |
| REPLY | 3 | 回复评论 |

### 7.4 ConfigType
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| SYSTEM | 1 | 系统配置 |
| USER | 2 | 用户配置 |
| ARTICLE | 3 | 文章配置 |
| NOTIFICATION | 4 | 通知配置 |

### 7.5 CountType
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| READ | 1 | 阅读量 |
| LIKE | 2 | 点赞数 |
| COMMENT | 3 | 评论数 |
| COLLECTION | 4 | 收藏数 |
| SHARE | 5 | 分享数 |

### 7.6 FeedbackStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| PENDING | 0 | 待处理 |
| PROCESSING | 1 | 处理中 |
| RESOLVED | 2 | 已解决 |
| CLOSED | 3 | 已关闭 |

### 7.7 HandleResultEnum
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| ACCEPTED | 1 | 已接受 |
| REJECTED | 2 | 已拒绝 |
| PENDING | 3 | 待处理 |

### 7.8 MessageType
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| TEXT | 0 | 文本 |
| IMAGE | 1 | 图片 |
| FILE | 2 | 文件 |
| SYSTEM | 3 | 系统通知 |

### 7.9 PushedStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| NOT_PUSHED | 0 | 未推送 |
| PUSHED | 1 | 已推送 |

### 7.10 ReadStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| UNREAD | 0 | 未读 |
| READ | 1 | 已读 |

### 7.11 RecommendType
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| HOT | 1 | 热门推荐 |
| NEW | 2 | 最新推荐 |
| PERSONAL | 3 | 个性化推荐 |

### 7.12 ReportStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| PENDING | 0 | 待处理 |
| PROCESSED | 1 | 已处理 |
| REJECTED | 2 | 已驳回 |

### 7.13 ReportTargetType
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| ARTICLE | 0 | 文章 |
| COMMENT | 1 | 评论 |
| USER | 2 | 用户 |
| MESSAGE | 3 | 私信 |

### 7.14 ReportTypeEnum
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| SPAM | 1 | 垃圾信息 |
| ABUSE | 2 | 辱骂攻击 |
| ILLEGAL | 3 | 违法违规 |
| PLAGIARISM | 4 | 抄袭侵权 |
| PRIVACY | 5 | 侵犯隐私 |
| OTHER | 6 | 其他 |

### 7.15 ReviewStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| PENDING | 0 | 待审核 |
| APPROVED | 1 | 审核通过 |
| REJECTED | 2 | 审核拒绝 |
| APPEALING | 3 | 申诉中 |
| DISABLED | 4 | 禁用 |

### 7.16 SearchType
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| ARTICLE | 1 | 文章搜索 |
| USER | 2 | 用户搜索 |
| TAG | 3 | 标签搜索 |
| CATEGORY | 4 | 分类搜索 |

### 7.17 TemplateType
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| EMAIL | 1 | 邮件模板 |
| SMS | 2 | 短信模板 |
| NOTIFICATION | 3 | 通知模板 |

### 7.18 VerificationStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| UNVERIFIED | 0 | 未验证 |
| VERIFIED | 1 | 已验证 |

### 7.19 VisibleStatus
| 枚举值 | 编码 | 描述 |
|-------|------|------|
| PRIVATE | 0 | 私有 |
| PUBLIC | 1 | 公开 |
| FOLLOWERS_ONLY | 2 | 仅关注者可见 |
