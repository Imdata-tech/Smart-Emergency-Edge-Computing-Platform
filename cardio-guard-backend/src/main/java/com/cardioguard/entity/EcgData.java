package com.cardioguard.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ECG心电图数据实体类(存储到InfluxDB时序数据库)
 */
@Data
public class EcgData implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 测量时间(作为InfluxDB的timestamp)
     */
    private LocalDateTime measureTime;
    
    /**
     * 设备ID
     */
    private Long deviceId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * ECG波形数据(压缩后的Base64字符串或JSON数组)
     * 包含多个采样点的电压值(mV)
     */
    private String waveform;
    
    /**
     * 采样率(Hz),通常为250Hz或500Hz
     */
    private Integer sampleRate;
    
    /**
     * 记录时长(秒)
     */
    private Double duration;
    
    /**
     * 平均心率(从ECG计算得出)
     */
    private Double averageHeartRate;
    
    /**
     * 最低心率
     */
    private Double minHeartRate;
    
    /**
     * 最高心率
     */
    private Double maxHeartRate;
    
    /**
     * HRV心率变异性(毫秒)
     */
    private Double hrv;
    
    /**
     * PR间期(毫秒)
     */
    private Double prInterval;
    
    /**
     * QRS时限(毫秒)
     */
    private Double qrsDuration;
    
    /**
     * QT间期(毫秒)
     */
    private Double qtInterval;
    
    /**
     * 电轴(度)
     */
    private Double axis;
    
    /**
     * AI分析结果ID(关联ecg_analysis_result表)
     */
    private Long analysisResultId;
    
    /**
     * 数据质量评分(0-100)
     */
    private Integer dataQuality;
    
    /**
     * 是否异常(0-正常, 1-异常)
     */
    private Integer isAbnormal;
    
    /**
     * 异常类型(NORMAL-正常, AFIB-房颤, PVC-室性早搏, PAC-房性早搏等)
     */
    private String abnormalType;
}
