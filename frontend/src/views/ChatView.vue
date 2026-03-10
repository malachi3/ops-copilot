<template>
  <div class="chat-container">
    <!-- 消息列表 -->
    <div class="chat-messages" ref="messagesRef">
      <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.role]">
        <div class="message-avatar">
          {{ msg.role === 'user' ? '你' : '🤖' }}
        </div>
        <div class="message-content">
          <!-- 文本内容 -->
          <div v-if="msg.content" v-html="formatContent(msg.content)"></div>
          
          <!-- 图表内容 -->
          <div v-if="msg.chart" class="message-chart">
            <div ref="chartRef" class="chart-container"></div>
          </div>
          
          <!-- 数据表格 -->
          <div v-if="msg.table" class="message-table">
            <el-table :data="msg.table.data" stripe size="small">
              <el-table-column v-for="col in msg.table.columns" :key="col.prop" :prop="col.prop" :label="col.label" />
            </el-table>
          </div>
          
          <!-- 数据卡片 -->
          <div v-if="msg.cards" class="message-cards">
            <div v-for="card in msg.cards" :key="card.label" class="data-card-item">
              <div class="card-value">{{ card.value }}</div>
              <div class="card-label">{{ card.label }}</div>
            </div>
          </div>
          
          <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
        </div>
      </div>
      
      <!-- 加载中 -->
      <div v-if="loading" class="message bot">
        <div class="message-avatar">🤖</div>
        <div class="message-content">
          <div style="display: flex; gap: 4px;">
            <span class="loading-dot">.</span>
            <span class="loading-dot">.</span>
            <span class="loading-dot">.</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 快捷输入 -->
    <div v-if="messages.length <= 1" class="quick-commands">
      <div class="quick-title">试试这样问我：</div>
      <div class="quick-list">
        <el-button size="small" @click="sendQuick('最近有哪些严重告警？')">🔍 查询严重告警</el-button>
        <el-button size="small" @click="sendQuick('本周告警数量统计')">📊 告警统计</el-button>
        <el-button size="small" @click="sendQuick('给我们部门所有MySQL服务器')">💻 查询MySQL服务器</el-button>
        <el-button size="small" @click="sendQuick('Redis连接数满了怎么解决')">📚 Redis问题解答</el-button>
        <el-button size="small" @click="sendQuick('本周运维数据统计')">📈 运维周报</el-button>
      </div>
    </div>
    
    <!-- 输入区域 -->
    <div class="chat-input-container">
      <el-input
        v-model="inputMessage"
        type="textarea"
        :rows="1"
        placeholder="输入你的问题..."
        @keydown.enter="sendMessage"
        :disabled="loading"
        resize="none"
        class="chat-input"
      />
      <el-button 
        type="primary" 
        @click="sendMessage" 
        :loading="loading"
        :disabled="!inputMessage.trim()"
        style="border-radius: 20px; padding: 12px 24px;"
      >
        <el-icon><Promotion /></el-icon> 发送
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { chat } from '@/api'
import { marked } from 'marked'
import * as echarts from 'echarts'

const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesRef = ref(null)
const chartRef = ref(null)

// 初始化欢迎消息
onMounted(() => {
  messages.value = [{
    role: 'bot',
    content: `👋 你好！我是 **OpsCopilot** 运维智能体 🤖

我可以帮你：
- 🔍 **查询告警** - "最近有哪些严重告警？"
- 📊 **数据统计** - "本周告警数量多少？"  
- 💻 **查询资产** - "给我们部门所有MySQL服务器"
- 📝 **创建工单** - "帮我申请开通数据库权限"
- 📚 **知识问答** - "Redis连接数满了怎么解决？"
- 🖥️ **运维大屏** - 点击左侧"运维大屏"查看

有什么可以帮你的？`,
    timestamp: new Date()
  }]
})

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return
  
  const userMessage = inputMessage.value.trim()
  inputMessage.value = ''
  
  messages.value.push({
    role: 'user',
    content: userMessage,
    timestamp: new Date()
  })
  
  loading.value = true
  scrollToBottom()
  
  try {
    const res = await chat({ message: userMessage })
    
    if (res.code === 200) {
      const botMsg = {
        role: 'bot',
        content: res.data.content,
        timestamp: new Date()
      }
      
      // 检查是否需要展示图表
      if (res.data.chart) {
        botMsg.chart = res.data.chart
      }
      if (res.data.table) {
        botMsg.table = res.data.table
      }
      if (res.data.cards) {
        botMsg.cards = res.data.cards
      }
      
      messages.value.push(botMsg)
      
      // 渲染图表
      if (botMsg.chart) {
        nextTick(() => renderChart(botMsg.chart))
      }
    } else {
      messages.value.push({
        role: 'bot',
        content: `抱歉，发生了错误：${res.message}`,
        timestamp: new Date()
      })
    }
  } catch (error) {
    messages.value.push({
      role: 'bot',
      content: '抱歉，连接服务器失败，请稍后重试。',
      timestamp: new Date()
    })
  }
  
  loading.value = false
  scrollToBottom()
}

// 渲染图表
const renderChart = (chartConfig) => {
  if (!chartConfig || !chartConfig.type) return
  
  // 找到最后一个图表容器
  const chartContainers = document.querySelectorAll('.chart-container')
  const container = chartContainers[chartContainers.length - 1]
  if (!container) return
  
  const chart = echarts.init(container)
  
  const options = {
    line: {
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: chartConfig.xData || [] },
      yAxis: { type: 'value' },
      series: (chartConfig.series || []).map(s => ({
        name: s.name,
        type: 'line',
        data: s.data,
        itemStyle: { color: s.color || '#409EFF' },
        areaStyle: s.area ? { color: s.area } : undefined
      }))
    },
    bar: {
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: chartConfig.xData || [] },
      yAxis: { type: 'value' },
      series: (chartConfig.series || []).map(s => ({
        name: s.name,
        type: 'bar',
        data: s.data,
        itemStyle: { color: s.color || '#409EFF' }
      }))
    },
    pie: {
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: (chartConfig.data || []).map(d => ({
          value: d.value,
          name: d.name,
          itemStyle: { color: d.color }
        }))
      }]
    }
  }
  
  chart.setOption(options[chartConfig.type] || {})
}

// 快捷命令
const sendQuick = (msg) => {
  inputMessage.value = msg
  sendMessage()
}

// 格式化内容
const formatContent = (content) => {
  return marked.parse(content)
}

// 格式化时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}
</script>

<style scoped>
.quick-commands {
  padding: 12px 20px;
  background: #F8FAFC;
  border-top: 1px solid var(--border-light);
}

.quick-title {
  font-size: 12px;
  color: var(--text-light);
  margin-bottom: 8px;
}

.quick-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.loading-dot {
  animation: loading 1.4s infinite;
  display: inline-block;
}

.loading-dot:nth-child(2) {
  animation-delay: 0.2s;
}

.loading-dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes loading {
  0%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-6px); }
}

.message-chart {
  margin: 12px 0;
}

.message-chart .chart-container {
  width: 100%;
  height: 250px;
}

.message-table {
  margin: 12px 0;
  max-height: 300px;
  overflow: auto;
}

.message-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 12px;
  margin: 12px 0;
}

.data-card-item {
  background: #F5F7FA;
  border-radius: 8px;
  padding: 12px;
  text-align: center;
}

.card-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--primary-color);
}

.card-label {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}
</style>
