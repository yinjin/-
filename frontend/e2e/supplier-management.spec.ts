/**
 * 供应商管理页面E2E测试
 * 
 * 遵循规范：
 * - E2E测试规范-第6.1条（使用Playwright框架）
 * - E2E测试规范-第6.2条（测试用例设计）
 * - E2E测试规范-第6.3条（测试执行规范）
 * 
 * 测试范围：
 * - 供应商列表页面功能测试
 * - 供应商表单页面功能测试
 * - 供应商详情页面功能测试
 * - 供应商评价功能测试
 * 
 * @author haocai
 * @date 2026-01-12
 */

import { test, expect } from '@playwright/test';

test.describe('供应商管理页面 - 功能测试', () => {
  test.beforeEach(async ({ page }) => {
    // 登录系统
    await page.goto('http://localhost:5173/login');
    await page.waitForLoadState('networkidle');
    
    // 等待登录表单加载
    await page.waitForSelector('input[placeholder="请输入用户名"]', { timeout: 10000 });
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[placeholder="请输入密码"]', 'admin123');
    
    // 点击登录按钮并等待
    await page.click('button:has-text("登录")');
    
    // 等待登录成功并跳转到首页
    await page.waitForURL('http://localhost:5173/', { timeout: 15000 });
    
    // 等待首页加载完成
    await page.waitForSelector('.home', { timeout: 10000 });
    
    // 验证token已保存到localStorage
    const token = await page.evaluate(() => localStorage.getItem('token'));
    console.log('登录后的token:', token ? '已获取' : '未获取');
    
    // 通过菜单导航到供应商管理页面
    await page.click('.menu-card:has-text("耗材管理")');
    await page.waitForTimeout(1000);
    await page.click('.system-menu-item:has-text("供应商管理")');
    await page.waitForURL('http://localhost:5173/suppliers', { timeout: 15000 });
    
    // 等待表格加载
    await page.waitForSelector('.el-table', { timeout: 10000 });
    await page.waitForTimeout(2000);
  });

  test('应该显示供应商管理页面', async ({ page }) => {
    // 验证页面容器存在
    await expect(page.locator('.supplier-manage-container')).toBeVisible({ timeout: 10000 });
  });

  test('应该显示供应商列表表格', async ({ page }) => {
    // 验证表格存在
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });
    
    // 验证表格列头
    await expect(page.locator('.el-table__header th')).toContainText(['供应商编码', '供应商名称', '联系人', '联系电话', '信用等级', '合作状态', '状态', '操作']);
  });

  test('应该能够新增供应商', async ({ page }) => {
    // 点击新增按钮 - 使用更精确的定位器
    await page.getByRole('button', { name: '新增供应商' }).click();
    
    // 等待弹窗出现 - 使用更长的超时
    await expect(page.locator('.el-dialog').first()).toBeVisible({ timeout: 15000 });
    
    // 填写供应商信息 - 使用更精确的表单定位器
    await page.locator('.el-dialog .el-form-item').first().locator('input').fill('测试供应商E2E');
    await page.locator('.el-dialog .el-form-item').nth(2).locator('input').fill('测试联系人');
    await page.locator('.el-dialog .el-form-item').nth(3).locator('input').fill('13800138000');
    await page.locator('.el-dialog .el-form-item').nth(4).locator('input').fill('test@e2e.com');
    await page.locator('.el-dialog .el-form-item').nth(5).locator('input').fill('北京市海淀区测试路1号');
    await page.locator('.el-dialog .el-form-item').nth(6).locator('input').fill('91110000E2ETEST');
    await page.locator('.el-dialog .el-form-item').nth(8).locator('input').fill('123456789012345');
    await page.locator('.el-dialog .el-form-item').nth(9).locator('input').fill('6222021234567890123');
    await page.locator('.el-dialog .el-form-item').nth(10).locator('input').fill('中国工商银行测试支行');
    
    // 点击确定
    await page.locator('.el-dialog__footer .el-button--primary').click();
    
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 15000 });
    
    // 验证新供应商出现在列表中
    await expect(page.locator('.el-table__row')).toContainText('测试供应商E2E', { timeout: 15000 });
  });

  test('应该能够编辑供应商', async ({ page }) => {
    // 找到包含测试供应商的行
    const row = page.locator('.el-table__row:has-text("测试供应商E2E")');
    await expect(row).toBeVisible({ timeout: 15000 });
    
    // 点击编辑按钮 - 使用更精确的定位器
    await row.getByRole('button', { name: '编辑' }).click();
    
    // 等待弹窗出现
    await expect(page.locator('.el-dialog').first()).toBeVisible({ timeout: 15000 });
    
    // 修改供应商名称
    await page.locator('.el-dialog .el-form-item').first().locator('input').fill('测试供应商E2E-已修改');
    
    // 点击确定
    await page.locator('.el-dialog__footer .el-button--primary').click();
    
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 15000 });
    
    // 验证修改后的名称
    await expect(page.locator('.el-table__row')).toContainText('测试供应商E2E-已修改', { timeout: 15000 });
  });

  test('应该能够切换供应商状态', async ({ page }) => {
    // 找到测试供应商
    const row = page.locator('.el-table__row:has-text("测试供应商E2E-已修改")');
    await expect(row).toBeVisible({ timeout: 15000 });
    
    // 点击禁用按钮 - 使用更精确的定位器
    await row.getByRole('button', { name: '禁用' }).click();
    
    // 确认操作
    await expect(page.locator('.el-message-box')).toBeVisible({ timeout: 5000 });
    await page.getByRole('button', { name: '确定' }).click();
    
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 15000 });
    
    // 验证状态变为禁用
    await expect(row.locator('.el-tag:has-text("禁用")')).toBeVisible({ timeout: 15000 });
    
    // 点击启用按钮
    await row.getByRole('button', { name: '启用' }).click();
    
    // 确认操作
    await expect(page.locator('.el-message-box')).toBeVisible({ timeout: 5000 });
    await page.getByRole('button', { name: '确定' }).click();
    
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 15000 });
    
    // 验证状态变为启用
    await expect(row.locator('.el-tag:has-text("启用")')).toBeVisible({ timeout: 15000 });
  });

  test('应该能够查看供应商详情', async ({ page }) => {
    // 找到包含测试供应商的行
    const row = page.locator('.el-table__row:has-text("测试供应商E2E-已修改")');
    await expect(row).toBeVisible({ timeout: 15000 });
    
    // 点击详情按钮 - 使用更精确的定位器
    await row.getByRole('button', { name: '详情' }).click();
    
    // 等待详情页面加载
    await page.waitForURL(/\/suppliers\/\d+/, { timeout: 15000 });
    await page.waitForTimeout(2000);
    
    // 验证详情页面显示
    await expect(page.locator('.supplier-detail-container')).toBeVisible({ timeout: 15000 });
    
    // 验证供应商信息显示
    await expect(page.locator('.supplier-detail-container')).toContainText('测试供应商E2E-已修改');
  });

  test('应该能够删除供应商（无关联数据）', async ({ page }) => {
    // 先返回列表页
    await page.goto('http://localhost:5173/suppliers');
    await page.waitForURL('http://localhost:5173/suppliers', { timeout: 15000 });
    await page.waitForTimeout(2000);
    
    // 找到测试供应商
    const row = page.locator('.el-table__row:has-text("测试供应商E2E-已修改")');
    await expect(row).toBeVisible({ timeout: 15000 });
    
    // 点击删除按钮 - 使用更精确的定位器
    await row.getByRole('button', { name: '删除' }).click();
    
    // 等待删除确认对话框
    await expect(page.locator('.el-message-box')).toBeVisible({ timeout: 5000 });
    
    // 点击确定删除
    await page.getByRole('button', { name: '确定' }).click();
    
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible({ timeout: 15000 });
    
    // 验证供应商已被删除
    await expect(page.locator('.el-table__row')).not.toContainText('测试供应商E2E-已修改', { timeout: 15000 });
  });

  test('应该能够搜索供应商', async ({ page }) => {
    // 在搜索框中输入关键词
    await page.getByPlaceholder('请输入供应商名称').fill('测试');
    
    // 点击搜索按钮
    await page.getByRole('button', { name: '搜索' }).click();
    
    // 等待搜索结果
    await page.waitForTimeout(2000);
    
    // 验证只显示包含"测试"的供应商
    const rows = page.locator('.el-table__row');
    const count = await rows.count();
    expect(count).toBeGreaterThan(0);
    for (let i = 0; i < count; i++) {
      await expect(rows.nth(i)).toContainText('测试');
    }
  });

  test('应该能够按状态筛选供应商', async ({ page }) => {
    // 点击状态筛选下拉框 - 使用更精确的定位器
    await page.getByRole('combobox', { name: '请选择状态' }).click();
    
    // 选择"启用"选项
    await page.getByRole('option', { name: '启用' }).click();
    
    // 等待筛选结果
    await page.waitForTimeout(2000);
    
    // 验证只显示启用状态的供应商
    const rows = page.locator('.el-table__row');
    const count = await rows.count();
    for (let i = 0; i < count; i++) {
      await expect(rows.nth(i)).toContainText('启用');
    }
  });

  test('应该能够按合作状态筛选供应商', async ({ page }) => {
    // 点击合作状态筛选下拉框 - 使用更精确的定位器
    await page.getByRole('combobox', { name: '请选择合作状态' }).click();
    
    // 选择"合作中"选项
    await page.getByRole('option', { name: '合作中' }).click();
    
    // 等待筛选结果
    await page.waitForTimeout(2000);
    
    // 验证只显示合作中的供应商
    const rows = page.locator('.el-table__row');
    const count = await rows.count();
    for (let i = 0; i < count; i++) {
      await expect(rows.nth(i)).toContainText('合作中');
    }
  });

  test('必填字段验证', async ({ page }) => {
    // 点击新增按钮 - 使用更精确的定位器
    await page.getByRole('button', { name: '新增供应商' }).click();
    
    // 等待弹窗出现
    await expect(page.locator('.el-dialog').first()).toBeVisible({ timeout: 15000 });
    
    // 不填写任何内容直接点击确定
    await page.locator('.el-dialog__footer .el-button--primary').click();
    
    // 验证表单验证错误提示出现
    await expect(page.locator('.el-form-item__error')).toBeVisible({ timeout: 15000 });
  });

  test('邮箱格式验证', async ({ page }) => {
    // 点击新增按钮 - 使用更精确的定位器
    await page.getByRole('button', { name: '新增供应商' }).click();
    
    // 等待弹窗出现
    await expect(page.locator('.el-dialog').first()).toBeVisible({ timeout: 15000 });
    
    // 输入错误的邮箱格式
    await page.locator('.el-dialog .el-form-item').nth(4).locator('input').fill('invalid-email');
    
    // 点击确定
    await page.locator('.el-dialog__footer .el-button--primary').click();
    
    // 验证邮箱格式错误提示
    await expect(page.locator('.el-form-item__error')).toContainText('邮箱', { timeout: 15000 });
  });

  test('联系电话格式验证', async ({ page }) => {
    // 点击新增按钮 - 使用更精确的定位器
    await page.getByRole('button', { name: '新增供应商' }).click();
    
    // 等待弹窗出现
    await expect(page.locator('.el-dialog').first()).toBeVisible({ timeout: 15000 });
    
    // 输入错误的电话格式
    await page.locator('.el-dialog .el-form-item').nth(3).locator('input').fill('123');
    
    // 点击确定
    await page.locator('.el-dialog__footer .el-button--primary').click();
    
    // 验证电话格式错误提示
    await expect(page.locator('.el-form-item__error')).toContainText('电话', { timeout: 15000 });
  });
});

test.describe('供应商管理页面 - 详情页测试', () => {
  test.beforeEach(async ({ page }) => {
    // 登录系统
    await page.goto('http://localhost:5173/login');
    await page.waitForLoadState('networkidle');
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[placeholder="请输入密码"]', 'admin123');
    await page.click('button:has-text("登录")');
    await page.waitForURL('http://localhost:5173/', { timeout: 10000 });
  });

  test('应该显示供应商详情信息', async ({ page }) => {
    // 导航到供应商详情页
    await page.goto('http://localhost:5173/suppliers/1');
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
    
    // 验证详情页面显示
    await expect(page.locator('.supplier-detail-container')).toBeVisible({ timeout: 10000 });
    
    // 验证基本信息显示
    await expect(page.locator('.info-cards')).toBeVisible();
  });

  test('应该显示供应商评价历史', async ({ page }) => {
    // 导航到供应商详情页
    await page.goto('http://localhost:5173/suppliers/1');
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
    
    // 点击评价历史标签
    await page.locator('.el-tabs__item:has-text("评价历史")').click();
    
    // 验证评价历史区域显示
    await expect(page.locator('.el-tabs__content')).toBeVisible({ timeout: 10000 });
  });

  test('应该能够从详情页编辑供应商', async ({ page }) => {
    // 导航到供应商详情页
    await page.goto('http://localhost:5173/suppliers/1');
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
    
    // 点击编辑按钮 - 使用更精确的定位器
    await page.getByRole('button', { name: '编辑供应商' }).click();
    
    // 等待编辑弹窗出现
    await expect(page.locator('.el-dialog').first()).toBeVisible({ timeout: 15000 });
    
    // 验证表单已填充数据
    await expect(page.locator('.el-dialog .el-form-item').first().locator('input')).not.toBeEmpty();
  });
});

test.describe('供应商管理页面 - 权限测试', () => {
  test.beforeEach(async ({ page }) => {
    // 先访问供应商管理页面
    await page.goto('http://localhost:5173/suppliers');
    await page.waitForLoadState('networkidle');
    
    // 如果需要登录，跳转到登录页
    if (page.url().includes('/login')) {
      await page.fill('input[placeholder="请输入用户名"]', 'admin');
      await page.fill('input[placeholder="请输入密码"]', 'admin123');
      await page.click('button:has-text("登录")');
      await page.waitForURL('http://localhost:5173/suppliers', { timeout: 10000 });
    }
    await page.waitForTimeout(2000);
  });

  test('应该能够访问供应商管理页面', async ({ page }) => {
    // 验证页面正常加载
    await expect(page.locator('.supplier-manage-container')).toBeVisible({ timeout: 10000 });
  });
});

test.describe('供应商管理页面 - 边界测试', () => {
  test.beforeEach(async ({ page }) => {
    // 登录系统
    await page.goto('http://localhost:5173/login');
    await page.waitForLoadState('networkidle');
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[placeholder="请输入密码"]', 'admin123');
    await page.click('button:has-text("登录")');
    await page.waitForURL('http://localhost:5173/', { timeout: 10000 });
    
    // 导航到供应商管理页面
    await page.click('.menu-card:has-text("耗材管理")');
    await page.waitForTimeout(500);
    await page.click('.system-menu-item:has-text("供应商管理")');
    await page.waitForURL('http://localhost:5173/suppliers', { timeout: 10000 });
  });

  test('分页功能测试', async ({ page }) => {
    // 切换每页显示数量
    await page.click('.el-pagination .el-select');
    await page.click('.el-popper .el-select-dropdown__item:has-text("20")');
    
    // 验证分页更新
    await expect(page.locator('.el-pagination')).toBeVisible();
  });

  test('供应商编码自动生成', async ({ page }) => {
    // 点击新增按钮 - 使用更精确的定位器
    await page.getByRole('button', { name: '新增供应商' }).click();
    
    // 等待弹窗出现
    await expect(page.locator('.el-dialog').first()).toBeVisible({ timeout: 15000 });
    
    // 验证供应商编码输入框
    const codeInput = page.locator('.el-dialog .el-form-item').nth(1).locator('input');
    await expect(codeInput).toBeVisible();
    
    // 点击生成编码按钮
    const generateButton = page.getByRole('button', { name: '生成编码' });
    if (await generateButton.isVisible()) {
      await generateButton.click();
      await page.waitForTimeout(500);
      
      // 验证编码已生成
      await expect(codeInput).not.toBeEmpty();
    }
  });
});
