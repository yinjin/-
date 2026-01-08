import { test, expect } from '@playwright/test';

test.describe('用户管理功能 - 数据校验规则测试', () => {
  test.beforeEach(async ({ page, context }) => {
    // 模拟登录状态 - 设置localStorage
    await context.addInitScript(() => {
      localStorage.setItem('token', 'test-token');
      localStorage.setItem('userInfo', JSON.stringify({
        id: 1,
        username: 'admin',
        name: '管理员',
        email: 'admin@test.com',
        phone: '13800138000'
      }));
    });

    // 访问用户管理页面
    await page.goto('/users');
    
    // 等待页面加载完成
    await page.waitForLoadState('networkidle');
  });

  test('username字段校验 - 允许中文、字母、数字、下划线', async ({ page }) => {
    // 点击新增按钮
    await page.getByRole('button', { name: '新增用户' }).click();
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 输入包含中文的用户名（使用更具体的选择器，定位对话框内的输入框）
    await page.locator('.el-dialog input[placeholder="请输入用户名"]').fill('用户名123');
    
    // 触发验证
    await page.locator('.el-dialog input[placeholder="请输入密码"]').click();
    
    // 等待验证结果
    await page.waitForTimeout(500);
    
    // 检查是否没有错误提示
    const usernameError = await page.locator('.el-form-item__error').filter({ hasText: '用户名' }).count();
    expect(usernameError).toBe(0);
  });

  test('username字段校验 - 最小长度3', async ({ page }) => {
    // 点击新增按钮
    await page.getByRole('button', { name: '新增用户' }).click();
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 输入长度不足的用户名（使用更具体的选择器）
    await page.locator('.el-dialog input[placeholder="请输入用户名"]').fill('ab');
    
    // 触发验证
    await page.locator('.el-dialog input[placeholder="请输入密码"]').click();
    
    // 等待验证结果
    await page.waitForTimeout(500);
    
    // 检查错误提示
    const errorText = await page.locator('.el-form-item__error').filter({ hasText: '用户名长度' }).textContent();
    expect(errorText).toContain('用户名长度在 3 到 20 个字符');
  });

  test('username字段校验 - 最大长度20', async ({ page }) => {
    // 点击新增按钮
    await page.getByRole('button', { name: '新增用户' }).click();
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 输入超长用户名（使用更具体的选择器）
    await page.locator('.el-dialog input[placeholder="请输入用户名"]').fill('a'.repeat(21));
    
    // 触发验证
    await page.locator('.el-dialog input[placeholder="请输入密码"]').click();
    
    // 等待验证结果
    await page.waitForTimeout(500);
    
    // 检查错误提示
    const errorText = await page.locator('.el-form-item__error').filter({ hasText: '用户名长度' }).textContent();
    expect(errorText).toContain('用户名长度在 3 到 20 个字符');
  });

  test('username字段校验 - 不允许特殊字符', async ({ page }) => {
    // 点击新增按钮
    await page.getByRole('button', { name: '新增用户' }).click();
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 输入包含特殊字符的用户名（使用更具体的选择器）
    await page.locator('.el-dialog input[placeholder="请输入用户名"]').fill('user@name');
    
    // 触发验证
    await page.locator('.el-dialog input[placeholder="请输入密码"]').click();
    
    // 等待验证结果
    await page.waitForTimeout(500);
    
    // 检查错误提示
    const errorText = await page.locator('.el-form-item__error').filter({ hasText: '用户名只能包含' }).textContent();
    expect(errorText).toContain('用户名只能包含中文、字母、数字和下划线');
  });

  test('password字段校验 - 最小长度8', async ({ page }) => {
    // 点击新增按钮
    await page.getByRole('button', { name: '新增用户' }).click();
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 输入长度不足的密码（使用更具体的选择器）
    await page.locator('.el-dialog input[placeholder="请输入密码"]').fill('Abc123!');
    
    // 触发验证
    await page.locator('.el-dialog input[placeholder="请再次输入密码"]').click();
    
    // 等待验证结果
    await page.waitForTimeout(500);
    
    // 检查错误提示
    const errorText = await page.locator('.el-form-item__error').filter({ hasText: '密码长度' }).textContent();
    expect(errorText).toContain('密码长度必须在 8 到 20 个字符之间');
  });

  test('password字段校验 - 最大长度20', async ({ page }) => {
    // 点击新增按钮
    await page.getByRole('button', { name: '新增用户' }).click();
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 输入超长密码（使用更具体的选择器）
    await page.locator('.el-dialog input[placeholder="请输入密码"]').fill('Aa1!' + 'a'.repeat(20));
    
    // 触发验证
    await page.locator('.el-dialog input[placeholder="请再次输入密码"]').click();
    
    // 等待验证结果
    await page.waitForTimeout(500);
    
    // 检查错误提示
    const errorText = await page.locator('.el-form-item__error').filter({ hasText: '密码长度' }).textContent();
    expect(errorText).toContain('密码长度必须在 8 到 20 个字符之间');
  });

  test('password字段校验 - 必须包含大小写字母数字特殊字符', async ({ page }) => {
    // 点击新增按钮
    await page.getByRole('button', { name: '新增用户' }).click();
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 输入不符合复杂度要求的密码（使用更具体的选择器）
    await page.locator('.el-dialog input[placeholder="请输入密码"]').fill('password123');
    
    // 触发验证
    await page.locator('.el-dialog input[placeholder="请再次输入密码"]').click();
    
    // 等待验证结果
    await page.waitForTimeout(500);
    
    // 检查错误提示
    const errorText = await page.locator('.el-form-item__error').filter({ hasText: '密码必须包含' }).textContent();
    expect(errorText).toContain('密码必须包含至少一个大写字母、小写字母、数字和特殊字符');
  });

  test('name字段校验 - 允许中文、字母、数字、下划线', async ({ page }) => {
    // 点击新增按钮
    await page.getByRole('button', { name: '新增用户' }).click();
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 输入包含中文、字母、数字、下划线的姓名（使用更具体的选择器）
    await page.locator('.el-dialog input[placeholder="请输入姓名"]').fill('张三_123');
    
    // 触发验证
    await page.locator('.el-dialog input[placeholder="请输入邮箱"]').click();
    
    // 等待验证结果
    await page.waitForTimeout(500);
    
    // 检查是否没有错误提示
    const nameError = await page.locator('.el-form-item__error').filter({ hasText: '姓名' }).count();
    expect(nameError).toBe(0);
  });

  test('name字段校验 - 不允许特殊字符', async ({ page }) => {
    // 点击新增按钮
    await page.getByRole('button', { name: '新增用户' }).click();
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 输入包含特殊字符的姓名（使用更具体的选择器）
    await page.locator('.el-dialog input[placeholder="请输入姓名"]').fill('张三@123');
    
    // 触发验证
    await page.locator('.el-dialog input[placeholder="请输入邮箱"]').click();
    
    // 等待验证结果
    await page.waitForTimeout(500);
    
    // 检查错误提示
    const errorText = await page.locator('.el-form-item__error').filter({ hasText: '姓名只能包含' }).textContent();
    expect(errorText).toContain('姓名只能包含中文、字母、数字、下划线和空格');
  });

  test('name字段校验 - 最大长度50', async ({ page }) => {
    // 点击新增按钮
    await page.getByRole('button', { name: '新增用户' }).click();
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 输入超长姓名（使用更具体的选择器）
    await page.locator('.el-dialog input[placeholder="请输入姓名"]').fill('张'.repeat(51));
    
    // 触发验证
    await page.locator('.el-dialog input[placeholder="请输入邮箱"]').click();
    
    // 等待验证结果
    await page.waitForTimeout(500);
    
    // 检查错误提示
    const errorText = await page.locator('.el-form-item__error').filter({ hasText: '姓名长度' }).textContent();
    expect(errorText).toContain('姓名长度不能超过 50 个字符');
  });

  test('email字段校验 - 格式验证', async ({ page }) => {
    // 点击新增按钮
    await page.getByRole('button', { name: '新增用户' }).click();
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 输入格式错误的邮箱（使用更具体的选择器）
    await page.locator('.el-dialog input[placeholder="请输入邮箱"]').fill('invalid-email');
    
    // 触发验证
    await page.locator('.el-dialog input[placeholder="请输入手机号"]').click();
    
    // 等待验证结果
    await page.waitForTimeout(500);
    
    // 检查错误提示
    const errorText = await page.locator('.el-form-item__error').filter({ hasText: '邮箱' }).textContent();
    expect(errorText).toContain('请输入正确的邮箱格式');
  });

  test('phone字段校验 - 格式验证', async ({ page }) => {
    // 点击新增按钮
    await page.getByRole('button', { name: '新增用户' }).click();
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 输入格式错误的手机号（使用更具体的选择器）
    await page.locator('.el-dialog input[placeholder="请输入手机号"]').fill('1234567890');
    
    // 触发验证
    await page.locator('.el-dialog button:has-text("确定")').click();
    
    // 等待验证结果
    await page.waitForTimeout(500);
    
    // 检查错误提示
    const errorText = await page.locator('.el-form-item__error').filter({ hasText: '手机号' }).textContent();
    expect(errorText).toContain('请输入正确的手机号格式');
  });

  test('phone字段校验 - 必须以1开头', async ({ page }) => {
    // 点击新增按钮
    await page.getByRole('button', { name: '新增用户' }).click();
    
    // 等待对话框出现
    await page.waitForSelector('.el-dialog');
    
    // 输入不以1开头的手机号（使用更具体的选择器）
    await page.locator('.el-dialog input[placeholder="请输入手机号"]').fill('23456789012');
    
    // 触发验证
    await page.locator('.el-dialog button:has-text("确定")').click();
    
    // 等待验证结果
    await page.waitForTimeout(500);
    
    // 检查错误提示
    const errorText = await page.locator('.el-form-item__error').filter({ hasText: '手机号' }).textContent();
    expect(errorText).toContain('请输入正确的手机号格式');
  });

  test('编辑用户 - name字段允许中文、字母、数字、下划线', async ({ page }) => {
    // 等待表格加载
    await page.waitForSelector('.el-table');
    
    // 点击第一个编辑按钮
    const editButtons = await page.locator('button:has-text("编辑")').all();
    if (editButtons.length > 0) {
      await editButtons[0].click();
      
      // 等待对话框出现
      await page.waitForSelector('.el-dialog');
      
      // 输入包含中文、字母、数字、下划线的姓名（使用更具体的选择器）
      await page.locator('.el-dialog input[placeholder="请输入姓名"]').fill('李四_456');
      
      // 触发验证
      await page.locator('.el-dialog input[placeholder="请输入邮箱"]').click();
      
      // 等待验证结果
      await page.waitForTimeout(500);
      
      // 检查是否没有错误提示
      const nameError = await page.locator('.el-form-item__error').filter({ hasText: '姓名' }).count();
      expect(nameError).toBe(0);
    }
  });
});
