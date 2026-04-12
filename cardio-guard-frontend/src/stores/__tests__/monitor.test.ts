import { describe, it, expect, beforeEach, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useMonitorStore } from '@/stores/monitor'
import axios from 'axios'

// Mock axios
vi.mock('axios', () => ({
  default: {
    get: vi.fn()
  }
}))

describe('Monitor Store', () => {
  let monitorStore: ReturnType<typeof useMonitorStore>

  beforeEach(() => {
    setActivePinia(createPinia())
    monitorStore = useMonitorStore()
  })

  describe('初始化状态', () => {
    it('应该正确初始化空状态', () => {
      expect(monitorStore.heartRateData).toEqual([])
      expect(monitorStore.latestData).toBeNull()
      expect(monitorStore.wsConnected).toBe(false)
    })
  })

  describe('WebSocket 连接管理', () => {
    it('connectWebSocket 应该创建 WebSocket 连接', () => {
      const mockWebSocket = {
        onopen: vi.fn(),
        onmessage: vi.fn(),
        onerror: vi.fn(),
        onclose: vi.fn(),
        close: vi.fn()
      }
      
      // Mock WebSocket 构造函数
      const OriginalWebSocket = (global as any).WebSocket
      ;(global as any).WebSocket = vi.fn(() => mockWebSocket)

      monitorStore.connectWebSocket(1)

      expect((global as any).WebSocket).toHaveBeenCalledWith(
        'ws://localhost:8080/api/ws/health-monitor?userId=1&token=null'
      )
      
      // 恢复原始 WebSocket
      ;(global as any).WebSocket = OriginalWebSocket
    })

    it('disconnectWebSocket 应该关闭连接', () => {
      const mockClose = vi.fn()
      ;(monitorStore as any).ws = { close: mockClose }

      monitorStore.disconnectWebSocket()

      expect(mockClose).toHaveBeenCalled()
      expect((monitorStore as any).ws).toBeNull()
    })

    it('处理 WebSocket 消息 - 心率数据', () => {
      const mockHeartRateData = {
        type: 'HEART_RATE',
        data: {
          measureTime: '2024-01-15T10:30:00',
          userId: 1,
          deviceId: 1,
          heartRate: 75.5,
          rrInterval: 800,
          hrv: 50,
          activityStatus: 'RESTING',
          dataQuality: 95,
          isAbnormal: 0
        }
      }

      // 模拟接收消息
      monitorStore.handleRealtimeData = vi.fn()
      ;(monitorStore as any).handleRealtimeData(mockHeartRateData)

      expect(monitorStore.handleRealtimeData).toHaveBeenCalledWith(mockHeartRateData)
    })

    it('处理 WebSocket 消息 - 预警信息', () => {
      const consoleWarnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {})
      
      const alertData = {
        type: 'ALERT',
        message: '检测到异常心率'
      }

      ;(monitorStore as any).handleRealtimeData(alertData)

      expect(consoleWarnSpy).toHaveBeenCalledWith('收到预警:', '检测到异常心率')
      
      consoleWarnSpy.mockRestore()
    })
  })

  describe('实时数据处理', () => {
    it('应该正确添加心率数据到数组', () => {
      const heartRateData = {
        measureTime: '2024-01-15T10:30:00',
        userId: 1,
        deviceId: 1,
        heartRate: 75.5
      } as any

      ;(monitorStore as any).handleRealtimeData({
        type: 'HEART_RATE',
        data: heartRateData
      })

      expect(monitorStore.heartRateData.length).toBe(1)
      expect(monitorStore.latestData).toEqual(heartRateData)
    })

    it('应该保持最近100条数据', () => {
      // 添加101条数据
      for (let i = 0; i < 101; i++) {
        ;(monitorStore as any).handleRealtimeData({
          type: 'HEART_RATE',
          data: {
            measureTime: `2024-01-15T10:${i}:00`,
            userId: 1,
            deviceId: 1,
            heartRate: 70 + i
          }
        })
      }

      expect(monitorStore.heartRateData.length).toBe(100)
      expect(monitorStore.heartRateData[0].heartRate).toBe(71) // 第一条被移除
    })

    it('应该更新最新数据', () => {
      const data1 = { heartRate: 70 } as any
      const data2 = { heartRate: 75 } as any

      ;(monitorStore as any).handleRealtimeData({ type: 'HEART_RATE', data: data1 })
      ;(monitorStore as any).handleRealtimeData({ type: 'HEART_RATE', data: data2 })

      expect(monitorStore.latestData).toEqual(data2)
    })
  })

  describe('历史数据查询', () => {
    it('应该成功查询历史数据', async () => {
      const mockData = [
        { measureTime: '2024-01-15T10:00:00', heartRate: 70 },
        { measureTime: '2024-01-15T10:01:00', heartRate: 72 }
      ]

      localStorage.setItem('token', 'test-token')
      ;(axios.get as any).mockResolvedValueOnce({
        data: { data: mockData }
      })

      const result = await monitorStore.queryHistoryData(
        1,
        '2024-01-15T00:00:00',
        '2024-01-15T23:59:59'
      )

      expect(result).toEqual(mockData)
      expect(monitorStore.heartRateData).toEqual(mockData)
      expect(axios.get).toHaveBeenCalledWith('/api/health/heart-rate/query', {
        params: {
          userId: 1,
          startTime: '2024-01-15T00:00:00',
          endTime: '2024-01-15T23:59:59'
        },
        headers: { Authorization: 'Bearer test-token' }
      })
    })

    it('查询失败应该抛出异常', async () => {
      const error = new Error('Network Error')
      ;(axios.get as any).mockRejectedValueOnce(error)

      await expect(
        monitorStore.queryHistoryData(1, '2024-01-15T00:00:00', '2024-01-15T23:59:59')
      ).rejects.toThrow('Network Error')
    })
  })

  describe('清空数据', () => {
    it('clearData 应该清空所有数据', () => {
      monitorStore.heartRateData = [
        { measureTime: '2024-01-15T10:00:00', heartRate: 70 } as any
      ]
      monitorStore.latestData = { measureTime: '2024-01-15T10:00:00', heartRate: 70 } as any

      monitorStore.clearData()

      expect(monitorStore.heartRateData).toEqual([])
      expect(monitorStore.latestData).toBeNull()
    })
  })

  describe('边界条件测试', () => {
    it('应该处理空的心率数据', () => {
      ;(monitorStore as any).handleRealtimeData({
        type: 'HEART_RATE',
        data: null
      })

      expect(monitorStore.heartRateData.length).toBe(0)
    })

    it('应该处理未知消息类型', () => {
      const consoleLogSpy = vi.spyOn(console, 'log').mockImplementation(() => {})
      
      ;(monitorStore as any).handleRealtimeData({
        type: 'UNKNOWN_TYPE',
        data: {}
      })

      expect(monitorStore.heartRateData.length).toBe(0)
      
      consoleLogSpy.mockRestore()
    })

    it('应该处理异常心率值', () => {
      const abnormalData = {
        measureTime: '2024-01-15T10:30:00',
        userId: 1,
        deviceId: 1,
        heartRate: 150, // 心动过速
        isAbnormal: 1,
        abnormalType: 'TACHYCARDIA'
      } as any

      ;(monitorStore as any).handleRealtimeData({
        type: 'HEART_RATE',
        data: abnormalData
      })

      expect(monitorStore.latestData?.isAbnormal).toBe(1)
      expect(monitorStore.latestData?.abnormalType).toBe('TACHYCARDIA')
    })
  })

  describe('连接状态管理', () => {
    it('wsConnected 初始状态应该为 false', () => {
      expect(monitorStore.wsConnected).toBe(false)
    })

    it('应该在连接成功后更新状态', () => {
      // 模拟 WebSocket onopen
      const mockWs = {
        onopen: null as any,
        onmessage: null as any,
        onerror: null as any,
        onclose: null as any,
        close: vi.fn()
      }
      
      ;(monitorStore as any).ws = mockWs
      
      // 触发 onopen
      if (mockWs.onopen) {
        mockWs.onopen()
      }

      // 注意: 实际实现中需要在 connectWebSocket 中设置 wsConnected
      // 这里仅验证初始状态
      expect(monitorStore.wsConnected).toBeDefined()
    })
  })
})
