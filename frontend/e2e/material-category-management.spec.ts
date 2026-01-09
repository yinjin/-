import { test, expect } from '@playwright/test';

test.describe('耗材分类管理页面功能测试', () => {
  test.beforeEach(async ({ page }) => {
    // 访问登录页面
    await page.goto('http://localhost:5173/login');
    
    // 登录
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[type="password"]', 'admin123');
    await page.click('.el-button--primary:has-text("登录")');
    
    // 等待跳转到首页
    await page.waitForURL('http://localhost:5173/', { timeout: 10000 });
    
    // 导航到耗材分类管理页面
    await page.goto('http://localhost:5173/material-categories');
    
    // 等待页面加载完成
    await page.waitForLoadState('networkidle');
  });

  test('应该显示耗材分类树', async ({ page }) => {
    // 检查页面标题
    await expect(page.locator('h1')).toContainText('耗材分类管理');
    
    // 检查搜索框是否存在
    await expect(page.locator('input[placeholder="请输入分类名称或编码"]')).toBeVisible();
    
    // 检查新增按钮是否存在
    await expect(page.locator('button:has-text("新增分类")')).toBeVisible();
    
    // 检查展开全部按钮是否存在
    await expect(page.locator('button:has-text("展开全部")')).toBeVisible();
    
    // 检查折叠全部按钮是否存在
    await expect(page.locator('button:has-text("折叠全部")')).toBeVisible();
    
    // 检查树形结构是否显示
    await expect(page.locator('.el-tree')).toBeVisible();
  });

  test('应该能够搜索分类', async ({ page }) => {
    // 输入搜索关键词
    await page.fill('input[placeholder="请输入分类名称"]', '电子');
    
    // 等待搜索结果
    await page.waitForTimeout(500);
    
    // 检查搜索结果是否显示
    const treeNodes = await page.locator('.el-tree-node__content').count();
    expect(treeNodes).toBeGreaterThan(0);
    
    // 重置搜索
    await page.click('button:has-text("重置")');
    await page.waitForTimeout(500);
  });

  test('应该能够展开全部分类', async ({ page }) => {
    // 点击展开全部按钮
    await page.click('button:has-text("展开全部")');
    await page.waitForTimeout(500);
    
    // 检查所有节点是否展开
    const expandedNodes = await page.locator('.el-tree-node.is-expanded').count();
    expect(expandedNodes).toBeGreaterThan(0);
  });

  test('应该能够折叠全部分类', async ({ page }) => {
    // 先展开全部
    await page.click('button:has-text("展开全部")');
    await page.waitForTimeout(500);
    
    // 点击折叠全部按钮
    await page.click('button:has-text("折叠全部")');
    await page.waitForTimeout(500);
    
    // 检查顶级节点是否仍然可见
    const topLevelNodes = await page.locator('.el-tree-node__content').count();
    expect(topLevelNodes).toBeGreaterThan(0);
  });

  test('应该能够新增顶级分类', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增分类")');
    
    // 等待对话框打开
    await expect(page.locator('.el-dialog')).toBeVisible();
    
    // 填写分类名称
    await page.fill('input[placeholder="请输入分类名称"]', '测试分类');
    
    // 填写分类编码
    await page.fill('input[placeholder="请输入分类编码（不填则自动生成）"]', 'TEST001');
    
    // 父分类保持默认值0（顶级分类）
    
    // 点击确定按钮
    await page.click('.el-dialog__footer button:has-text("确定")');
    
    // 等待操作完成
    await page.waitForTimeout(1000);
    
    // 检查是否显示成功消息
    await expect(page.locator('.el-message--success')).toBeVisible();
  });

  test('应该能够新增子分类', async ({ page }) => {
    // 先展开全部以查看所有节点
    await page.click('button:has-text("展开全部")');
    await page.waitForTimeout(500);
    
    // 找到第一个节点并点击新增子分类按钮
    const firstNode = page.locator('.el-tree-node__content').first();
    await firstNode.hover();
    await page.click('.el-tree-node__content .el-button:has-text("新增子分类")');
    
    // 等待对话框打开
    await expect(page.locator('.el-dialog')).toBeVisible();
    
    // 填写分类名称
    await page.fill('input[placeholder="请输入分类名称"]', '测试子分类');
    
    // 填写分类编码
    await page.fill('input[placeholder="请输入分类编码（不填则自动生成）"]', 'TESTSUB001');
    
    // 点击确定按钮
    await page.click('.el-dialog__footer button:has-text("确定")');
    
    // 等待操作完成
    await page.waitForTimeout(1000);
    
    // 检查是否显示成功消息
    await expect(page.locator('.el-message--success')).toBeVisible();
  });

  test('应该能够编辑分类', async ({ page }) => {
    // 先展开全部以查看所有节点
    await page.click('button:has-text("展开全部")');
    await page.waitForTimeout(500);
    
    // 找到第一个节点并点击编辑按钮
    const firstNode = page.locator('.el-tree-node__content').first();
    await firstNode.hover();
    await page.click('.el-tree-node__content .el-button:has-text("编辑")');
    
    // 等待对话框打开
    await expect(page.locator('.el-dialog')).toBeVisible();
    
    // 修改分类名称
    await page.fill('input[placeholder="请输入分类名称"]', '修改后的分类名称');
    
    // 点击确定按钮
    await page.click('.el-dialog__footer button:has-text("确定")');
    
    // 等待操作完成
    await page.waitForTimeout(1000);
    
    // 检查是否显示成功消息
    await expect(page.locator('.el-message--success')).toBeVisible();
  });

  test('应该能够切换分类状态', async ({ page }) => {
    // 先展开全部以查看所有节点
    await page.click('button:has-text("展开全部")');
    await page.waitForTimeout(500);
    
    // 找到第一个节点并点击状态切换按钮
    const firstNode = page.locator('.el-tree-node__content').first();
    await firstNode.hover();
    await page.click('.el-tree-node__content .el-button:has-text("禁用")');
    
    // 等待操作完成
    await page.waitForTimeout(1000);
    
    // 检查是否显示成功消息
    await expect(page.locator('.el-message--success')).toBeVisible();
  });

  test('应该能够删除分类', async ({ page }) => {
    // 先展开全部以查看所有节点
    await page.click('button:has-text("展开全部")');
    await page.waitForTimeout(500);
    
    // 找到第一个节点并点击删除按钮
    const firstNode = page.locator('.el-tree-node__content').first();
    await firstNode.hover();
    await page.click('.el-tree-node__content .el-button:has-text("删除")');
    
    // 等待确认对话框打开
    await expect(page.locator('.el-message-box')).toBeVisible();
    
    // 点击确认按钮
    await page.click('.el-message-box__btns button:has-text("确定")');
    
    // 等待操作完成
    await page.waitForTimeout(1000);
    
    // 检查是否显示成功消息
    await expect(page.locator('.el-message--success')).toBeVisible();
  });

  test('表单验证应该正常工作', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增分类")');
    
    // 等待对话框打开
    await expect(page.locator('.el-dialog')).toBeVisible();
    
    // 直接点击确定按钮，不填写任何内容
    await page.click('.el-dialog__footer button:has-text("确定")');
    
    // 检查是否显示验证错误
    await expect(page.locator('.el-form-item__error')).toBeVisible();
    
    // 关闭对话框
    await page.click('.el-dialog__headerbtn');
  });

  test('分类编码唯一性检查应该正常工作', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增分类")');
    
    // 等待对话框打开
    await expect(page.locator('.el-dialog')).toBeVisible();
    
    // 填写已存在的分类编码
    await page.fill('input[placeholder="请输入分类编码（不填则自动生成）"]', 'ELEC001');
    
    // 触发失焦事件以检查编码唯一性
    await page.click('input[placeholder="请输入分类名称"]');
    
    // 等待验证
    await page.waitForTimeout(500);
    
    // 检查是否显示编码已存在的错误
    const errorMessage = await page.locator('.error-tip').textContent();
    expect(errorMessage).toContain('该分类编码已存在');
    
    // 关闭对话框
    await page.click('.el-dialog__headerbtn');
  });

  test('应该能够查看分类详情', async ({ page }) => {
    // 先展开全部以查看所有节点
    await page.click('button:has-text("展开全部")');
    await page.waitForTimeout(500);
    
    // 找到第一个节点并点击查看详情按钮
    const firstNode = page.locator('.el-tree-node__content').first();
    await firstNode.hover();
    await page.click('.el-tree-node__content .el-button:has-text("编辑")');
    
    // 等待对话框打开
    await expect(page.locator('.el-dialog')).toBeVisible();
    
    // 检查对话框标题
    await expect(page.locator('.el-dialog__title')).toContainText('编辑分类');
    
    // 关闭对话框
    await page.click('.el-dialog__headerbtn');
  });
});
