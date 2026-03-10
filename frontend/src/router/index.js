import { createRouter, createWebHistory } from 'vue-router'
import ChatView from '@/views/ChatView.vue'
import AlertsView from '@/views/AlertsView.vue'
import AssetsView from '@/views/AssetsView.vue'
import TicketsView from '@/views/TicketsView.vue'
import KnowledgeView from '@/views/KnowledgeView.vue'
import DashboardView from '@/views/DashboardView.vue'

const routes = [
  {
    path: '/',
    name: 'Chat',
    component: ChatView
  },
  {
    path: '/alerts',
    name: 'Alerts',
    component: AlertsView
  },
  {
    path: '/assets',
    name: 'Assets',
    component: AssetsView
  },
  {
    path: '/tickets',
    name: 'Tickets',
    component: TicketsView
  },
  {
    path: '/knowledge',
    name: 'Knowledge',
    component: KnowledgeView
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: DashboardView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
