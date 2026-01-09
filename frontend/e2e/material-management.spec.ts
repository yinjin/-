import { test, expect } from '@playwright/test';

test.describe('耗材管理页面功能测试', () => {
  test.beforeEach(async ({ page }) => {
    // 访问登录页面
    await page.goto('http://localhost:5173');
    
    // 登录
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[placeholder="请输入密码"]', 'admin123');
    await page.click('button:has-text("登录")');
    
    // 等待登录成功，跳转到首页
    await page.waitForURL('http://localhost:5173/');
    await page.waitForTimeout(1000);
  });

  test('应该能够访问耗材管理页面', async ({ page }) => {
    // 点击耗材管理菜单
    await page.click('text=耗材管理');
    
    // 等待页面加载
    await page.waitForTimeout(1000);
    
    // 验证URL
    expect(page.url()).toContain('/materials');
    
    // 验证页面标题
    await expect(page.locator('h1')).toContainText('耗材管理');
  });

  test('应该显示耗材列表', async ({ page }) => {
    // 导航到耗材管理页面
    await page.goto('http://localhost:5173/materials');
    await page.waitForTimeout(1000);
    
    // 验证表格存在
    await expect(page.locator('table')).toBeVisible();
    
    // 验证表头
    await expect(page.locator('th').filter({ hasText: '耗材名称' })).toBeVisible();
    await expect(page.locator('th').filter({ hasText: '耗材编码' })).toBeVisible();
    await expect(page.locator('th').filter({ hasText: '分类' })).toBeVisible();
    await expect(page.locator('th').filter({ hasText: '品牌' })).toBeVisible();
    await expect(page.locator('th').filter({ hasText: '制造商' })).toBeVisible();
    await expect(page.locator('th').filter({ hasText: '状态' })).toBeVisible();
  });

  test('应该能够搜索耗材', async ({ page }) => {
    // 导航到耗材管理页面
    await page.goto('http://localhost:5173/materials');
    await page.waitForTimeout(1000);
    
    // 输入搜索条件
    await page.fill('input[placeholder="耗材名称"]', 'CPU');
    await page.click('button:has-text("查询")');
    
    // 等待搜索结果
    await page.waitForTimeout(1000);
    
    // 验证搜索结果
    const rows = await page.locator('tbody tr').count();
    expect(rows).toBeGreaterThan(0);
  });

  test('应该能够按分类筛选耗材', async ({ page }) => {
    // 导航到耗材管理页面
    await page.goto('http://localhost:5173/materials');
    await page.waitForTimeout(1000);
    
    // 点击分类选择器
    await page.click('.category-select .el-input__inner');
    
    // 等待下拉框显示
    await page.waitForTimeout(500);
    
    // 选择一个分类
    await page.click('text=硬件类');
    
    // 点击查询按钮
    await page.click('button:has-text("查询")');
    
    // 等待筛选结果
    await page.waitForTimeout(1000);
    
    // 验证筛选结果
    const rows = await page.locator('tbody tr').count();
    expect(rows).toBeGreaterThan(0);
  });

  test('应该能够打开新增耗材对话框', async ({ page }) => {
    // 导航到耗材管理页面
    await page.goto('http://localhost:5173/materials');
    await page.waitForTimeout(1000);
    
    // 点击新增按钮
    await page.click('button:has-text("新增耗材")');
    
    // 等待对话框显示
    await page.waitForTimeout(500);
    
    // 验证对话框标题
    await expect(page.locator('.el-dialog__title')).toContainText('新增耗材');
    
    // 验证表单字段
    await expect(page.locator('input[placeholder="请输入耗材名称"]')).toBeVisible();
    await expect(page.locator('input[placeholder="请输入耗材编码"]')).toBeVisible();
    await expect(page.locator('input[placeholder="请选择分类"]')).toBeVisible();
    await expect(page.locator('input[placeholder="请输入品牌"]')).toBeVisible();
    await expect(page.locator('input[placeholder="请输入制造商"]')).toBeVisible();
  });

  test('应该能够打开编辑耗材对话框', async ({ page }) => {
    // 导航到耗材管理页面
    await page.goto('http://localhost:5173/materials');
    await page.waitForTimeout(1000);
    
    // 点击第一个编辑按钮
    await page.click('tbody tr:first-child button:has-text("编辑")');
    
    // 等待对话框显示
    await page.waitForTimeout(500);
    
    // 验证对话框标题
    await expect(page.locator('.el-dialog__title')).toContainText('编辑耗材');
    
    // 验证表单字段有值
    const materialName = await page.locator('input[placeholder="请输入耗材名称"]').inputValue();
    expect(materialName).not.toBe('');
  });

  test('应该能够删除耗材', async ({ page }) => {
    // 导航到耗材管理页面
    await page.goto('http://localhost:5173/materials');
    await page.waitForTimeout(1000);
    
    // 获取删除前的行数
    const rowsBefore = await page.locator('tbody tr').count();
    
    // 点击第一个删除按钮
    await page.click('tbody tr:first-child button:has-text("删除")');
    
    // 等待确认对话框
    await page.waitForTimeout(500);
    
    // 点击确认按钮
    await page.click('.el-message-box__btns button:has-text("确定")');
    
    // 等待删除完成
    await page.waitForTimeout(1000);
    
    // 验证删除成功消息
    await expect(page.locator('.el-message')).toContainText('删除成功');
    
    // 验证行数减少
    const rowsAfter = await page.locator('tbody tr').count();
    expect(rowsAfter).toBe(rowsBefore - 1);
  });

  test('应该能够切换耗材状态', async ({ page }) => {
    // 导航到耗材管理页面
    await page.goto('http://localhost:5173/materials');
    await page.waitForTimeout(1000);
    
    // 获取第一个状态开关
    const statusSwitch = page.locator('tbody tr:first-child .el-switch');
    
    // 获取切换前的状态
    const isCheckedBefore = await statusSwitch.locator('.el-switch__input').isChecked();
    
    // 点击状态开关
    await statusSwitch.click();
    
    // 等待切换完成
    await page.waitForTimeout(1000);
    
    // 验证状态已切换
    const isCheckedAfter = await statusSwitch.locator('.el-switch__input').isChecked();
    expect(isCheckedAfter).not.toBe(isCheckedBefore);
  });

  test('应该能够分页浏览耗材', async ({ page }) => {
    // 导航到耗材管理页面
    await page.goto('http://localhost:5173/materials');
    await page.waitForTimeout(1000);
    
    // 验证分页组件存在
    await expect(page.locator('.el-pagination')).toBeVisible();
    
    // 点击下一页
    await page.click('.el-pagination .btn-next');
    
    // 等待页面加载
    await page.waitForTimeout(1000);
    
    // 验证页码已更新
    const currentPage = await page.locator('.el-pager .is-active').textContent();
    expect(currentPage).not.toBe('1');
  });
});
