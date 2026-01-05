import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 在发送请求之前做些什么
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    // 对请求错误做些什么
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 对响应数据做点什么
    return response.data
  },
  (error) => {
    // 对响应错误做点什么
    console.error('响应错误:', error)
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          // 未授权，清除token并跳转到登录页
          ElMessage.error('登录已过期，请重新登录')
          localStorage.removeItem('token')
          localStorage.removeItem('tokenExpireTime')
          
          // 保存当前路径，登录后跳转回来
          const currentPath = router.currentRoute.value.fullPath
          router.push({
            path: '/login',
            query: { redirect: currentPath }
          })
          break
          
        case 403:
          // 禁止访问
          ElMessage.error('没有权限访问该资源')
          break
          
        case 404:
          // 资源不存在
          ElMessage.error('请求的资源不存在')
          break
          
        case 500:
          // 服务器错误
          ElMessage.error(data?.message || '服务器内部错误')
          break
          
        default:
          // 其他错误
          ElMessage.error(data?.message || `请求失败: ${status}`)
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      ElMessage.error('网络错误，请检查网络连接')
    } else {
      // 请求配置错误
      ElMessage.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

export default request

// API接口
export const api = {
  // 测试接口
  test: {
    hello: () => request.get('/test/hello'),
  },
}
