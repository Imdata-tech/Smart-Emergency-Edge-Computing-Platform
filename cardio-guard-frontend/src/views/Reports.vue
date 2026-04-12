<template>
  <div class="reports">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>健康报告</span>
          <el-button type="primary" @click="generateReport">
            <el-icon><Plus /></el-icon>
            生成报告
          </el-button>
        </div>
      </template>
      
      <el-table :data="reportList" style="width: 100%">
        <el-table-column prop="reportDate" label="报告日期" width="120" />
        <el-table-column prop="reportType" label="报告类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ getReportTypeText(row.reportType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="avgHeartRate" label="平均心率" width="100" />
        <el-table-column prop="minHeartRate" label="最低心率" width="100" />
        <el-table-column prop="maxHeartRate" label="最高心率" width="100" />
        <el-table-column prop="alertCount" label="预警次数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'APPROVED' ? 'success' : 'warning'">
              {{ row.status === 'APPROVED' ? '已审核' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewReport(row)">查看</el-button>
            <el-button size="small" type="primary" @click="downloadReport(row)">
              下载
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const reportList = ref([
  {
    id: 1,
    reportDate: '2024-01-15',
    reportType: 'DAILY',
    avgHeartRate: 72,
    minHeartRate: 58,
    maxHeartRate: 95,
    alertCount: 2,
    status: 'APPROVED'
  },
  {
    id: 2,
    reportDate: '2024-01-14',
    reportType: 'DAILY',
    avgHeartRate: 75,
    minHeartRate: 60,
    maxHeartRate: 102,
    alertCount: 3,
    status: 'PENDING'
  }
])

const getReportTypeText = (type: string) => {
  const map: Record<string, string> = {
    'DAILY': '日报',
    'WEEKLY': '周报',
    'MONTHLY': '月报'
  }
  return map[type] || type
}

const generateReport = () => {
  ElMessage.info('生成功能开发中...')
}

const viewReport = (report: any) => {
  ElMessage.info(`查看报告: ${report.id}`)
}

const downloadReport = (report: any) => {
  ElMessage.success(`下载报告: ${report.id}`)
}
</script>

<style scoped lang="scss">
.reports {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
