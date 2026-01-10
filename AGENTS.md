# AGENTS.md - Development Guidelines

This document provides guidelines for AI agents working on the 高职人工智能学院实训耗材管理系统.

## Project Overview

- **Backend**: Spring Boot 3.1.6 + MyBatis-Plus + MySQL + Redis
- **Frontend**: Vue 3.4 + TypeScript 5.3 + Element Plus + Pinia
- **Test Framework**: Playwright (E2E) + JUnit/Spring Boot Test

## Build Commands

### Backend (Java/Spring Boot)

```bash
cd backend

# Build and run
mvn clean install && mvn spring-boot:run

# Run tests
mvn clean test

# Run single test class
mvn test -Dtest=UserServiceTest

# Run single test method
mvn test -Dtest=UserServiceTest#testCreateUser

# Check coverage (80% line, 70% branch)
mvn jacoco:check
```

### Frontend (Vue 3 + TypeScript)

```bash
cd frontend

npm install && npm run dev

# Type check + build
npm run build

# Lint with auto-fix
npm run lint

# E2E tests
npm run test:e2e
npx playwright test e2e/material-management.spec.ts
npx playwright test e2e/material-management.spec.ts --grep "搜索耗材"
```

## Code Style Guidelines

### Backend (Java)

**Naming**: Classes PascalCase, methods/variables camelCase, constants UPPER_SNAKE_CASE, packages lowercase, DB tables snake_case.

**Error Handling**:
- Use `BusinessException` for business errors (throw, don't return null)
- Use `@Transactional(rollbackFor = Exception.class)` for multi-table operations
- Controller must return `ApiResponse<T>` wrapper
- Use `@RestControllerAdvice` for global exception handling
- Validate inputs with `@Validated` + JSR-303 annotations

**Type Handling**:
- Enums stored as VARCHAR with enum name (e.g., `ACTIVE`)
- Must use custom `TypeHandler`: `@TableField(value = "status", typeHandler = UserStatusHandler.class)`

**Required Audit Fields**: `create_time`, `update_time`, `create_by`, `update_by`, `deleted`

**Unique Index + Logical Delete**:
```java
// Before insert with unique key: physically delete old records
lambdaQueryWrapper.eq(Entity::getUniqueCode, code);
// DO NOT add .eq(Entity::getDeleted, 0);
mapper.delete(lambdaQueryWrapper);
```

### Frontend (Vue 3 + TypeScript)

**Syntax**: Always use `<script setup>` Composition API.

**Import Order**: vue → vue-router/pinia → element-plus → utilities → custom imports.

**TypeScript**: Define interfaces for all API responses, use types not `any`.

**Async Operations**:
```typescript
try {
  loading.value = true
  const { data } = await apiGetList()
  ElMessage.success('操作成功')
  await fetchData() // Always refresh after CRUD
} catch (error) {
  ElMessage.error(error.response?.data?.message || '操作失败')
} finally {
  loading.value = false
}
```

## Directory Structure

```
backend/src/main/java/com/haocai/management/
├── config/     # Configuration classes
├── controller/ # REST API endpoints
├── service/    # Business logic
├── mapper/     # Data access layer
├── entity/     # MyBatis-Plus entities
├── dto/        # Data transfer objects
├── handler/    # TypeHandlers, MetaObjectHandlers
└── utils/      # Utility classes

frontend/src/
├── views/      # Page components (by feature)
├── components/ # Reusable components
├── api/        # API interface definitions
├── types/      # TypeScript types
├── store/      # Pinia stores
├── router/     # Vue Router
└── utils/      # Utility functions
```

## Testing Requirements

**Backend**: `@SpringBootTest` for integration tests, H2 in-memory database. Min coverage: 80% line, 70% branch.

**Frontend E2E**: Playwright with `@playwright/test`, auto-starts dev server. Chinese test descriptions. Include beforeEach login.

Example E2E test:
```typescript
import { test, expect } from '@playwright/test';

test.describe('功能模块', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:5173');
    await page.fill('input[placeholder="请输入用户名"]', 'admin');
    await page.fill('input[placeholder="请输入密码"]', 'admin123');
    await page.click('button:has-text("登录")');
    await page.waitForURL('http://localhost:5173/');
  });

  test('应该能够执行操作', async ({ page }) => { /* ... */ });
});
```

## Git Commit Conventions

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting
- `refactor`: Restructuring
- `test`: Test-related
- `chore`: Build/tool updates

## Environment Ports

- Backend: 8081
- Frontend: 5173
- MySQL: 3306
- Redis: 6379

## Common Pitfalls

1. **Enum + Logical Delete**: Physically delete before re-inserting with same unique key
2. **Missing Loading State**: Always show loading during async operations
3. **No Refresh After CRUD**: Always refresh data after create/update/delete
4. **TypeHandler Missing**: Register enum TypeHandler in MyBatis-Plus config
5. **Transaction Scope**: Use `@Transactional(rollbackFor = Exception.class)`

## IDE Recommendations

- **Backend**: IntelliJ IDEA 2023.3+ (install Lombok plugin)
- **Frontend**: VS Code 1.85+ with Volar, ESLint, Prettier extensions
