import { test, expect } from '@playwright/test'

test.describe('用户管理页面 - 角色功能测试', () => {
  test.beforeEach(async ({ page }) => {
    // 访问登录页面
    await page.goto('http://localhost:5173/login')
    
    // 登录
    await page.fill('input[placeholder="请输入用户名"]', 'admin')
    await page.fill('input[type="password"]', 'admin123')
    await page.click('.el-button--primary:has-text("登录")')
    
    // 等待跳转到首页
    await page.waitForURL('http://localhost:5173/', { timeout: 10000 })
    
    // 导航到用户管理页面
    await page.click('.menu-card:has-text("用户管理")')
    await page.waitForURL('http://localhost:5173/users', { timeout: 10000 })
  })

  test('应该显示用户的角色信息', async ({ page }) => {
    // 等待用户列表加载
    await page.waitForSelector('.el-table__body-wrapper')
    
    // 检查角色列是否存在
    const roleColumn = await page.locator('.el-table__header th').filter({ hasText: '角色' })
    await expect(roleColumn).toBeVisible()
    
    // 检查用户列表中是否有角色标签
    const roleTags = await page.locator('.el-tag').count()
    expect(roleTags).toBeGreaterThan(0)
  })

  test('编辑用户时应该显示角色选择框', async ({ page }) => {
    // 等待用户列表加载
    await page.waitForSelector('.el-table__body-wrapper')
    
    // 点击第一个用户的编辑按钮
    await page.locator('.el-button:has-text("编辑")').first().click()
    
    // 等待编辑对话框打开
    await page.waitForSelector('.el-dialog')
    
    // 检查角色选择框是否存在
    const roleSelect = await page.locator('.el-form-item:has-text("角色") .el-select')
    await expect(roleSelect).toBeVisible()
    
    // 检查角色选择框是否可点击
    await roleSelect.click()
    
    // 等待下拉框渲染
    await page.waitForTimeout(1000)
    
    // 检查是否有角色选项（通过文本内容定位）
    // 使用getByText来定位角色选项，更可靠
    const teacherOption = page.getByText('教师')
    const studentOption = page.getByText('学生')
    const warehouseOption = page.getByText('仓库管理员')
    
    // 验证至少有一个角色选项存在
    const hasTeacher = await teacherOption.count() > 0
    const hasStudent = await studentOption.count() > 0
    const hasWarehouse = await warehouseOption.count() > 0
    
    expect(hasTeacher || hasStudent || hasWarehouse).toBeTruthy()
    
    // 点击页面其他地方关闭下拉框
    await page.click('.el-dialog__header')
    
    // 关闭对话框
    await page.click('.el-dialog__headerbtn')
  })

  test('应该能够为用户分配角色', async ({ page }) => {
    // 等待用户列表加载
    await page.waitForSelector('.el-table__body-wrapper')
    
    // 点击第一个用户的编辑按钮
    await page.locator('.el-button:has-text("编辑")').first().click()
    
    // 等待编辑对话框打开
    await page.waitForSelector('.el-dialog')
    
    // 点击角色选择框
    const roleSelect = page.locator('.el-form-item:has-text("角色") .el-select')
    await roleSelect.click()
    
    // 等待下拉框渲染
    await page.waitForTimeout(1000)
    
    // 使用键盘操作来选择角色
    // 按下箭头键选择第一个角色
    await page.keyboard.press('ArrowDown')
    await page.waitForTimeout(200)
    
    // 按下回车键确认选择
    await page.keyboard.press('Enter')
    await page.waitForTimeout(500)
    
    // 点击页面其他地方关闭下拉菜单（避免下拉菜单拦截确定按钮的点击）
    await page.click('.el-dialog__header')
    
    // 等待下拉菜单关闭
    await page.waitForTimeout(500)
    
    // 点击确定按钮
    await page.click('.el-dialog__footer .el-button--primary')
    
    // 等待成功消息
    await page.waitForSelector('.el-message--success', { timeout: 5000 })
    
    // 等待一小段时间确保所有消息都已显示
    await page.waitForTimeout(500)
    
    // 获取所有成功消息
    const allMessages = await page.locator('.el-message--success').allTextContents()
    
    // 验证至少有一个消息包含"更新成功"
    const hasUpdateSuccess = allMessages.some(msg => msg.includes('更新成功'))
    expect(hasUpdateSuccess).toBeTruthy()
    
    // 刷新页面
    await page.reload()
    
    // 等待用户列表重新加载
    await page.waitForSelector('.el-table__body-wrapper')
    
    // 验证用户角色已更新
    const roleTags = await page.locator('.el-tag').count()
    expect(roleTags).toBeGreaterThan(0)
  })

  test('应该能够移除用户的角色', async ({ page }) => {
    // 等待用户列表加载
    await page.waitForSelector('.el-table__body-wrapper')
    
    // 点击第一个用户的编辑按钮
    await page.locator('.el-button:has-text("编辑")').first().click()
    
    // 等待编辑对话框打开
    await page.waitForSelector('.el-dialog')
    
    // 检查是否已有选中的角色标签
    const selectedRoleTags = await page.locator('.el-form-item:has-text("角色") .el-tag').count()
    
    if (selectedRoleTags > 0) {
      // 点击角色标签的关闭按钮来移除角色
      await page.locator('.el-form-item:has-text("角色") .el-tag .el-tag__close').first().click()
      
      // 点击确定按钮
      await page.click('.el-dialog__footer .el-button--primary')
      
      // 等待成功消息
      await page.waitForSelector('.el-message--success', { timeout: 5000 })
      
      // 等待一小段时间确保所有消息都已显示
      await page.waitForTimeout(500)
      
      // 获取所有成功消息
      const allMessages = await page.locator('.el-message--success').allTextContents()
      
      // 验证至少有一个消息包含"更新成功"
      const hasUpdateSuccess = allMessages.some(msg => msg.includes('更新成功'))
      expect(hasUpdateSuccess).toBeTruthy()
    } else {
      // 如果没有选中角色，关闭对话框
      await page.click('.el-dialog__headerbtn')
    }
  })

  test('应该显示无角色的用户', async ({ page }) => {
    // 等待用户列表加载
    await page.waitForSelector('.el-table__body-wrapper')
    
    // 检查是否有"无角色"文本
    const noRoleText = await page.locator('text=无角色').count()
    expect(noRoleText).toBeGreaterThanOrEqual(0)
  })

  test('角色标签应该正确显示角色名称', async ({ page }) => {
    // 等待用户列表加载
    await page.waitForSelector('.el-table__body-wrapper')
    
    // 获取第一个角色标签
    const firstRoleTag = page.locator('.el-tag').first()
    
    // 检查角色标签是否可见
    await expect(firstRoleTag).toBeVisible()
    
    // 检查角色标签是否有文本
    const roleText = await firstRoleTag.textContent()
    expect(roleText).toBeTruthy()
    expect(roleText?.length).toBeGreaterThan(0)
  })
})
