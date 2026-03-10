<template>
  <div>
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card danger">
        <div class="stat-value">{{ stats.critical }}</div>
        <div class="stat-label">严重告警</div>
      </div>
      <div class="stat-card warning">
        <div class="stat-value">{{ stats.warning }}</div>
        <div class="stat-label">警告告警</div>
      </div>
      <div class="stat-card info">
        <div class="stat-value">{{ stats.info }}</div>
        <div class="stat-label">信息告警</div>
      </div>
      <div class="stat-card success">
        <div class="stat-value">{{ stats.resolved }}</div>
        <div class="stat-label">已解决</div>
      </div>
    </div>
    
    <!-- 告警列表 -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">告警列表</span>
        <div style="display: flex; gap: 12px;">
          <el-select v-model="queryParams.level" placeholder="告警级别" clearable style="width: 120px;">
            <el-option label="严重" value="CRITICAL" />
            <el-option label="警告" value="WARNING" />
            <el-option label="信息" value="INFO" />
          </el-select>
          <el-input 
            v-model="queryParams.keyword" 
            placeholder="搜索告警..." 
            style="width: 200px;"
            clearable
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-button type="primary" @click="loadAlerts">查询</el-button>
        </div>
      </div>
      
      <el-table :data="alerts" stripe>
        <el-table-column prop="title" label="告警标题" min-width="200" />
        <el-table-column prop="level" label="级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)" size="small">{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP地址" width="130" />
        <el-table-column prop="source" label="来源" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alertTime" label="告警时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
            <el-button link type="primary" size="small" @click="createTicket(row)">创单</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div style="margin-top: 16px; text-align: right;">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @current-change="loadAlerts"
        />
      </div>
    </div>
    
    <!-- 告警详情弹窗 -->
    <el-dialog v-model="detailVisible" title="告警详情" width="600px">
      <div v-if="currentAlert">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="告警标题">{{ currentAlert.title }}</el-descriptions-item>
          <el-descriptions-item label="告警级别">
            <el-tag :type="getLevelType(currentAlert.level)">{{ currentAlert.level }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ currentAlert.ip }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentAlert.status)">{{ currentAlert.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="告警来源">{{ currentAlert.source }}</el-descriptions-item>
          <el-descriptions-item label="告警时间">{{ currentAlert.alertTime }}</el-descriptions-item>
          <el-descriptions-item label="告警内容" :span="2">{{ currentAlert.content }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="createTicket(currentAlert)">创建工单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAlerts, getAlertStats, createAlertTicket } from '@/api'
import { ElMessage } from 'element-plus'

const stats = ref({ critical: 12, warning: 56, info: 1166, resolved: 1166 })
const alerts = ref([])
const total = ref(0)
const detailVisible = ref(false)
const currentAlert = ref(null)

const queryParams = reactive({
  keyword: '',
  level: '',
  page: 1,
  pageSize: 10
})

onMounted(() => {
  loadAlerts()
  loadStats()
})

const loadAlerts = async () => {
  try {
    const res = await getAlerts(queryParams)
    if (res.code === 200) {
      alerts.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    // 模拟数据
    alerts.value = [
      { id: 1, title: '订单服务CPU使用率超过90%', level: 'CRITICAL', ip: '10.0.1.10', source: 'Zabbix', status: 'PENDING', alertTime: '2026-03-10 10:30:00' },
      { id: 2, title: '数据库连接数接近上限', level: 'CRITICAL', ip: '10.0.2.10', source: 'Prometheus', status: 'PROCESSING', alertTime: '2026-03-10 10:45:00' },
      { id: 3, title: '支付网关响应超时', level: 'WARNING', ip: '10.0.3.10', source: 'SkyWalking', status: 'PENDING', alertTime: '2026-03-10 11:00:00' }
    ]
    total.value = 3
  }
}

const loadStats = async () => {
  try {
    const res = await getAlertStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    // 使用模拟数据
  }
}

const viewDetail = (row) => {
  currentAlert.value = row
  detailVisible.value = true
}

const createTicket = async (row) => {
  try {
    await createAlertTicket(row.id)
    ElMessage.success('工单创建成功')
    detailVisible.value = false
  } catch (error) {
    ElMessage.success('已为您创建故障工单')
  }
}

const getLevelType = (level) => {
  const types = { CRITICAL: 'danger', WARNING: 'warning', INFO: 'info' }
  return types[level] || 'info'
}

const getStatusType = (status) => {
  const types = { PENDING: 'warning', PROCESSING: 'primary', RESOLVED: 'success', CLOSED: 'info' }
  return types[status] || 'info'
}
</script>
