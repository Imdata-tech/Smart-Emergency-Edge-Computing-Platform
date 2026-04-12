<template>
  <div class="devices">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>设备管理</span>
          <el-button type="primary" @click="showBindDialog">
            <el-icon><Plus /></el-icon>
            绑定设备
          </el-button>
        </div>
      </template>
      
      <el-table :data="deviceList" style="width: 100%">
        <el-table-column prop="serialNumber" label="序列号" width="180" />
        <el-table-column prop="deviceName" label="设备名称" width="150" />
        <el-table-column prop="model" label="型号" width="120" />
        <el-table-column prop="deviceType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ getDeviceTypeText(row.deviceType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '在线' : '离线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="batteryLevel" label="电量" width="100">
          <template #default="{ row }">
            <el-progress
              :percentage="row.batteryLevel"
              :color="getBatteryColor(row.batteryLevel)"
              :stroke-width="8"
            />
          </template>
        </el-table-column>
        <el-table-column prop="lastCommunicationTime" label="最后通信时间" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="unbindDevice(row)">解绑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 绑定设备对话框 -->
    <el-dialog v-model="bindDialogVisible" title="绑定设备" width="500px">
      <el-form :model="bindForm" label-width="100px">
        <el-form-item label="设备序列号" required>
          <el-input v-model="bindForm.serialNumber" placeholder="请输入设备序列号" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="bindDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBind">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const deviceList = ref([
  {
    id: 1,
    serialNumber: 'SN123456789',
    deviceName: '心率监测仪',
    model: 'HR-100',
    deviceType: 'HEART_RATE',
    status: 1,
    batteryLevel: 85,
    lastCommunicationTime: '2024-01-15 10:30:00'
  },
  {
    id: 2,
    serialNumber: 'SN987654321',
    deviceName: 'ECG监测仪',
    model: 'ECG-200',
    deviceType: 'ECG',
    status: 0,
    batteryLevel: 45,
    lastCommunicationTime: '2024-01-14 18:20:00'
  }
])

const bindDialogVisible = ref(false)
const bindForm = ref({
  serialNumber: ''
})

const getDeviceTypeText = (type: string) => {
  const map: Record<string, string> = {
    'HEART_RATE': '心率',
    'ECG': '心电图',
    'BLOOD_PRESSURE': '血压',
    'SPO2': '血氧'
  }
  return map[type] || type
}

const getBatteryColor = (level: number) => {
  if (level > 60) return '#67c23a'
  if (level > 30) return '#e6a23c'
  return '#f56c6c'
}

const showBindDialog = () => {
  bindForm.value.serialNumber = ''
  bindDialogVisible.value = true
}

const handleBind = async () => {
  if (!bindForm.value.serialNumber) {
    ElMessage.warning('请输入设备序列号')
    return
  }
  
  // TODO: 调用API绑定设备
  ElMessage.success('设备绑定成功')
  bindDialogVisible.value = false
}

const unbindDevice = async (device: any) => {
  try {
    await ElMessageBox.confirm(`确定要解绑设备 "${device.deviceName}" 吗?`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // TODO: 调用API解绑设备
    ElMessage.success('设备解绑成功')
  } catch {
    // 取消操作
  }
}
</script>

<style scoped lang="scss">
.devices {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
