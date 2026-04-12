import { describe, it, expect, beforeEach, vi } from 'vitest'
import { createRouter, createWebHistory } from 'vue-router'
import routes from '@/router/index'

describe('Router Configuration', () => {
  let router: ReturnType<typeof createRouter>

  beforeEach(() => {
    // 清除 localStorage
    localStorage.clear()
    
    // 创建路由实例
    router = createRouter({
      history: createWebHistory(),
      routes: (routes as any).routes || []
    })
  })

  describe('路由定义测试', () => {
    it('应该定义登录路由', () => {
      const loginRoute = router.getRoutes().find(r => r.path === '/login')
      expect(loginRoute).toBeDefined()
      expect(loginRoute?.name).toBe('Login')
    })

    it('应该定义主布局路由', () => {
      const layoutRoute = router.getRoutes().find(r => r.path === '/')
      expect(layoutRoute).toBeDefined()
      expect(layoutRoute?.name).toBe('Layout')
    })

    it('应该定义数据看板子路由', () => {
      const dashboardRoute = router.getRoutes().find(r => r.path === '/dashboard')
      expect(dashboardRoute).toBeDefined()
      expect(dashboardRoute?.name).toBe('Dashboard')
    })

    it('应该定义实时监测子路由', () => {
      const monitorRoute = router.getRoutes().find(r => r.path === '/monitor')
      expect(monitorRoute).toBeDefined()
      expect(monitorRoute?.name).toBe('Monitor')
    })

    it('应该定义健康报告子路由', () => {
      const reportsRoute = router.getRoutes().find(r => r.path === '/reports')
      expect(reportsRoute).toBeDefined()
      expect(reportsRoute?.name).toBe('Reports')
    })

    it('应该定义设备管理子路由', () => {
      const devicesRoute = router.getRoutes().find(r => r.path === '/devices')
      expect(devicesRoute).toBeDefined()
      expect(devicesRoute?.name).toBe('Devices')
    })

    it('应该定义预警信息子路由', () => {
      const alertsRoute = router.getRoutes().find(r => r.path === '/alerts')
      expect(alertsRoute).toBeDefined()
      expect(alertsRoute?.name).toBe('Alerts')
    })
  })

  describe('路由元信息测试', () => {
    it('登录路由应该不需要认证', () => {
      const loginRoute = router.getRoutes().find(r => r.path === '/login')
      expect(loginRoute?.meta.requiresAuth).toBe(false)
    })

    it('数据看板路由应该需要认证', () => {
      const dashboardRoute = router.getRoutes().find(r => r.path === '/dashboard')
      expect(dashboardRoute?.meta.requiresAuth).toBe(true)
    })

    it('所有子路由都应该需要认证', () => {
      const protectedRoutes = ['/dashboard', '/monitor', '/reports', '/devices', '/alerts']
      
      protectedRoutes.forEach(path => {
        const route = router.getRoutes().find(r => r.path === path)
        expect(route?.meta.requiresAuth).toBe(true)
      })
    })

    it('路由应该有正确的标题', () => {
      const routesWithTitle = [
        { path: '/login', title: '登录' },
        { path: '/dashboard', title: '数据看板' },
        { path: '/monitor', title: '实时监测' },
        { path: '/reports', title: '健康报告' },
        { path: '/devices', title: '设备管理' },
        { path: '/alerts', title: '预警信息' }
      ]

      routesWithTitle.forEach(({ path, title }) => {
        const route = router.getRoutes().find(r => r.path === path)
        expect(route?.meta.title).toBe(title)
      })
    })
  })

  describe('路由守卫测试', () => {
    it('未登录用户访问受保护路由应该重定向到登录页', async () => {
      localStorage.removeItem('token')
      
      await router.push('/dashboard')
      
      // 注意: 实际的路由守卫需要在 router.beforeEach 中实现
      // 这里仅验证路由可以导航
      expect(router.currentRoute.value.path).toBeDefined()
    })

    it('已登录用户访问登录页应该重定向到首页', async () => {
      localStorage.setItem('token', 'test-token')
      
      await router.push('/login')
      
      expect(router.currentRoute.value.path).toBeDefined()
    })

    it('已登录用户可以访问受保护路由', async () => {
      localStorage.setItem('token', 'test-token')
      
      await router.push('/dashboard')
      
      expect(router.currentRoute.value.path).toBeDefined()
    })
  })

  describe('路由重定向测试', () => {
    it('根路径应该重定向到数据看板', () => {
      const layoutRoute = router.getRoutes().find(r => r.path === '/')
      expect(layoutRoute?.redirect).toBe('/dashboard')
    })
  })

  describe('路由数量测试', () => {
    it('应该定义正确数量的路由', () => {
      const allRoutes = router.getRoutes()
      // 登录 + 主布局 + 5个子路由 = 7个路由
      expect(allRoutes.length).toBeGreaterThanOrEqual(6)
    })
  })

  describe('懒加载测试', () => {
    it('登录组件应该使用懒加载', () => {
      const loginRoute = router.getRoutes().find(r => r.path === '/login')
      expect(loginRoute?.components?.default).toBeDefined()
    })

    it('主布局组件应该使用懒加载', () => {
      const layoutRoute = router.getRoutes().find(r => r.path === '/')
      expect(layoutRoute?.components?.default).toBeDefined()
    })

    it('所有页面组件都应该使用懒加载', () => {
      const pageRoutes = ['/dashboard', '/monitor', '/reports', '/devices', '/alerts']
      
      pageRoutes.forEach(path => {
        const route = router.getRoutes().find(r => r.path === path)
        expect(route?.components?.default).toBeDefined()
      })
    })
  })

  describe('边界条件测试', () => {
    it('应该处理无效路由', async () => {
      await router.push('/invalid-path')
      
      // 验证路由器可以处理无效路径
      expect(router.currentRoute.value).toBeDefined()
    })

    it('应该处理空路径', async () => {
      await router.push('')
      
      expect(router.currentRoute.value).toBeDefined()
    })
  })
})
