package com.cardioguard.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 健康预警实体类
 */
@Data
@TableName("health_alert")
public class HealthAlert implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 预警ID
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
     * 预警类型(TACHYCARDIA-心动过速, BRADYCARDIA-心动过缓, ARRHYTHMIA-心律失常, HIGH_BP-高血压, LOW_BP-低血压)
     */
    private String alertType;
    
    /**
     * 预警级别(LOW-低, MEDIUM-中, HIGH-高, CRITICAL-危急)
     */
    private String alertLevel;
    
    /**
     * 预警标题
     */
    private String title;
    
    /**
     * 预警描述
     */
    private String description;
    
    /**
     * 触发值
     */
    private Double triggerValue;
    
    /**
     * 正常范围最小值
     */
    private Double normalRangeMin;
    
    /**
     * 正常范围最大值
     */
    private Double normalRangeMax;
    
    /**
     * 状态(UNREAD-未读, READ-已读, PROCESSED-已处理)
     */
    private String status;
    
    /**
     * 处理医生ID
     */
    private Long doctorId;
    
    /**
     * 处理意见
     */
    private String doctorAdvice;
    
    /**
     * 处理时间
     */
    private LocalDateTime processTime;
    
    /**
     * 逻辑删除(0-未删除, 1-已删除)
     */
    @TableLogic
    private Integer deleted;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
