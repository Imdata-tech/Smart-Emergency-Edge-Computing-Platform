# CardioGuard 360 API 接口规范文档

## 文档版本: v1.0.0

---

## 目录

1. [认证接口](#1-认证接口)
2. [用户管理接口](#2-用户管理接口)
3. [设备管理接口](#3-设备管理接口)
4. [心率数据接口](#4-心率数据接口)
5. [健康预警接口](#5-健康预警接口)
6. [WebSocket实时推送](#6-websocket实时推送)

---

## 基础信息

### API Base URL
```
http://localhost:8080/api
```

### 响应格式
所有接口返回统一的JSON格式:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1704067200000
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 认证方式

大部分接口需要在Header中携带JWT Token:

```
Authorization: Bearer {token}
```

---

## 1. 认证接口

### 1.1 用户注册

**接口地址:** `POST /api/auth/register`

**请求参数:**
```json
{
  "username": "patient1",
  "password": "123456",
  "realName": "张三",
  "phone": "13800138000",
  "email": "patient1@example.com",
  "gender": 1,
  "age": 60,
  "idCard": "110101196001011234",
  "role": "PATIENT"
}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "patient1",
    "realName": "张三",
    "phone": "13800138000",
    "role": "PATIENT",
    "status": 1,
    "createTime": "2024-01-01T10:00:00"
  },
  "timestamp": 1704067200000
}
```

### 1.2 用户登录

**接口地址:** `POST /api/auth/login`

**请求参数:**
```
username=patient1&password=123456
```

**响应示例:**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "username": "patient1",
      "realName": "张三",
      "role": "PATIENT"
    }
  },
  "timestamp": 1704067200000
}
```

### 1.3 刷新Token

**接口地址:** `POST /api/auth/refresh-token`

**请求参数:**
```
token={旧token}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "刷新成功",
  "data": "eyJhbGciOiJIUzI1NiJ9...",
  "timestamp": 1704067200000
}
```

---

## 2. 用户管理接口

### 2.1 获取用户信息

**接口地址:** `GET /api/auth/{id}`

**路径参数:**
- `id`: 用户ID

**响应示例:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "patient1",
    "realName": "张三",
    "phone": "13800138000",
    "email": "patient1@example.com",
    "gender": 1,
    "age": 60,
    "role": "PATIENT",
    "status": 1
  },
  "timestamp": 1704067200000
}
```

### 2.2 更新用户信息

**接口地址:** `PUT /api/auth`

**请求参数:**
```json
{
  "id": 1,
  "realName": "张三丰",
  "phone": "13800138000",
  "email": "newemail@example.com",
  "age": 61
}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null,
  "timestamp": 1704067200000
}
```

### 2.3 删除用户

**接口地址:** `DELETE /api/auth/{id}`

**路径参数:**
- `id`: 用户ID

**响应示例:**
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1704067200000
}
```

### 2.4 查询用户列表

**接口地址:** `GET /api/auth/list`

**请求参数:**
- `pageNum`: 页码(默认1)
- `pageSize`: 每页大小(默认10)
- `role`: 角色筛选(可选)

**响应示例:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [...],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 10,
    "hasPrevious": false,
    "hasNext": true
  },
  "timestamp": 1704067200000
}
```

### 2.5 绑定设备

**接口地址:** `POST /api/auth/bind-device`

**请求参数:**
```
userId=1&serialNumber=ECG20240001
```

**响应示例:**
```json
{
  "code": 200,
  "message": "设备绑定成功",
  "data": null,
  "timestamp": 1704067200000
}
```

### 2.6 解绑设备

**接口地址:** `POST /api/auth/unbind-device`

**请求参数:**
```
userId=1&deviceId=1
```

**响应示例:**
```json
{
  "code": 200,
  "message": "设备解绑成功",
  "data": null,
  "timestamp": 1704067200000
}
```

---

## 3. 设备管理接口

### 3.1 获取设备列表

**接口地址:** `GET /api/device/list`

**请求参数:**
- `userId`: 用户ID(可选)
- `deviceType`: 设备类型(可选)
- `status`: 设备状态(可选)

**响应示例:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "serialNumber": "ECG20240001",
      "deviceName": "心电监测仪-001",
      "model": "ECG-Pro-X1",
      "deviceType": "ECG",
      "manufacturer": "CardioTech",
      "userId": 1,
      "status": 1,
      "batteryLevel": 85,
      "firmwareVersion": "v1.2.3"
    }
  ],
  "timestamp": 1704067200000
}
```

---

## 4. 心率数据接口

### 4.1 上报心率数据

**接口地址:** `POST /api/health/heart-rate/report`

**请求参数:**
```json
{
  "userId": 1,
  "deviceId": 1,
  "heartRate": 75.5,
  "rrInterval": 800.0,
  "hrv": 50.0,
  "activityStatus": "REST",
  "dataQuality": 95
}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "数据上报成功",
  "data": null,
  "timestamp": 1704067200000
}
```

### 4.2 批量上报心率数据

**接口地址:** `POST /api/health/heart-rate/batch-report`

**请求参数:**
```json
[
  {
    "userId": 1,
    "deviceId": 1,
    "heartRate": 75.5,
    "measureTime": "2024-01-01T10:00:00"
  },
  {
    "userId": 1,
    "deviceId": 1,
    "heartRate": 76.2,
    "measureTime": "2024-01-01T10:01:00"
  }
]
```

**响应示例:**
```json
{
  "code": 200,
  "message": "批量上报成功,共2条",
  "data": null,
  "timestamp": 1704067200000
}
```

### 4.3 查询心率数据

**接口地址:** `GET /api/health/heart-rate/query`

**请求参数:**
- `userId`: 用户ID
- `startTime`: 开始时间(ISO格式)
- `endTime`: 结束时间(ISO格式)

**响应示例:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "userId": 1,
      "deviceId": 1,
      "measureTime": "2024-01-01T10:00:00",
      "heartRate": 75.5,
      "rrInterval": 800.0,
      "hrv": 50.0,
      "activityStatus": "REST",
      "dataQuality": 95
    }
  ],
  "timestamp": 1704067200000
}
```

### 4.4 获取最新心率数据

**接口地址:** `GET /api/health/heart-rate/latest`

**请求参数:**
- `userId`: 用户ID

**响应示例:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": 1,
    "deviceId": 1,
    "measureTime": "2024-01-01T10:30:00",
    "heartRate": 72.3,
    "rrInterval": 830.0,
    "hrv": 55.0,
    "activityStatus": "REST",
    "dataQuality": 98
  },
  "timestamp": 1704067200000
}
```

### 4.5 获取平均心率

**接口地址:** `GET /api/health/heart-rate/average`

**请求参数:**
- `userId`: 用户ID
- `startTime`: 开始时间
- `endTime`: 结束时间

**响应示例:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 74.5,
  "timestamp": 1704067200000
}
```

### 4.6 检测异常心率

**接口地址:** `GET /api/health/heart-rate/abnormal`

**请求参数:**
- `userId`: 用户ID
- `startTime`: 开始时间
- `endTime`: 结束时间

**响应示例:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "userId": 1,
      "deviceId": 1,
      "measureTime": "2024-01-01T10:15:00",
      "heartRate": 105.2,
      "isAbnormal": 1,
      "abnormalType": "TACHYCARDIA"
    }
  ],
  "timestamp": 1704067200000
}
```

---

## 5. 健康预警接口

### 5.1 查询预警列表

**接口地址:** `GET /api/alert/list`

**请求参数:**
- `userId`: 用户ID
- `status`: 状态筛选(可选)
- `alertLevel`: 预警级别筛选(可选)

**响应示例:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "deviceId": 1,
      "alertType": "TACHYCARDIA",
      "alertLevel": "HIGH",
      "title": "检测到心动过速",
      "description": "心率超过正常范围上限",
      "triggerValue": 105.2,
      "normalRangeMin": 60.0,
      "normalRangeMax": 100.0,
      "status": "UNREAD",
      "createTime": "2024-01-01T10:15:00"
    }
  ],
  "timestamp": 1704067200000
}
```

### 5.2 处理预警

**接口地址:** `PUT /api/alert/process`

**请求参数:**
```json
{
  "id": 1,
  "doctorId": 2,
  "doctorAdvice": "建议患者休息,持续观察。如症状加重请及时就医。"
}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "处理成功",
  "data": null,
  "timestamp": 1704067200000
}
```

---

## 6. WebSocket实时推送

### 6.1 建立连接

**WebSocket地址:**
```
ws://localhost:8080/api/ws/health-monitor?userId=1
```

### 6.2 消息格式

**服务端推送 - 实时数据:**
```json
{
  "type": "realtime-data",
  "data": {
    "heartRate": 75.5,
    "measureTime": "2024-01-01T10:30:00",
    ...
  }
}
```

**服务端推送 - 预警消息:**
```json
{
  "type": "ALERT",
  "message": "检测到异常心率: 105.2次/分钟",
  "data": {
    "alertType": "TACHYCARDIA",
    "alertLevel": "HIGH",
    ...
  }
}
```

**客户端心跳:**
```json
{
  "type": "ping"
}
```

**服务端响应:**
```json
{
  "type": "ack",
  "message": "消息已接收"
}
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400001 | 参数验证失败 |
| 401001 | Token无效或过期 |
| 403001 | 权限不足 |
| 404001 | 资源不存在 |
| 500001 | 数据库操作失败 |
| 500002 | 外部服务调用失败 |

---

## 速率限制

- 普通接口: 100次/分钟
- 数据上报接口: 60次/分钟
- 登录接口: 10次/分钟

---

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 实现核心功能接口
- 支持WebSocket实时推送

---

**文档维护:** CardioGuard Team  
**联系方式:** api-support@cardioguard.com
