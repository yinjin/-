import { chromium } from 'playwright';

async function runIntegrationTest() {
  console.log('=== 前后端联调测试开始 ===\n');

  const browser = await chromium.launch({ headless: true });
  const context = await browser.newContext();
  const page = await context.newPage();

  // 收集控制台消息
  const consoleMessages = [];
  page.on('console', msg => {
    const text = msg.text();
    consoleMessages.push({ type: msg.type(), text });
  });

  page.on('pageerror', error => {
    console.log(`[PAGE ERROR] ${error.message}`);
  });

  try {
    // 1. 测试登录页面加载
    console.log('\n--- 测试1: 登录页面加载 ---');
    await page.goto('http://localhost:5174/login', { waitUntil: 'networkidle' });
    console.log(`页面标题: ${await page.title()}`);
    
    const usernameInput = await page.$('input[placeholder="请输入用户名"]');
    const passwordInput = await page.$('input[placeholder="请输入密码"]');
    console.log(`用户名输入框存在: ${usernameInput !== null}`);
    console.log(`密码输入框存在: ${passwordInput !== null}`);

    // 2. 测试登录功能
    console.log('\n--- 测试2: 登录功能 ---');
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[placeholder="请输入密码"]', 'admin123');
    
    // 监听登录响应
    const loginResponse = page.waitForResponse(response => 
      response.url().includes('/api/users/login')
    );
    await page.click('button:has-text("登录")');
    
    const loginRes = await loginResponse;
    const loginData = await loginRes.json();
    console.log(`登录API响应: ${loginRes.status()} - code=${loginData.code}`);
    
    if (loginData.data && loginData.data.token) {
      // 保存token到localStorage
      await page.evaluate((token) => localStorage.setItem('token', token), loginData.data.token);
      console.log('Token已保存到localStorage');
    }
    
    // 等待页面稳定
    await page.waitForTimeout(2000);
    
    // 手动导航到用户列表页面
    console.log('手动导航到用户列表页面...');
    await page.goto('http://localhost:5174/users', { waitUntil: 'networkidle' });
    await page.waitForTimeout(2000);
    console.log(`当前URL: ${page.url()}`);

    // 3. 测试用户列表页面
    console.log('\n--- 测试3: 用户列表页面 ---');
    await page.waitForSelector('.el-table', { timeout: 10000 });
    console.log('用户表格已加载');
    
    const userCount = await page.$$eval('.el-table .el-table__row', rows => rows.length);
    console.log(`当前用户数量: ${userCount}`);

    // 4. 测试新增用户功能
    console.log('\n--- 测试4: 新增用户功能 ---');
    const addButton = await page.$('button:has-text("新增用户")');
    if (addButton) {
      await addButton.click();
      await page.waitForSelector('.el-dialog', { timeout: 5000 });
      console.log('新增弹窗已打开');
      
      // 生成唯一用户名
      const timestamp = Date.now();
      const testUsername = `testuser${timestamp.toString().slice(-8)}`;
      
      // 填写表单 - 使用符合后端验证的格式
      await page.fill('input[placeholder="请输入用户名"]', testUsername);
      await page.fill('input[placeholder="请输入密码"]', 'Test123!');
      await page.fill('input[placeholder="请再次输入密码"]', 'Test123!');
      await page.fill('input[placeholder="请输入姓名"]', '测试用户');
      await page.fill('input[placeholder="请输入邮箱"]', `test${timestamp}@example.com`);
      await page.fill('input[placeholder="请输入手机号"]', `13800138${(timestamp % 100).toString().padStart(2, '0')}`);
      console.log(`表单已填写 - 用户名: ${testUsername}`);
      
      // 监听所有响应
      const allResponses = [];
      page.on('response', response => {
        if (response.url().includes('/api/users')) {
          allResponses.push({ url: response.url(), status: response.status() });
        }
      });
      
      // 点击确定按钮
      await page.click('.el-dialog .el-button--primary');
      console.log('已点击确定按钮');
      
      // 等待API完成
      await page.waitForTimeout(3000);
      
      // 检查响应
      const registerResponse = allResponses.find(r => r.url.includes('/register'));
      if (registerResponse) {
        console.log(`注册API响应: ${registerResponse.status}`);
      } else {
        console.log('未检测到注册API响应');
      }
      
      // 检查弹窗是否关闭
      const dialogVisible = await page.$('.el-dialog');
      console.log(`弹窗状态: ${dialogVisible ? '仍打开（可能验证失败或API失败）' : '已关闭（新增成功）'}`);
      
      // 检查页面是否有错误消息
      const errorMessages = await page.$$eval('.el-message--error', els => els.map(el => el.textContent));
      if (errorMessages.length > 0) {
        console.log(`错误消息: ${errorMessages.join(', ')}`);
      }
    } else {
      console.log('新增用户按钮未找到');
    }

    // 5. 测试用户列表API
    console.log('\n--- 测试5: 用户列表API ---');
    const userListResponse = page.waitForResponse(response => 
      response.url().includes('/api/users') && response.request().method() === 'GET'
    );
    await page.reload({ waitUntil: 'networkidle' });
    
    const userListRes = await userListResponse;
    const userListData = await userListRes.json();
    console.log(`用户列表API: ${userListRes.status()} - code=${userListData.code}, total=${userListData.data?.total || 0}`);

    // 6. 检查控制台错误
    console.log('\n--- 测试6: 控制台错误检查 ---');
    const errors = consoleMessages.filter(m => m.type === 'error');
    if (errors.length > 0) {
      console.log('发现控制台错误:');
      errors.forEach(e => console.log(`  - ${e.text}`));
    } else {
      console.log('没有发现控制台错误');
    }

    console.log('\n=== 前后端联调测试完成 ===');
    
  } catch (error) {
    console.error('\n测试过程中发生错误:', error.message);
  } finally {
    await browser.close();
  }
}

runIntegrationTest();
