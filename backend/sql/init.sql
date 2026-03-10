-- OpsCopilot 数据库初始化脚本
-- Version: 1.0.0-MVP

CREATE DATABASE IF NOT EXISTS ops_copilot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ops_copilot;

-- 告警表
CREATE TABLE IF NOT EXISTS `t_alert` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` VARCHAR(255) NOT NULL COMMENT '告警标题',
  `content` TEXT COMMENT '告警内容',
  `level` VARCHAR(20) NOT NULL COMMENT '告警级别: CRITICAL/WARNING/INFO',
  `source` VARCHAR(50) COMMENT '告警来源',
  `asset_id` BIGINT COMMENT '关联资产ID',
  `ip` VARCHAR(50) COMMENT '关联服务器IP',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/PROCESSING/RESOLVED/CLOSED',
  `alert_time` DATETIME COMMENT '告警时间',
  `resolve_time` DATETIME COMMENT '解决时间',
  `assignee_id` BIGINT COMMENT '负责人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  INDEX `idx_alert_time` (`alert_time`),
  INDEX `idx_status` (`status`),
  INDEX `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警表';

-- 资产表
CREATE TABLE IF NOT EXISTS `t_asset` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(100) NOT NULL COMMENT '资产名称',
  `type` VARCHAR(20) NOT NULL COMMENT '资产类型: SERVER/NETWORK/MIDDLEWARE/DATABASE/STORAGE',
  `ip` VARCHAR(50) COMMENT 'IP地址',
  `port` INT COMMENT '端口',
  `os` VARCHAR(50) COMMENT '操作系统',
  `specification` VARCHAR(100) COMMENT '规格配置',
  `department` VARCHAR(50) COMMENT '所属部门',
  `business` VARCHAR(50) COMMENT '所属业务',
  `owner_id` BIGINT COMMENT '负责人ID',
  `owner_name` VARCHAR(50) COMMENT '负责人名称',
  `status` VARCHAR(20) NOT NULL DEFAULT 'RUNNING' COMMENT '状态: RUNNING/STOPPED/MAINTENANCE/RETIRED',
  `tags` JSON COMMENT '标签',
  `remark` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  INDEX `idx_ip` (`ip`),
  INDEX `idx_type` (`type`),
  INDEX `idx_status` (`status`),
  INDEX `idx_department` (`department`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产表';

-- 工单表
CREATE TABLE IF NOT EXISTS `t_ticket` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ticket_no` VARCHAR(50) NOT NULL COMMENT '工单编号',
  `title` VARCHAR(255) NOT NULL COMMENT '工单标题',
  `content` TEXT COMMENT '工单内容',
  `type` VARCHAR(20) NOT NULL COMMENT '工单类型: INCIDENT/PERMISSION/SERVICE_CHANGE/QUESTION',
  `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT/PENDING/APPROVED/PROCESSING/CLOSED/REJECTED',
  `priority` VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级: LOW/MEDIUM/HIGH/URGENT',
  `applicant_id` BIGINT NOT NULL COMMENT '申请人ID',
  `applicant_name` VARCHAR(50) COMMENT '申请人名称',
  `applicant_dept` VARCHAR(50) COMMENT '申请人部门',
  `approver_id` BIGINT COMMENT '审批人ID',
  `approver_name` VARCHAR(50) COMMENT '审批人名称',
  `handler_id` BIGINT COMMENT '处理人ID',
  `handler_name` VARCHAR(50) COMMENT '处理人名称',
  `due_time` DATETIME COMMENT '预计完成时间',
  `complete_time` DATETIME COMMENT '实际完成时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_ticket_no` (`ticket_no`),
  INDEX `idx_applicant_id` (`applicant_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表';

-- 知识库表
CREATE TABLE IF NOT EXISTS `t_knowledge` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` VARCHAR(255) NOT NULL COMMENT '文档标题',
  `content` TEXT COMMENT '文档内容',
  `type` VARCHAR(20) NOT NULL COMMENT '文档类型: OPERATION/FAQ/INCIDENT/DOCUMENT',
  `tags` JSON COMMENT '标签',
  `keywords` VARCHAR(500) COMMENT '关键词',
  `author` VARCHAR(50) COMMENT '作者',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `like_count` INT DEFAULT 0 COMMENT '点赞次数',
  `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT/PUBLISHED/ARCHIVED',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_status` (`status`),
  FULLTEXT INDEX `ft_keywords` (`title`, `keywords`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库表';

-- 插入示例数据
INSERT INTO `t_alert` (`title`, `content`, `level`, `source`, `ip`, `status`, `alert_time`) VALUES
('订单服务CPU使用率超过90%', '服务器 web-server-01 CPU使用率92%，持续12分钟', 'CRITICAL', 'Zabbix', '10.0.1.10', 'PENDING', NOW()),
('数据库连接数接近上限', 'MySQL连接数达到450，接近上限500', 'CRITICAL', 'Prometheus', '10.0.2.10', 'PROCESSING', NOW()),
('支付网关响应超时', '支付网关响应时间超过5秒', 'WARNING', 'SkyWalking', '10.0.3.10', 'PENDING', NOW());

INSERT INTO `t_asset` (`name`, `type`, `ip`, `os`, `specification`, `department`, `business`, `owner_name`, `status`) VALUES
('web-server-01', 'SERVER', '10.0.1.10', 'CentOS 7.9', '4核8G', '电商技术部', '订单服务', '张三', 'RUNNING'),
('db-mysql-01', 'DATABASE', '10.0.2.10', 'CentOS 7.9', '8核16G', '电商技术部', '订单服务', '李四', 'RUNNING'),
('redis-01', 'MIDDLEWARE', '10.0.3.10', 'CentOS 7.9', '4核8G', '电商技术部', '订单服务', '张三', 'RUNNING');

INSERT INTO `t_knowledge` (`title`, `content`, `type`, `keywords`, `author`, `view_count`, `like_count`, `status`) VALUES
('Redis连接数满了怎么解决', 'Redis连接数满了，一般有以下几种原因和解决方法：\n\n1. 连接泄漏\n- 排查：redis-cli info clients\n- 解决：检查代码是否正确关闭连接\n\n2. 连接池配置过小\n- 解决：调整maxclients参数\n\n3. 慢查询阻塞\n- 排查：redis-cli slowlog get 10', 'OPERATION', 'redis,连接数,maxclients', '运维团队', 156, 23, 'PUBLISHED'),
('Nginx服务重启方法', 'Nginx重启方法：\n\n1. 优雅重启：nginx -s reload\n2. 完全重启：systemctl restart nginx\n3. 检查配置：nginx -t', 'OPERATION', 'nginx,重启,reload', '运维团队', 89, 12, 'PUBLISHED');
