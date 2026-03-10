<template>
  <div>
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-value">{{ stats.total }}</div>
        <div class="stat-label">资产总数</div>
      </div>
      <div class="stat-card success">
        <div class="stat-value">{{ stats.running }}</div>
        <div class="stat-label">运行中</div>
      </div>
      <div class="stat-card warning">
        <div class="stat-value">{{ stats.maintenance }}</div>
        <div class="stat-label">维护中</div>
      </div>
      <div class="stat-card danger">
        <div class="stat-value">{{ stats.stopped }}</div>
        <div class="stat-label">已停止</div>
      </div>
    </div>
    
    <!-- 资产列表 -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">资产列表</span>
        <div style="display: flex; gap: 12px;">
          <el-select v-model="queryParams.type" placeholder="资产类型" clearable style="width: 140px;">
            <el-option label="服务器" value="SERVER" />
            <el-option label="数据库" value="DATABASE" />
            <el-option label="中间件" value="MIDDLEWARE" />
            <el-option label="网络设备" value="NETWORK" />
          </el-select>
          <el-select v-model="queryParams.department" placeholder="所属部门" clearable style="width: 140px;">
            <el-option v-for="dept in departments" :key="dept" :label="dept" :value="dept" />
          </el-select>
          <el-input 
            v-model="queryParams.keyword" 
            placeholder="搜索名称/IP/业务..." 
            style="width: 200px;"
            clearable
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-button type="primary" @click="loadAssets">查询</el-button>
        </div>
      </div>
      
      <el-table :data="assets" stripe>
        <el-table-column prop="name" label="资产名称" min-width="150" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ getTypeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP地址" width="130" />
        <el-table-column prop="specification" label="规格" width="120" />
        <el-table-column prop="department" label="所属部门" width="120" />
        <el-table-column prop="business" label="所属业务" width="120" />
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div style="margin-top: 16px; text-align: right;">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @current-change="loadAssets"
        />
      </div>
    </div>
    
    <!-- 资产详情弹窗 -->
    <el-dialog v-model="detailVisible" title="资产详情" width="700px">
      <div v-if="currentAsset">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="资产名称">{{ currentAsset.name }}</el-descriptions-item>
          <el-descriptions-item label="资产类型">{{ getTypeLabel(currentAsset.type) }}</el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ currentAsset.ip }}</el-descriptions-item>
          <el-descriptions-item label="端口">{{ currentAsset.port || '-' }}</el-descriptions-item>
          <el-descriptions-item label="操作系统">{{ currentAsset.os }}</el-descriptions-item>
          <el-descriptions-item label="规格配置">{{ currentAsset.specification }}</el-descriptions-item>
          <el-descriptions-item label="所属部门">{{ currentAsset.department }}</el-descriptions-item>
          <el-descriptions-item label="所属业务">{{ currentAsset.business }}</el-descriptions-item>
          <el-descriptions-item label="负责人">{{ currentAsset.ownerName }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentAsset.status)">{{ currentAsset.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentAsset.remark || '-' }}</el-descriptions-item>
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
import { getAssets, getAssetStats, getDepartments } from '@/api'

const stats = ref({ total: 150, running: 130, stopped: 10, maintenance: 5 })
const assets = ref([])
const departments = ref([])
const total = ref(0)
const detailVisible = ref(false)
const currentAsset = ref(null)

const queryParams = reactive({
  keyword: '',
  type: '',
  department: '',
  business: '',
  page: 1,
  pageSize: 10
})

onMounted(() => {
  loadAssets()
  loadStats()
  loadDepartments()
})

const loadAssets = async () => {
  try {
    const res = await getAssets(queryParams)
    if (res.code === 200) {
      assets.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    // 模拟数据
    assets.value = [
      { id: 1, name: 'web-server-01', type: 'SERVER', ip: '10.0.1.10', specification: '4核8G', department: '电商技术部', business: '订单服务', ownerName: '张三', status: 'RUNNING' },
      { id: 2, name: 'db-mysql-01', type: 'DATABASE', ip: '10.0.2.10', specification: '8核16G', department: '电商技术部', business: '订单服务', ownerName: '李四', status: 'RUNNING' },
      { id: 3, name: 'redis-01', type: 'MIDDLEWARE', ip: '10.0.3.10', specification: '4核8G', department: '电商技术部', business: '订单服务', ownerName: '张三', status: 'RUNNING' },
      { id: 4, name: 'nginx-01', type: 'MIDDLEWARE', ip: '10.0.4.10', specification: '2核4G', department: '基础架构部', business: '网关', ownerName: '王五', status: 'RUNNING' }
    ]
    total.value = 4
  }
}

const loadStats = async () => {
  try {
    const res = await getAssetStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {}
}

const loadDepartments = async () => {
  try {
    const res = await getDepartments()
    if (res.code === 200) {
      departments.value = res.data || []
    }
  } catch (error) {
    departments.value = ['电商技术部', '支付技术部', '物流技术部', '基础架构部', '安全部']
  }
}

const viewDetail = (row) => {
  currentAsset.value = row
  detailVisible.value = true
}

const getTypeLabel = (type) => {
  const labels = { SERVER: '服务器', DATABASE: '数据库', MIDDLEWARE: '中间件', NETWORK: '网络设备', STORAGE: '存储' }
  return labels[type] || type
}

const getStatusType = (status) => {
  const types = { RUNNING: 'success', STOPPED: 'danger', MAINTENANCE: 'warning', RETIRED: 'info' }
  return types[status] || 'info'
}
</script>
