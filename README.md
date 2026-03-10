# OpsCopilot - IT运维智能体

[![Version](https://img.shields.io/badge/version-2.0-blue.svg)](https://github.com/your-repo/ops-copilot)
[![Spring Boot](https://img.shields.io/badge/SpringBoot-3.2.x-green.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4+-42b983.svg)](https://vuejs.org)

基于AI的IT运维智能助手，为运维团队提供智能告警处理、资产问答、流程自动化、知识库查询、智能统计和大屏展示等功能。

## 产品版本

| 版本 | 功能 | 状态 |
|------|------|------|
| MVP | 基础对话 + MCP工具 + 数据图表 | ✅ 完成 |
| V1.0 | 大屏展示 + 预测分析 | 🔄 开发中 |
| V2.0 | Agent自治能力 | 📋 规划中 |

## 核心功能

### 1. 智能对话
- 自然语言理解用户意图
- 多轮对话上下文
- 智能推荐

### 2. MCP工具系统
通过Model Context Protocol连接AI与运维系统：

| 工具 | 功能 |
|------|------|
| alert_query | 查询告警列表 |
| alert_stats | 告警统计分析 |
| asset_search | 搜索资产信息 |
| asset_stats | 资产统计 |
| ticket_query | 查询工单 |
| ticket_stats | 工单统计 |
| knowledge_search | 知识库搜索 |

### 3. 数据可视化
- 对话内图表展示（折线图、饼图、柱状图）
- 实时数据卡片
- 趋势分析

### 4. 运维大屏
- 全局运维态势总览
- 实时告警监控
- 资产分布可视化
- 工单处理效率
- 业务健康度

## 技术栈

- **后端**: Spring Boot 3.2.x + Java 17
- **前端**: Vue 3.4+ + Vite + Element Plus + ECharts
- **AI**: Ollama (本地) / OpenAI / Claude
- **数据库**: MySQL

## 快速开始

### 后端启动

```bash
cd backend
mvn spring-boot:run
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

## 项目结构

```
ops-copilot/
├── backend/                 # Spring Boot后端
│   └── src/main/
│       ├── java/com/opscopilot/
│       │   ├── controller/ # 控制器
│       │   ├── service/    # 业务逻辑
│       │   ├── mcp/        # MCP工具系统
│       │   ├── model/     # 数据模型
│       │   └── dto/        # 数据传输对象
│       └── resources/
│           └── application.yml
├── frontend/               # Vue前端
│   └── src/
│       ├── views/         # 页面视图 (含大屏)
│       ├── components/    # 组件
│       ├── api/           # API调用
│       └── router/       # 路由配置
└── docs/                 # 产品文档
```

## API文档

启动后访问: `http://localhost:8080/swagger-ui.html`

## 目录结构

---

## License

MIT License
