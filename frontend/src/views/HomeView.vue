<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { User, Box, Document, DataLine, Setting, TrendCharts, ArrowDown, UserFilled, Lock, OfficeBuilding } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 退出登录
const handleLogout = async () => {
  try {
    await userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  } catch (error: any) {
    ElMessage.error(error.message || '退出登录失败')
  }
}

// 跳转到用户管理页面
const goToUserManage = () => {
  router.push('/users')
}

// 跳转到角色管理页面
const goToRoleManage = () => {
  router.push('/roles')
}

// 跳转到权限管理页面
const goToPermissionManage = () => {
  router.push('/permissions')
}

// 跳转到部门管理页面
const goToDepartmentManage = () => {
  router.push('/departments')
}

// 跳转到耗材分类管理页面
const goToMaterialCategoryManage = () => {
  router.push('/material-categories')
}

// 跳转到耗材管理页面
const goToMaterialManage = () => {
  router.push('/materials')
}

// 功能开发中提示
const showDevelopingMessage = () => {
  ElMessage.info('该功能正在开发中，敬请期待')
}
</script>

<template>
  <div class="home">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>高职人工智能学院实训耗材管理系统</h1>
          <div class="user-info">
            <span>欢迎，{{ userStore.userInfo?.name || '用户' }}</span>
            <el-button type="primary" @click="handleLogout">退出登录</el-button>
          </div>
        </div>
      </el-header>
      
      <el-main>
        <el-card class="welcome-card">
          <h2>欢迎使用耗材管理系统</h2>
          <p>本系统提供完整的实训耗材管理功能，包括用户管理、耗材管理、库存管理、领用管理等模块。</p>
        </el-card>

        <el-row :gutter="20" class="menu-cards">
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="menu-card" shadow="hover" @click="goToUserManage">
              <div class="card-content">
                <el-icon :size="40" color="#409EFF"><User /></el-icon>
                <h3>用户管理</h3>
                <p>管理系统用户，包括新增、编辑、删除用户，以及用户状态管理</p>
              </div>
            </el-card>
          </el-col>
          
          <el-col :xs="24" :sm="12" :md="8">
            <el-popover placement="bottom" :width="200" trigger="click">
              <template #reference>
                <el-card class="menu-card menu-card-clickable" shadow="hover">
                  <div class="card-content">
                    <el-icon :size="40" color="#67C23A"><Box /></el-icon>
                    <h3>耗材管理</h3>
                    <p>管理实训耗材，包括耗材信息和分类结构</p>
                    <el-icon class="expand-icon"><ArrowDown /></el-icon>
                  </div>
                </el-card>
              </template>
              <div class="system-menu-list">
                <div class="system-menu-item" @click="goToMaterialManage">
                  <el-icon><Box /></el-icon>
                  <span>耗材信息管理</span>
                </div>
                <div class="system-menu-item" @click="goToMaterialCategoryManage">
                  <el-icon><Box /></el-icon>
                  <span>耗材分类管理</span>
                </div>
              </div>
            </el-popover>
          </el-col>
          
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="menu-card" shadow="hover" @click="showDevelopingMessage">
              <div class="card-content">
                <el-icon :size="40" color="#E6A23C"><Document /></el-icon>
                <h3>领用管理</h3>
                <p>管理耗材领用申请，包括申请审批、领用记录等</p>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" class="menu-cards">
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="menu-card" shadow="hover" @click="showDevelopingMessage">
              <div class="card-content">
                <el-icon :size="40" color="#F56C6C"><DataLine /></el-icon>
                <h3>库存管理</h3>
                <p>管理耗材库存，包括入库、出库、库存盘点等</p>
              </div>
            </el-card>
          </el-col>
          
          <el-col :xs="24" :sm="12" :md="8">
            <el-popover placement="bottom" :width="200" trigger="click">
              <template #reference>
                <el-card class="menu-card menu-card-clickable" shadow="hover">
                  <div class="card-content">
                    <el-icon :size="40" color="#909399"><Setting /></el-icon>
                    <h3>系统设置</h3>
                    <p>系统配置管理，包括参数设置、权限管理等</p>
                    <el-icon class="expand-icon"><ArrowDown /></el-icon>
                  </div>
                </el-card>
              </template>
              <div class="system-menu-list">
                <div class="system-menu-item" @click="goToRoleManage">
                  <el-icon><UserFilled /></el-icon>
                  <span>角色管理</span>
                </div>
                <div class="system-menu-item" @click="goToPermissionManage">
                  <el-icon><Lock /></el-icon>
                  <span>权限管理</span>
                </div>
                <div class="system-menu-item" @click="goToDepartmentManage">
                  <el-icon><OfficeBuilding /></el-icon>
                  <span>部门管理</span>
                </div>
              </div>
            </el-popover>
          </el-col>
          
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="menu-card" shadow="hover" @click="showDevelopingMessage">
              <div class="card-content">
                <el-icon :size="40" color="#409EFF"><TrendCharts /></el-icon>
                <h3>统计分析</h3>
                <p>耗材使用统计分析，包括领用统计、库存分析等</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
  background: #f5f7fa;
}

.el-header {
  background: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h1 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info span {
  font-size: 14px;
  color: #606266;
}

.el-main {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 30px;
}

.welcome-card h2 {
  margin: 0 0 15px 0;
  color: #303133;
}

.welcome-card p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.menu-cards {
  margin-bottom: 20px;
}

.menu-card {
  cursor: pointer;
  transition: transform 0.3s;
  height: 230px;
  overflow: hidden !important;
}

.menu-card:hover {
  transform: translateY(-5px);
}

.menu-card-clickable:hover {
  transform: translateY(-5px);
}

.card-content {
  text-align: center;
  padding: 20px;
  overflow: hidden;
}

.card-content h3 {
  margin: 15px 0 10px 0;
  color: #303133;
}

.card-content p {
  margin: 0;
  color: #909399;
  font-size: 14px;
  line-height: 1.5;
}

.expand-icon {
  margin-top: 10px;
  color: #909399;
  transition: transform 0.3s;
}

.el-popover:hover .expand-icon {
  transform: rotate(180deg);
  color: #409EFF;
}

.system-menu-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.system-menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  color: #606266;
}

.system-menu-item:hover {
  background: #409EFF;
  color: #fff;
}

.system-menu-item .el-icon {
  font-size: 18px;
}

/* 确保popover内容可交互 */
:deep(.el-popper) {
  max-height: none;
}

:deep(.el-popover__wrapper) {
  display: block;
}
</style>
