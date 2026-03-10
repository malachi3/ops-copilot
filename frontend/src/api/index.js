import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截
request.interceptors.request.use(
  config => {
    // 可以在这里添加token等认证信息
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    return Promise.reject(error)
  }
)

// 智能对话
export const chat = (data) => request.post('/chat', data)

// 告警相关
export const getAlerts = (params) => request.get('/alerts', { params })
export const getAlertById = (id) => request.get(`/alerts/${id}`)
export const createAlertTicket = (id) => request.post(`/alerts/${id}/ticket`)
export const getAlertStats = () => request.get('/alerts/statistics')

// 资产相关
export const getAssets = (params) => request.get('/assets', { params })
export const getAssetById = (id) => request.get(`/assets/${id}`)
export const getAssetByIp = (ip) => request.get(`/assets/ip/${ip}`)
export const getAssetStats = () => request.get('/assets/statistics')
export const getDepartments = () => request.get('/assets/departments')
export const getBusinesses = () => request.get('/assets/businesses')

// 工单相关
export const createTicket = (data) => request.post('/tickets', data)
export const getTickets = (params) => request.get('/tickets', { params })
export const getTicketById = (id) => request.get(`/tickets/${id}`)
export const remindTicket = (id) => request.post(`/tickets/${id}/remind`)
export const getTicketStats = () => request.get('/tickets/statistics')

// 知识库相关
export const searchKnowledge = (keyword) => request.get('/knowledge/search', { params: { keyword } })
export const getKnowledgeById = (id) => request.get(`/knowledge/${id}`)
export const likeKnowledge = (id) => request.post(`/knowledge/${id}/like`)
export const getHotKnowledge = (limit) => request.get('/knowledge/hot', { params: { limit } })

// 预测分析相关
export const getPredictionOverview = () => request.get('/prediction/overview')
export const getAlertPrediction = (period) => request.get('/prediction/alerts', { params: { period } })
export const getCapacityPrediction = (business) => request.get('/prediction/capacity', { params: { business } })
export const getFailurePrediction = () => request.get('/prediction/failure')

export default request
