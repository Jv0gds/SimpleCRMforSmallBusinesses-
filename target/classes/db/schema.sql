-- 用户认证与权限管理 - 用户表
-- 用于存储用户基本信息和登录凭证

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID，主键自增',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '用户邮箱，用于登录，必须唯一',
    password VARCHAR(255) NOT NULL COMMENT '用户密码（加密存储）',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT DEFAULT 1 COMMENT '账户状态：1-正常，0-禁用',
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
