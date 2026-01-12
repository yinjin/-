import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

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
    },
    {
      path: '/roles',
      name: 'roles',
      component: () => import('@/views/RoleManage.vue'),
      meta: { 
        requiresAuth: true,
        title: '角色管理'
      }
    },
    {
      path: '/permissions',
      name: 'permissions',
      component: () => import('@/views/PermissionManage.vue'),
      meta: { 
        requiresAuth: true,
        title: '权限管理'
      }
    },
    {
      path: '/departments',
      name: 'departments',
      component: () => import('@/views/DepartmentManage.vue'),
      meta: { 
        requiresAuth: true,
        title: '部门管理'
      }
    },
    {
      path: '/material-categories',
      name: 'material-categories',
      component: () => import('@/views/MaterialCategoryManage.vue'),
      meta: { 
        requiresAuth: true,
        title: '耗材分类管理'
      }
    },
    {
      path: '/materials',
      name: 'materials',
      component: () => import('@/views/MaterialManage.vue'),
      meta: { 
        requiresAuth: true,
        title: '耗材管理'
      }
    },
    {
      path: '/suppliers',
      name: 'suppliers',
      component: () => import('@/views/SupplierManage.vue'),
      meta: {
        requiresAuth: true,
        title: '供应商管理'
      }
    },
    {
      path: '/suppliers/:id',
      name: 'supplier-detail',
      component: () => import('@/views/SupplierDetail.vue'),
      meta: {
        requiresAuth: true,
        title: '供应商详情'
      }
    },
    {
      path: '/inventories',
      name: 'inventories',
      component: () => import('@/views/InventoryList.vue'),
      meta: {
        requiresAuth: true,
        title: '库存管理'
      }
    },
    {
      path: '/inventory-warnings',
      name: 'inventory-warnings',
      component: () => import('@/views/InventoryWarning.vue'),
      meta: {
        requiresAuth: true,
        title: '库存预警'
      }
    },
    {
      path: '/inventory-statistics',
      name: 'inventory-statistics',
      component: () => import('@/views/InventoryStatistics.vue'),
      meta: {
        requiresAuth: true,
        title: '库存统计'
      }
    }
  ]
})

// 全局路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const isLoggedIn = userStore.isLoggedIn
  
  // 如果路由需要认证
  if (to.meta.requiresAuth) {
    // 如果用户未登录，跳转到登录页
    if (!isLoggedIn) {
      ElMessage.warning('请先登录')
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 保存目标路径，登录后跳转
      })
      return
    }
    
    // 如果用户已登录，检查token是否过期
    if (isLoggedIn) {
      const isTokenValid = await userStore.checkAndRefreshToken()
      
      // 如果token无效（已过期或刷新失败），跳转到登录页
      if (!isTokenValid) {
        next({
          path: '/login',
          query: { redirect: to.fullPath }
        })
        return
      }
    }
  }
  
  // 如果用户已登录且访问登录页，跳转到首页
  if (to.path === '/login' && isLoggedIn) {
    next('/')
    return
  }
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 实训耗材管理系统`
  }
  
  // 其他情况正常放行
  next()
})

// 全局后置钩子
router.afterEach((to, from) => {
  // 可以在这里添加页面访问日志等
  console.log(`路由跳转: ${from.path} -> ${to.path}`)
})

export default router
