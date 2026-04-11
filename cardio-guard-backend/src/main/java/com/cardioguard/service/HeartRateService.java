package com.cardioguard.service;

import com.cardioguard.entity.HeartRateData;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 心率数据服务接口
 */
public interface HeartRateService {
    
    /**
     * 保存心率数据到InfluxDB
     */
    void saveHeartRateData(HeartRateData data);
    
    /**
     * 批量保存心率数据
     */
    void batchSaveHeartRateData(List<HeartRateData> dataList);
    
    /**
     * 查询用户指定时间范围内的心率数据
     */
    List<HeartRateData> queryHeartRateData(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询用户最新的心率数据
     */
    HeartRateData getLatestHeartRateData(Long userId);
    
    /**
     * 计算平均心率
     */
    Double getAverageHeartRate(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 检测异常心率数据
     */
    List<HeartRateData> detectAbnormalHeartRate(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}
