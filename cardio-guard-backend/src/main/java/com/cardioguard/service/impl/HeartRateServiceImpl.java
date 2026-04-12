package com.cardioguard.service.impl;

import com.cardioguard.config.HealthMonitorWebSocketHandler;
import com.cardioguard.entity.HeartRateData;
import com.cardioguard.service.HeartRateService;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 心率数据服务实现类(使用InfluxDB)
 */
@Slf4j
@Service
public class HeartRateServiceImpl implements HeartRateService {
    
    @Value("${influxdb.url}")
    private String influxUrl;
    
    @Value("${influxdb.token}")
    private String token;
    
    @Value("${influxdb.org}")
    private String org;
    
    @Value("${influxdb.bucket}")
    private String bucket;
    
    private InfluxDBClient influxDBClient;
    
    /**
     * 初始化InfluxDB客户端
     */
    private InfluxDBClient getInfluxDBClient() {
        if (influxDBClient == null) {
            synchronized (this) {
                if (influxDBClient == null) {
                    influxDBClient = InfluxDBClientFactory.create(influxUrl, token.toCharArray(), org, bucket);
                }
            }
        }
        return influxDBClient;
    }
    
    @Override
    public void saveHeartRateData(HeartRateData data) {
        try {
            InfluxDBClient client = getInfluxDBClient();
            WriteApiBlocking writeApi = client.getWriteApiBlocking();
            
            // 构建Point
            var point = com.influxdb.client.domain.Point.measurement("heart_rate")
                    .addTag("userId", String.valueOf(data.getUserId()))
                    .addTag("deviceId", String.valueOf(data.getDeviceId()))
                    .addField("heartRate", data.getHeartRate())
                    .addField("rrInterval", data.getRrInterval() != null ? data.getRrInterval() : 0.0)
                    .addField("hrv", data.getHrv() != null ? data.getHrv() : 0.0)
                    .addField("activityStatus", data.getActivityStatus() != null ? data.getActivityStatus() : "UNKNOWN")
                    .addField("dataQuality", data.getDataQuality() != null ? data.getDataQuality() : 0)
                    .addField("isAbnormal", data.getIsAbnormal() != null ? data.getIsAbnormal() : 0)
                    .addField("abnormalType", data.getAbnormalType() != null ? data.getAbnormalType() : "")
                    .time(Date.from(data.getMeasureTime().atZone(ZoneId.systemDefault()).toInstant()), WritePrecision.NS);
            
            writeApi.writePoint(point);
            log.debug("心率数据保存成功: userId={}, heartRate={}", data.getUserId(), data.getHeartRate());
        } catch (Exception e) {
            log.error("保存心率数据失败", e);
            throw new RuntimeException("保存心率数据失败", e);
        }
    }
    
    @Override
    public void batchSaveHeartRateData(List<HeartRateData> dataList) {
        try {
            InfluxDBClient client = getInfluxDBClient();
            WriteApiBlocking writeApi = client.getWriteApiBlocking();
            
            List<com.influxdb.client.domain.Point> points = new ArrayList<>();
            for (HeartRateData data : dataList) {
                var point = com.influxdb.client.domain.Point.measurement("heart_rate")
                        .addTag("userId", String.valueOf(data.getUserId()))
                        .addTag("deviceId", String.valueOf(data.getDeviceId()))
                        .addField("heartRate", data.getHeartRate())
                        .addField("rrInterval", data.getRrInterval() != null ? data.getRrInterval() : 0.0)
                        .addField("hrv", data.getHrv() != null ? data.getHrv() : 0.0)
                        .addField("activityStatus", data.getActivityStatus() != null ? data.getActivityStatus() : "UNKNOWN")
                        .addField("dataQuality", data.getDataQuality() != null ? data.getDataQuality() : 0)
                        .addField("isAbnormal", data.getIsAbnormal() != null ? data.getIsAbnormal() : 0)
                        .addField("abnormalType", data.getAbnormalType() != null ? data.getAbnormalType() : "")
                        .time(Date.from(data.getMeasureTime().atZone(ZoneId.systemDefault()).toInstant()), WritePrecision.NS);
                points.add(point);
            }
            
            writeApi.writePoints(points);
            log.info("批量保存心率数据成功: 共{}条", dataList.size());
        } catch (Exception e) {
            log.error("批量保存心率数据失败", e);
            throw new RuntimeException("批量保存心率数据失败", e);
        }
    }
    
    @Override
    public List<HeartRateData> queryHeartRateData(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        List<HeartRateData> resultList = new ArrayList<>();
        
        try {
            InfluxDBClient client = getInfluxDBClient();
            
            String flux = String.format(
                "from(bucket: \"%s\")\n" +
                "  |> range(start: %s, stop: %s)\n" +
                "  |> filter(fn: (r) => r._measurement == \"heart_rate\")\n" +
                "  |> filter(fn: (r) => r.userId == \"%s\")\n" +
                "  |> sort(columns: [\"_time\"])",
                bucket,
                startTime.toString(),
                endTime.toString(),
                userId
            );
            
            List<FluxTable> tables = client.getQueryApi().query(flux);
            
            for (FluxTable table : tables) {
                for (var record : table.getRecords()) {
                    HeartRateData data = new HeartRateData();
                    data.setUserId(userId);
                    data.setMeasureTime(LocalDateTime.ofInstant(
                        record.getTime(), ZoneId.systemDefault()));
                    
                    // 从tags中获取deviceId
                    String deviceIdStr = record.getValueByKey("deviceId");
                    if (deviceIdStr != null && !deviceIdStr.isEmpty()) {
                        data.setDeviceId(Long.parseLong(deviceIdStr));
                    }
                    
                    // 从fields中获取各项指标
                    Object heartRate = record.getValueByKey("heartRate");
                    if (heartRate != null) {
                        data.setHeartRate(Double.parseDouble(heartRate.toString()));
                    }
                    
                    resultList.add(data);
                }
            }
            
            log.debug("查询心率数据成功: userId={}, 共{}条", userId, resultList.size());
        } catch (Exception e) {
            log.error("查询心率数据失败", e);
            throw new RuntimeException("查询心率数据失败", e);
        }
        
        return resultList;
    }
    
    @Override
    public HeartRateData getLatestHeartRateData(Long userId) {
        try {
            InfluxDBClient client = getInfluxDBClient();
            
            String flux = String.format(
                "from(bucket: \"%s\")\n" +
                "  |> range(start: -24h)\n" +
                "  |> filter(fn: (r) => r._measurement == \"heart_rate\")\n" +
                "  |> filter(fn: (r) => r.userId == \"%s\")\n" +
                "  |> last()",
                bucket,
                userId
            );
            
            List<FluxTable> tables = client.getQueryApi().query(flux);
            
            if (tables.isEmpty() || tables.get(0).getRecords().isEmpty()) {
                return null;
            }
            
            var record = tables.get(0).getRecords().get(0);
            HeartRateData data = new HeartRateData();
            data.setUserId(userId);
            data.setMeasureTime(LocalDateTime.ofInstant(record.getTime(), ZoneId.systemDefault()));
            
            String deviceIdStr = record.getValueByKey("deviceId");
            if (deviceIdStr != null && !deviceIdStr.isEmpty()) {
                data.setDeviceId(Long.parseLong(deviceIdStr));
            }
            
            Object heartRate = record.getValueByKey("heartRate");
            if (heartRate != null) {
                data.setHeartRate(Double.parseDouble(heartRate.toString()));
            }
            
            return data;
        } catch (Exception e) {
            log.error("查询最新心率数据失败", e);
            return null;
        }
    }
    
    @Override
    public Double getAverageHeartRate(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            InfluxDBClient client = getInfluxDBClient();
            
            String flux = String.format(
                "from(bucket: \"%s\")\n" +
                "  |> range(start: %s, stop: %s)\n" +
                "  |> filter(fn: (r) => r._measurement == \"heart_rate\")\n" +
                "  |> filter(fn: (r) => r.userId == \"%s\")\n" +
                "  |> mean(column: \"heartRate\")",
                bucket,
                startTime.toString(),
                endTime.toString(),
                userId
            );
            
            List<FluxTable> tables = client.getQueryApi().query(flux);
            
            if (tables.isEmpty() || tables.get(0).getRecords().isEmpty()) {
                return null;
            }
            
            var record = tables.get(0).getRecords().get(0);
            Object value = record.getValue();
            if (value != null) {
                return Double.parseDouble(value.toString());
            }
            
            return null;
        } catch (Exception e) {
            log.error("计算平均心率失败", e);
            return null;
        }
    }
    
    @Override
    public List<HeartRateData> detectAbnormalHeartRate(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        List<HeartRateData> abnormalList = new ArrayList<>();
        
        try {
            // 查询所有心率数据
            List<HeartRateData> allData = queryHeartRateData(userId, startTime, endTime);
            
            for (HeartRateData data : allData) {
                // 检测异常: 心动过速(>100) 或 心动过缓(<60)
                if (data.getHeartRate() != null) {
                    if (data.getHeartRate() > 100 || data.getHeartRate() < 60) {
                        data.setIsAbnormal(1);
                        data.setAbnormalType(
                            data.getHeartRate() > 100 ? "TACHYCARDIA" : "BRADYCARDIA"
                        );
                        abnormalList.add(data);
                        
                        // 实时推送预警
                        com.cardioguard.config.HealthMonitorWebSocketHandler.pushData(
                            userId, 
                            Map.of(
                                "type", "ALERT",
                                "message", "检测到异常心率: " + data.getHeartRate() + "次/分钟",
                                "data", data
                            )
                        );
                    }
                }
            }
            
            log.info("异常心率检测完成: userId={}, 异常数量={}", userId, abnormalList.size());
        } catch (Exception e) {
            log.error("异常心率检测失败", e);
        }
        
        return abnormalList;
    }
}
