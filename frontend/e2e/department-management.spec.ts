/**
 * 部门管理页面E2E测试
 * 
 * 遵循规范：
 * - E2E测试规范-第6.1条（使用Playwright框架）
 * - E2E测试规范-第6.2条（测试用例设计）
 * - E2E测试规范-第6.3条（测试执行规范）
 * 
 * @author haocai
 * @date 2026-01-08
 */

import { test, expect } from '@playwright/test';

test.describe('部门管理页面 - 功能测试', () => {
  test.beforeEach(async ({ page }) => {
    // 登录系统
    await page.goto('http://localhost:5173/login');
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[placeholder="请输入密码"]', 'admin123');
    await page.click('button:has-text("登录")');
    await page.waitForURL('http://localhost:5173/');
    
    // 导航到部门管理页面
    await page.click('text=系统设置');
    await page.waitForTimeout(500);
    await page.click('text=部门管理');
    await page.waitForURL('http://localhost:5173/departments');
    await page.waitForTimeout(1000);
  });

  test('应该显示部门管理页面', async ({ page }) => {
    // 验证页面标题
    await expect(page.locator('.el-card h3:has-text("部门树形结构")')).toBeVisible();
    await expect(page.locator('.list-header span:has-text("部门列表")')).toBeVisible();
  });

  test('应该显示部门树形结构', async ({ page }) => {
    // 验证树形结构存在
    await expect(page.locator('.el-tree')).toBeVisible();
    
    // 验证初始部门数据
    await expect(page.locator('.el-tree-node__content')).toContainText('高职人工智能学院');
  });

  test('应该能够新增顶级部门', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增部门")');
    
    // 等待弹窗出现
    await expect(page.locator('.el-dialog')).toBeVisible();
    
    // 填写部门信息
    await page.fill('input[placeholder="请输入部门名称"]', '测试部门');
    await page.fill('input[placeholder="请输入部门编码"]', 'TEST_DEPT');
    await page.fill('input[placeholder="请输入联系方式"]', '010-12345678');
    await page.fill('textarea[placeholder="请输入部门描述"]', '测试部门描述');
    
    // 点击确定
    await page.click('.el-dialog button:has-text("确定")');
    
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 验证新部门出现在列表中
    await expect(page.locator('.el-table__row')).toContainText('测试部门');
  });

  test('应该能够编辑部门', async ({ page }) => {
    // 找到测试部门并点击编辑
    await page.click('button:has-text("编辑").near(:text("测试部门"))');
    
    // 等待弹窗出现
    await expect(page.locator('.el-dialog')).toBeVisible();
    
    // 修改部门名称
    await page.fill('input[placeholder="请输入部门名称"]', '测试部门-已修改');
    
    // 点击确定
    await page.click('.el-dialog button:has-text("确定")');
    
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 验证修改后的名称
    await expect(page.locator('.el-table__row')).toContainText('测试部门-已修改');
  });

  test('应该能够禁用和启用部门', async ({ page }) => {
    // 找到测试部门
    const row = page.locator('.el-table__row:has-text("测试部门-已修改")');
    
    // 点击禁用按钮
    await row.locator('button:has-text("禁用")').click();
    
    // 确认操作
    await page.click('.el-message-box button:has-text("确定")');
    
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 验证状态变为禁用
    await expect(row.locator('.el-tag:has-text("禁用")')).toBeVisible();
    
    // 点击启用按钮
    await row.locator('button:has-text("启用")').click();
    
    // 确认操作
    await page.click('.el-message-box button:has-text("确定")');
    
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 验证状态变为正常
    await expect(row.locator('.el-tag:has-text("正常")')).toBeVisible();
  });

  test('应该能够新增子部门', async ({ page }) => {
    // 点击树形结构中测试部门旁边的"新增"按钮
    await page.locator('.el-tree-node:has-text("测试部门-已修改")').locator('button:has-text("新增")').click();
    
    // 等待弹窗出现
    await expect(page.locator('.el-dialog')).toBeVisible();
    
    // 验证父部门已自动填充
    // 填写子部门信息
    await page.fill('input[placeholder="请输入部门名称"]', '测试子部门');
    await page.fill('input[placeholder="请输入部门编码"]', 'TEST_CHILD');
    
    // 点击确定
    await page.click('.el-dialog button:has-text("确定")');
    
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 展开树形节点验证子部门
    await page.locator('.el-tree-node:has-text("测试部门-已修改") .el-icon:has-text("箭头")').click();
    await expect(page.locator('.el-tree-node')).toContainText('测试子部门');
  });

  test('应该能够删除没有子部门的部门', async ({ page }) => {
    // 找到测试子部门
    await page.locator('.el-tree-node:has-text("测试部门-已修改") .el-icon:has-text("箭头")').click();
    
    // 点击删除按钮
    await page.locator('.el-tree-node:has-text("测试子部门")').locator('button:has-text("删除")').click();
    
    // 等待删除确认对话框
    await expect(page.locator('.el-dialog:has-text("删除确认")')).toBeVisible();
    
    // 点击确定删除
    await page.click('.el-dialog button:has-text("确定删除")');
    
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
    
    // 验证子部门已被删除
    await expect(page.locator('.el-tree')).not.toContainText('测试子部门');
  });

  test('删除有子部门的部门应该提示错误', async ({ page }) => {
    // 找到测试部门-已修改
    await page.locator('.el-tree-node:has-text("测试部门-已修改")').locator('button:has-text("删除")').click();
    
    // 等待删除确认对话框
    await expect(page.locator('.el-dialog:has-text("删除确认")')).toBeVisible();
    
    // 验证提示信息
    await expect(page.locator('.el-alert--warning')).toContainText('该部门下存在');
    
    // 验证删除按钮被禁用
    await expect(page.locator('.el-dialog button:has-text("确定删除")')).toBeDisabled();
  });

  test('应该能够搜索部门', async ({ page }) => {
    // 在搜索框中输入关键词
    await page.fill('input[placeholder="请输入部门名称"]', '测试');
    
    // 点击搜索按钮
    await page.click('button:has-text("搜索")');
    
    // 等待搜索结果
    await page.waitForTimeout(1000);
    
    // 验证只显示包含"测试"的部门
    await expect(page.locator('.el-table__row')).toContainText('测试');
  });

  test('应该能够刷新树形结构', async ({ page }) => {
    // 点击刷新按钮
    await page.click('button:has-text("刷新树形结构")');
    
    // 验证成功提示
    await expect(page.locator('.el-message--success')).toBeVisible();
  });

  test('应该能够展开和收起全部', async ({ page }) => {
    // 点击展开全部
    await page.click('button:has-text("展开全部")');
    
    // 验证所有节点都已展开（显示所有部门）
    await expect(page.locator('.el-tree')).toContainText('软件工程教研室');
    
    // 点击收起全部
    await page.click('button:has-text("收起全部")');
    
    // 验证收起（可能只显示顶级部门）
  });

  test('部门编码重复应该提示错误', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增部门")');
    
    // 填写已存在的编码
    await page.fill('input[placeholder="请输入部门名称"]', '重复编码部门');
    await page.fill('input[placeholder="请输入部门编码"]', 'TEST_DEPT');
    
    // 点击确定
    await page.click('.el-dialog button:has-text("确定")');
    
    // 等待错误提示
    await expect(page.locator('.el-message--error')).toBeVisible();
  });

  test('必填字段验证', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增部门")');
    
    // 不填写任何内容直接点击确定
    await page.click('.el-dialog button:has-text("确定")');
    
    // 验证表单验证错误提示出现
    await expect(page.locator('.el-form-item__error')).toBeVisible();
  });
});

test.describe('部门管理页面 - 权限测试', () => {
  test.beforeEach(async ({ page }) => {
    // 先访问部门管理页面
    await page.goto('http://localhost:5173/departments');
    
    // 如果需要登录，跳转到登录页
    if (page.url().includes('/login')) {
      await page.fill('input[placeholder="请输入用户名"]', 'admin');
      await page.fill('input[placeholder="请输入密码"]', 'admin123');
      await page.click('button:has-text("登录")');
      await page.waitForURL('http://localhost:5173/departments');
    }
  });

  test('应该能够访问部门管理页面', async ({ page }) => {
    // 验证页面正常加载
    await expect(page.locator('.department-manage-container')).toBeVisible();
  });
});

test.describe('部门管理页面 - 边界测试', () => {
  test.beforeEach(async ({ page }) => {
    // 登录系统
    await page.goto('http://localhost:5173/login');
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[placeholder="请输入密码"]', 'admin123');
    await page.click('button:has-text("登录")');
    
    // 导航到部门管理页面
    await page.click('text=系统设置');
    await page.click('text=部门管理');
    await page.waitForURL('http://localhost:5173/departments');
  });

  test('部门名称长度限制验证', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增部门")');
    
    // 输入超过100个字符的部门名称
    await page.fill('input[placeholder="请输入部门名称"]', 'a'.repeat(101));
    
    // 验证字数统计显示
    await expect(page.locator('.el-input__count')).toContainText('101/100');
  });

  test('部门编码格式验证', async ({ page }) => {
    // 点击新增按钮
    await page.click('button:has-text("新增部门")');
    
    // 输入包含特殊字符的编码
    await page.fill('input[placeholder="请输入部门编码"]', 'test-dept!');
    
    // 点击确定
    await page.click('.el-dialog button:has-text("确定")');
    
    // 验证表单验证错误
    await expect(page.locator('.el-form-item__error')).toBeVisible();
  });

  test('分页功能测试', async ({ page }) => {
    // 切换每页显示数量
    await page.click('.el-pagination .el-select');
    await page.click('.el-popper .el-select-dropdown__item:has-text("20")');
    
    // 验证分页更新
    await expect(page.locator('.el-pagination')).toBeVisible();
  });
});
