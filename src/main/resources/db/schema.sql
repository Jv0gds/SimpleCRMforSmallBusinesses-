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

-- 角色访问控制 - 角色表
-- 用于存储系统角色信息（访客、普通用户、销售代表、销售经理、管理员）

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID，主键自增',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称：GUEST/USER/SALES_REP/SALES_MANAGER/ADMIN',
    description VARCHAR(200) COMMENT '角色描述',
    level INT NOT NULL COMMENT '权限级别：1-访客, 2-普通用户, 3-销售代表, 4-销售经理, 5-管理员',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_role_name (name),
    INDEX idx_role_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 角色访问控制 - 权限表
-- 用于存储细粒度的操作权限

CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID，主键自增',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '权限名称：如READ_CUSTOMER、EDIT_OPPORTUNITY',
    resource VARCHAR(50) NOT NULL COMMENT '资源类型：CUSTOMER/OPPORTUNITY/USER/REPORT/SYSTEM',
    action VARCHAR(50) NOT NULL COMMENT '操作类型：READ/CREATE/UPDATE/DELETE/MANAGE',
    description VARCHAR(200) COMMENT '权限描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_permission_name (name),
    INDEX idx_permission_resource (resource)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色访问控制 - 用户角色关联表
-- 用于建立用户与角色的多对多关系

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色访问控制 - 角色权限关联表
-- 用于建立角色与权限的多对多关系

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 预设角色数据
INSERT INTO roles (name, description, level) VALUES
('GUEST', '访客 - 未登录用户，仅能访问公开页面', 1),
('USER', '普通用户 - 已注册用户，可查看和编辑个人信息', 2),
('SALES_REP', '销售代表 - 可管理自己的客户和商机', 3),
('SALES_MANAGER', '销售经理 - 可查看和管理团队的客户与商机', 4),
('ADMIN', '管理员 - 系统全部功能权限', 5)
ON DUPLICATE KEY UPDATE description=VALUES(description), level=VALUES(level);

-- 预设权限数据
INSERT INTO permissions (name, resource, action, description) VALUES
-- 个人信息权限
('READ_PROFILE', 'USER', 'READ', '查看个人信息'),
('UPDATE_PROFILE', 'USER', 'UPDATE', '编辑个人信息'),
('MANAGE_USERS', 'USER', 'MANAGE', '管理所有用户'),

-- 客户管理权限
('READ_OWN_CUSTOMER', 'CUSTOMER', 'READ', '查看自己的客户'),
('CREATE_CUSTOMER', 'CUSTOMER', 'CREATE', '创建客户'),
('UPDATE_OWN_CUSTOMER', 'CUSTOMER', 'UPDATE', '编辑自己的客户'),
('DELETE_OWN_CUSTOMER', 'CUSTOMER', 'DELETE', '删除自己的客户'),
('READ_TEAM_CUSTOMER', 'CUSTOMER', 'READ', '查看团队的客户'),
('UPDATE_TEAM_CUSTOMER', 'CUSTOMER', 'UPDATE', '编辑团队的客户'),
('MANAGE_ALL_CUSTOMERS', 'CUSTOMER', 'MANAGE', '管理所有客户'),

-- 商机管理权限
('READ_OWN_OPPORTUNITY', 'OPPORTUNITY', 'READ', '查看自己的商机'),
('CREATE_OPPORTUNITY', 'OPPORTUNITY', 'CREATE', '创建商机'),
('UPDATE_OWN_OPPORTUNITY', 'OPPORTUNITY', 'UPDATE', '编辑自己的商机'),
('DELETE_OWN_OPPORTUNITY', 'OPPORTUNITY', 'DELETE', '删除自己的商机'),
('READ_TEAM_OPPORTUNITY', 'OPPORTUNITY', 'READ', '查看团队的商机'),
('UPDATE_TEAM_OPPORTUNITY', 'OPPORTUNITY', 'UPDATE', '编辑团队的商机'),
('MANAGE_ALL_OPPORTUNITIES', 'OPPORTUNITY', 'MANAGE', '管理所有商机'),

-- 报表权限
('READ_OWN_REPORT', 'REPORT', 'READ', '查看个人报表'),
('READ_TEAM_REPORT', 'REPORT', 'READ', '查看团队报表'),
('READ_ALL_REPORTS', 'REPORT', 'READ', '查看所有报表'),

-- 系统配置权限
('MANAGE_SYSTEM', 'SYSTEM', 'MANAGE', '管理系统配置')
ON DUPLICATE KEY UPDATE description=VALUES(description);

-- 角色权限映射 - 普通用户（USER）
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'USER' AND p.name IN ('READ_PROFILE', 'UPDATE_PROFILE')
ON DUPLICATE KEY UPDATE role_id=role_id;

-- 角色权限映射 - 销售代表（SALES_REP）
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'SALES_REP' AND p.name IN (
    'READ_PROFILE', 'UPDATE_PROFILE',
    'READ_OWN_CUSTOMER', 'CREATE_CUSTOMER', 'UPDATE_OWN_CUSTOMER', 'DELETE_OWN_CUSTOMER',
    'READ_OWN_OPPORTUNITY', 'CREATE_OPPORTUNITY', 'UPDATE_OWN_OPPORTUNITY', 'DELETE_OWN_OPPORTUNITY',
    'READ_OWN_REPORT'
)
ON DUPLICATE KEY UPDATE role_id=role_id;

-- 角色权限映射 - 销售经理（SALES_MANAGER）
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'SALES_MANAGER' AND p.name IN (
    'READ_PROFILE', 'UPDATE_PROFILE',
    'READ_OWN_CUSTOMER', 'CREATE_CUSTOMER', 'UPDATE_OWN_CUSTOMER', 'DELETE_OWN_CUSTOMER',
    'READ_TEAM_CUSTOMER', 'UPDATE_TEAM_CUSTOMER',
    'READ_OWN_OPPORTUNITY', 'CREATE_OPPORTUNITY', 'UPDATE_OWN_OPPORTUNITY', 'DELETE_OWN_OPPORTUNITY',
    'READ_TEAM_OPPORTUNITY', 'UPDATE_TEAM_OPPORTUNITY',
    'READ_OWN_REPORT', 'READ_TEAM_REPORT'
)
ON DUPLICATE KEY UPDATE role_id=role_id;

-- 角色权限映射 - 管理员（ADMIN）
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN'
ON DUPLICATE KEY UPDATE role_id=role_id;
