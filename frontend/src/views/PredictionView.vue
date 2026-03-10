<template>
  <div>
    <!-- 统计概览 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">📈</div>
        <div class="stat-info">
          <div class="stat-value">上升 ↑</div>
          <div class="stat-label">告警趋势</div>
        </div>
      </div>
      <div class="stat-card success">
        <div class="stat-icon">📊</div>
        <div class="stat-info">
          <div class="stat-value">充足</div>
          <div class="stat-label">容量状态</div>
        </div>
      </div>
      <div class="stat-card warning">
        <div class="stat-icon">⚠️</div>
        <div class="stat-info">
          <div class="stat-value">中等</div>
          <div class="stat-label">故障风险</div>
        </div>
      </div>
      <div class="stat-card info">
        <div class="stat-icon">🤖</div>
        <div class="stat-info">
          <div class="stat-value">85%</div>
          <div class="stat-label">预测准确率</div>
        </div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">📈 告警趋势预测</span>
        <el-radio-group v-model="period" size="small" @change="loadAlertPrediction">
          <el-radio-button value="week">周</el-radio-button>
          <el-radio-button value="month">月</el-radio-button>
        </el-radio-group>
      </div>
      <div ref="alertChartRef" class="chart-container"></div>
      <div class="prediction-conclusion">
        <h4>📋 预测结论</h4>
        <ul>
          <li v-for="(item, index) in alertPrediction.conclusions" :key="index">{{ item }}</li>
        </ul>
      </div>
    </div>
    
    <!-- 容量预测 -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">💻 容量需求预测</span>
      </div>
      <div class="capacity-grid">
        <div v-for="(cap, key) in capacityPrediction" :key="key" class="capacity-item">
          <div class="capacity-header">
            <span class="capacity-label">{{ getCapacityLabel(key) }}</span>
            <el-tag :type="getCapacityType(cap.current, cap.threshold)" size="small">
              {{ cap.current }} / {{ cap.threshold }}
            </el-tag>
          </div>
          <el-progress 
            :percentage="parseInt(cap.current)" 
            :color="getCapacityColor(cap.current, cap.threshold)"
            :stroke-width="8"
          />
          <div class="capacity-predicted">
            预测值: {{ cap.predicted }}
          </div>
          <div class="capacity-recommendation">
            {{ cap.recommendation }}
          </div>
        </div>
      </div>
    </div>
    
    <!-- 故障预测 -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">⚠️ 故障风险预测</span>
      </div>
      <el-table :data="failurePrediction.highRisk" stripe>
        <el-table-column prop="service" label="服务" />
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.riskLevel === '高' ? 'danger' : 'warning'" size="small">
              {{ row.riskLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="indicator" label="异常指标" />
        <el-table-column prop="probability" label="发生概率" width="100" />
        <el-table-column prop="suggestion" label="建议" />
      </el-table>
    </div>
    
    <!-- 建议行动 -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">💡 建议行动</span>
      </div>
      <div class="action-list">
        <div v-for="(action, index) in overview.recommendedActions" :key="index" class="action-item">
          {{ action }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getPredictionOverview, getAlertPrediction, getCapacityPrediction, getFailurePrediction } from '@/api'

const period = ref('week')
const alertChartRef = ref(null)

const alertPrediction = ref({
  dates: [],
  historical: [],
  predicted: [],
  conclusions: []
})

const capacityPrediction = ref({})
const failurePrediction = ref({ highRisk: [] })
const overview = ref({ recommendedActions: [] })

const loadData = async () => {
  await Promise.all([
    loadAlertPrediction(),
    loadCapacityPrediction(),
    loadFailurePrediction(),
    loadOverview()
  ])
}

const loadAlertPrediction = async () => {
  try {
    const res = await getPredictionOverview()
    if (res.code === 200) {
      overview.value = res.data
    }
  } catch (e) {
    // 模拟数据
    overview.value = {
      recommendedActions: [
        '1. 提前做好订单服务扩容准备',
        '2. 本周安排一次系统巡检',
        '3. 检查监控告警阈值设置'
      ]
    }
  }
  
  // 模拟图表数据
  const dates = ['周一', '周二', '周三', '周四', '周五', '周六', '周日', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const historical = [120, 132, 101, 134, 90, 230, 210, 0, 0, 0, 0, 0, 0, 0]
  const predicted = [0, 0, 0, 0, 0, 0, 0, 145, 168, 152, 178, 165, 190, 175]
  
  alertPrediction.value = {
    dates,
    historical,
    predicted,
    conclusions: [
      '未来7天告警数量预计呈上升趋势',
      '周三、周五可能出现告警峰值',
      '建议提前做好容量准备'
    ]
  }
  
  renderAlertChart()
}

const loadCapacityPrediction = async () => {
  try {
    const res = await getCapacityPrediction()
    if (res.code === 200) {
      capacityPrediction.value = res.data
    }
  } catch (e) {
    capacityPrediction.value = {
      cpu: { current: '65%', predicted: '82%', threshold: '90%', recommendation: '建议2周内扩容' },
      memory: { current: '72%', predicted: '88%', threshold: '90%', recommendation: '建议1个月内扩容' },
      disk: { current: '45%', predicted: '65%', threshold: '80%', recommendation: '暂无扩容计划' }
    }
  }
}

const loadFailurePrediction = async () => {
  try {
    const res = await getFailurePrediction()
    if (res.code === 200) {
      failurePrediction.value = res.data
    }
  } catch (e) {
    failurePrediction.value = {
      highRisk: [
        { service: '订单服务-API', riskLevel: '高', indicator: '错误率上升', probability: '75%', suggestion: '建议检查最近部署是否有问题' },
        { service: '数据库-主库', riskLevel: '中', indicator: '连接数接近上限', probability: '45%', suggestion: '建议扩容连接池' }
      ]
    }
  }
}

const loadOverview = async () => {
  // 已在 loadAlertPrediction 中处理
}

const renderAlertChart = () => {
  if (!alertChartRef.value) return
  
  const chart = echarts.init(alertChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['历史数据', '预测数据'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { 
      type: 'category', 
      data: alertPrediction.value.dates,
      boundaryGap: false
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '历史数据',
        type: 'line',
        data: alertPrediction.value.historical,
        itemStyle: { color: '#409EFF' },
        areaStyle: { color: 'rgba(64,158,255,0.2)' },
        smooth: true
      },
      {
        name: '预测数据',
        type: 'line',
        data: alertPrediction.value.predicted,
        itemStyle: { color: '#E6A23C' },
        lineStyle: { type: 'dashed' },
        areaStyle: { color: 'rgba(230,162,60,0.2)' },
        smooth: true
      }
    ]
  })
}

const getCapacityLabel = (key) => {
  const labels = { cpu: 'CPU使用率', memory: '内存使用率', disk: '磁盘使用率' }
  return labels[key] || key
}

const getCapacityType = (current, threshold) => {
  const currentVal = parseInt(current)
  const thresholdVal = parseInt(threshold)
  if (currentVal >= thresholdVal) return 'danger'
  if (currentVal >= thresholdVal * 0.8) return 'warning'
  return 'success'
}

const getCapacityColor = (current, threshold) => {
  const currentVal = parseInt(current)
  const thresholdVal = parseInt(threshold)
  if (currentVal >= thresholdVal) return '#F56C6C'
  if (currentVal >= thresholdVal * 0.8) return '#E6A23C'
  return '#67C23A'
}

onMounted(() => {
  loadData()
  setTimeout(renderAlertChart, 100)
})
</script>

<style scoped>
.prediction-conclusion {
  margin-top: 16px;
  padding: 12px;
  background: #F5F7FA;
  border-radius: 8px;
}

.prediction-conclusion h4 {
  margin-bottom: 8px;
  font-size: 14px;
}

.prediction-conclusion ul {
  margin: 0;
  padding-left: 20px;
}

.prediction-conclusion li {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}

.capacity-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.capacity-item {
  padding: 16px;
  background: #F5F7FA;
  border-radius: 8px;
}

.capacity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.capacity-label {
  font-weight: 600;
}

.capacity-predicted {
  font-size: 12px;
  color: #E6A23C;
  margin-top: 8px;
}

.capacity-recommendation {
  font-size: 12px;
  color: #67C23A;
  margin-top: 4px;
}

.action-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-item {
  padding: 12px;
  background: #F5F7FA;
  border-radius: 6px;
  font-size: 14px;
}
</style>
