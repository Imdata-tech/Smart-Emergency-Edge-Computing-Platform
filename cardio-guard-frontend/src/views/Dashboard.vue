<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon heart-rate">
              <el-icon><Heart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ latestHeartRate || '--' }}</div>
              <div class="stat-label">当前心率 (bpm)</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon average">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ averageHeartRate || '--' }}</div>
              <div class="stat-label">平均心率 (bpm)</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon alerts">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ alertCount }}</div>
              <div class="stat-label">今日预警</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon devices">
              <el-icon><Cpu /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ deviceCount }}</div>
              <div class="stat-label">在线设备</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 心率趋势图 -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>心率趋势</span>
          <el-radio-group v-model="timeRange" size="small" @change="handleTimeRangeChange">
            <el-radio-button label="1h">1小时</el-radio-button>
            <el-radio-button label="24h">24小时</el-radio-button>
            <el-radio-button label="7d">7天</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      
      <v-chart
        class="chart"
        :option="heartRateChartOption"
        autoresize
      />
    </el-card>
    
    <!-- HRV 分析图 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>HRV 分析</span>
          </template>
          <v-chart
            class="chart"
            :option="hrvChartOption"
            autoresize
          />
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>活动状态分布</span>
          </template>
          <v-chart
            class="chart"
            :option="activityChartOption"
            autoresize
          />
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
import { LineChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import { useMonitorStore } from '@/stores/monitor'
import { useUserStore } from '@/stores/user'

// 注册 ECharts 组件
use([
  CanvasRenderer,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const monitorStore = useMonitorStore()
const userStore = useUserStore()

const timeRange = ref('1h')
const latestHeartRate = ref<number | null>(null)
const averageHeartRate = ref<number | null>(null)
const alertCount = ref(0)
const deviceCount = ref(0)

// 心率趋势图配置
const heartRateChartOption = computed(() => ({
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
  series: [
    {
      name: '心率',
      type: 'line',
      smooth: true,
      data: monitorStore.heartRateData.map(d => d.heartRate),
      itemStyle: {
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

// HRV 图表配置
const hrvChartOption = computed(() => ({
  tooltip: {
    trigger: 'axis'
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  },
  yAxis: {
    type: 'value',
    name: 'HRV (ms)'
  },
  series: [
    {
      name: 'HRV',
      type: 'line',
      data: [45, 52, 48, 55, 50, 58, 53],
      smooth: true,
      itemStyle: {
        color: '#67c23a'
      }
    }
  ]
}))

// 活动状态分布图配置
const activityChartOption = computed(() => ({
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '活动状态',
      type: 'pie',
      radius: '60%',
      data: [
        { value: 335, name: '静息' },
        { value: 310, name: '轻度活动' },
        { value: 234, name: '中度活动' },
        { value: 135, name: '剧烈运动' }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}))

// 时间范围切换
const handleTimeRangeChange = async () => {
  // TODO: 根据时间范围查询数据
  console.log('时间范围:', timeRange.value)
}

// 连接 WebSocket
onMounted(() => {
  if (userStore.userInfo?.id) {
    monitorStore.connectWebSocket(userStore.userInfo.id)
  }
})

onUnmounted(() => {
  monitorStore.disconnectWebSocket()
})
</script>

<style scoped lang="scss">
.dashboard {
  .stats-row {
    margin-bottom: 20px;
  }
  
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      gap: 15px;
      
      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        color: #fff;
        
        &.heart-rate {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        &.average {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }
        
        &.alerts {
          background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
        }
        
        &.devices {
          background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
        }
      }
      
      .stat-info {
        flex: 1;
        
        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
          margin-bottom: 5px;
        }
        
        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }
  
  .chart-card {
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .chart {
      height: 350px;
    }
  }
}
</style>
