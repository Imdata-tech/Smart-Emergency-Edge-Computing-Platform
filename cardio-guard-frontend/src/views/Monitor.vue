<template>
  <div class="monitor">
    <el-row :gutter="20">
      <!-- 实时心率显示 -->
      <el-col :span="16">
        <el-card class="realtime-card">
          <template #header>
            <div class="card-header">
              <span>实时心率监测</span>
              <el-tag :type="connectionStatus.type">
                {{ connectionStatus.text }}
              </el-tag>
            </div>
          </template>
          
          <!-- 大字体心率显示 -->
          <div class="heart-rate-display">
            <div class="heart-icon" :class="{ 'pulse': isAbnormal }">
              <el-icon><Heart /></el-icon>
            </div>
            <div class="heart-rate-value">
              {{ monitorStore.latestData?.heartRate || '--' }}
            </div>
            <div class="heart-rate-unit">bpm</div>
          </div>
          
          <!-- 实时波形图 -->
          <v-chart
            class="waveform-chart"
            :option="waveformOption"
            autoresize
          />
          
          <!-- 详细信息 -->
          <el-descriptions :column="3" border class="detail-info">
            <el-descriptions-item label="RR间期">
              {{ monitorStore.latestData?.rrInterval || '--' }} ms
            </el-descriptions-item>
            <el-descriptions-item label="HRV">
              {{ monitorStore.latestData?.hrv || '--' }} ms
            </el-descriptions-item>
            <el-descriptions-item label="数据质量">
              {{ monitorStore.latestData?.dataQuality || '--' }}%
            </el-descriptions-item>
            <el-descriptions-item label="活动状态">
              {{ getActivityText(monitorStore.latestData?.activityStatus) }}
            </el-descriptions-item>
            <el-descriptions-item label="测量时间">
              {{ formatTime(monitorStore.latestData?.measureTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="异常状态">
              <el-tag :type="getAbnormalType(monitorStore.latestData?.isAbnormal)">
                {{ getAbnormalText(monitorStore.latestData) }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      
      <!-- 预警信息 -->
      <el-col :span="8">
        <el-card class="alerts-card">
          <template #header>
            <span>最近预警</span>
          </template>
          
          <el-timeline>
            <el-timeline-item
              v-for="alert in recentAlerts"
              :key="alert.id"
              :timestamp="alert.createTime"
              placement="top"
              :type="getAlertType(alert.level)"
            >
              <el-card>
                <h4>{{ alert.alertType }}</h4>
                <p>{{ alert.message }}</p>
                <p class="alert-heart-rate">心率: {{ alert.heartRate }} bpm</p>
              </el-card>
            </el-timeline-item>
            
            <el-empty v-if="recentAlerts.length === 0" description="暂无预警" />
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent
} from 'echarts/components'
import dayjs from 'dayjs'
import { useMonitorStore } from '@/stores/monitor'
import { useUserStore } from '@/stores/user'

// 注册 ECharts
use([CanvasRenderer, LineChart, GridComponent, TooltipComponent])

const monitorStore = useMonitorStore()
const userStore = useUserStore()

const isAbnormal = ref(false)
const recentAlerts = ref<any[]>([])

// 连接状态
const connectionStatus = computed(() => {
  if (monitorStore.wsConnected) {
    return { type: 'success', text: '已连接' }
  }
  return { type: 'danger', text: '未连接' }
})

// 波形图配置
const waveformOption = computed(() => ({
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    top: '10%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    show: false,
    data: Array.from({ length: 50 }, (_, i) => i)
  },
  yAxis: {
    type: 'value',
    min: 40,
    max: 180,
    name: '心率 (bpm)'
  },
  series: [
    {
      name: '心率',
      type: 'line',
      smooth: true,
      showSymbol: false,
      data: monitorStore.heartRateData.slice(-50).map(d => d.heartRate),
      lineStyle: {
        width: 2,
        color: '#409eff'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ]
        }
      }
    }
  ]
}))

// 格式化时间
const formatTime = (time?: string) => {
  if (!time) return '--'
  return dayjs(time).format('HH:mm:ss')
}

// 获取活动状态文本
const getActivityText = (status?: string) => {
  const map: Record<string, string> = {
    'RESTING': '静息',
    'WALKING': '行走',
    'RUNNING': '跑步',
    'SLEEPING': '睡眠',
    'UNKNOWN': '未知'
  }
  return status ? map[status] || status : '--'
}

// 获取异常类型
const getAbnormalType = (isAbnormal?: number) => {
  if (!isAbnormal) return 'success'
  return 'danger'
}

// 获取异常文本
const getAbnormalText = (data?: any) => {
  if (!data?.isAbnormal) return '正常'
  return data.abnormalType === 'TACHYCARDIA' ? '心动过速' : '心动过缓'
}

// 获取预警类型
const getAlertType = (level: string) => {
  const map: Record<string, any> = {
    'LOW': 'info',
    'MEDIUM': 'warning',
    'HIGH': 'danger',
    'CRITICAL': 'danger'
  }
  return map[level] || 'info'
}

// 监听最新数据
const checkAbnormal = () => {
  const latest = monitorStore.latestData
  if (latest) {
    isAbnormal.value = latest.isAbnormal === 1
  }
}

onMounted(() => {
  if (userStore.userInfo?.id) {
    monitorStore.connectWebSocket(userStore.userInfo.id)
  }
  
  // 定期检查异常状态
  const timer = setInterval(checkAbnormal, 1000)
  
  onUnmounted(() => {
    clearInterval(timer)
    monitorStore.disconnectWebSocket()
  })
})
</script>

<style scoped lang="scss">
.monitor {
  .realtime-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .heart-rate-display {
      display: flex;
      align-items: baseline;
      justify-content: center;
      padding: 40px 0;
      gap: 10px;
      
      .heart-icon {
        font-size: 48px;
        color: #f56c6c;
        
        &.pulse {
          animation: pulse 1s infinite;
        }
      }
      
      .heart-rate-value {
        font-size: 72px;
        font-weight: bold;
        color: #303133;
        line-height: 1;
      }
      
      .heart-rate-unit {
        font-size: 24px;
        color: #909399;
      }
    }
    
    @keyframes pulse {
      0%, 100% {
        transform: scale(1);
      }
      50% {
        transform: scale(1.2);
      }
    }
    
    .waveform-chart {
      height: 300px;
      margin: 20px 0;
    }
    
    .detail-info {
      margin-top: 20px;
    }
  }
  
  .alerts-card {
    .alert-heart-rate {
      margin-top: 10px;
      color: #f56c6c;
      font-weight: bold;
    }
  }
}
</style>
