import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export interface HeartRateData {
  measureTime: string
  deviceId: number
  userId: number
  heartRate: number
  rrInterval?: number
  hrv?: number
  activityStatus?: string
  dataQuality?: number
  isAbnormal?: number
  abnormalType?: string
}

export const useMonitorStore = defineStore('monitor', () => {
  const heartRateData = ref<HeartRateData[]>([])
  const latestData = ref<HeartRateData | null>(null)
  const wsConnected = ref(false)
  let ws: WebSocket | null = null

  // 连接 WebSocket
  function connectWebSocket(userId: number) {
    const token = localStorage.getItem('token')
    const wsUrl = `ws://localhost:8080/api/ws/health-monitor?userId=${userId}&token=${token}`
    
    ws = new WebSocket(wsUrl)
    
    ws.onopen = () => {
      console.log('WebSocket 连接成功')
      wsConnected.value = true
    }
    
    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        handleRealtimeData(data)
      } catch (error) {
        console.error('解析 WebSocket 数据失败:', error)
      }
    }
    
    ws.onerror = (error) => {
      console.error('WebSocket 错误:', error)
      wsConnected.value = false
    }
    
    ws.onclose = () => {
      console.log('WebSocket 连接关闭')
      wsConnected.value = false
    }
  }

  // 处理实时数据
  function handleRealtimeData(data: any) {
    if (data.type === 'HEART_RATE') {
      const heartRate: HeartRateData = data.data
      heartRateData.value.push(heartRate)
      latestData.value = heartRate
      
      // 保持最近100条数据
      if (heartRateData.value.length > 100) {
        heartRateData.value.shift()
      }
    } else if (data.type === 'ALERT') {
      // 处理预警信息
      console.warn('收到预警:', data.message)
    }
  }

  // 断开 WebSocket
  function disconnectWebSocket() {
    if (ws) {
      ws.close()
      ws = null
    }
  }

  // 查询历史数据
  async function queryHistoryData(
    userId: number,
    startTime: string,
    endTime: string
  ) {
    try {
      const response = await axios.get('/api/health/heart-rate/query', {
        params: { userId, startTime, endTime },
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      })
      heartRateData.value = response.data.data || []
      return response.data.data
    } catch (error) {
      console.error('查询历史数据失败:', error)
      throw error
    }
  }

  // 清空数据
  function clearData() {
    heartRateData.value = []
    latestData.value = null
  }

  return {
    heartRateData,
    latestData,
    wsConnected,
    connectWebSocket,
    disconnectWebSocket,
    queryHistoryData,
    clearData
  }
})
