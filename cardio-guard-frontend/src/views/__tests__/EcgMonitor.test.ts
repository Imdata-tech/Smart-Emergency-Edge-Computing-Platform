import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'
import ElementPlus from 'element-plus'
import EcgMonitor from '@/views/EcgMonitor.vue'
import * as echarts from 'echarts'

// Mock axios
vi.mock('axios', () => ({
  default: {
    post: vi.fn(),
    get: vi.fn()
  }
}))

describe('EcgMonitor Component', () => {
  let wrapper: any
  let router: any

  beforeEach(() => {
    setActivePinia(createPinia())
    
    // 创建路由
    router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: '/ecg', component: EcgMonitor }
      ]
    })
    
    router.push('/ecg')
    
    // 挂载组件
    wrapper = mount(EcgMonitor, {
      global: {
        plugins: [router, ElementPlus]
      }
    })
  })

  it('应该正确渲染ECG监测页面', () => {
    expect(wrapper.exists()).toBe(true)
  })

  it('应该显示统计卡片', () => {
    const statCards = wrapper.findAll('.stat-card')
    expect(statCards.length).toBe(4)
  })

  it('应该显示当前心率', () => {
    expect(wrapper.text()).toContain('当前心率')
  })

  it('应该显示采样率', () => {
    expect(wrapper.text()).toContain('采样率')
  })

  it('应该显示记录时长', () => {
    expect(wrapper.text()).toContain('记录时长')
  })

  it('应该显示连接状态', () => {
    expect(wrapper.text()).toContain('连接状态')
  })

  it('应该包含ECG波形图容器', () => {
    const chartContainer = wrapper.find('.waveform-card')
    expect(chartContainer.exists()).toBe(true)
  })

  it('应该显示开始记录按钮', () => {
    const buttons = wrapper.findAll('button')
    const startButton = buttons.find(btn => btn.text().includes('开始记录'))
    expect(startButton).toBeDefined()
  })

  it('应该显示停止记录按钮', () => {
    const buttons = wrapper.findAll('button')
    const stopButton = buttons.find(btn => btn.text().includes('停止记录'))
    expect(stopButton).toBeDefined()
  })

  it('应该显示AI分析按钮', () => {
    const buttons = wrapper.findAll('button')
    const analyzeButton = buttons.find(btn => btn.text().includes('AI 分析'))
    expect(analyzeButton).toBeDefined()
  })

  it('应该显示心电参数卡片', () => {
    expect(wrapper.text()).toContain('心电参数')
  })

  it('应该显示AI分析结果卡片', () => {
    expect(wrapper.text()).toContain('AI 分析结果')
  })

  it('应该显示分析历史记录表格', () => {
    const historyCard = wrapper.find('.history-card')
    expect(historyCard.exists()).toBe(true)
  })

  it('应该初始化默认数据', () => {
    const vm = wrapper.vm as any
    expect(vm.currentHeartRate).toBe(72)
    expect(vm.sampleRate).toBe(250)
    expect(vm.recordingDuration).toBe(0)
    expect(vm.isRecording).toBe(false)
  })

  it('应该正确计算连接状态文本', () => {
    const vm = wrapper.vm as any
    vm.connectionStatus = 'connected'
    expect(vm.connectionText).toBe('已连接')
    
    vm.connectionStatus = 'disconnected'
    expect(vm.connectionText).toBe('未连接')
  })

  it('应该正确获取诊断类型', () => {
    const vm = wrapper.vm as any
    expect(vm.getDiagnosisType('NORMAL')).toBe('success')
    expect(vm.getDiagnosisType('ABNORMAL')).toBe('danger')
  })

  it('应该正确获取心律失常名称', () => {
    const vm = wrapper.vm as any
    expect(vm.getArrhythmiaName('AFIB')).toBe('房颤')
    expect(vm.getArrhythmiaName('PVC')).toBe('室性早搏')
    expect(vm.getArrhythmiaName('PAC')).toBe('房性早搏')
    expect(vm.getArrhythmiaName('ST_ELEVATION')).toBe('ST段抬高')
  })

  it('应该正确获取风险等级类型', () => {
    const vm = wrapper.vm as any
    expect(vm.getRiskLevelType('LOW')).toBe('success')
    expect(vm.getRiskLevelType('MEDIUM')).toBe('warning')
    expect(vm.getRiskLevelType('HIGH')).toBe('danger')
    expect(vm.getRiskLevelType('CRITICAL')).toBe('danger')
  })

  it('应该正确获取风险等级名称', () => {
    const vm = wrapper.vm as any
    expect(vm.getRiskLevelName('LOW')).toBe('低风险')
    expect(vm.getRiskLevelName('MEDIUM')).toBe('中风险')
    expect(vm.getRiskLevelName('HIGH')).toBe('高风险')
    expect(vm.getRiskLevelName('CRITICAL')).toBe('危急')
  })

  it('应该正确获取审核状态类型', () => {
    const vm = wrapper.vm as any
    expect(vm.getReviewStatusType('PENDING')).toBe('warning')
    expect(vm.getReviewStatusType('APPROVED')).toBe('success')
    expect(vm.getReviewStatusType('REJECTED')).toBe('danger')
  })

  it('应该正确获取审核状态名称', () => {
    const vm = wrapper.vm as any
    expect(vm.getReviewStatusName('PENDING')).toBe('待审核')
    expect(vm.getReviewStatusName('APPROVED')).toBe('已通过')
    expect(vm.getReviewStatusName('REJECTED')).toBe('已拒绝')
  })

  it('应该正确获取数据质量颜色', () => {
    const vm = wrapper.vm as any
    expect(vm.getQualityColor(95)).toBe('#67c23a')
    expect(vm.getQualityColor(80)).toBe('#e6a23c')
    expect(vm.getQualityColor(60)).toBe('#f56c6c')
  })

  it('应该生成模拟ECG数据', () => {
    const vm = wrapper.vm as any
    const data = vm.generateMockEcgData()
    expect(typeof data).toBe('number')
    expect(data).toBeGreaterThanOrEqual(-2)
    expect(data).toBeLessThanOrEqual(2)
  })

  it('应该在点击开始记录时启动计时器', async () => {
    const vm = wrapper.vm as any
    const startButton = wrapper.findAll('button').find((btn: any) => 
      btn.text().includes('开始记录')
    )
    
    if (startButton) {
      await startButton.trigger('click')
      expect(vm.isRecording).toBe(true)
    }
  })

  it('应该在点击停止记录时停止计时器', async () => {
    const vm = wrapper.vm as any
    vm.isRecording = true
    
    const stopButton = wrapper.findAll('button').find((btn: any) => 
      btn.text().includes('停止记录')
    )
    
    if (stopButton) {
      await stopButton.trigger('click')
      expect(vm.isRecording).toBe(false)
    }
  })

  it('应该在无数据时提示先记录', async () => {
    const vm = wrapper.vm as any
    vm.ecgData = []
    
    const analyzeButton = wrapper.findAll('button').find((btn: any) => 
      btn.text().includes('AI 分析')
    )
    
    if (analyzeButton) {
      await analyzeButton.trigger('click')
      // 应该显示警告消息
    }
  })

  it('应该正确处理分页变化', async () => {
    const vm = wrapper.vm as any
    vm.handlePageChange(2)
    expect(vm.currentPage).toBe(2)
  })

  it('ECG图表配置应该正确', () => {
    const vm = wrapper.vm as any
    const option = vm.ecgChartOption
    
    expect(option.tooltip).toBeDefined()
    expect(option.grid).toBeDefined()
    expect(option.xAxis).toBeDefined()
    expect(option.yAxis).toBeDefined()
    expect(option.series).toBeDefined()
    expect(option.series[0].type).toBe('line')
  })

  it('应该在组件卸载时清理资源', async () => {
    const vm = wrapper.vm as any
    vm.isRecording = true
    
    wrapper.unmount()
    
    expect(vm.isRecording).toBe(false)
  })
})
