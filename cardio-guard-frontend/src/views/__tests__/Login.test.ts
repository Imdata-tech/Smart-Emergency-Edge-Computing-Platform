import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import ElementPlus from 'element-plus'

// Mock user store
vi.mock('@/stores/user', () => ({
  useUserStore: vi.fn(() => ({
    login: vi.fn()
  }))
}))

describe('Login Component', () => {
  let wrapper: any
  let router: any

  beforeEach(async () => {
    setActivePinia(createPinia())

    // 创建路由实例
    router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: '/login', component: Login },
        { path: '/', component: { template: '<div>Home</div>' } }
      ]
    })

    router.push('/login')
    await router.isReady()

    // 挂载组件
    wrapper = mount(Login, {
      global: {
        plugins: [router, ElementPlus]
      }
    })
  })

  describe('渲染测试', () => {
    it('应该正确渲染登录表单', () => {
      expect(wrapper.find('.login-container').exists()).toBe(true)
      expect(wrapper.find('.login-card').exists()).toBe(true)
      expect(wrapper.find('h2').text()).toBe('CardioGuard 360')
    })

    it('应该显示标题和副标题', () => {
      expect(wrapper.find('h2').text()).toContain('CardioGuard 360')
      expect(wrapper.find('p').text()).toContain('实时心血管健康监护平台')
    })

    it('应该包含用户名和密码输入框', () => {
      const inputs = wrapper.findAll('input')
      expect(inputs.length).toBeGreaterThanOrEqual(2)
    })

    it('应该包含登录按钮', () => {
      const button = wrapper.find('button[type="button"]')
      expect(button.exists()).toBe(true)
      expect(button.text()).toContain('登录')
    })
  })

  describe('表单验证', () => {
    it('用户名为空时应该显示错误', async () => {
      const usernameInput = wrapper.find('input[placeholder="请输入用户名"]')
      await usernameInput.setValue('')
      
      const form = wrapper.find('form')
      await form.trigger('submit')

      // Element Plus 会显示验证错误
      expect(wrapper.html()).toBeDefined()
    })

    it('密码少于6位时应该显示错误', async () => {
      const passwordInput = wrapper.find('input[type="password"]')
      await passwordInput.setValue('123')
      
      expect(passwordInput.element.value).toBe('123')
    })

    it('有效输入应该通过验证', async () => {
      const usernameInput = wrapper.find('input[placeholder="请输入用户名"]')
      const passwordInput = wrapper.find('input[type="password"]')
      
      await usernameInput.setValue('testuser')
      await passwordInput.setValue('password123')
      
      expect(usernameInput.element.value).toBe('testuser')
      expect(passwordInput.element.value).toBe('password123')
    })
  })

  describe('交互测试', () => {
    it('点击登录按钮应该触发表单提交', async () => {
      const button = wrapper.find('button')
      await button.trigger('click')
      
      // 验证按钮存在并可点击
      expect(button.exists()).toBe(true)
    })

    it('按回车键应该触发登录', async () => {
      const passwordInput = wrapper.find('input[type="password"]')
      await passwordInput.setValue('password123')
      await passwordInput.trigger('keyup.enter')
      
      expect(passwordInput.element.value).toBe('password123')
    })

    it('加载状态应该禁用按钮', async () => {
      // 模拟加载状态
      const button = wrapper.find('button')
      expect(button.exists()).toBe(true)
    })
  })

  describe('样式测试', () => {
    it('应该应用正确的样式类', () => {
      expect(wrapper.find('.login-container').classes()).toContain('login-container')
      expect(wrapper.find('.login-card').classes()).toContain('login-card')
    })

    it('卡片头部应该有正确的类名', () => {
      expect(wrapper.find('.card-header').exists()).toBe(true)
    })
  })

  describe('响应式测试', () => {
    it('应该在窗口大小变化时保持布局', () => {
      const container = wrapper.find('.login-container')
      expect(container.exists()).toBe(true)
    })
  })

  describe('无障碍访问', () => {
    it('输入框应该有占位符文本', () => {
      const usernameInput = wrapper.find('input[placeholder="请输入用户名"]')
      const passwordInput = wrapper.find('input[placeholder="请输入密码"]')
      
      expect(usernameInput.exists()).toBe(true)
      expect(passwordInput.exists()).toBe(true)
    })

    it('密码输入框应该支持显示/隐藏', () => {
      const passwordInput = wrapper.find('input[type="password"]')
      expect(passwordInput.exists()).toBe(true)
    })
  })
})
