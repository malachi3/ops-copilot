# OpsCopilot - IT运维智能体

[![Version](https://img.shields.io/badge/version-MVP-blue.svg)](https://github.com/your-repo/ops-copilot)
[![Spring Boot](https://img.shields.io/badge/SpringBoot-3.2.x-green.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4+-42b983.svg)](https://vuejs.org)

基于AI的IT运维智能助手，为运维团队提供智能告警处理、资产问答、流程自动化、知识库查询和智能报表等功能。

## 产品版本

| 版本 | 功能 | 状态 |
|------|------|------|
| MVP | 资产问答 + 智能告警解读 | 开发中 |
| V1.0 | 流程自动化 + 知识库问答 | 规划中 |
| V2.0 | 智能报表 + 预测分析 | 规划中 |

## 技术栈

- **后端**: Spring Boot 3.2.x + Java 17
- **前端**: Vue 3.4+ + Vite + Element Plus
- **AI**: 通义千问 / Claude / Gemini (可配置)
- **数据库**: MySQL (可扩展)

## 快速开始

### 后端启动

```bash
cd backend
./mvnw spring-boot:run
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

## 核心功能

### 1. 智能告警处理
- 自然语言告警查询
- 根因分析
- 一键创建工单

### 2. 资产问答
- 自然语言资产搜索
- 多维条件筛选
- 资产关联查询

### 3. 流程自动化
- 智能填单
- 流程进度跟踪
- 催办提醒

### 4. 知识库问答
- RAG智能问答
- 操作指引
- 案例学习

### 5. 智能报表
- 自动生成周报/月报
- 数据解读
- 预测分析

## API文档

启动后访问: `http://localhost:8080/swagger-ui.html`

## 目录结构

```
ops-copilot/
├── backend/                 # Spring Boot后端
│   └── src/main/
│       ├── java/com/opscopilot/
│       │   ├── controller/ # 控制器
│       │   ├── service/    # 业务逻辑
│       │   ├── model/      # 数据模型
│       │   ├── dto/        # 数据传输对象
│       │   └── config/     # 配置类
│       └── resources/
│           └── application.yml
├── frontend/                # Vue前端
│   └── src/
│       ├── views/          # 页面视图
│       ├── components/     # 组件
│       ├── api/            # API调用
│       ├── router/         # 路由配置
│       └── assets/         # 静态资源
└── README.md
```

##  License

MIT License
