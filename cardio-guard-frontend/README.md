# CardioGuard 360 Frontend

CardioGuard 360 实时心血管健康监护平台 - Vue 3 前端应用

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **TypeScript** - 类型安全的 JavaScript 超集
- **Vite** - 下一代前端构建工具
- **Element Plus** - Vue 3 组件库
- **Pinia** - Vue 状态管理
- **Vue Router** - Vue 官方路由
- **ECharts** - 数据可视化图表库
- **Axios** - HTTP 客户端

## 功能特性

✅ **用户认证** - JWT Token 登录  
✅ **数据看板** - ECharts 可视化心率趋势、HRV分析  
✅ **实时监测** - WebSocket 实时心率数据推送和波形图  
✅ **健康报告** - 日报/周报/月报查看和下载  
✅ **设备管理** - 可穿戴设备绑定和解绑  
✅ **预警信息** - 多级预警展示和处理  

## 快速开始

### 前置要求

- Node.js 18+
- npm 9+ 或 yarn 1.22+

### 安装依赖

```bash
npm install
# 或
yarn install
```

### 开发模式

```bash
npm run dev
# 或
yarn dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
npm run build
# 或
yarn build
```

### 预览生产构建

```bash
npm run preview
# 或
yarn preview
```

## 项目结构

```
cardio-guard-frontend/
├── src/
│   ├── components/       # 公共组件
│   ├── layouts/          # 布局组件
│   ├── views/            # 页面组件
│   ├── stores/           # Pinia 状态管理
│   ├── router/           # 路由配置
│   ├── api/              # API 接口
│   ├── utils/            # 工具函数
│   ├── types/            # TypeScript 类型定义
│   ├── App.vue           # 根组件
│   └── main.ts           # 入口文件
├── public/               # 静态资源
├── index.html            # HTML 模板
├── vite.config.ts        # Vite 配置
├── tsconfig.json         # TypeScript 配置
└── package.json          # 项目依赖
```

## 核心功能说明

### 1. 用户认证

使用 JWT Token 进行身份验证,Token 存储在 localStorage 中。

### 2. 实时监测

通过 WebSocket 连接后端,实时接收心率数据并更新图表。

### 3. 数据可视化

使用 ECharts 展示:
- 心率趋势折线图
- HRV 分析图表
- 活动状态分布饼图
- 实时波形图

### 4. 状态管理

使用 Pinia 管理:
- 用户信息和认证状态
- 监测数据和 WebSocket 连接

## API 代理配置

开发环境下,Vite 配置了 API 代理:

- `/api` → `http://localhost:8080/api`
- `/ws` → `ws://localhost:8080/ws`

## 环境变量

创建 `.env` 文件:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_WS_URL=ws://localhost:8080
```

## 代码规范

项目使用 ESLint + TypeScript 进行代码质量检查:

```bash
npm run lint
# 或
yarn lint
```

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## 部署

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name cardioguard.example.com;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /ws {
        proxy_pass http://backend:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

### Docker 部署

```dockerfile
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 开发计划

- [ ] 添加单元测试 (Vitest)
- [ ] 添加 E2E 测试 (Cypress)
- [ ] 国际化支持 (i18n)
- [ ] 主题定制
- [ ] PWA 支持
- [ ] 性能优化

## 许可证

MIT

---

Made with ❤️ by CardioGuard Team
