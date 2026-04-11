-- CardioGuard 360 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS cardioguard DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE cardioguard;

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(加密)',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    gender TINYINT DEFAULT 0 COMMENT '性别(0-未知, 1-男, 2-女)',
    age INT COMMENT '年龄',
    id_card VARCHAR(18) COMMENT '身份证号',
    role VARCHAR(20) NOT NULL DEFAULT 'PATIENT' COMMENT '用户角色(PATIENT, DOCTOR, ADMIN)',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态(0-禁用, 1-正常)',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除(0-未删除, 1-已删除)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_phone (phone),
    KEY idx_role (role),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 设备表
DROP TABLE IF EXISTS device;
CREATE TABLE device (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '设备ID',
    serial_number VARCHAR(50) NOT NULL COMMENT '设备序列号',
    device_name VARCHAR(100) COMMENT '设备名称',
    model VARCHAR(50) COMMENT '设备型号',
    device_type VARCHAR(20) NOT NULL COMMENT '设备类型(ECG, HR, BP, SPO2)',
    manufacturer VARCHAR(100) COMMENT '制造商',
    user_id BIGINT COMMENT '绑定的用户ID',
    status TINYINT DEFAULT 0 COMMENT '设备状态(0-离线, 1-在线, 2-故障)',
    battery_level INT COMMENT '电池电量(0-100)',
    firmware_version VARCHAR(20) COMMENT '固件版本',
    last_communication_time DATETIME COMMENT '最后通信时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除(0-未删除, 1-已删除)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_serial_number (serial_number),
    KEY idx_user_id (user_id),
    KEY idx_device_type (device_type),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备表';

-- 健康预警表
DROP TABLE IF EXISTS health_alert;
CREATE TABLE health_alert (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '预警ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    device_id BIGINT COMMENT '设备ID',
    alert_type VARCHAR(30) NOT NULL COMMENT '预警类型(TACHYCARDIA, BRADYCARDIA, ARRHYTHMIA, HIGH_BP, LOW_BP)',
    alert_level VARCHAR(20) NOT NULL COMMENT '预警级别(LOW, MEDIUM, HIGH, CRITICAL)',
    title VARCHAR(200) NOT NULL COMMENT '预警标题',
    description TEXT COMMENT '预警描述',
    trigger_value DECIMAL(10, 2) COMMENT '触发值',
    normal_range_min DECIMAL(10, 2) COMMENT '正常范围最小值',
    normal_range_max DECIMAL(10, 2) COMMENT '正常范围最大值',
    status VARCHAR(20) DEFAULT 'UNREAD' COMMENT '状态(UNREAD, READ, PROCESSED)',
    doctor_id BIGINT COMMENT '处理医生ID',
    doctor_advice TEXT COMMENT '处理意见',
    process_time DATETIME COMMENT '处理时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除(0-未删除, 1-已删除)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_device_id (device_id),
    KEY idx_alert_type (alert_type),
    KEY idx_alert_level (alert_level),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康预警表';

-- 健康报告表
DROP TABLE IF EXISTS health_report;
CREATE TABLE health_report (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '报告ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    report_type VARCHAR(30) NOT NULL COMMENT '报告类型(DAILY-日报, WEEKLY-周报, MONTHLY-月报)',
    report_date DATE NOT NULL COMMENT '报告日期',
    title VARCHAR(200) NOT NULL COMMENT '报告标题',
    content TEXT COMMENT '报告内容(JSON格式)',
    summary TEXT COMMENT '摘要',
    conclusion TEXT COMMENT '结论和建议',
    risk_level VARCHAR(20) COMMENT '风险等级(LOW, MEDIUM, HIGH)',
    doctor_id BIGINT COMMENT '审核医生ID',
    doctor_review TEXT COMMENT '医生评语',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态(DRAFT-草稿, PENDING_REVIEW-待审核, APPROVED-已通过)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_report_type (report_type),
    KEY idx_report_date (report_date),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康报告表';

-- 插入测试数据
INSERT INTO sys_user (username, password, real_name, phone, email, gender, age, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjH3rS', '系统管理员', '13800000000', 'admin@cardioguard.com', 1, 35, 'ADMIN', 1),
('doctor1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjH3rS', '张医生', '13800000001', 'doctor1@cardioguard.com', 1, 45, 'DOCTOR', 1),
('patient1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjH3rS', '李明', '13800000002', 'patient1@cardioguard.com', 1, 60, 'PATIENT', 1);

-- 插入测试设备数据
INSERT INTO device (serial_number, device_name, model, device_type, manufacturer, user_id, status, battery_level) VALUES
('ECG20240001', '心电监测仪-001', 'ECG-Pro-X1', 'ECG', 'CardioTech', 3, 1, 85),
('HR20240001', '心率手环-001', 'HR-Monitor-V2', 'HR', 'HealthBand', 3, 1, 72);

COMMIT;
