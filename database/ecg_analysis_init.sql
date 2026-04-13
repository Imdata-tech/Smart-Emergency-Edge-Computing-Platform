-- CardioGuard 360 v1.3.0 ECG心电图分析模块数据库脚本

-- 创建ECG分析结果表
CREATE TABLE IF NOT EXISTS `ecg_analysis_result` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `device_id` BIGINT NOT NULL COMMENT '设备ID',
  `ecg_data_id` VARCHAR(100) NOT NULL COMMENT 'ECG数据ID(InfluxDB中的measurement ID)',
  `diagnosis` VARCHAR(20) NOT NULL COMMENT '诊断结果(NORMAL-正常, ABNORMAL-异常)',
  `arrhythmia_type` VARCHAR(50) DEFAULT NULL COMMENT '心律失常类型(AFIB-房颤, PVC-室性早搏, PAC-房性早搏等)',
  `confidence` DECIMAL(5,4) NOT NULL COMMENT 'AI置信度(0-1)',
  `model_version` VARCHAR(50) NOT NULL COMMENT 'AI模型版本',
  `analysis_report` TEXT COMMENT '详细分析报告(JSON格式)',
  `risk_level` VARCHAR(20) NOT NULL COMMENT '风险等级(LOW-低, MEDIUM-中, HIGH-高, CRITICAL-危急)',
  `recommendation` TEXT COMMENT '建议措施',
  `needs_review` TINYINT NOT NULL DEFAULT 0 COMMENT '是否需要医生审核(0-否, 1-是)',
  `review_status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '审核状态(PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝)',
  `reviewed_by` BIGINT DEFAULT NULL COMMENT '审核医生ID',
  `reviewed_at` DATETIME DEFAULT NULL COMMENT '审核时间',
  `review_comment` TEXT COMMENT '审核意见',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_diagnosis` (`diagnosis`),
  KEY `idx_risk_level` (`risk_level`),
  KEY `idx_review_status` (`review_status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ECG心电图分析结果表';

-- 插入测试数据
INSERT INTO `ecg_analysis_result` 
(`user_id`, `device_id`, `ecg_data_id`, `diagnosis`, `arrhythmia_type`, `confidence`, `model_version`, `analysis_report`, `risk_level`, `recommendation`, `needs_review`, `review_status`, `reviewed_by`, `reviewed_at`, `review_comment`) 
VALUES
(1, 1, 'ecg-test-001', 'NORMAL', NULL, 0.9500, 'ECG-AI-v1.0.0', '{"diagnosis":"NORMAL","confidence":0.95,"riskLevel":"LOW"}', 'LOW', '心电图正常,请继续保持健康生活方式。', 0, 'APPROVED', 100, NOW(), '确认正常'),
(2, 2, 'ecg-test-002', 'ABNORMAL', 'AFIB', 0.9200, 'ECG-AI-v1.0.0', '{"diagnosis":"ABNORMAL","arrhythmiaType":"AFIB","confidence":0.92,"riskLevel":"HIGH"}', 'HIGH', '检测到房颤,建议立即就医,进行进一步检查和治疗。', 1, 'PENDING', NULL, NULL, NULL),
(3, 3, 'ecg-test-003', 'ABNORMAL', 'PVC', 0.8800, 'ECG-AI-v1.0.0', '{"diagnosis":"ABNORMAL","arrhythmiaType":"PVC","confidence":0.88,"riskLevel":"MEDIUM"}', 'MEDIUM', '检测到室性早搏,建议减少咖啡因摄入,避免过度劳累,必要时就医。', 1, 'APPROVED', 101, NOW(), '建议定期复查'),
(1, 1, 'ecg-test-004', 'NORMAL', NULL, 0.9700, 'ECG-AI-v1.0.0', '{"diagnosis":"NORMAL","confidence":0.97,"riskLevel":"LOW"}', 'LOW', '心电图正常,请继续保持健康生活方式。', 0, 'APPROVED', 100, NOW(), '正常'),
(2, 2, 'ecg-test-005', 'ABNORMAL', 'ST_ELEVATION', 0.9100, 'ECG-AI-v1.0.0', '{"diagnosis":"ABNORMAL","arrhythmiaType":"ST_ELEVATION","confidence":0.91,"riskLevel":"CRITICAL"}', 'CRITICAL', '检测到ST段抬高,可能存在心肌缺血,建议立即就医!', 1, 'PENDING', NULL, NULL, NULL);

-- 查询验证
SELECT * FROM `ecg_analysis_result` ORDER BY `created_at` DESC;
