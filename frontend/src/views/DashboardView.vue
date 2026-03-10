<template>
  <div class="dashboard-container">
    <!-- 顶部标题 -->
    <header class="dashboard-header">
      <h1>🖥️ 运维智能体 - 数据大屏</h1>
      <div class="header-right">
        <span class="time">{{ currentTime }}</span>
        <span class="date">{{ currentDate }}</span>
      </div>
    </header>

    <!-- 主要内容 -->
    <div class="dashboard-content">
      <!-- 第一行：关键指标卡片 -->
      <div class="stat-row">
        <div class="stat-card large danger">
          <div class="stat-icon">🚨</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.criticalAlerts }}</div>
            <div class="stat-label">严重告警</div>
          </div>
        </div>
        <div class="stat-card large warning">
          <div class="stat-icon">⚠️</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingTickets }}</div>
            <div class="stat-label">待处理工单</div>
          </div>
        </div>
        <div class="stat-card large success">
          <div class="stat-icon">✅</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.runningAssets }}</div>
            <div class="stat-label">运行资产</div>
          </div>
        </div>
        <div class="stat-card large info">
          <div class="stat-icon">📊</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.uptime }}%</div>
            <div class="stat-label">系统可用率</div>
          </div>
        </div>
      </div>

      <!-- 第二行：图表区域 -->
      <div class="chart-row">
        <!-- 告警趋势图 -->
        <div class="chart-card">
          <h3>📈 告警趋势 (近7天)</h3>
          <div ref="alertChartRef" class="chart-container"></div>
        </div>
        
        <!-- 资产分布饼图 -->
        <div class="chart-card">
          <h3>🍩 资产类型分布</h3>
          <div ref="assetPieChartRef" class="chart-container"></div>
        </div>
        
        <!-- 工单状态图 -->
        <div class="chart-card">
          <h3>📋 工单处理状态</h3>
          <div ref="ticketChartRef" class="chart-container"></div>
        </div>
      </div>

      <!-- 第三行：实时数据 -->
      <div class="data-row">
        <!-- 实时告警列表 -->
        <div class="data-card">
          <h3>🚨 实时告警</h3>
          <div class="alert-list">
            <div v-for="alert in recentAlerts" :key="alert.id" class="alert-item" :class="alert.level">
              <span class="alert-time">{{ alert.time }}</span>
              <span class="alert-title">{{ alert.title }}</span>
              <span class="alert-ip">{{ alert.ip }}</span>
            </div>
          </div>
        </div>
        
        <!-- 资产状态 -->
        <div class="data-card">
          <h3>💻 资产状态</h3>
          <div ref="assetStatusChartRef" class="chart-container-small"></div>
        </div>
        
        <!-- 业务健康度 -->
        <div class="data-card">
          <h3>🏥 业务健康度</h3>
          <div class="business-list">
            <div v-for="biz in businesses" :key="biz.name" class="business-item">
              <span class="biz-name">{{ biz.name }}</span>
              <el-progress :percentage="biz.health" :color="getHealthColor(biz.health)" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const currentTime = ref('')
const currentDate = ref('')
const alertChartRef = ref(null)
const assetPieChartRef = ref(null)
const ticketChartRef = ref(null)
const assetStatusChartRef = ref(null)

const stats = ref({
  criticalAlerts: 12,
  pendingTickets: 8,
  runningAssets: 130,
  uptime: 99.95
})

const recentAlerts = ref([
  { id: 1, level: 'critical', time: '10:30', title: 'CPU使用率过高', ip: '10.0.1.10' },
  { id: 2, level: 'warning', time: '10:25', title: '内存使用率超80%', ip: '10.0.2.10' },
  { id: 3, level: 'critical', time: '10:20', title: '数据库连接满', ip: '10.0.3.10' },
  { id: 4, level: 'info', time: '10:15', title: '磁盘容量预警', ip: '10.0.4.10' },
  { id: 5, level: 'warning', time: '10:10', title: '响应超时', ip: '10.0.5.10' }
])

const businesses = ref([
  { name: '订单服务', health: 98 },
  { name: '支付服务', health: 95 },
  { name: '用户服务', health: 92 },
  { name: '物流服务', health: 88 },
  { name: '商品服务', health: 99 }
])

let timer = null

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
  currentDate.value = now.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
}

const getHealthColor = (health) => {
  if (health >= 95) return '#67C23A'
  if (health >= 80) return '#E6A23C'
  return '#F56C6C'
}

const initCharts = () => {
  // 告警趋势图
  const alertChart = echarts.init(alertChartRef.value)
  alertChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
    yAxis: { type: 'value' },
    series: [
      { name: '严重', type: 'line', data: [12, 15, 8, 10, 18, 5, 12], itemStyle: { color: '#F56C6C' }, areaStyle: { color: 'rgba(245,108,108,0.2)' } },
      { name: '警告', type: 'line', data: [56, 48, 62, 55, 70, 40, 56], itemStyle: { color: '#E6A23C' } },
      { name: '信息', type: 'line', data: [120, 110, 130, 125, 140, 100, 120], itemStyle: { color: '#409EFF' } }
    ]
  })

  // 资产分布饼图
  const assetPieChart = echarts.init(assetPieChartRef.value)
  assetPieChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 50, name: '服务器', itemStyle: { color: '#409EFF' } },
        { value: 30, name: '数据库', itemStyle: { color: '#67C23A' } },
        { value: 25, name: '中间件', itemStyle: { color: '#E6A23C' } },
        { value: 20, name: '网络设备', itemStyle: { color: '#909399' } },
        { value: 25, name: '存储', itemStyle: { color: '#F56C6C' } }
      ]
    }]
  })

  // 工单状态图
  const ticketChart = echarts.init(ticketChartRef.value)
  ticketChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: ['已关闭', '处理中', '待处理', '待审批'] },
    series: [{
      type: 'bar',
      data: [
        { value: 72, itemStyle: { color: '#67C23A' } },
        { value: 15, itemStyle: { color: '#409EFF' } },
        { value: 8, itemStyle: { color: '#E6A23C' } },
        { value: 5, itemStyle: { color: '#F56C6C' } }
      ],
      label: { show: true, position: 'right' }
    }]
  })

  // 资产状态
  const assetStatusChart = echarts.init(assetStatusChartRef.value)
  assetStatusChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['0%', '80%'],
      data: [
        { value: 130, name: '运行中', itemStyle: { color: '#67C23A' } },
        { value: 10, name: '已停止', itemStyle: { color: '#F56C6C' } },
        { value: 5, name: '维护中', itemStyle: { color: '#E6A23C' } },
        { value: 5, name: '未知', itemStyle: { color: '#909399' } }
      ],
      label: { show: true, formatter: '{b}: {c}' }
    }]
  })
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  setTimeout(initCharts, 100)
  
  window.addEventListener('resize', () => {
    echarts.getInstanceByDom(alertChartRef.value)?.resize()
    echarts.getInstanceByDom(assetPieChartRef.value)?.resize()
    echarts.getInstanceByDom(ticketChartRef.value)?.resize()
    echarts.getInstanceByDom(assetStatusChartRef.value)?.resize()
  })
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.dashboard-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a1929 0%, #1a2332 100%);
  color: #fff;
  padding: 0;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 40px;
  background: rgba(0, 0, 0, 0.3);
  border-bottom: 1px solid rgba(64, 158, 255, 0.3);
}

.dashboard-header h1 {
  font-size: 28px;
  font-weight: 600;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.header-right {
  text-align: right;
}

.header-right .time {
  font-size: 36px;
  font-weight: 600;
  color: #409EFF;
  display: block;
}

.header-right .date {
  font-size: 14px;
  color: #909399;
}

.dashboard-content {
  padding: 20px;
}

/* 统计卡片 */
.stat-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.stat-card.large {
  padding: 30px;
}

.stat-card.large .stat-value {
  font-size: 48px;
}

.stat-icon {
  font-size: 48px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: #fff;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.stat-card.danger { border-left: 4px solid #F56C6C; }
.stat-card.warning { border-left: 4px solid #E6A23C; }
.stat-card.success { border-left: 4px solid #67C23A; }
.stat-card.info { border-left: 4px solid #409EFF; }

/* 图表行 */
.chart-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.chart-card h3 {
  font-size: 16px;
  color: #fff;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.chart-container {
  height: 280px;
}

.chart-container-small {
  height: 200px;
}

/* 数据行 */
.data-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 20px;
}

.data-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.data-card h3 {
  font-size: 16px;
  color: #fff;
  margin-bottom: 15px;
}

/* 告警列表 */
.alert-list {
  max-height: 250px;
  overflow-y: auto;
}

.alert-item {
  display: flex;
  align-items: center;
  padding: 10px;
  margin-bottom: 8px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 6px;
  font-size: 13px;
}

.alert-item.critical { border-left: 3px solid #F56C6C; }
.alert-item.warning { border-left: 3px solid #E6A23C; }
.alert-item.info { border-left: 3px solid #409EFF; }

.alert-time {
  color: #909399;
  margin-right: 12px;
  min-width: 50px;
}

.alert-title {
  flex: 1;
  color: #fff;
}

.alert-ip {
  color: #909399;
  font-size: 12px;
}

/* 业务列表 */
.business-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.business-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.biz-name {
  min-width: 80px;
  font-size: 14px;
}

.business-item .el-progress {
  flex: 1;
}
</style>
