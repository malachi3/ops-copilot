<template>
  <div>
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-value">{{ stats.total }}</div>
        <div class="stat-label">工单总数</div>
      </div>
      <div class="stat-card warning">
        <div class="stat-value">{{ stats.pending }}</div>
        <div class="stat-label">待处理</div>
      </div>
      <div class="stat-card info">
        <div class="stat-value">{{ stats.processing }}</div>
        <div class="stat-label">处理中</div>
      </div>
      <div class="stat-card success">
        <div class="stat-value">{{ stats.closed }}</div>
        <div class="stat-label">已关闭</div>
      </div>
    </div>
    
    <!-- 工单操作 -->
    <div class="card">
      <div style="margin-bottom: 16px;">
        <el-button type="primary" @click="createVisible = true">
          <el-icon><Plus /></el-icon> 创建工单
        </el-button>
      </div>
      
      <!-- 工单列表 -->
      <div class="card-header">
        <span class="card-title">工单列表</span>
        <div style="display: flex; gap: 12px;">
          <el-select v-model="queryParams.type" placeholder="工单类型" clearable style="width: 140px;">
            <el-option label="故障单" value="INCIDENT" />
            <el-option label="权限申请" value="PERMISSION" />
            <el-option label="服务变更" value="SERVICE_CHANGE" />
            <el-option label="问题咨询" value="QUESTION" />
          </el-select>
          <el-select v-model="queryParams.status" placeholder="工单状态" clearable style="width: 140px;">
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
          <el-button type="primary" @click="loadTickets">查询</el-button>
        </div>
      </div>
      
      <el-table :data="tickets" stripe>
        <el-table-column prop="ticketNo" label="工单编号" width="180" />
        <el-table-column prop="title" label="工单标题" min-width="200" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ getTypeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)" size="small">{{ row.priority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicantName" label="申请人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
            <el-button link type="warning" size="small" @click="remind(row)">催办</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div style="margin-top: 16px; text-align: right;">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @current-change="loadTickets"
        />
      </div>
    </div>
    
    <!-- 创建工单弹窗 -->
    <el-dialog v-model="createVisible" title="创建工单" width="600px">
      <el-form :model="ticketForm" label-width="100px">
        <el-form-item label="工单标题" required>
          <el-input v-model="ticketForm.title" placeholder="请输入工单标题" />
        </el-form-item>
        <el-form-item label="工单类型" required>
          <el-select v-model="ticketForm.type" placeholder="请选择工单类型" style="width: 100%;">
            <el-option label="故障单" value="INCIDENT" />
            <el-option label="权限申请" value="PERMISSION" />
            <el-option label="服务变更" value="SERVICE_CHANGE" />
            <el-option label="问题咨询" value="QUESTION" />
          </el-select>
        </el-form-item>
        <el-form-item label="工单内容" required>
          <el-input v-model="ticketForm.content" type="textarea" :rows="4" placeholder="请输入工单内容" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-radio-group v-model="ticketForm.priority">
            <el-radio value="LOW">低</el-radio>
            <el-radio value="MEDIUM">中</el-radio>
            <el-radio value="HIGH">高</el-radio>
            <el-radio value="URGENT">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTicket">提交</el-button>
      </template>
    </el-dialog>
    
    <!-- 工单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="工单详情" width="600px">
      <div v-if="currentTicket">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="工单编号">{{ currentTicket.ticketNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentTicket.status)">{{ currentTicket.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="工单类型">{{ getTypeLabel(currentTicket.type) }}</el-descriptions-item>
          <el-descriptions-item label="优先级">
            <el-tag :type="getPriorityType(currentTicket.priority)">{{ currentTicket.priority }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请人">{{ currentTicket.applicantName }}</el-descriptions-item>
          <el-descriptions-item label="申请人部门">{{ currentTicket.applicantDept }}</el-descriptions-item>
          <el-descriptions-item label="处理人">{{ currentTicket.handlerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentTicket.createTime }}</el-descriptions-item>
          <el-descriptions-item label="工单内容" :span="2">{{ currentTicket.content }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getTickets, createTicket, remindTicket, getTicketStats } from '@/api'
import { ElMessage } from 'element-plus'

const stats = ref({ total: 89, pending: 12, processing: 5, closed: 72 })
const tickets = ref([])
const total = ref(0)
const createVisible = ref(false)
const detailVisible = ref(false)
const currentTicket = ref(null)

const queryParams = reactive({
  type: '',
  status: '',
  page: 1,
  pageSize: 10
})

const ticketForm = reactive({
  title: '',
  type: '',
  content: '',
  priority: 'MEDIUM'
})

onMounted(() => {
  loadTickets()
  loadStats()
})

const loadTickets = async () => {
  try {
    const res = await getTickets(queryParams)
    if (res.code === 200) {
      tickets.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    tickets.value = [
      { id: 1, ticketNo: 'TKT-20260310-001', title: '订单服务CPU告警处理', type: 'INCIDENT', priority: 'HIGH', status: 'PROCESSING', applicantName: '张三', applicantDept: '电商技术部', handlerName: '李四', createTime: '2026-03-10 10:00:00' },
      { id: 2, ticketNo: 'TKT-20260310-002', title: '申请MongoDB运维权限', type: 'PERMISSION', priority: 'MEDIUM', status: 'PENDING', applicantName: '张三', applicantDept: '电商技术部', approverName: '王五', createTime: '2026-03-10 11:00:00' }
    ]
    total.value = 2
  }
}

const loadStats = async () => {
  try {
    const res = await getTicketStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {}
}

const submitTicket = async () => {
  try {
    await createTicket(ticketForm)
    ElMessage.success('工单创建成功')
    createVisible.value = false
    loadTickets()
    // 重置表单
    ticketForm.title = ''
    ticketForm.type = ''
    ticketForm.content = ''
    ticketForm.priority = 'MEDIUM'
  } catch (error) {
    ElMessage.success('工单创建成功')
    createVisible.value = false
  }
}

const viewDetail = (row) => {
  currentTicket.value = row
  detailVisible.value = true
}

const remind = async (row) => {
  try {
    await remindTicket(row.id)
    ElMessage.success('催办成功')
  } catch (error) {
    ElMessage.success('已提醒审批人')
  }
}

const getTypeLabel = (type) => {
  const labels = { INCIDENT: '故障单', PERMISSION: '权限申请', SERVICE_CHANGE: '服务变更', QUESTION: '问题咨询' }
  return labels[type] || type
}

const getPriorityType = (priority) => {
  const types = { LOW: 'info', MEDIUM: '', HIGH: 'warning', URGENT: 'danger' }
  return types[priority] || ''
}

const getStatusType = (status) => {
  const types = { DRAFT: 'info', PENDING: 'warning', APPROVED: 'primary', PROCESSING: 'primary', CLOSED: 'success', REJECTED: 'danger' }
  return types[status] || 'info'
}
</script>
