<template>
  <div class="alerts">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>预警信息</span>
          <el-radio-group v-model="filterStatus" size="small">
            <el-radio-button label="ALL">全部</el-radio-button>
            <el-radio-button label="UNPROCESSED">未处理</el-radio-button>
            <el-radio-button label="PROCESSED">已处理</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      
      <el-table :data="alertList" style="width: 100%">
        <el-table-column prop="alertTime" label="预警时间" width="180" />
        <el-table-column prop="alertType" label="预警类型" width="150">
          <template #default="{ row }">
            <el-tag :type="getAlertTypeColor(row.alertType)">
              {{ getAlertTypeText(row.alertType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="预警级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelColor(row.level)">
              {{ getLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="heartRate" label="心率值" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold">
              {{ row.heartRate }} bpm
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="预警信息" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PROCESSED' ? 'success' : 'warning'">
              {{ row.status === 'PROCESSED' ? '已处理' : '未处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'UNPROCESSED'"
              size="small"
              type="primary"
              @click="processAlert(row)"
            >
              处理
            </el-button>
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const filterStatus = ref('ALL')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(25)

const alertList = ref([
  {
    id: 1,
    alertTime: '2024-01-15 10:30:25',
    alertType: 'TACHYCARDIA',
    level: 'HIGH',
    heartRate: 125,
    message: '检测到心动过速,心率超过正常范围',
    status: 'UNPROCESSED'
  },
  {
    id: 2,
    alertTime: '2024-01-15 09:15:10',
    alertType: 'BRADYCARDIA',
    level: 'MEDIUM',
    heartRate: 52,
    message: '检测到心动过缓,心率低于正常范围',
    status: 'PROCESSED'
  },
  {
    id: 3,
    alertTime: '2024-01-14 22:45:30',
    alertType: 'TACHYCARDIA',
    level: 'CRITICAL',
    heartRate: 145,
    message: '严重心动过速,建议立即就医',
    status: 'UNPROCESSED'
  }
])

const getAlertTypeText = (type: string) => {
  const map: Record<string, string> = {
    'TACHYCARDIA': '心动过速',
    'BRADYCARDIA': '心动过缓',
    'ARRHYTHMIA': '心律失常'
  }
  return map[type] || type
}

const getAlertTypeColor = (type: string) => {
  if (type === 'TACHYCARDIA') return 'danger'
  if (type === 'BRADYCARDIA') return 'warning'
  return 'info'
}

const getLevelText = (level: string) => {
  const map: Record<string, string> = {
    'LOW': '低',
    'MEDIUM': '中',
    'HIGH': '高',
    'CRITICAL': '危急'
  }
  return map[level] || level
}

const getLevelColor = (level: string) => {
  const map: Record<string, any> = {
    'LOW': 'info',
    'MEDIUM': 'warning',
    'HIGH': 'danger',
    'CRITICAL': 'danger'
  }
  return map[level] || 'info'
}

const processAlert = (alert: any) => {
  ElMessage.success(`预警 ${alert.id} 已处理`)
}

const viewDetail = (alert: any) => {
  ElMessage.info(`查看预警详情: ${alert.id}`)
}
</script>

<style scoped lang="scss">
.alerts {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
