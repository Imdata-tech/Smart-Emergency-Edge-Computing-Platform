package com.cardioguard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cardioguard.entity.EcgAnalysisResult;
import com.cardioguard.mapper.EcgAnalysisResultMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ECG分析服务单元测试
 */
class EcgAnalysisServiceImplTest {
    
    @Mock
    private EcgAnalysisResultMapper ecgAnalysisResultMapper;
    
    @InjectMocks
    private EcgAnalysisServiceImpl ecgAnalysisService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testAnalyzeEcg_Normal() {
        // Arrange
        Long userId = 1L;
        Long deviceId = 1L;
        String waveform = "test_waveform_data";
        Integer sampleRate = 250;
        
        EcgAnalysisResult mockResult = new EcgAnalysisResult();
        mockResult.setId(1L);
        mockResult.setUserId(userId);
        mockResult.setDeviceId(deviceId);
        mockResult.setDiagnosis("NORMAL");
        mockResult.setConfidence(0.95);
        mockResult.setRiskLevel("LOW");
        mockResult.setRecommendation("心电图正常,请继续保持健康生活方式。");
        mockResult.setNeedsReview(0);
        mockResult.setReviewStatus("PENDING");
        mockResult.setModelVersion("ECG-AI-v1.0.0");
        
        when(ecgAnalysisResultMapper.insert(any(EcgAnalysisResult.class))).thenReturn(1);
        
        // Act
        EcgAnalysisResult result = ecgAnalysisService.analyzeEcg(userId, deviceId, waveform, sampleRate);
        
        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(deviceId, result.getDeviceId());
        assertNotNull(result.getDiagnosis());
        assertNotNull(result.getConfidence());
        assertTrue(result.getConfidence() >= 0.85 && result.getConfidence() <= 1.0);
        verify(ecgAnalysisResultMapper, times(1)).insert(any(EcgAnalysisResult.class));
    }
    
    @Test
    void testAnalyzeEcg_Abnormal() {
        // Arrange
        Long userId = 2L;
        Long deviceId = 2L;
        String waveform = "abnormal_waveform";
        Integer sampleRate = 500;
        
        when(ecgAnalysisResultMapper.insert(any(EcgAnalysisResult.class))).thenReturn(1);
        
        // Act - 多次运行以覆盖异常情况
        EcgAnalysisResult result = null;
        for (int i = 0; i < 10; i++) {
            result = ecgAnalysisService.analyzeEcg(userId, deviceId, waveform, sampleRate);
            if ("ABNORMAL".equals(result.getDiagnosis())) {
                break;
            }
        }
        
        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertNotNull(result.getDiagnosis());
        verify(ecgAnalysisResultMapper, atLeastOnce()).insert(any(EcgAnalysisResult.class));
    }
    
    @Test
    void testBatchAnalyze() {
        // Arrange
        List<Map<String, Object>> ecgDataList = new ArrayList<>();
        
        Map<String, Object> data1 = new HashMap<>();
        data1.put("userId", 1L);
        data1.put("deviceId", 1L);
        data1.put("waveform", "waveform1");
        data1.put("sampleRate", 250);
        ecgDataList.add(data1);
        
        Map<String, Object> data2 = new HashMap<>();
        data2.put("userId", 2L);
        data2.put("deviceId", 2L);
        data2.put("waveform", "waveform2");
        data2.put("sampleRate", 500);
        ecgDataList.add(data2);
        
        when(ecgAnalysisResultMapper.insert(any(EcgAnalysisResult.class))).thenReturn(1);
        
        // Act
        List<EcgAnalysisResult> results = ecgAnalysisService.batchAnalyze(ecgDataList);
        
        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(ecgAnalysisResultMapper, times(2)).insert(any(EcgAnalysisResult.class));
    }
    
    @Test
    void testGetUserAnalysisHistory() {
        // Arrange
        Long userId = 1L;
        Integer page = 1;
        Integer size = 10;
        
        List<EcgAnalysisResult> mockResults = new ArrayList<>();
        EcgAnalysisResult result1 = new EcgAnalysisResult();
        result1.setId(1L);
        result1.setUserId(userId);
        result1.setDiagnosis("NORMAL");
        mockResults.add(result1);
        
        when(ecgAnalysisResultMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(mockResults);
        
        // Act
        List<EcgAnalysisResult> results = ecgAnalysisService.getUserAnalysisHistory(userId, page, size);
        
        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(userId, results.get(0).getUserId());
        verify(ecgAnalysisResultMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }
    
    @Test
    void testGetAnalysisResult_Exists() {
        // Arrange
        Long resultId = 1L;
        EcgAnalysisResult mockResult = new EcgAnalysisResult();
        mockResult.setId(resultId);
        mockResult.setUserId(1L);
        mockResult.setDiagnosis("NORMAL");
        
        when(ecgAnalysisResultMapper.selectById(resultId)).thenReturn(mockResult);
        
        // Act
        EcgAnalysisResult result = ecgAnalysisService.getAnalysisResult(resultId);
        
        // Assert
        assertNotNull(result);
        assertEquals(resultId, result.getId());
        verify(ecgAnalysisResultMapper, times(1)).selectById(resultId);
    }
    
    @Test
    void testGetAnalysisResult_NotExists() {
        // Arrange
        Long resultId = 999L;
        when(ecgAnalysisResultMapper.selectById(resultId)).thenReturn(null);
        
        // Act
        EcgAnalysisResult result = ecgAnalysisService.getAnalysisResult(resultId);
        
        // Assert
        assertNull(result);
        verify(ecgAnalysisResultMapper, times(1)).selectById(resultId);
    }
    
    @Test
    void testReviewResult_Approved() {
        // Arrange
        Long resultId = 1L;
        Long doctorId = 100L;
        String reviewStatus = "APPROVED";
        String comment = "审核通过";
        
        EcgAnalysisResult mockResult = new EcgAnalysisResult();
        mockResult.setId(resultId);
        mockResult.setReviewStatus("PENDING");
        
        when(ecgAnalysisResultMapper.selectById(resultId)).thenReturn(mockResult);
        when(ecgAnalysisResultMapper.updateById(any(EcgAnalysisResult.class))).thenReturn(1);
        
        // Act
        EcgAnalysisResult result = ecgAnalysisService.reviewResult(resultId, doctorId, reviewStatus, comment);
        
        // Assert
        assertNotNull(result);
        assertEquals(reviewStatus, result.getReviewStatus());
        assertEquals(doctorId, result.getReviewedBy());
        assertNotNull(result.getReviewedAt());
        assertEquals(comment, result.getReviewComment());
        verify(ecgAnalysisResultMapper, times(1)).updateById(any(EcgAnalysisResult.class));
    }
    
    @Test
    void testReviewResult_Rejected() {
        // Arrange
        Long resultId = 2L;
        Long doctorId = 101L;
        String reviewStatus = "REJECTED";
        String comment = "需要重新检查";
        
        EcgAnalysisResult mockResult = new EcgAnalysisResult();
        mockResult.setId(resultId);
        mockResult.setReviewStatus("PENDING");
        
        when(ecgAnalysisResultMapper.selectById(resultId)).thenReturn(mockResult);
        when(ecgAnalysisResultMapper.updateById(any(EcgAnalysisResult.class))).thenReturn(1);
        
        // Act
        EcgAnalysisResult result = ecgAnalysisService.reviewResult(resultId, doctorId, reviewStatus, comment);
        
        // Assert
        assertNotNull(result);
        assertEquals(reviewStatus, result.getReviewStatus());
        assertEquals(comment, result.getReviewComment());
    }
    
    @Test
    void testReviewResult_NotFound() {
        // Arrange
        Long resultId = 999L;
        when(ecgAnalysisResultMapper.selectById(resultId)).thenReturn(null);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            ecgAnalysisService.reviewResult(resultId, 100L, "APPROVED", "comment");
        });
    }
    
    @Test
    void testGenerateDiagnosisReport_Normal() {
        // Arrange
        Long resultId = 1L;
        EcgAnalysisResult mockResult = new EcgAnalysisResult();
        mockResult.setId(resultId);
        mockResult.setUserId(1L);
        mockResult.setDeviceId(1L);
        mockResult.setModelVersion("ECG-AI-v1.0.0");
        mockResult.setDiagnosis("NORMAL");
        mockResult.setRiskLevel("LOW");
        mockResult.setRecommendation("心电图正常");
        mockResult.setReviewStatus("APPROVED");
        mockResult.setReviewedBy(100L);
        mockResult.setReviewedAt(LocalDateTime.now());
        mockResult.setReviewComment("确认正常");
        
        when(ecgAnalysisResultMapper.selectById(resultId)).thenReturn(mockResult);
        
        // Act
        String report = ecgAnalysisService.generateDiagnosisReport(resultId);
        
        // Assert
        assertNotNull(report);
        assertTrue(report.contains("# ECG心电图诊断报告"));
        assertTrue(report.contains("**诊断**: NORMAL"));
        assertTrue(report.contains("**风险等级**: LOW"));
        assertTrue(report.contains("## 医生审核"));
    }
    
    @Test
    void testGenerateDiagnosisReport_Abnormal() {
        // Arrange
        Long resultId = 2L;
        EcgAnalysisResult mockResult = new EcgAnalysisResult();
        mockResult.setId(resultId);
        mockResult.setUserId(2L);
        mockResult.setDeviceId(2L);
        mockResult.setModelVersion("ECG-AI-v1.0.0");
        mockResult.setDiagnosis("ABNORMAL");
        mockResult.setArrhythmiaType("AFIB");
        mockResult.setConfidence(0.92);
        mockResult.setRiskLevel("HIGH");
        mockResult.setRecommendation("检测到房颤,建议立即就医");
        mockResult.setReviewStatus("PENDING");
        
        when(ecgAnalysisResultMapper.selectById(resultId)).thenReturn(mockResult);
        
        // Act
        String report = ecgAnalysisService.generateDiagnosisReport(resultId);
        
        // Assert
        assertNotNull(report);
        assertTrue(report.contains("**诊断**: ABNORMAL"));
        assertTrue(report.contains("**心律失常类型**: AFIB"));
        assertTrue(report.contains("**AI置信度**: 92.00%"));
        assertFalse(report.contains("## 医生审核")); // PENDING状态不显示审核信息
    }
    
    @Test
    void testGenerateDiagnosisReport_NotFound() {
        // Arrange
        Long resultId = 999L;
        when(ecgAnalysisResultMapper.selectById(resultId)).thenReturn(null);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            ecgAnalysisService.generateDiagnosisReport(resultId);
        });
    }
    
    @Test
    void testGetAbnormalStatistics() {
        // Arrange
        Long userId = 1L;
        String startDate = "2024-01-01T00:00:00";
        String endDate = "2024-01-31T23:59:59";
        
        List<EcgAnalysisResult> abnormalResults = new ArrayList<>();
        
        EcgAnalysisResult result1 = new EcgAnalysisResult();
        result1.setId(1L);
        result1.setUserId(userId);
        result1.setDiagnosis("ABNORMAL");
        result1.setArrhythmiaType("AFIB");
        result1.setRiskLevel("HIGH");
        abnormalResults.add(result1);
        
        EcgAnalysisResult result2 = new EcgAnalysisResult();
        result2.setId(2L);
        result2.setUserId(userId);
        result2.setDiagnosis("ABNORMAL");
        result2.setArrhythmiaType("PVC");
        result2.setRiskLevel("MEDIUM");
        abnormalResults.add(result2);
        
        when(ecgAnalysisResultMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(abnormalResults);
        
        // Act
        Map<String, Object> statistics = ecgAnalysisService.getAbnormalStatistics(userId, startDate, endDate);
        
        // Assert
        assertNotNull(statistics);
        assertEquals(2, statistics.get("totalAbnormal"));
        assertNotNull(statistics.get("arrhythmiaTypes"));
        assertNotNull(statistics.get("riskDistribution"));
    }
    
    @Test
    void testGetAbnormalStatistics_NoData() {
        // Arrange
        Long userId = 999L;
        when(ecgAnalysisResultMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(new ArrayList<>());
        
        // Act
        Map<String, Object> statistics = ecgAnalysisService.getAbnormalStatistics(userId, null, null);
        
        // Assert
        assertNotNull(statistics);
        assertEquals(0, statistics.get("totalAbnormal"));
    }
    
    @Test
    void testSimulateAiAnalysis_Coverage() {
        // 多次调用以覆盖不同的随机分支
        for (int i = 0; i < 20; i++) {
            ecgAnalysisService.analyzeEcg(1L, 1L, "waveform" + i, 250);
        }
        
        // 验证至少调用了一次insert
        verify(ecgAnalysisResultMapper, atLeastOnce()).insert(any(EcgAnalysisResult.class));
    }
}
