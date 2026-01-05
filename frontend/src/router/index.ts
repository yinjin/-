import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/AboutView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/users',
      name: 'users',
      component: () => import('@/views/UserManage.vue'),
      meta: { 
        requiresAuth: true,
        title: '用户管理'
      }
    }
  ]
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isLoggedIn = userStore.isLoggedIn
  
  // 如果路由需要认证但用户未登录，跳转到登录页
  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')
  } 
  // 如果用户已登录且访问登录页，跳转到首页
  else if (to.path === '/login' && isLoggedIn) {
    next('/')
  } 
  // 其他情况正常放行
  else {
    next()
  }
})

export default router
