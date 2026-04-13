package com.cardioguard.service;

import com.cardioguard.entity.EcgAnalysisResult;

import java.util.List;
import java.util.Map;

/**
 * ECG心电图分析服务接口
 */
public interface EcgAnalysisService {
    
    /**
     * 分析ECG数据
     * 
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param waveform ECG波形数据
     * @param sampleRate 采样率
     * @return 分析结果
     */
    EcgAnalysisResult analyzeEcg(Long userId, Long deviceId, String waveform, Integer sampleRate);
    
    /**
     * 批量分析ECG数据
     * 
     * @param ecgDataList ECG数据列表
     * @return 分析结果列表
     */
    List<EcgAnalysisResult> batchAnalyze(List<Map<String, Object>> ecgDataList);
    
    /**
     * 获取用户的ECG分析历史
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 分析结果列表
     */
    List<EcgAnalysisResult> getUserAnalysisHistory(Long userId, Integer page, Integer size);
    
    /**
     * 获取分析结果详情
     * 
     * @param resultId 结果ID
     * @return 分析结果
     */
    EcgAnalysisResult getAnalysisResult(Long resultId);
    
    /**
     * 医生审核分析结果
     * 
     * @param resultId 结果ID
     * @param doctorId 医生ID
     * @param reviewStatus 审核状态(APPROVED/REJECTED)
     * @param comment 审核意见
     * @return 更新后的结果
     */
    EcgAnalysisResult reviewResult(Long resultId, Long doctorId, String reviewStatus, String comment);
    
    /**
     * 生成ECG诊断报告
     * 
     * @param resultId 结果ID
     * @return 诊断报告(Markdown格式)
     */
    String generateDiagnosisReport(Long resultId);
    
    /**
     * 统计用户的ECG异常情况
     * 
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据
     */
    Map<String, Object> getAbnormalStatistics(Long userId, String startDate, String endDate);
}
