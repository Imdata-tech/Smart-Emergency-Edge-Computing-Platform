import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export interface UserInfo {
  id: number
  username: string
  realName: string
  role: string
  phone?: string
  email?: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  const isAuthenticated = computed(() => !!token.value)
  const userRole = computed(() => userInfo.value?.role || '')

  // 登录
  async function login(username: string, password: string) {
    try {
      const response = await axios.post(`/api/auth/login?username=${username}&password=${password}`)
      token.value = response.data.data
      localStorage.setItem('token', token.value)
      
      // 获取用户信息
      await fetchUserInfo()
      
      return true
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    }
  }

  // 获取用户信息
  async function fetchUserInfo() {
    try {
      const response = await axios.get('/api/user/info', {
        headers: { Authorization: `Bearer ${token.value}` }
      })
      userInfo.value = response.data.data
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  // 登出
  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return {
    token,
    userInfo,
    isAuthenticated,
    userRole,
    login,
    logout,
    fetchUserInfo
  }
})
