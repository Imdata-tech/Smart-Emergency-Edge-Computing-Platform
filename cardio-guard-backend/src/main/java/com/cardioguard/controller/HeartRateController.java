package com.cardioguard.controller;

import com.cardioguard.common.Result;
import com.cardioguard.entity.HeartRateData;
import com.cardioguard.service.HeartRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 心率数据控制器
 */
@Slf4j
@RestController
@RequestMapping("/health/heart-rate")
@RequiredArgsConstructor
@Tag(name = "心率数据管理", description = "心率数据采集、查询、分析接口")
public class HeartRateController {
    
    private final HeartRateService heartRateService;
    
    /**
     * 上报心率数据
     */
    @PostMapping("/report")
    @Operation(summary = "上报心率数据", description = "设备上报实时心率数据")
    public Result<String> reportHeartRate(@RequestBody HeartRateData data) {
        try {
            data.setMeasureTime(LocalDateTime.now());
            heartRateService.saveHeartRateData(data);
            
            // 检测异常并推送预警
            if (data.getHeartRate() != null && (data.getHeartRate() > 100 || data.getHeartRate() < 60)) {
                log.warn("检测到异常心率: userId={}, heartRate={}", data.getUserId(), data.getHeartRate());
            }
            
            return Result.success("数据上报成功");
        } catch (Exception e) {
            log.error("上报心率数据失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量上报心率数据
     */
    @PostMapping("/batch-report")
    @Operation(summary = "批量上报心率数据", description = "批量上报历史心率数据")
    public Result<String> batchReportHeartRate(@RequestBody List<HeartRateData> dataList) {
        try {
            heartRateService.batchSaveHeartRateData(dataList);
            return Result.success("批量上报成功,共" + dataList.size() + "条");
        } catch (Exception e) {
            log.error("批量上报心率数据失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询心率数据
     */
    @GetMapping("/query")
    @Operation(summary = "查询心率数据", description = "查询指定时间范围内的心率数据")
    public Result<List<HeartRateData>> queryHeartRate(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "开始时间") @RequestParam LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam LocalDateTime endTime) {
        try {
            List<HeartRateData> dataList = heartRateService.queryHeartRateData(userId, startTime, endTime);
            return Result.success(dataList);
        } catch (Exception e) {
            log.error("查询心率数据失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取最新心率数据
     */
    @GetMapping("/latest")
    @Operation(summary = "获取最新心率数据", description = "获取用户最新的心率监测数据")
    public Result<HeartRateData> getLatestHeartRate(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            HeartRateData data = heartRateService.getLatestHeartRateData(userId);
            if (data != null) {
                return Result.success(data);
            } else {
                return Result.notFound("暂无心率数据");
            }
        } catch (Exception e) {
            log.error("获取最新心率数据失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取平均心率
     */
    @GetMapping("/average")
    @Operation(summary = "获取平均心率", description = "计算指定时间段内的平均心率")
    public Result<Double> getAverageHeartRate(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "开始时间") @RequestParam LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam LocalDateTime endTime) {
        try {
            Double avgHeartRate = heartRateService.getAverageHeartRate(userId, startTime, endTime);
            if (avgHeartRate != null) {
                return Result.success(avgHeartRate);
            } else {
                return Result.notFound("暂无数据");
            }
        } catch (Exception e) {
            log.error("获取平均心率失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 检测异常心率
     */
    @GetMapping("/abnormal")
    @Operation(summary = "检测异常心率", description = "检测并返回异常心率数据")
    public Result<List<HeartRateData>> detectAbnormalHeartRate(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "开始时间") @RequestParam LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam LocalDateTime endTime) {
        try {
            List<HeartRateData> abnormalList = heartRateService.detectAbnormalHeartRate(userId, startTime, endTime);
            return Result.success(abnormalList);
        } catch (Exception e) {
            log.error("检测异常心率失败", e);
            return Result.error(e.getMessage());
        }
    }
}
