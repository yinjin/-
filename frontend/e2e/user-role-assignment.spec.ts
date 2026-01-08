import { test, expect } from '@playwright/test';

/**
 * 用户角色分配功能测试
 * 
 * 测试范围：
 * 1. 用户列表中显示角色信息
 * 2. 用户角色分配功能
 * 3. 多角色分配
 * 4. 角色移除
 * 5. 角色清空
 * 6. 用户搜索
 * 7. 取消操作
 * 8. 角色标签显示
 * 
 * 遵循规范：
 * - E2E测试规范-第6.2条（测试用例设计）
 * - 前端交互规范-第5.2条（加载状态管理）
 * - 前端交互规范-第5.2条（用户反馈）
 * - 数据刷新规范-第5.2条（数据刷新）
 * - 错误处理规范-第5.3条（错误提示）
 */

test.describe('用户角色分配功能测试', () => {
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
    
    // 导航到用户管理页面
    // 遵循：E2E测试规范-第6.1条（选择器稳定性）
    // 使用更稳定的选择器定位用户管理卡片
    await page.locator('h3:has-text("用户管理")').click();
    await page.waitForURL('http://localhost:5175/users');
  });

  /**
   * 测试用例1：应该显示用户的角色信息
   * 
   * 测试目标：验证用户列表中是否正确显示角色信息
   * 测试类型：功能测试
   */
  test('应该显示用户的角色信息', async ({ page }) => {
    // 遵循：前端交互规范-第5.2条（加载状态管理）
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 验证角色列是否显示
    const roleColumn = page.locator('.el-table__header th').filter({ hasText: '角色' });
    await expect(roleColumn).toBeVisible();
    
    // 验证角色标签是否显示
    const roleTags = page.locator('.el-table__body .el-tag');
    const count = await roleTags.count();
    expect(count).toBeGreaterThan(0);
  });

  /**
   * 测试用例2：应该能够为用户分配角色
   * 
   * 测试目标：验证用户角色分配功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够为用户分配角色', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 点击第一行的编辑按钮
    await page.click('.el-table__body .el-table__row:first-child button:has-text("编辑")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 点击角色选择下拉框
    await page.click('.el-select');
    
    // 选择一个角色
    await page.click('text=教师');
    
    // 点击确定按钮
    await page.click('.el-dialog button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证用户列表已刷新
    await page.waitForSelector('.el-table__body .el-table__row');
    
    // 验证角色标签是否显示
    const roleTag = page.locator('.el-table__body .el-table__row:first-child .el-tag');
    await expect(roleTag).toBeVisible();
  });

  /**
   * 测试用例3：应该能够为用户分配多个角色
   * 
   * 测试目标：验证多角色分配功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够为用户分配多个角色', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 点击第一行的编辑按钮
    await page.click('.el-table__body .el-table__row:first-child button:has-text("编辑")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 点击角色选择下拉框
    await page.click('.el-select');
    
    // 选择多个角色
    await page.click('text=教师');
    await page.click('text=学生');
    
    // 点击确定按钮
    await page.click('.el-dialog button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证用户列表已刷新
    await page.waitForSelector('.el-table__body .el-table__row');
    
    // 验证多个角色标签是否显示
    const roleTags = page.locator('.el-table__body .el-table__row:first-child .el-tag');
    const count = await roleTags.count();
    expect(count).toBeGreaterThan(1);
  });

  /**
   * 测试用例4：应该能够移除用户的角色
   * 
   * 测试目标：验证角色移除功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够移除用户的角色', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 点击第一行的编辑按钮
    await page.click('.el-table__body .el-table__row:first-child button:has-text("编辑")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 点击角色选择下拉框
    await page.click('.el-select');
    
    // 取消选择角色
    await page.click('text=教师');
    
    // 点击确定按钮
    await page.click('.el-dialog button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证用户列表已刷新
    await page.waitForSelector('.el-table__body .el-table__row');
  });

  /**
   * 测试用例5：应该能够清空用户的所有角色
   * 
   * 测试目标：验证清空角色功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够清空用户的所有角色', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 点击第一行的编辑按钮
    await page.click('.el-table__body .el-table__row:first-child button:has-text("编辑")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 点击角色选择下拉框
    await page.click('.el-select');
    
    // 取消选择所有角色
    await page.click('text=教师');
    await page.click('text=学生');
    await page.click('text=管理员');
    
    // 点击确定按钮
    await page.click('.el-dialog button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证用户列表已刷新
    await page.waitForSelector('.el-table__body .el-table__row');
    
    // 验证角色标签是否不显示
    const roleTags = page.locator('.el-table__body .el-table__row:first-child .el-tag');
    const count = await roleTags.count();
    expect(count).toBe(0);
  });

  /**
   * 测试用例6：应该能够搜索用户并分配角色
   * 
   * 测试目标：验证用户搜索和角色分配功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够搜索用户并分配角色', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 输入搜索关键词
    await page.fill('input[placeholder*="用户名"]', 'admin');
    
    // 点击搜索按钮
    await page.click('button:has-text("搜索")');
    
    // 遵循：前端交互规范-第5.2条（加载状态管理）
    // 等待搜索结果加载
    await page.waitForSelector('.el-table__body .el-table__row');
    
    // 点击第一行的编辑按钮
    await page.click('.el-table__body .el-table__row:first-child button:has-text("编辑")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 点击角色选择下拉框
    await page.click('.el-select');
    
    // 选择一个角色
    await page.click('text=教师');
    
    // 点击确定按钮
    await page.click('.el-dialog button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
  });

  /**
   * 测试用例7：应该能够取消角色分配操作
   * 
   * 测试目标：验证取消操作功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够取消角色分配操作', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 点击第一行的编辑按钮
    await page.click('.el-table__body .el-table__row:first-child button:has-text("编辑")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 点击角色选择下拉框
    await page.click('.el-select');
    
    // 选择一个角色
    await page.click('text=教师');
    
    // 点击取消按钮
    await page.click('.el-dialog button:has-text("取消")');
    
    // 验证对话框是否关闭
    await expect(page.locator('.el-dialog')).not.toBeVisible();
    
    // 验证角色是否未分配
    const roleTags = page.locator('.el-table__body .el-table__row:first-child .el-tag');
    const count = await roleTags.count();
    expect(count).toBe(0);
  });

  /**
   * 测试用例8：应该能够查看用户的角色列表
   * 
   * 测试目标：验证用户角色列表查看功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够查看用户的角色列表', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 点击第一行的编辑按钮
    await page.click('.el-table__body .el-table__row:first-child button:has-text("编辑")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 验证角色选择下拉框是否显示
    const roleSelect = page.locator('.el-select');
    await expect(roleSelect).toBeVisible();
    
    // 点击角色选择下拉框
    await page.click('.el-select');
    
    // 验证角色选项是否显示
    await expect(page.locator('.el-select-dropdown')).toBeVisible();
    
    // 验证角色选项（管理员、教师、学生、仓库管理员）
    await expect(page.locator('text=管理员')).toBeVisible();
    await expect(page.locator('text=教师')).toBeVisible();
    await expect(page.locator('text=学生')).toBeVisible();
    await expect(page.locator('text=仓库管理员')).toBeVisible();
  });

  /**
   * 测试用例9：应该能够为不同用户分配不同角色
   * 
   * 测试目标：验证为不同用户分配不同角色的功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够为不同用户分配不同角色', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 为第一个用户分配教师角色
    await page.click('.el-table__body .el-table__row:nth-child(1) button:has-text("编辑")');
    await page.waitForSelector('.el-dialog');
    await page.click('.el-select');
    await page.click('text=教师');
    await page.click('.el-dialog button:has-text("确定")');
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 为第二个用户分配学生角色
    await page.click('.el-table__body .el-table__row:nth-child(2) button:has-text("编辑")');
    await page.waitForSelector('.el-dialog');
    await page.click('.el-select');
    await page.click('text=学生');
    await page.click('.el-dialog button:has-text("确定")');
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证用户列表已刷新
    await page.waitForSelector('.el-table__body .el-table__row');
    
    // 验证第一个用户的角色
    const firstUserRole = page.locator('.el-table__body .el-table__row:nth-child(1) .el-tag');
    await expect(firstUserRole).toContainText('教师');
    
    // 验证第二个用户的角色
    const secondUserRole = page.locator('.el-table__body .el-table__row:nth-child(2) .el-tag');
    await expect(secondUserRole).toContainText('学生');
  });

  /**
   * 测试用例10：应该显示无角色的用户
   * 
   * 测试目标：验证无角色用户的显示是否正常
   * 测试类型：功能测试
   */
  test('应该显示无角色的用户', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 查找没有角色标签的用户行
    const rows = page.locator('.el-table__body .el-table__row');
    const count = await rows.count();
    
    let foundNoRoleUser = false;
    for (let i = 0; i < count; i++) {
      const row = rows.nth(i);
      const roleTags = row.locator('.el-tag');
      const tagCount = await roleTags.count();
      
      if (tagCount === 0) {
        foundNoRoleUser = true;
        break;
      }
    }
    
    // 验证是否找到无角色的用户
    expect(foundNoRoleUser).toBe(true);
  });

  /**
   * 测试用例11：应该显示用户列表中的角色信息
   * 
   * 测试目标：验证用户列表中角色信息的显示是否正常
   * 测试类型：功能测试
   */
  test('应该显示用户列表中的角色信息', async ({ page }) => {
    // 遵循：前端交互规范-第5.2条（加载状态管理）
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 验证角色列是否显示
    const roleColumn = page.locator('.el-table__header th').filter({ hasText: '角色' });
    await expect(roleColumn).toBeVisible();
    
    // 验证角色标签是否显示
    const roleTags = page.locator('.el-table__body .el-tag');
    const count = await roleTags.count();
    expect(count).toBeGreaterThan(0);
    
    // 验证角色标签的样式
    const firstRoleTag = roleTags.first();
    await expect(firstRoleTag).toHaveClass(/el-tag/);
  });

  /**
   * 测试用例12：角色标签应该正确显示角色名称
   * 
   * 测试目标：验证角色标签是否正确显示角色名称
   * 测试类型：功能测试
   */
  test('角色标签应该正确显示角色名称', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 查找有角色标签的用户行
    const rows = page.locator('.el-table__body .el-table__row');
    const count = await rows.count();
    
    let foundRoleTag = false;
    for (let i = 0; i < count; i++) {
      const row = rows.nth(i);
      const roleTags = row.locator('.el-tag');
      const tagCount = await roleTags.count();
      
      if (tagCount > 0) {
        const firstTag = roleTags.first();
        const text = await firstTag.textContent();
        
        // 遵循：错误处理规范-第5.3条（空值检查）
        // 验证文本不为空
        if (!text) {
          continue;
        }
        
        // 验证角色名称是否在预定义的角色列表中
        const validRoles = ['管理员', '教师', '学生', '仓库管理员'];
        const isValidRole = validRoles.some(role => text.includes(role));
        
        if (isValidRole) {
          foundRoleTag = true;
          break;
        }
      }
    }
    
    // 验证是否找到有效的角色标签
    expect(foundRoleTag).toBe(true);
  });

  /**
   * 测试用例13：角色分配不应该在新增用户时显示
   * 
   * 测试目标：验证新增用户时角色分配功能是否不显示
   * 测试类型：功能测试
   */
  test('角色分配不应该在新增用户时显示', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 点击新增按钮
    await page.click('button:has-text("新增")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 验证角色选择下拉框是否不显示
    const roleSelect = page.locator('.el-select');
    const isVisible = await roleSelect.isVisible();
    expect(isVisible).toBe(false);
  });

  /**
   * 测试用例14：角色分配应该在编辑用户时可用
   * 
   * 测试目标：验证编辑用户时角色分配功能是否可用
   * 测试类型：功能测试
   */
  test('角色分配应该在编辑用户时可用', async ({ page }) => {
    // 等待表格加载完成
    await page.waitForSelector('.el-table');
    
    // 点击第一行的编辑按钮
    await page.click('.el-table__body .el-table__row:first-child button:has-text("编辑")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 验证角色选择下拉框是否显示
    const roleSelect = page.locator('.el-select');
    await expect(roleSelect).toBeVisible();
    
    // 验证角色选择下拉框是否可用
    const isDisabled = await roleSelect.isDisabled();
    expect(isDisabled).toBe(false);
  });
});
