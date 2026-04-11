package com.cardioguard.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 心率数据实体类(存储到InfluxDB时序数据库)
 */
@Data
public class HeartRateData implements Serializable {
    
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
     * 心率值(次/分钟)
     */
    private Double heartRate;
    
    /**
     * RR间期(毫秒)
     */
    private Double rrInterval;
    
    /**
     * 心率变异性HRV(毫秒)
     */
    private Double hrv;
    
    /**
     * 运动状态(REST-静止, WALK-行走, RUN-跑步)
     */
    private String activityStatus;
    
    /**
     * 数据质量(0-100)
     */
    private Integer dataQuality;
    
    /**
     * 是否异常(0-正常, 1-异常)
     */
    private Integer isAbnormal;
    
    /**
     * 异常类型(TACHYCARDIA-心动过速, BRADYCARDIA-心动过缓, ARRHYTHMIA-心律失常)
     */
    private String abnormalType;
}
