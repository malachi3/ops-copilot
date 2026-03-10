<template>
  <div class="chat-container">
    <!-- 消息列表 -->
    <div class="chat-messages" ref="messagesRef">
      <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.role]">
        <div class="message-avatar">
          {{ msg.role === 'user' ? '你' : '🤖' }}
        </div>
        <div class="message-content">
          <div v-html="formatContent(msg.content)"></div>
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
        <el-button size="small" @click="sendQuick('给我们部门所有MySQL服务器')">💻 查询MySQL服务器</el-button>
        <el-button size="small" @click="sendQuick('Redis连接数满了怎么解决')">📚 Redis问题解答</el-button>
        <el-button size="small" @click="sendQuick('本周运维数据统计')">📊 运维数据统计</el-button>
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
import { format } from '@/utils/date'

const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesRef = ref(null)

// 初始化欢迎消息
onMounted(() => {
  messages.value = [{
    role: 'bot',
    content: `👋 你好！我是 **OpsCopilot** 运维智能体 🤖

我可以帮你：
- 🔍 **查询告警** - "最近有哪些严重告警？"
- 💻 **查询资产** - "给我们部门所有MySQL服务器"
- 📝 **创建工单** - "帮我申请开通数据库权限"
- 📚 **知识问答** - "Redis连接数满了怎么解决？"
- 📊 **数据统计** - "本周告警数量多少？"

有什么可以帮你的？`,
    timestamp: new Date()
  }]
})

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return
  
  const userMessage = inputMessage.value.trim()
  inputMessage.value = ''
  
  // 添加用户消息
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
      messages.value.push({
        role: 'bot',
        content: res.data.content,
        timestamp: new Date()
      })
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
  return format(timestamp, 'HH:mm:ss')
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
</style>
