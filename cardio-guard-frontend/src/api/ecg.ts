import request from '@/utils/request'

/**
 * ECG心电图分析API
 */

// 分析ECG数据
export function analyzeEcg(userId: number, deviceId: number, data: {
  waveform: string
  sampleRate: number
}) {
  return request.post(`/api/ecg/analyze?userId=${userId}&deviceId=${deviceId}`, data)
}

// 批量分析ECG数据
export function batchAnalyzeEcg(ecgDataList: Array<{
  userId: number
  deviceId: number
  waveform: string
  sampleRate: number
}>) {
  return request.post('/api/ecg/batch-analyze', ecgDataList)
}

// 获取用户ECG分析历史
export function getEcgHistory(userId: number, page: number = 1, size: number = 10) {
  return request.get('/api/ecg/history', {
    params: { userId, page, size }
  })
}

// 获取分析结果详情
export function getEcgResult(resultId: number) {
  return request.get(`/api/ecg/result/${resultId}`)
}

// 医生审核分析结果
export function reviewEcgResult(
  resultId: number,
  doctorId: number,
  reviewStatus: string,
  comment?: string
) {
  return request.put(`/api/ecg/review/${resultId}`, null, {
    params: { doctorId, reviewStatus, comment }
  })
}

// 生成ECG诊断报告
export function getEcgReport(resultId: number) {
  return request.get(`/api/ecg/report/${resultId}`)
}

// 统计用户ECG异常情况
export function getEcgAbnormalStats(
  userId: number,
  startDate?: string,
  endDate?: string
) {
  return request.get('/api/ecg/statistics/abnormal', {
    params: { userId, startDate, endDate }
  })
}
