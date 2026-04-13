package com.cardioguard.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ECG分析结果实体类(存储到MySQL)
 */
@Data
@TableName("ecg_analysis_result")
public class EcgAnalysisResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 设备ID
     */
    private Long deviceId;
    
    /**
     * ECG数据ID(关联InfluxDB中的measurement)
     */
    private String ecgDataId;
    
    /**
     * 诊断结果(NORMAL-正常, ABNORMAL-异常)
     */
    private String diagnosis;
    
    /**
     * 心律失常类型(AFIB-房颤, PVC-室性早搏, PAC-房性早搏, ST_ELEVATION-ST段抬高等)
     */
    private String arrhythmiaType;
    
    /**
     * AI置信度(0-1)
     */
    private Double confidence;
    
    /**
     * AI模型版本
     */
    private String modelVersion;
    
    /**
     * 详细分析报告(JSON格式)
     */
    private String analysisReport;
    
    /**
     * 风险等级(LOW-低, MEDIUM-中, HIGH-高, CRITICAL-危急)
     */
    private String riskLevel;
    
    /**
     * 建议措施
     */
    private String recommendation;
    
    /**
     * 是否需要医生审核(0-否, 1-是)
     */
    private Integer needsReview;
    
    /**
     * 审核状态(PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝)
     */
    private String reviewStatus;
    
    /**
     * 审核医生ID
     */
    private Long reviewedBy;
    
    /**
     * 审核时间
     */
    private LocalDateTime reviewedAt;
    
    /**
     * 审核意见
     */
    private String reviewComment;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
