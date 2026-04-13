package com.cardioguard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cardioguard.entity.EcgAnalysisResult;
import com.cardioguard.mapper.EcgAnalysisResultMapper;
import com.cardioguard.service.EcgAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * ECG心电图分析服务实现类
 */
@Slf4j
@Service
public class EcgAnalysisServiceImpl implements EcgAnalysisService {
    
    @Autowired
    private EcgAnalysisResultMapper ecgAnalysisResultMapper;
    
    /**
     * AI模型版本
     */
    private static final String MODEL_VERSION = "ECG-AI-v1.0.0";
    
    @Override
    public EcgAnalysisResult analyzeEcg(Long userId, Long deviceId, String waveform, Integer sampleRate) {
        log.info("开始分析ECG数据 - userId: {}, deviceId: {}, sampleRate: {}", userId, deviceId, sampleRate);
        
        // TODO: 集成真实的AI模型进行ECG分析
        // 目前使用模拟算法进行演示
        
        EcgAnalysisResult result = new EcgAnalysisResult();
        result.setUserId(userId);
        result.setDeviceId(deviceId);
        result.setEcgDataId(UUID.randomUUID().toString());
        result.setModelVersion(MODEL_VERSION);
        
        // 模拟AI分析结果
        Map<String, Object> analysisData = simulateAiAnalysis(waveform, sampleRate);
        
        // 设置诊断结果
        result.setDiagnosis((String) analysisData.get("diagnosis"));
        result.setArrhythmiaType((String) analysisData.get("arrhythmiaType"));
        result.setConfidence((Double) analysisData.get("confidence"));
        result.setRiskLevel((String) analysisData.get("riskLevel"));
        result.setRecommendation((String) analysisData.get("recommendation"));
        
        // 生成详细分析报告
        result.setAnalysisReport(generateAnalysisReport(analysisData));
        
        // 设置审核状态
        result.setNeedsReview("ABNORMAL".equals(result.getDiagnosis()) ? 1 : 0);
        result.setReviewStatus("PENDING");
        
        // 保存到数据库
        ecgAnalysisResultMapper.insert(result);
        
        log.info("ECG分析完成 - resultId: {}, diagnosis: {}, confidence: {}", 
                result.getId(), result.getDiagnosis(), result.getConfidence());
        
        return result;
    }
    
    @Override
    public List<EcgAnalysisResult> batchAnalyze(List<Map<String, Object>> ecgDataList) {
        log.info("批量分析ECG数据 - 数量: {}", ecgDataList.size());
        
        List<EcgAnalysisResult> results = new ArrayList<>();
        
        for (Map<String, Object> ecgData : ecgDataList) {
            try {
                Long userId = ((Number) ecgData.get("userId")).longValue();
                Long deviceId = ((Number) ecgData.get("deviceId")).longValue();
                String waveform = (String) ecgData.get("waveform");
                Integer sampleRate = (Integer) ecgData.get("sampleRate");
                
                EcgAnalysisResult result = analyzeEcg(userId, deviceId, waveform, sampleRate);
                results.add(result);
            } catch (Exception e) {
                log.error("批量分析ECG数据失败", e);
            }
        }
        
        log.info("批量分析完成 - 成功: {}/{}", results.size(), ecgDataList.size());
        return results;
    }
    
    @Override
    public List<EcgAnalysisResult> getUserAnalysisHistory(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<EcgAnalysisResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EcgAnalysisResult::getUserId, userId)
               .orderByDesc(EcgAnalysisResult::getCreatedAt);
        
        // 分页查询
        int offset = (page - 1) * size;
        wrapper.last("LIMIT " + size + " OFFSET " + offset);
        
        return ecgAnalysisResultMapper.selectList(wrapper);
    }
    
    @Override
    public EcgAnalysisResult getAnalysisResult(Long resultId) {
        return ecgAnalysisResultMapper.selectById(resultId);
    }
    
    @Override
    public EcgAnalysisResult reviewResult(Long resultId, Long doctorId, String reviewStatus, String comment) {
        EcgAnalysisResult result = ecgAnalysisResultMapper.selectById(resultId);
        
        if (result == null) {
            throw new RuntimeException("分析结果不存在: " + resultId);
        }
        
        result.setReviewStatus(reviewStatus);
        result.setReviewedBy(doctorId);
        result.setReviewedAt(LocalDateTime.now());
        result.setReviewComment(comment);
        
        ecgAnalysisResultMapper.updateById(result);
        
        log.info("医生审核ECG结果 - resultId: {}, doctorId: {}, status: {}", 
                resultId, doctorId, reviewStatus);
        
        return result;
    }
    
    @Override
    public String generateDiagnosisReport(Long resultId) {
        EcgAnalysisResult result = getAnalysisResult(resultId);
        
        if (result == null) {
            throw new RuntimeException("分析结果不存在: " + resultId);
        }
        
        StringBuilder report = new StringBuilder();
        report.append("# ECG心电图诊断报告\n\n");
        report.append("**报告ID**: ").append(result.getId()).append("\n");
        report.append("**生成时间**: ").append(LocalDateTime.now()).append("\n\n");
        
        report.append("## 基本信息\n");
        report.append("- **用户ID**: ").append(result.getUserId()).append("\n");
        report.append("- **设备ID**: ").append(result.getDeviceId()).append("\n");
        report.append("- **AI模型版本**: ").append(result.getModelVersion()).append("\n\n");
        
        report.append("## 诊断结果\n");
        report.append("- **诊断**: ").append(result.getDiagnosis()).append("\n");
        if (result.getArrhythmiaType() != null) {
            report.append("- **心律失常类型**: ").append(result.getArrhythmiaType()).append("\n");
        }
        report.append("- **AI置信度**: ").append(String.format("%.2f%%", result.getConfidence() * 100)).append("\n");
        report.append("- **风险等级**: ").append(result.getRiskLevel()).append("\n\n");
        
        report.append("## 建议措施\n");
        report.append(result.getRecommendation()).append("\n\n");
        
        if (result.getReviewStatus() != null && !"PENDING".equals(result.getReviewStatus())) {
            report.append("## 医生审核\n");
            report.append("- **审核状态**: ").append(result.getReviewStatus()).append("\n");
            report.append("- **审核医生**: ").append(result.getReviewedBy()).append("\n");
            report.append("- **审核时间**: ").append(result.getReviewedAt()).append("\n");
            if (result.getReviewComment() != null) {
                report.append("- **审核意见**: ").append(result.getReviewComment()).append("\n");
            }
        }
        
        return report.toString();
    }
    
    @Override
    public Map<String, Object> getAbnormalStatistics(Long userId, String startDate, String endDate) {
        LambdaQueryWrapper<EcgAnalysisResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EcgAnalysisResult::getUserId, userId)
               .eq(EcgAnalysisResult::getDiagnosis, "ABNORMAL");
        
        if (startDate != null && endDate != null) {
            wrapper.ge(EcgAnalysisResult::getCreatedAt, startDate)
                   .le(EcgAnalysisResult::getCreatedAt, endDate);
        }
        
        List<EcgAnalysisResult> abnormalResults = ecgAnalysisResultMapper.selectList(wrapper);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalAbnormal", abnormalResults.size());
        
        // 统计不同类型的心律失常
        Map<String, Long> typeCount = new HashMap<>();
        for (EcgAnalysisResult result : abnormalResults) {
            String type = result.getArrhythmiaType() != null ? result.getArrhythmiaType() : "UNKNOWN";
            typeCount.merge(type, 1L, Long::sum);
        }
        statistics.put("arrhythmiaTypes", typeCount);
        
        // 统计风险等级分布
        Map<String, Long> riskDistribution = new HashMap<>();
        for (EcgAnalysisResult result : abnormalResults) {
            String risk = result.getRiskLevel() != null ? result.getRiskLevel() : "UNKNOWN";
            riskDistribution.merge(risk, 1L, Long::sum);
        }
        statistics.put("riskDistribution", riskDistribution);
        
        return statistics;
    }
    
    /**
     * 模拟AI分析(实际项目中应替换为真实的AI模型调用)
     */
    private Map<String, Object> simulateAiAnalysis(String waveform, Integer sampleRate) {
        Map<String, Object> result = new HashMap<>();
        
        // 简单的模拟逻辑:根据波形长度随机生成结果
        Random random = new Random();
        double confidence = 0.85 + random.nextDouble() * 0.14; // 85%-99%
        
        // 20%概率检测到异常
        boolean isAbnormal = random.nextDouble() < 0.2;
        
        if (isAbnormal) {
            String[] arrhythmiaTypes = {"AFIB", "PVC", "PAC", "ST_ELEVATION"};
            String arrhythmiaType = arrhythmiaTypes[random.nextInt(arrhythmiaTypes.length)];
            
            result.put("diagnosis", "ABNORMAL");
            result.put("arrhythmiaType", arrhythmiaType);
            result.put("riskLevel", getRandomRiskLevel());
            result.put("recommendation", generateRecommendation(arrhythmiaType));
        } else {
            result.put("diagnosis", "NORMAL");
            result.put("arrhythmiaType", null);
            result.put("riskLevel", "LOW");
            result.put("recommendation", "心电图正常,请继续保持健康生活方式。");
        }
        
        result.put("confidence", confidence);
        
        return result;
    }
    
    /**
     * 生成随机风险等级
     */
    private String getRandomRiskLevel() {
        String[] levels = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
        return levels[new Random().nextInt(levels.length)];
    }
    
    /**
     * 根据心律失常类型生成建议
     */
    private String generateRecommendation(String arrhythmiaType) {
        switch (arrhythmiaType) {
            case "AFIB":
                return "检测到房颤,建议立即就医,进行进一步检查和治疗。";
            case "PVC":
                return "检测到室性早搏,建议减少咖啡因摄入,避免过度劳累,必要时就医。";
            case "PAC":
                return "检测到房性早搏,建议保持规律作息,避免情绪激动,定期复查。";
            case "ST_ELEVATION":
                return "检测到ST段抬高,可能存在心肌缺血,建议立即就医!";
            default:
                return "检测到异常心律,建议咨询心脏专科医生。";
        }
    }
    
    /**
     * 生成详细分析报告
     */
    private String generateAnalysisReport(Map<String, Object> analysisData) {
        return String.format(
            "{\"diagnosis\":\"%s\",\"arrhythmiaType\":\"%s\",\"confidence\":%.2f,\"riskLevel\":\"%s\"}",
            analysisData.get("diagnosis"),
            analysisData.get("arrhythmiaType"),
            analysisData.get("confidence"),
            analysisData.get("riskLevel")
        );
    }
}
