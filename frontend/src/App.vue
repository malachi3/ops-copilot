<template>
  <div class="app-container">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div style="padding: 20px 24px; border-bottom: 1px solid rgba(255,255,255,0.1);">
        <div style="font-size: 20px; font-weight: 700;">🤖 OpsCopilot</div>
        <div style="font-size: 12px; opacity: 0.7; margin-top: 4px;">运维智能体 MVP</div>
      </div>
      
      <el-menu
        :default-active="currentRoute"
        class="sidebar-menu"
        background-color="transparent"
        text-color="rgba(255,255,255,0.8)"
        active-text-color="white"
        router
      >
        <el-menu-item index="/">
          <el-icon><ChatDotRound /></el-icon>
          <span>智能对话</span>
        </el-menu-item>
        <el-menu-item index="/alerts">
          <el-icon><Bell /></el-icon>
          <span>告警管理</span>
        </el-menu-item>
        <el-menu-item index="/assets">
          <el-icon><Monitor /></el-icon>
          <span>资产管理</span>
        </el-menu-item>
        <el-menu-item index="/tickets">
          <el-icon><Document /></el-icon>
          <span>工单管理</span>
        </el-menu-item>
        <el-menu-item index="/knowledge">
          <el-icon><Reading /></el-icon>
          <span>知识库</span>
        </el-menu-item>
        <el-menu-item index="/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>运维大屏</span>
        </el-menu-item>
        <el-menu-item index="/prediction">
          <el-icon><TrendCharts /></el-icon>
          <span>预测分析</span>
        </el-menu-item>
      </el-menu>
    </aside>
    
    <!-- 主内容 -->
    <main class="main-content">
      <!-- 顶部导航 -->
      <header class="top-header">
        <div class="header-title">
          {{ pageTitle }}
        </div>
        <div class="header-user">
          <el-tag type="success" size="small">🟢 在线</el-tag>
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" style="background: var(--primary-color);">张</el-avatar>
              <span style="margin-left: 8px;">张三</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item>设置</el-dropdown-item>
                <el-dropdown-item divided>退出</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      
      <!-- 页面内容 -->
      <div class="page-content">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const currentRoute = computed(() => route.path)

const pageTitle = computed(() => {
  const titles = {
    '/': '智能对话',
    '/alerts': '告警管理',
    '/assets': '资产管理',
    '/tickets': '工单管理',
    '/knowledge': '知识库',
    '/dashboard': '运维大屏',
    '/prediction': '预测分析'
  }
  return titles[route.path] || 'OpsCopilot'
})
</script>

<style scoped>
.sidebar-menu {
  background-color: transparent !important;
  border-right: none !important;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}
</style>
