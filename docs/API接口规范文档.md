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

## 7. 前端集成指南

### 7.1 Vue 3 项目结构

```
cardio-guard-frontend/
├── src/
│   ├── stores/           # Pinia 状态管理
│   │   ├── user.ts       # 用户认证 Store
│   │   └── monitor.ts    # 监测数据 Store
│   ├── views/            # 页面组件
│   │   ├── Login.vue     # 登录页
│   │   ├── Dashboard.vue # 数据看板
│   │   └── Monitor.vue   # 实时监测
│   └── router/           # 路由配置
```

### 7.2 用户认证集成

**登录流程:**

```typescript
// stores/user.ts
import { defineStore } from 'pinia'
import axios from 'axios'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)

  async function login(username: string, password: string) {
    // 调用后端登录接口
    const response = await axios.post(
      `/api/auth/login?username=${username}&password=${password}`
    )
    
    // 保存 Token
    token.value = response.data.data.token
    localStorage.setItem('token', token.value)
    
    // 获取用户信息
    userInfo.value = response.data.data.user
  }

  async function fetchUserInfo() {
    const response = await axios.get('/api/auth/info', {
      headers: { Authorization: `Bearer ${token.value}` }
    })
    userInfo.value = response.data.data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, login, logout, fetchUserInfo }
})
```

**路由守卫:**

```typescript
// router/index.ts
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})
```

### 7.3 WebSocket 实时通信

**连接管理:**

```typescript
// stores/monitor.ts
export const useMonitorStore = defineStore('monitor', () => {
  const heartRateData = ref<HeartRateData[]>([])
  const latestData = ref<HeartRateData | null>(null)
  const wsConnected = ref(false)
  let ws: WebSocket | null = null

  function connectWebSocket(userId: number) {
    const token = localStorage.getItem('token')
    // 注意：根据实际后端实现，可能需要将 token 放在 header 或 query param 中
    const wsUrl = `ws://localhost:8080/api/ws/health-monitor?userId=${userId}`
    
    ws = new WebSocket(wsUrl)
    
    ws.onopen = () => {
      console.log('WebSocket 连接成功')
      wsConnected.value = true
      // 可选：发送鉴权消息
      // ws.send(JSON.stringify({ type: 'auth', token: token }))
    }
    
    ws.onmessage = (event) => {
      const data = JSON.parse(event.data)
      handleRealtimeData(data)
    }
    
    ws.onerror = (error) => {
      console.error('WebSocket 错误:', error)
      wsConnected.value = false
    }
    
    ws.onclose = () => {
      console.log('WebSocket 连接关闭')
      wsConnected.value = false
      // 5秒后自动重连
      setTimeout(() => connectWebSocket(userId), 5000)
    }
  }

  function handleRealtimeData(data: any) {
    if (data.type === 'realtime-data') {
      heartRateData.value.push(data.data)
      latestData.value = data.data
      
      // 保持最近100条数据
      if (heartRateData.value.length > 100) {
        heartRateData.value.shift()
      }
    } else if (data.type === 'ALERT') {
      // 处理预警消息
      console.warn('收到预警:', data.message)
    }
  }

  function disconnectWebSocket() {
    if (ws) {
      ws.close()
      ws = null
    }
  }

  return {
    heartRateData,
    latestData,
    wsConnected,
    connectWebSocket,
    disconnectWebSocket
  }
})
```

**组件中使用:**

```vue
<!-- views/Monitor.vue -->
<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { useMonitorStore } from '@/stores/monitor'
import { useUserStore } from '@/stores/user'

const monitorStore = useMonitorStore()
const userStore = useUserStore()

onMounted(() => {
  if (userStore.userInfo?.id) {
    monitorStore.connectWebSocket(userStore.userInfo.id)
  }
})

onUnmounted(() => {
  monitorStore.disconnectWebSocket()
})
</script>

<template>
  <div class="monitor">
    <div class="heart-rate">
      {{ monitorStore.latestData?.heartRate || '--' }} bpm
    </div>
  </div>
</template>
```

### 7.4 ECharts 数据可视化

**心率趋势图:**

```vue
<!-- views/Dashboard.vue -->
<script setup lang="ts">
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'
import { useMonitorStore } from '@/stores/monitor'
import { computed } from 'vue'

// 按需引入 ECharts 组件
use([CanvasRenderer, LineChart, GridComponent, TooltipComponent])

const monitorStore = useMonitorStore()

const chartOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    formatter: '{b}<br/>心率: {c} bpm'
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: monitorStore.heartRateData.map(d => 
      new Date(d.measureTime).toLocaleTimeString()
    )
  },
  yAxis: {
    type: 'value',
    name: '心率 (bpm)',
    min: 40,
    max: 180
  },
  series: [{
    name: '心率',
    type: 'line',
    smooth: true,
    data: monitorStore.heartRateData.map(d => d.heartRate),
    itemStyle: { color: '#409eff' },
    areaStyle: {
      color: {
        type: 'linear',
        x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
        ]
      }
    }
  }]
}))
</script>

<template>
  <v-chart :option="chartOption" autoresize style="height: 350px" />
</template>
```

### 7.5 Axios 拦截器配置

**请求拦截器 - 自动添加 Token:**

```typescript
// utils/request.ts
import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    if (error.response?.status === 401) {
      ElMessage.error('未授权,请重新登录')
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default service
```

### 7.6 历史数据查询

**查询接口封装:**

```typescript
// api/health.ts
import request from '@/utils/request'

export function queryHeartRateData(params: {
  userId: number
  startTime: string
  endTime: string
}) {
  return request.get('/health/heart-rate/query', { params })
}

export function getAverageHeartRate(params: {
  userId: number
  startTime: string
  endTime: string
}) {
  return request.get('/health/heart-rate/average', { params })
}
```

**组件中调用:**

```vue
<script setup lang="ts">
import { queryHeartRateData } from '@/api/health'

const loadHistoryData = async () => {
  try {
    const data = await queryHeartRateData({
      userId: 1,
      startTime: '2024-01-15T00:00:00',
      endTime: '2024-01-15T23:59:59'
    })
    console.log('历史数据:', data)
  } catch (error) {
    console.error('查询失败:', error)
  }
}
</script>
```

### 7.7 设备管理集成

**设备列表查询:**

```typescript
// api/device.ts
import request from '@/utils/request'

export function getUserDevices(userId: number) {
  return request.get(`/device/list?userId=${userId}`)
}

export function bindDevice(data: {
  userId: number
  serialNumber: string
}) {
  return request.post('/auth/bind-device', null, { params: data })
}

export function unbindDevice(userId: number, deviceId: number) {
  return request.post('/auth/unbind-device', null, { params: { userId, deviceId } })
}
```

### 7.8 预警信息处理

**预警列表查询:**

```typescript
// api/alert.ts
import request from '@/utils/request'

export function getAlerts(params: {
  userId?: number
  status?: string
  alertLevel?: string
}) {
  return request.get('/alert/list', { params })
}

export function processAlert(data: {
  id: number
  doctorId: number
  doctorAdvice: string
}) {
  return request.put('/alert/process', data)
}
```

### 7.9 前端性能优化

**代码分割:**

```typescript
// router/index.ts - 路由懒加载
{
  path: '/dashboard',
  component: () => import('@/views/Dashboard.vue')
}
```

**ECharts 按需引入:**

```typescript
// 只引入需要的图表类型,减少打包体积
import { LineChart, PieChart } from 'echarts/charts'
use([LineChart, PieChart])
```

**防抖处理:**

```typescript
import { debounce } from 'lodash-es'

const updateChart = debounce(() => {
  // 更新图表
}, 300)
```

### 7.10 错误处理最佳实践

**全局错误处理:**

```typescript
// main.ts
app.config.errorHandler = (err, vm, info) => {
  console.error('Vue Error:', err, info)
  // 上报错误到监控系统
}
```

**异步错误处理:**

```typescript
try {
  await userStore.login(username, password)
  ElMessage.success('登录成功')
} catch (error: any) {
  ElMessage.error(error.response?.data?.message || '登录失败')
}
```

---

## 8. 测试指南

### 8.1 API 测试

**使用 Postman 或 curl 测试:**

```bash
# 登录测试
curl -X POST "http://localhost:8080/api/auth/login?username=patient1&password=123456"

# 查询心率数据
curl -X GET "http://localhost:8080/api/health/heart-rate/query?userId=1&startTime=2024-01-01T00:00:00&endTime=2024-01-01T23:59:59" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 8.2 WebSocket 测试

**使用 wscat 测试:**

```bash
# 安装 wscat
npm install -g wscat

# 连接 WebSocket
wscat -c "ws://localhost:8080/api/ws/health-monitor?userId=1"
```

---

## 9. 常见问题

### Q1: Token 过期如何处理?

**A:** 在响应拦截器中捕获 401 错误,清除本地 Token 并跳转到登录页。

### Q2: WebSocket 断线如何重连?

**A:** 在 `onclose` 事件中使用 `setTimeout` 延迟5秒后重新调用 `connectWebSocket()`。

### Q3: ECharts 图表不显示?

**A:** 确保已正确注册所需的组件和图表类型,检查容器是否有明确的高度。

### Q4: 跨域问题如何解决?

**A:** 开发环境使用 Vite 代理,生产环境配置 Nginx 反向代理。

---

## 更新日志

### v1.2.0 (2026-04-12)
- 新增前端 Vue 3 项目
- 完善 WebSocket 实时通信规范
- 添加前端集成示例代码
- 补充 ECharts 数据可视化接口说明

### v1.0.0 (2024-01-01)
- 初始版本发布
- 实现核心功能接口
- 支持WebSocket实时推送

---

**文档维护:** CardioGuard Team  
**联系方式:** api-support@cardioguard.com  
**最后更新:** 2026-04-12
