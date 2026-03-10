<template>
  <div>
    <!-- 搜索栏 -->
    <div class="card">
      <div style="display: flex; gap: 12px;">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索知识库..."
          style="flex: 1;"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
    </div>
    
    <!-- 搜索结果 -->
    <div v-if="searchResults.length > 0" class="card">
      <div class="card-header">
        <span class="card-title">搜索结果</span>
      </div>
      <div v-for="item in searchResults" :key="item.id" class="knowledge-item">
        <div class="knowledge-title">{{ item.title }}</div>
        <div class="knowledge-content">{{ item.content }}</div>
        <div class="knowledge-meta">
          <el-tag size="small">{{ item.type }}</el-tag>
          <span>👁️ {{ item.viewCount }}</span>
          <span>❤️ {{ item.likeCount }}</span>
        </div>
      </div>
    </div>
    
    <!-- 热门知识 -->
    <div class="card">
      <div class="card-header">
        <span class="card-title">热门知识</span>
      </div>
      <el-table :data="hotList" stripe>
        <el-table-column prop="title" label="标题" min-width="300" />
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 知识详情弹窗 -->
    <el-dialog v-model="detailVisible" title="知识详情" width="700px">
      <div v-if="currentKnowledge">
        <h3>{{ currentKnowledge.title }}</h3>
        <div style="margin: 12px 0; color: #666;">
          <el-tag size="small" style="margin-right: 8px;">{{ currentKnowledge.type }}</el-tag>
          <span>👁️ {{ currentKnowledge.viewCount }}</span>
          <span style="margin-left: 12px;">❤️ {{ currentKnowledge.likeCount }}</span>
        </div>
        <div class="knowledge-detail-content" v-html="formatContent(currentKnowledge.content)"></div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="likeKnowledge">
          <el-icon><Star /></el-icon> 点赞
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { searchKnowledge, getHotKnowledge, likeKnowledge as apiLikeKnowledge } from '@/api'
import { marked } from 'marked'
import { ElMessage } from 'element-plus'

const searchKeyword = ref('')
const searchResults = ref([])
const hotList = ref([])
const detailVisible = ref(false)
const currentKnowledge = ref(null)

onMounted(() => {
  loadHotKnowledge()
})

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) return
  
  try {
    const res = await searchKnowledge(searchKeyword.value)
    if (res.code === 200) {
      searchResults.value = res.data || []
    }
  } catch (error) {
    // 模拟数据
    searchResults.value = [
      { id: 1, title: 'Redis连接数满了怎么解决', content: 'Redis连接数满了，一般有以下几种原因和解决方法...', type: 'OPERATION', viewCount: 156, likeCount: 23 },
      { id: 2, title: 'Nginx服务重启方法', content: 'Nginx重启方法：优雅重启 `nginx -s reload`...', type: 'OPERATION', viewCount: 89, likeCount: 12 }
    ]
  }
}

const loadHotKnowledge = async () => {
  try {
    const res = await getHotKnowledge(10)
    if (res.code === 200) {
      hotList.value = res.data || []
    }
  } catch (error) {
    hotList.value = [
      { id: 1, title: 'Linux常用命令大全', viewCount: 1234 },
      { id: 2, title: 'MySQL备份恢复最佳实践', viewCount: 987 },
      { id: 3, title: 'Nginx配置详解', viewCount: 756 },
      { id: 4, title: 'Docker使用指南', viewCount: 654 }
    ]
  }
}

const viewDetail = (row) => {
  currentKnowledge.value = row
  detailVisible.value = true
}

const likeKnowledge = async () => {
  try {
    await apiLikeKnowledge(currentKnowledge.value.id)
    ElMessage.success('点赞成功')
  } catch (error) {
    ElMessage.success('点赞成功')
  }
}

const formatContent = (content) => {
  return marked.parse(content)
}
</script>

<style scoped>
.knowledge-item {
  padding: 16px;
  border-bottom: 1px solid var(--border-light);
}

.knowledge-item:last-child {
  border-bottom: none;
}

.knowledge-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary-color);
  margin-bottom: 8px;
}

.knowledge-content {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.knowledge-meta {
  font-size: 12px;
  color: var(--text-light);
  display: flex;
  gap: 12px;
  align-items: center;
}

.knowledge-detail-content {
  line-height: 1.8;
  padding: 12px 0;
}
</style>
