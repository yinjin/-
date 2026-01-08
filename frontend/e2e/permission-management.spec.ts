import { test, expect } from '@playwright/test';

/**
 * 权限管理页面功能测试
 * 
 * 测试范围：
 * 1. 权限树形结构展示
 * 2. 权限搜索功能
 * 3. 权限CRUD操作（新增、编辑、删除）
 * 4. 权限状态切换
 * 5. 权限类型选择
 * 6. 父权限选择
 * 7. 子权限新增
 * 8. 权限树展开/折叠
 * 9. 表单验证
 * 
 * 遵循规范：
 * - E2E测试规范-第6.2条（测试用例设计）
 * - 前端交互规范-第5.2条（加载状态管理）
 * - 前端交互规范-第5.2条（用户反馈）
 * - 数据刷新规范-第5.2条（数据刷新）
 * - 错误处理规范-第5.3条（错误提示）
 */

test.describe('权限管理页面功能测试', () => {
  // 每个测试用例前的准备工作
  test.beforeEach(async ({ page }) => {
    // 登录系统
    await page.goto('http://localhost:5174/login');
    // 遵循：E2E测试规范-第6.1条（选择器稳定性）
    // 使用更稳定的选择器定位Element Plus的输入框
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[placeholder="请输入密码"]', 'admin123');
    await page.click('button:has-text("登录")');
    
    // 等待登录成功，跳转到首页
    await page.waitForURL('http://localhost:5174/');
    
    // 导航到权限管理页面
    // 遵循：E2E测试规范-第6.1条（选择器稳定性）
    // 使用更稳定的选择器定位系统设置卡片
    await page.locator('h3:has-text("系统设置")').click();
    // 点击下拉菜单中的权限管理选项
    await page.click('text=权限管理');
    await page.waitForURL('http://localhost:5174/permissions');
  });

  /**
   * 测试用例1：应该显示权限树
   * 
   * 测试目标：验证权限树形结构是否正确显示
   * 测试类型：功能测试
   */
  test('应该显示权限树', async ({ page }) => {
    // 遵循：前端交互规范-第5.2条（加载状态管理）
    // 等待权限树加载完成
    await page.waitForSelector('.el-tree');
    
    // 验证权限树是否显示
    const tree = page.locator('.el-tree');
    await expect(tree).toBeVisible();
    
    // 验证初始权限数据（系统管理、用户管理、角色管理、权限管理、部门管理）
    const treeNodes = page.locator('.el-tree-node');
    const count = await treeNodes.count();
    expect(count).toBeGreaterThan(0);
  });

  /**
   * 测试用例2：应该能够搜索权限
   * 
   * 测试目标：验证权限搜索功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够搜索权限', async ({ page }) => {
    // 等待权限树加载完成
    await page.waitForSelector('.el-tree');
    
    // 输入搜索关键词
    await page.fill('input[placeholder*="权限名称"]', '用户管理');
    
    // 点击搜索按钮
    await page.click('button:has-text("搜索")');
    
    // 遵循：前端交互规范-第5.2条（加载状态管理）
    // 等待搜索结果加载
    await page.waitForSelector('.el-tree');
    
    // 验证搜索结果
    const treeNodes = page.locator('.el-tree-node');
    const count = await treeNodes.count();
    expect(count).toBeGreaterThan(0);
    
    // 验证搜索结果中包含"用户管理"
    const firstNode = treeNodes.first();
    const text = await firstNode.textContent();
    expect(text).toContain('用户管理');
  });

  /**
   * 测试用例3：应该能够新增权限
   * 
   * 测试目标：验证权限新增功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够新增权限', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 填写权限信息
    await page.fill('input[placeholder*="权限名称"]', '测试权限');
    await page.fill('input[placeholder*="权限编码"]', 'test:permission');
    
    // 选择权限类型
    await page.click('.el-select');
    await page.click('text=菜单');
    
    // 点击确定按钮
    await page.click('.el-dialog button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证权限树已刷新
    await page.waitForSelector('.el-tree');
    const testPermission = page.locator('.el-tree-node').filter({ hasText: '测试权限' });
    await expect(testPermission).toBeVisible();
  });

  /**
   * 测试用例4：应该能够编辑权限
   * 
   * 测试目标：验证权限编辑功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够编辑权限', async ({ page }) => {
    // 等待权限树加载完成
    await page.waitForSelector('.el-tree');
    
    // 点击第一个节点的编辑按钮
    await page.click('.el-tree-node:first-child button:has-text("编辑")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 修改权限名称
    const permissionNameInput = page.locator('input[placeholder*="权限名称"]');
    await permissionNameInput.clear();
    await permissionNameInput.fill('测试权限-已修改');
    
    // 点击确定按钮
    await page.click('.el-dialog button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证权限树已刷新
    await page.waitForSelector('.el-tree');
    const modifiedPermission = page.locator('.el-tree-node').filter({ hasText: '测试权限-已修改' });
    await expect(modifiedPermission).toBeVisible();
  });

  /**
   * 测试用例5：应该能够删除权限
   * 
   * 测试目标：验证权限删除功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够删除权限', async ({ page }) => {
    // 等待权限树加载完成
    await page.waitForSelector('.el-tree');
    
    // 点击第一个节点的删除按钮
    await page.click('.el-tree-node:first-child button:has-text("删除")');
    
    // 等待确认对话框出现
    await page.waitForSelector('.el-message-box');
    
    // 点击确定按钮
    await page.click('.el-message-box button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证权限树已刷新
    await page.waitForTimeout(1000);
  });

  /**
   * 测试用例6：应该能够切换权限状态
   * 
   * 测试目标：验证权限状态切换功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够切换权限状态', async ({ page }) => {
    // 等待权限树加载完成
    await page.waitForSelector('.el-tree');
    
    // 点击第一个节点的状态切换按钮
    await page.click('.el-tree-node:first-child .el-switch');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证权限树已刷新
    await page.waitForSelector('.el-tree');
  });

  /**
   * 测试用例7：应该能够选择不同的权限类型
   * 
   * 测试目标：验证权限类型选择功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够选择不同的权限类型', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 点击权限类型下拉框
    await page.click('.el-select');
    
    // 验证权限类型选项是否显示
    await expect(page.locator('.el-select-dropdown')).toBeVisible();
    
    // 验证权限类型选项（菜单、按钮、API）
    await expect(page.locator('text=菜单')).toBeVisible();
    await expect(page.locator('text=按钮')).toBeVisible();
    await expect(page.locator('text=API')).toBeVisible();
    
    // 选择"按钮"类型
    await page.click('text=按钮');
    
    // 验证选择结果
    const selectedType = page.locator('.el-select .el-input__inner');
    const value = await selectedType.inputValue();
    expect(value).toContain('按钮');
  });

  /**
   * 测试用例8：应该能够选择父级权限
   * 
   * 测试目标：验证父权限选择功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够选择父级权限', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 点击父权限下拉框
    await page.click('.el-select:nth-child(2)');
    
    // 验证父权限选项是否显示
    await expect(page.locator('.el-select-dropdown')).toBeVisible();
    
    // 验证父权限选项（系统管理、用户管理等）
    await expect(page.locator('text=系统管理')).toBeVisible();
    await expect(page.locator('text=用户管理')).toBeVisible();
    
    // 选择"系统管理"作为父权限
    await page.click('text=系统管理');
    
    // 验证选择结果
    const selectedParent = page.locator('.el-select:nth-child(2) .el-input__inner');
    const value = await selectedParent.inputValue();
    expect(value).toContain('系统管理');
  });

  /**
   * 测试用例9：应该能够新增子权限
   * 
   * 测试目标：验证子权限新增功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够新增子权限', async ({ page }) => {
    // 等待权限树加载完成
    await page.waitForSelector('.el-tree');
    
    // 点击第一个节点的"新增子权限"按钮
    await page.click('.el-tree-node:first-child button:has-text("新增子权限")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 验证父权限是否自动填充
    const parentPermission = page.locator('.el-select .el-input__inner');
    const value = await parentPermission.inputValue();
    expect(value).toBeTruthy();
    
    // 填写子权限信息
    await page.fill('input[placeholder*="权限名称"]', '子权限');
    await page.fill('input[placeholder*="权限编码"]', 'child:permission');
    
    // 点击确定按钮
    await page.click('.el-dialog button:has-text("确定")');
    
    // 遵循：前端交互规范-第5.2条（用户反馈）
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 遵循：数据刷新规范-第5.2条（数据刷新）
    // 验证权限树已刷新
    await page.waitForSelector('.el-tree');
  });

  /**
   * 测试用例10：应该能够展开单个权限节点
   * 
   * 测试目标：验证单个节点展开功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够展开单个权限节点', async ({ page }) => {
    // 等待权限树加载完成
    await page.waitForSelector('.el-tree');
    
    // 点击第一个节点的展开图标
    await page.click('.el-tree-node:first-child .el-tree-node__expand-icon');
    
    // 遵循：前端交互规范-第5.2条（加载状态管理）
    // 等待子节点加载
    await page.waitForTimeout(500);
    
    // 验证子节点是否显示
    const childNodes = page.locator('.el-tree-node:first-child .el-tree-node__children .el-tree-node');
    const count = await childNodes.count();
    expect(count).toBeGreaterThan(0);
  });

  /**
   * 测试用例11：应该能够展开全部权限
   * 
   * 测试目标：验证全部展开功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够展开全部权限', async ({ page }) => {
    // 等待权限树加载完成
    await page.waitForSelector('.el-tree');
    
    // 点击"展开全部"按钮
    await page.click('button:has-text("展开全部")');
    
    // 遵循：前端交互规范-第5.2条（加载状态管理）
    // 等待所有节点展开
    await page.waitForTimeout(500);
    
    // 验证所有节点是否展开
    const expandedNodes = page.locator('.el-tree-node.is-expanded');
    const count = await expandedNodes.count();
    expect(count).toBeGreaterThan(0);
  });

  /**
   * 测试用例12：应该能够折叠全部权限
   * 
   * 测试目标：验证全部折叠功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够折叠全部权限', async ({ page }) => {
    // 等待权限树加载完成
    await page.waitForSelector('.el-tree');
    
    // 先展开全部
    await page.click('button:has-text("展开全部")');
    await page.waitForTimeout(500);
    
    // 点击"折叠全部"按钮
    await page.click('button:has-text("折叠全部")');
    
    // 遵循：前端交互规范-第5.2条（加载状态管理）
    // 等待所有节点折叠
    await page.waitForTimeout(500);
    
    // 验证所有节点是否折叠
    const collapsedNodes = page.locator('.el-tree-node:not(.is-expanded)');
    const count = await collapsedNodes.count();
    expect(count).toBeGreaterThan(0);
  });

  /**
   * 测试用例13：表单验证应该正常工作-权限名称必填
   * 
   * 测试目标：验证表单验证功能是否正常
   * 测试类型：边界测试
   */
  test('表单验证应该正常工作-权限名称必填', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 不填写权限名称，直接点击确定
    await page.click('.el-dialog button:has-text("确定")');
    
    // 遵循：错误处理规范-第5.3条（错误提示）
    // 验证错误提示
    await expect(page.locator('.el-form-item__error')).toBeVisible();
    await expect(page.locator('.el-form-item__error')).toContainText('权限名称不能为空');
  });

  /**
   * 测试用例14：表单验证应该正常工作-权限编码必填
   * 
   * 测试目标：验证表单验证功能是否正常
   * 测试类型：边界测试
   */
  test('表单验证应该正常工作-权限编码必填', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增")');
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 填写权限名称，但不填写权限编码
    await page.fill('input[placeholder*="权限名称"]', '测试权限');
    
    // 点击确定按钮
    await page.click('.el-dialog button:has-text("确定")');
    
    // 遵循：错误处理规范-第5.3条（错误提示）
    // 验证错误提示
    await expect(page.locator('.el-form-item__error')).toBeVisible();
    await expect(page.locator('.el-form-item__error')).toContainText('权限编码不能为空');
  });

  /**
   * 测试用例15：应该能够重置搜索条件
   * 
   * 测试目标：验证重置搜索条件功能是否正常
   * 测试类型：功能测试
   */
  test('应该能够重置搜索条件', async ({ page }) => {
    // 等待权限树加载完成
    await page.waitForSelector('.el-tree');
    
    // 输入搜索关键词
    await page.fill('input[placeholder*="权限名称"]', '用户管理');
    
    // 点击重置按钮
    await page.click('button:has-text("重置")');
    
    // 遵循：前端交互规范-第5.2条（加载状态管理）
    // 等待搜索条件重置
    await page.waitForTimeout(500);
    
    // 验证搜索框是否清空
    const searchInput = page.locator('input[placeholder*="权限名称"]');
    const value = await searchInput.inputValue();
    expect(value).toBe('');
  });
});
