import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi, logout as logoutApi } from '@/api/user'
import type { UserInfo, LoginRequest, LoginResponse } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)

  // 方法
  const setToken = (newToken: string) => {
    token.value = newToken
    // 同时保存到localStorage
    localStorage.setItem('token', newToken)
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
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
    }
  }

  // 初始化：从localStorage恢复token
  const initFromStorage = () => {
    const savedToken = localStorage.getItem('token')
    if (savedToken) {
      token.value = savedToken
    }
  }

  // 初始化调用
  initFromStorage()

  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    login,
    getUserInfo,
    logout
  }
})
