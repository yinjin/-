import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi, logout as logoutApi } from '@/api/user'
import type { UserInfo, LoginRequest, LoginResponse } from '@/api/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)
  const tokenExpireTime = ref<number>(0) // token过期时间戳

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isTokenExpired = computed(() => {
    if (!tokenExpireTime.value) return false
    return Date.now() >= tokenExpireTime.value
  })

  // 方法
  const setToken = (newToken: string, expiresIn?: number) => {
    token.value = newToken
    // 同时保存到localStorage
    localStorage.setItem('token', newToken)
    
    // 设置token过期时间（默认2小时）
    if (expiresIn) {
      tokenExpireTime.value = Date.now() + expiresIn * 1000
    } else {
      tokenExpireTime.value = Date.now() + 2 * 60 * 60 * 1000 // 2小时
    }
    localStorage.setItem('tokenExpireTime', tokenExpireTime.value.toString())
  }

  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
  }

  const login = async (loginData: LoginRequest): Promise<LoginResponse> => {
    try {
      const response = await loginApi(loginData)
      if (response.code === 200 && response.data) {
        setToken(response.data.token)
        setUserInfo(response.data.user)
        return response.data
      } else {
        throw new Error(response.message || '登录失败')
      }
    } catch (error: any) {
      throw new Error(error.message || '登录失败')
    }
  }

  const getUserInfo = async (): Promise<UserInfo> => {
    try {
      const response = await getUserInfoApi()
      if (response.code === 200 && response.data) {
        setUserInfo(response.data)
        return response.data
      } else {
        throw new Error(response.message || '获取用户信息失败')
      }
    } catch (error: any) {
      throw new Error(error.message || '获取用户信息失败')
    }
  }

  const logout = async () => {
    try {
      await logoutApi()
    } catch (error) {
      console.error('退出登录失败:', error)
    } finally {
      // 无论API调用是否成功，都清除本地状态
      clearAuth()
    }
  }

  // 清除认证信息
  const clearAuth = () => {
    token.value = ''
    userInfo.value = null
    tokenExpireTime.value = 0
    localStorage.removeItem('token')
    localStorage.removeItem('tokenExpireTime')
  }

  // 检查并刷新token
  const checkAndRefreshToken = async (): Promise<boolean> => {
    // 如果没有token，返回false
    if (!token.value) {
      return false
    }

    // 如果token即将过期（剩余时间小于30分钟），尝试刷新
    const timeUntilExpiry = tokenExpireTime.value - Date.now()
    if (timeUntilExpiry < 30 * 60 * 1000 && timeUntilExpiry > 0) {
      try {
        // 调用刷新token接口（如果后端支持）
        // 这里暂时使用重新获取用户信息的方式
        await getUserInfo()
        return true
      } catch (error) {
        console.error('刷新token失败:', error)
        return false
      }
    }

    // 如果token已过期，清除认证信息
    if (isTokenExpired.value) {
      clearAuth()
      ElMessage.warning('登录已过期，请重新登录')
      return false
    }

    return true
  }

  // 初始化：从localStorage恢复token
  const initFromStorage = () => {
    const savedToken = localStorage.getItem('token')
    const savedExpireTime = localStorage.getItem('tokenExpireTime')
    
    if (savedToken) {
      token.value = savedToken
    }
    
    if (savedExpireTime) {
      tokenExpireTime.value = parseInt(savedExpireTime, 10)
    }
  }

  // 初始化调用
  initFromStorage()

  return {
    token,
    userInfo,
    tokenExpireTime,
    isLoggedIn,
    isTokenExpired,
    setToken,
    setUserInfo,
    login,
    getUserInfo,
    logout,
    clearAuth,
    checkAndRefreshToken
  }
})
