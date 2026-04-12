import { describe, it, expect, beforeEach, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useUserStore } from '@/stores/user'
import axios from 'axios'

// Mock axios
vi.mock('axios', () => ({
  default: {
    post: vi.fn(),
    get: vi.fn()
  }
}))

describe('User Store', () => {
  let userStore: ReturnType<typeof useUserStore>

  beforeEach(() => {
    // 创建新的 Pinia 实例
    setActivePinia(createPinia())
    userStore = useUserStore()
    
    // 清除 localStorage
    localStorage.clear()
  })

  describe('初始化状态', () => {
    it('应该正确初始化空状态', () => {
      expect(userStore.token).toBe('')
      expect(userStore.userInfo).toBeNull()
      expect(userStore.isAuthenticated).toBe(false)
      expect(userStore.userRole).toBe('')
    })

    it('应该从 localStorage 恢复 token', () => {
      localStorage.setItem('token', 'test-token-123')
      setActivePinia(createPinia())
      userStore = useUserStore()
      
      expect(userStore.token).toBe('test-token-123')
      expect(userStore.isAuthenticated).toBe(true)
    })
  })

  describe('登录功能', () => {
    it('登录成功应该设置 token 和用户信息', async () => {
      const mockToken = 'jwt-token-xyz'
      const mockUserInfo = {
        id: 1,
        username: 'testuser',
        realName: '测试用户',
        role: 'PATIENT',
        phone: '13800138000'
      }

      // Mock API 响应
      ;(axios.post as any).mockResolvedValueOnce({
        data: { data: mockToken }
      })
      ;(axios.get as any).mockResolvedValueOnce({
        data: { data: mockUserInfo }
      })

      await userStore.login('testuser', 'password123')

      expect(userStore.token).toBe(mockToken)
      expect(userStore.userInfo).toEqual(mockUserInfo)
      expect(userStore.isAuthenticated).toBe(true)
      expect(localStorage.getItem('token')).toBe(mockToken)
      expect(axios.post).toHaveBeenCalledWith('/api/auth/login?username=testuser&password=password123')
    })

    it('登录失败应该抛出异常', async () => {
      const error = new Error('用户名或密码错误')
      ;(axios.post as any).mockRejectedValueOnce(error)

      await expect(userStore.login('wronguser', 'wrongpass'))
        .rejects
        .toThrow('用户名或密码错误')
      
      expect(userStore.token).toBe('')
      expect(userStore.isAuthenticated).toBe(false)
    })

    it('应该验证用户名和密码参数', async () => {
      ;(axios.post as any).mockResolvedValueOnce({
        data: { data: 'token' }
      })
      ;(axios.get as any).mockResolvedValueOnce({
        data: { data: {} }
      })

      await userStore.login('user', 'pass')

      expect(axios.post).toHaveBeenCalled()
    })
  })

  describe('获取用户信息', () => {
    it('应该成功获取用户信息', async () => {
      const mockUserInfo = {
        id: 1,
        username: 'testuser',
        realName: '测试用户',
        role: 'DOCTOR'
      }

      localStorage.setItem('token', 'test-token')
      setActivePinia(createPinia())
      userStore = useUserStore()

      ;(axios.get as any).mockResolvedValueOnce({
        data: { data: mockUserInfo }
      })

      await userStore.fetchUserInfo()

      expect(userStore.userInfo).toEqual(mockUserInfo)
      expect(userStore.userRole).toBe('DOCTOR')
      expect(axios.get).toHaveBeenCalledWith('/api/user/info', {
        headers: { Authorization: 'Bearer test-token' }
      })
    })

    it('获取用户信息失败不应该影响其他状态', async () => {
      localStorage.setItem('token', 'test-token')
      setActivePinia(createPinia())
      userStore = useUserStore()

      ;(axios.get as any).mockRejectedValueOnce(new Error('Network Error'))

      await userStore.fetchUserInfo()

      expect(userStore.userInfo).toBeNull()
      expect(userStore.token).toBe('test-token')
    })
  })

  describe('登出功能', () => {
    it('登出应该清除所有状态', () => {
      localStorage.setItem('token', 'test-token')
      setActivePinia(createPinia())
      userStore = useUserStore()
      
      userStore.userInfo = {
        id: 1,
        username: 'testuser',
        realName: 'Test',
        role: 'PATIENT'
      }

      userStore.logout()

      expect(userStore.token).toBe('')
      expect(userStore.userInfo).toBeNull()
      expect(userStore.isAuthenticated).toBe(false)
      expect(userStore.userRole).toBe('')
      expect(localStorage.getItem('token')).toBeNull()
    })
  })

  describe('计算属性', () => {
    it('isAuthenticated 应该根据 token 返回正确值', () => {
      expect(userStore.isAuthenticated).toBe(false)
      
      userStore.token = 'some-token'
      expect(userStore.isAuthenticated).toBe(true)
      
      userStore.token = ''
      expect(userStore.isAuthenticated).toBe(false)
    })

    it('userRole 应该返回用户角色', () => {
      expect(userStore.userRole).toBe('')
      
      userStore.userInfo = {
        id: 1,
        username: 'test',
        realName: 'Test',
        role: 'ADMIN'
      }
      expect(userStore.userRole).toBe('ADMIN')
    })

    it('userRole 在 userInfo 为空时应该返回空字符串', () => {
      userStore.userInfo = null
      expect(userStore.userRole).toBe('')
    })
  })

  describe('不同角色测试', () => {
    it('应该正确处理 PATIENT 角色', async () => {
      ;(axios.post as any).mockResolvedValueOnce({ data: { data: 'token' } })
      ;(axios.get as any).mockResolvedValueOnce({
        data: {
          data: {
            id: 1,
            username: 'patient',
            realName: '患者',
            role: 'PATIENT'
          }
        }
      })

      await userStore.login('patient', 'pass')
      expect(userStore.userRole).toBe('PATIENT')
    })

    it('应该正确处理 DOCTOR 角色', async () => {
      ;(axios.post as any).mockResolvedValueOnce({ data: { data: 'token' } })
      ;(axios.get as any).mockResolvedValueOnce({
        data: {
          data: {
            id: 2,
            username: 'doctor',
            realName: '医生',
            role: 'DOCTOR'
          }
        }
      })

      await userStore.login('doctor', 'pass')
      expect(userStore.userRole).toBe('DOCTOR')
    })

    it('应该正确处理 ADMIN 角色', async () => {
      ;(axios.post as any).mockResolvedValueOnce({ data: { data: 'token' } })
      ;(axios.get as any).mockResolvedValueOnce({
        data: {
          data: {
            id: 3,
            username: 'admin',
            realName: '管理员',
            role: 'ADMIN'
          }
        }
      })

      await userStore.login('admin', 'pass')
      expect(userStore.userRole).toBe('ADMIN')
    })
  })
})
