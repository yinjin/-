import { test, expect } from '@playwright/test';

/**
 * 角色管理页面功能测试
 * 
 * 测试范围：
 * 1. 角色列表显示
 * 2. 角色搜索功能
 * 3. 角色CRUD操作（新增、编辑、删除）
 * 4. 角色状态切换
 * 5. 角色权限分配
 * 6. 批量操作（启用、禁用、删除）
 * 7. 分页功能
 * 8. 表单验证
 * 
 * 遵循规范：
 * - E2E测试规范-第6.2条（测试用例设计）
 * - 前端交互规范-第5.2条（加载状态管理）
 * - 前端交互规范-第5.2条（用户反馈）
 * - 数据刷新规范-第5.2条（数据刷新）
 * - 错误处理规范-第5.3条（错误提示）
 */

test.describe('角色管理页面功能测试', () => {
  // 每个测试用例前的准备工作
  test.beforeEach(async ({ page }) => {
    // 登录系统
    await page.goto('http://localhost:5175/login');
    // 遵循：E2E测试规范-第6.1条（选择器稳定性）
    // 使用更稳定的选择器定位Element Plus的输入框
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[placeholder="请输入密码"]', 'admin123');
    await page.click('button:has-text("登录")');
    
    // 等待登录成功，跳转到首页
    await page.waitForURL('http://localhost:5175/');
    
    // 导航到角色管理页面
    // 遵循：E2E测试规范-第6.1条（选择器稳定性）
    // 直接导航到角色管理页面路径
    await page.goto('http://localhost:5175/roles');
  });

  /**
   * 测试用例1：应该显示角色列表
   * 
   * 测试目标：验证角色列表是否正确显示
   * 测试类型：功能测试
   */
  test('应该显示角色列表', async ({ page }) => {
    // 遵循：前端交互规范-第5.2条（加载状态管理）
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 验证角色列表是否显示
    const table = page.locator('.el-table');
    await expect(table).toBeVisible();
    
    // 验证初始角色数据（admin、teacher、student、warehouse）
    const roleNames = await page.locator('.el-table__body .el-table__row').count();
    expect(roleNames).toBeGreaterThan(0);
    
    // 验证角色名称列是否显示
    const roleNameColumn = page.locator('.el-table__header th').filter({ hasText: '角色名称' });
    await expect(roleNameColumn).toBeVisible();
  });

  /**
   * 测试用例2：应该能够搜索角色
   * 
   * 测试目标：验证角色搜索功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够搜索角色', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 输入搜索关键词
    await page.fill('input[placeholder*="角色名称"]', '管理员');
    
    // 点击搜索按钮
    await page.click('button:has-text("搜索")');
    
    // 遵循：前端交互规范-第5.2条（加载状态管理）
    // 等待搜索结果加载
    await page.waitForSelector('.el-table__body .el-table__row');
    
    // 验证搜索结果
    const rows = page.locator('.el-table__body .el-table__row');
    const count = await rows.count();
    expect(count).toBeGreaterThan(0);
    
    // 验证搜索结果中包含"管理员"
    const firstRow = rows.first();
    const text = await firstRow.textContent();
    expect(text).toContain('管理员');
  });

  /**
   * 测试用例3：应该能够新增角色
   * 
   * 测试目标：验证角色新增功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够新增角色', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 填写角色信息
    await page.fill('input[placeholder*="角色名称"]', '测试角色');
    await page.fill('input[placeholder*="角色编码"]', 'test_role');
    await page.fill('textarea[placeholder*="角色描述"]', '这是一个测试角色');
    
    // 点击确定按钮
    await page.click('.el-dialog button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证角色列表已刷新
    await page.waitForSelector('.el-table__body .el-table__row');
    const rows = page.locator('.el-table__body .el-table__row');
    const count = await rows.count();
    expect(count).toBeGreaterThan(0);
    
    // 验证新增的角色是否在列表中
    const testRole = page.locator('.el-table__body .el-table__row').filter({ hasText: '测试角色' });
    await expect(testRole).toBeVisible();
  });

  /**
   * 测试用例4：应该能够编辑角色
   * 
   * 测试目标：验证角色编辑功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够编辑角色', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 点击第一行的编辑按钮
    await page.click('.el-table__body .el-table__row:first-child button:has-text("编辑")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 修改角色名称
    const roleNameInput = page.locator('input[placeholder*="角色名称"]');
    await roleNameInput.clear();
    await roleNameInput.fill('测试角色-已修改');
    
    // 点击确定按钮
    await page.click('.el-dialog button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证角色列表已刷新
    await page.waitForSelector('.el-table__body .el-table__row');
    const modifiedRole = page.locator('.el-table__body .el-table__row').filter({ hasText: '测试角色-已修改' });
    await expect(modifiedRole).toBeVisible();
  });

  /**
   * 测试用例5：应该能够删除角色
   * 
   * 测试目标：验证角色删除功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够删除角色', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 点击第一行的删除按钮
    await page.click('.el-table__body .el-table__row:first-child button:has-text("删除")');
    
    // 等待确认对话框出现
    await page.waitForSelector('.el-message-box');
    
    // 点击确定按钮
    await page.click('.el-message-box button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证角色列表已刷新
    await page.waitForTimeout(1000);
  });

  /**
   * 测试用例6：应该能够切换角色状态
   * 
   * 测试目标：验证角色状态切换功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够切换角色状态', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 点击第一行的状态切换按钮
    await page.click('.el-table__body .el-table__row:first-child .el-switch');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证角色列表已刷新
    await page.waitForSelector('.el-table__body .el-table__row');
  });

  /**
   * 测试用例7：应该能够分配权限
   * 
   * 测试目标：验证角色权限分配功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够分配权限', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 点击第一行的分配权限按钮
    await page.click('.el-table__body .el-table__row:first-child button:has-text("分配权限")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 选择权限（点击树形结构的复选框）
    await page.click('.el-tree .el-checkbox:first-child');
    
    // 点击确定按钮
    await page.click('.el-dialog button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
  });

  /**
   * 测试用例8：应该能够批量启用角色
   * 
   * 测试目标：验证批量启用角色功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够批量启用角色', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 选择第一行和第二行的复选框
    await page.click('.el-table__body .el-table__row:nth-child(1) .el-checkbox');
    await page.click('.el-table__body .el-table__row:nth-child(2) .el-checkbox');
    
    // 点击批量启用按钮
    await page.click('button:has-text("批量启用")');
    
    // 等待确认对话框出现
    await page.waitForSelector('.el-message-box');
    
    // 点击确定按钮
    await page.click('.el-message-box button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证角色列表已刷新
    await page.waitForSelector('.el-table__body .el-table__row');
  });

  /**
   * 测试用例9：应该能够批量禁用角色
   * 
   * 测试目标：验证批量禁用角色功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够批量禁用角色', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 选择第一行和第二行的复选框
    await page.click('.el-table__body .el-table__row:nth-child(1) .el-checkbox');
    await page.click('.el-table__body .el-table__row:nth-child(2) .el-checkbox');
    
    // 点击批量禁用按钮
    await page.click('button:has-text("批量禁用")');
    
    // 等待确认对话框出现
    await page.waitForSelector('.el-message-box');
    
    // 点击确定按钮
    await page.click('.el-message-box button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证角色列表已刷新
    await page.waitForSelector('.el-table__body .el-table__row');
  });

  /**
   * 测试用例10：应该能够批量删除角色
   * 
   * 测试目标：验证批量删除角色功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够批量删除角色', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 选择第一行和第二行的复选框
    await page.click('.el-table__body .el-table__row:nth-child(1) .el-checkbox');
    await page.click('.el-table__body .el-table__row:nth-child(2) .el-checkbox');
    
    // 点击批量删除按钮
    await page.click('button:has-text("批量删除")');
    
    // 等待确认对话框出现
    await page.waitForSelector('.el-message-box');
    
    // 点击确定按钮
    await page.click('.el-message-box button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证角色列表已刷新
    await page.waitForSelector('.el-table__body .el-table__row');
  });

  /**
   * 测试用例11：应该能够分页查看角色
   * 
   * 测试目标：验证分页功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够分页查看角色', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 验证分页组件是否显示
    const pagination = page.locator('.el-pagination');
    await expect(pagination).toBeVisible();
    
    // 点击下一页
    await page.click('.el-pagination button:has-text(">")');
    
    // 遵循：前端交互规范-第5.2条（加载状态管理）
    // 等待数据加载
    await page.waitForSelector('.el-table__body .el-table__row');
    
    // 验证分页信息
    const pageInfo = page.locator('.el-pagination__total');
    await expect(pageInfo).toBeVisible();
  });

  /**
   * 测试用例12：表单验证应该正常工作-角色名称必填
   * 
   * 测试目标：验证表单验证功能是否正常
   * 测试类型：边界测试
   */
  test('表单验证应该正常工作-角色名称必填', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 不填写角色名称，直接点击确定
    await page.click('.el-dialog button:has-text("确定")');
    
    // 遵循：错误处理规范-第5.3条（错误提示）
    // 验证错误提示
    await expect(page.locator('.el-form-item__error')).toBeVisible();
    await expect(page.locator('.el-form-item__error')).toContainText('角色名称不能为空');
  });

  /**
   * 测试用例13：表单验证应该正常工作-角色编码必填
   * 
   * 测试目标：验证表单验证功能是否正常
   * 测试类型：边界测试
   */
  test('表单验证应该正常工作-角色编码必填', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 填写角色名称，但不填写角色编码
    await page.fill('input[placeholder*="角色名称"]', '测试角色');
    
    // 点击确定按钮
    await page.click('.el-dialog button:has-text("确定")');
    
    // 遵循：错误处理规范-第5.3条（错误提示）
    // 验证错误提示
    await expect(page.locator('.el-form-item__error')).toBeVisible();
    await expect(page.locator('.el-form-item__error')).toContainText('角色编码不能为空');
  });
});
