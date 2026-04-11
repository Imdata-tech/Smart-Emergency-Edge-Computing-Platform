package com.cardioguard.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 可穿戴设备实体类
 */
@Data
@TableName("device")
public class Device implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 设备ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 设备序列号
     */
    private String serialNumber;
    
    /**
     * 设备名称
     */
    private String deviceName;
    
    /**
     * 设备型号
     */
    private String model;
    
    /**
     * 设备类型(ECG-心电图, HR-心率, BP-血压, SPO2-血氧)
     */
    private String deviceType;
    
    /**
     * 制造商
     */
    private String manufacturer;
    
    /**
     * 用户ID(绑定用户)
     */
    private Long userId;
    
    /**
     * 设备状态(0-离线, 1-在线, 2-故障)
     */
    private Integer status;
    
    /**
     * 电池电量(0-100)
     */
    private Integer batteryLevel;
    
    /**
     * 固件版本
     */
    private String firmwareVersion;
    
    /**
     * 最后通信时间
     */
    private LocalDateTime lastCommunicationTime;
    
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
