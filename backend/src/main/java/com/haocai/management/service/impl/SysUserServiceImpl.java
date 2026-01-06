package com.haocai.management.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.dto.UserLoginDTO;
import com.haocai.management.dto.UserRegisterDTO;
import com.haocai.management.dto.UserUpdateDTO;
import com.haocai.management.entity.SysUser;
import com.haocai.management.entity.SysUserLoginLog;
import com.haocai.management.entity.UserStatus;
import com.haocai.management.exception.BusinessException;
import com.haocai.management.entity.SysRole;
import com.haocai.management.entity.SysUserRole;
import com.haocai.management.mapper.SysRoleMapper;
import com.haocai.management.mapper.SysUserLoginLogMapper;
import com.haocai.management.mapper.SysUserMapper;
import com.haocai.management.mapper.SysUserRoleMapper;
import com.haocai.management.service.ISysUserService;
import com.haocai.management.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户业务逻辑实现类
 * 实现用户管理的所有业务逻辑，包括注册、登录、信息管理等
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserLoginLogMapper loginLogMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    @Lazy
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public SysUser register(UserRegisterDTO registerDTO) {
        log.info("开始用户注册，用户名: {}", registerDTO.getUsername());

        // 1. 检查用户名是否已存在
        if (existsByUsername(registerDTO.getUsername())) {
            throw BusinessException.usernameExists();
        }

        // 2. 检查邮箱是否已存在
        if (StringUtils.hasText(registerDTO.getEmail()) && existsByEmail(registerDTO.getEmail(), null)) {
            throw BusinessException.emailExists();
        }

        // 3. 检查手机号是否已存在
        if (StringUtils.hasText(registerDTO.getPhone()) && existsByPhone(registerDTO.getPhone(), null)) {
            throw BusinessException.phoneExists();
        }

        // 4. 创建用户对象
        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword())); // 密码加密
        user.setName(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setAvatar(registerDTO.getAvatar());
        user.setDepartmentId(registerDTO.getDepartmentId());
        user.setStatus(UserStatus.NORMAL); // 新注册用户默认为正常状态
        user.setDeleted(0); // 未删除
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 5. 保存用户
        int result = sysUserMapper.insert(user);
        if (result <= 0) {
            log.error("用户注册失败，插入数据库失败，用户名: {}", registerDTO.getUsername());
            throw BusinessException.operationFailed("用户注册失败");
        }

        log.info("用户注册成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
        return user;
    }

    @Override
    public String login(UserLoginDTO loginDTO) {
        log.info("开始用户登录，用户名: {}", loginDTO.getUsername());

        try {
            // 遵循：安全规范-使用AuthenticationManager进行认证
            // 1. 使用Spring Security的AuthenticationManager进行认证
            // 这会自动调用UserDetailsServiceImpl加载用户信息并验证密码
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDTO.getUsername(),
                    loginDTO.getPassword()
                )
            );

            // 2. 认证成功后，从Authentication对象中获取用户信息
            // 注意：这里使用的是Spring Security的UserDetails，不是我们的SysUser
            Object principal = authentication.getPrincipal();
            log.debug("认证成功，principal类型: {}", principal.getClass().getName());

            // 3. 重新查询用户信息以获取完整的SysUser对象
            SysUser user = findByUsername(loginDTO.getUsername());
            if (user == null) {
                log.error("认证成功但未找到用户信息，用户名: {}", loginDTO.getUsername());
                throw BusinessException.userNotFound();
            }

            // 4. 检查用户状态（虽然AuthenticationManager已经检查过，但再次确认）
            if (user.getStatus() == UserStatus.DISABLED) {
                log.warn("用户登录失败，用户已被禁用，用户名: {}", loginDTO.getUsername());
                recordLoginLog(user.getId(), loginDTO.getIpAddress(), false, "用户已被禁用");
                throw BusinessException.userDisabled();
            }

            if (user.getStatus() == UserStatus.LOCKED) {
                log.warn("用户登录失败，用户已被锁定，用户名: {}", loginDTO.getUsername());
                recordLoginLog(user.getId(), loginDTO.getIpAddress(), false, "用户已被锁定");
                throw BusinessException.userLocked();
            }

            // 5. 更新最后登录时间
            updateLastLoginTime(user.getId());

            // 6. 记录登录成功日志
            recordLoginLog(user.getId(), loginDTO.getIpAddress(), true, null);

            // 7. 生成JWT token
            // 遵循：安全规范-使用JWT工具类生成token
            // 遵循：配置规范-从配置文件读取过期时间
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("username", user.getUsername());
            claims.put("name", user.getName());
            
            String token = jwtUtils.generateToken(claims, null);
            
            log.info("用户登录成功，用户名: {}, 用户ID: {}", user.getUsername(), user.getId());
            return token;

        } catch (AuthenticationException e) {
            // 遵循：异常处理规范-完善的异常处理
            // 认证失败（用户名或密码错误）
            log.warn("用户登录失败，认证失败，用户名: {}, 原因: {}", 
                    loginDTO.getUsername(), e.getMessage());
            
            // 尝试获取用户ID以记录日志
            SysUser user = findByUsername(loginDTO.getUsername());
            Long userId = user != null ? user.getId() : null;
            
            recordLoginLog(userId, loginDTO.getIpAddress(), false, "用户名或密码错误");
            throw BusinessException.passwordError();
            
        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
            
        } catch (Exception e) {
            // 其他异常
            log.error("用户登录失败，系统异常，用户名: {}", loginDTO.getUsername(), e);
            throw BusinessException.operationFailed("登录失败: " + e.getMessage());
        }
    }

    @Override
    public SysUser findByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }

    @Override
    public SysUser findById(Long userId) {
        return sysUserMapper.selectById(userId);
    }

    @Override
    @Transactional
    public SysUser updateUser(Long userId, UserUpdateDTO updateDTO) {
        log.info("开始更新用户信息，用户ID: {}", userId);

        // 1. 检查用户是否存在
        SysUser existingUser = findById(userId);
        if (existingUser == null) {
            throw BusinessException.userNotFound();
        }

        // 2. 检查邮箱唯一性（如果提供了邮箱）
        if (StringUtils.hasText(updateDTO.getEmail()) &&
            !updateDTO.getEmail().equals(existingUser.getEmail()) &&
            existsByEmail(updateDTO.getEmail(), userId)) {
            throw BusinessException.emailExists();
        }

        // 3. 检查手机号唯一性（如果提供了手机号）
        if (StringUtils.hasText(updateDTO.getPhone()) &&
            !updateDTO.getPhone().equals(existingUser.getPhone()) &&
            existsByPhone(updateDTO.getPhone(), userId)) {
            throw BusinessException.phoneExists();
        }

        // 4. 更新用户信息
        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setName(updateDTO.getName());
        updateUser.setEmail(updateDTO.getEmail());
        updateUser.setPhone(updateDTO.getPhone());
        updateUser.setAvatar(updateDTO.getAvatar());
        updateUser.setDepartmentId(updateDTO.getDepartmentId());
        updateUser.setUpdateTime(LocalDateTime.now());

        int result = sysUserMapper.updateById(updateUser);
        if (result <= 0) {
            log.error("更新用户信息失败，用户ID: {}", userId);
            throw BusinessException.operationFailed("更新用户信息失败");
        }

        // 5. 返回更新后的用户信息
        SysUser updatedUser = findById(userId);
        log.info("用户信息更新成功，用户ID: {}", userId);
        return updatedUser;
    }

    @Override
    @Transactional
    public boolean updateUserStatus(Long userId, UserStatus status, Long updateBy) {
        log.info("开始更新用户状态，用户ID: {}, 新状态: {}, 操作人: {}", userId, status, updateBy);

        // 1. 检查用户是否存在
        SysUser user = findById(userId);
        if (user == null) {
            throw BusinessException.userNotFound();
        }

        // 2. 使用MyBatis-Plus的UpdateWrapper更新状态
        SysUser updateUser = new SysUser();
        updateUser.setStatus(status);
        updateUser.setUpdateTime(LocalDateTime.now());
        updateUser.setUpdateBy(updateBy);

        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<SysUser> updateWrapper = 
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        updateWrapper.eq("id", userId);
        updateWrapper.eq("deleted", 0);

        int result = sysUserMapper.update(updateUser, updateWrapper);
        if (result <= 0) {
            log.error("更新用户状态失败，用户ID: {}", userId);
            return false;
        }

        log.info("用户状态更新成功，用户ID: {}, 新状态: {}", userId, status);
        return true;
    }

    @Override
    public IPage<SysUser> findUserPage(Page<SysUser> page, String username, String name,
                                     UserStatus status, Long departmentId) {
        return sysUserMapper.selectUserPage(page, username, name,
                                          status != null ? status.getCode() : null, departmentId);
    }

    @Override
    @Transactional
    public int batchUpdateStatus(List<Long> userIds, UserStatus status, Long updateBy) {
        log.info("开始批量更新用户状态，用户数量: {}, 新状态: {}, 操作人: {}", userIds.size(), status, updateBy);

        if (userIds == null || userIds.isEmpty()) {
            return 0;
        }

        // 使用MyBatis-Plus的UpdateWrapper批量更新状态
        SysUser updateUser = new SysUser();
        updateUser.setStatus(status);
        updateUser.setUpdateTime(LocalDateTime.now());
        updateUser.setUpdateBy(updateBy);

        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<SysUser> updateWrapper = 
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        updateWrapper.in("id", userIds);
        updateWrapper.eq("deleted", 0);

        int result = sysUserMapper.update(updateUser, updateWrapper);
        log.info("批量更新用户状态完成，影响用户数量: {}", result);
        return result;
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId, Long deleteBy) {
        log.info("开始删除用户，用户ID: {}, 操作人: {}", userId, deleteBy);

        // 1. 检查用户是否存在
        SysUser user = findById(userId);
        if (user == null) {
            throw BusinessException.userNotFound();
        }

        // 2. 使用MyBatis-Plus的逻辑删除方法
        int result = sysUserMapper.deleteById(userId);
        if (result <= 0) {
            log.error("删除用户失败，用户ID: {}", userId);
            return false;
        }

        log.info("用户删除成功，用户ID: {}", userId);
        return true;
    }

    @Override
    @Transactional
    public int batchDeleteUsers(List<Long> userIds, Long deleteBy) {
        log.info("开始批量删除用户，用户数量: {}, 操作人: {}", userIds.size(), deleteBy);

        if (userIds == null || userIds.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Long userId : userIds) {
            try {
                if (deleteUser(userId, deleteBy)) {
                    count++;
                }
            } catch (BusinessException e) {
                // 跳过不存在的用户，继续处理其他用户
                log.warn("批量删除用户时跳过不存在的用户ID: {}", userId);
            }
        }

        log.info("批量删除用户完成，成功删除用户数量: {}", count);
        return count;
    }

    @Override
    public boolean existsByUsername(String username) {
        return sysUserMapper.selectByUsername(username) != null;
    }

    @Override
    public boolean existsByEmail(String email, Long excludeUserId) {
        SysUser user = sysUserMapper.selectByEmail(email);
        if (user == null) {
            return false;
        }
        return excludeUserId == null || !user.getId().equals(excludeUserId);
    }

    @Override
    public boolean existsByPhone(String phone, Long excludeUserId) {
        SysUser user = sysUserMapper.selectByPhone(phone);
        if (user == null) {
            return false;
        }
        return excludeUserId == null || !user.getId().equals(excludeUserId);
    }

    @Override
    public void recordLoginLog(Long userId, String loginIp, boolean success, String failReason) {
        try {
            // 如果userId为null，说明用户不存在，不记录登录日志
            if (userId == null) {
                log.warn("用户ID为null，跳过记录登录日志");
                return;
            }
            
            SysUserLoginLog loginLog = new SysUserLoginLog();
            loginLog.setUserId(userId);
            
            SysUser user = findById(userId);
            if (user != null) {
                loginLog.setUsername(user.getUsername());
            }
            loginLog.setLoginIp(loginIp);
            loginLog.setLoginTime(LocalDateTime.now());
            loginLog.setLoginSuccess(success);
            loginLog.setFailReason(failReason);

            loginLogMapper.insert(loginLog);
        } catch (Exception e) {
            log.error("记录登录日志失败: {}", e.getMessage(), e);
            // 登录日志记录失败不应该影响正常业务流程
        }
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        try {
            SysUser updateUser = new SysUser();
            updateUser.setId(userId);
            updateUser.setLastLoginTime(LocalDateTime.now());
            updateUser.setUpdateTime(LocalDateTime.now());

            sysUserMapper.updateById(updateUser);
        } catch (Exception e) {
            log.error("更新用户最后登录时间失败，用户ID: {}", userId, e);
            // 更新登录时间失败不应该影响登录流程
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolesToUser(Long userId, List<Long> roleIds, Long operatorId) {
        // 遵循：开发规范-第6条（参数校验）
        if (userId == null) {
            throw new BusinessException(1009, "用户ID不能为空");
        }
        if (CollectionUtils.isEmpty(roleIds)) {
            throw new BusinessException(1009, "角色ID列表不能为空");
        }
        
        // 检查用户是否存在
        SysUser user = findById(userId);
        if (user == null) {
            throw new BusinessException(1010, "用户不存在，ID：" + userId);
        }
        
        // 检查所有角色是否存在
        List<SysRole> roles = roleMapper.selectBatchIds(roleIds);
        if (roles.size() != roleIds.size()) {
            throw new BusinessException(1010, "部分角色不存在");
        }
        
        // 删除用户原有的所有角色
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUserRole> deleteWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        deleteWrapper.eq(SysUserRole::getUserId, userId);
        userRoleMapper.delete(deleteWrapper);
        
        // 批量插入新的用户角色关联
        List<SysUserRole> userRoles = new ArrayList<>();
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreateBy(operatorId);
            userRoles.add(userRole);
        }
        
        for (SysUserRole userRole : userRoles) {
            userRoleMapper.insert(userRole);
        }
        
        log.info("给用户分配角色成功，用户ID：{}，角色数量：{}", userId, roleIds.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRolesFromUser(Long userId, List<Long> roleIds, Long operatorId) {
        // 遵循：开发规范-第6条（参数校验）
        if (userId == null) {
            throw new BusinessException(1009, "用户ID不能为空");
        }
        if (CollectionUtils.isEmpty(roleIds)) {
            throw new BusinessException(1009, "角色ID列表不能为空");
        }
        
        // 检查用户是否存在
        SysUser user = findById(userId);
        if (user == null) {
            throw new BusinessException(1010, "用户不存在，ID：" + userId);
        }
        
        // 删除指定的用户角色关联
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUserRole> deleteWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        deleteWrapper.eq(SysUserRole::getUserId, userId);
        deleteWrapper.in(SysUserRole::getRoleId, roleIds);
        
        int count = userRoleMapper.delete(deleteWrapper);
        
        log.info("移除用户角色成功，用户ID：{}，移除角色数量：{}", userId, count);
    }

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        // 遵循：开发规范-第6条（参数校验）
        if (userId == null) {
            throw new BusinessException(1009, "用户ID不能为空");
        }
        
        // 查询用户角色关联
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUserRole> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        
        List<SysUserRole> userRoles = userRoleMapper.selectList(wrapper);
        
        return userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        // 遵循：开发规范-第6条（参数校验）
        if (userId == null) {
            throw new BusinessException(1009, "用户ID不能为空");
        }
        
        // 获取用户的角色ID列表
        List<Long> roleIds = getRoleIdsByUserId(userId);
        
        if (CollectionUtils.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        
        // 批量查询角色信息
        return roleMapper.selectBatchIds(roleIds);
    }
}
