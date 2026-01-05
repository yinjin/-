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
import com.haocai.management.mapper.SysUserLoginLogMapper;
import com.haocai.management.mapper.SysUserMapper;
import com.haocai.management.mapper.SysUserRepository;
import com.haocai.management.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户业务逻辑实现类
 * 实现用户管理的所有业务逻辑，包括注册、登录、信息管理等
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRepository sysUserRepository;
    private final SysUserLoginLogMapper loginLogMapper;
    private final PasswordEncoder passwordEncoder;

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
        user.setRealName(registerDTO.getRealName());
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

        // 1. 根据用户名查找用户
        SysUser user = findByUsername(loginDTO.getUsername());
        if (user == null) {
            log.warn("用户登录失败，用户不存在，用户名: {}", loginDTO.getUsername());
            recordLoginLog(null, loginDTO.getIpAddress(), false, "用户不存在");
            throw BusinessException.userNotFound();
        }

        // 2. 检查用户状态
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

        // 3. 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            log.warn("用户登录失败，密码错误，用户名: {}", loginDTO.getUsername());
            recordLoginLog(user.getId(), loginDTO.getIpAddress(), false, "密码错误");
            throw BusinessException.passwordError();
        }

        // 4. 更新最后登录时间
        updateLastLoginTime(user.getId());

        // 5. 记录登录成功日志
        recordLoginLog(user.getId(), loginDTO.getIpAddress(), true, null);

        // 6. 生成JWT token (暂时返回简单token，后续完善)
        String token = "temp-token-" + user.getId() + "-" + System.currentTimeMillis();
        log.info("用户登录成功，用户名: {}, 用户ID: {}", user.getUsername(), user.getId());

        return token;
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
        updateUser.setRealName(updateDTO.getRealName());
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

        // 2. 更新状态
        int result = sysUserMapper.updateStatusBatch(List.of(userId), status.getCode(), updateBy);
        if (result <= 0) {
            log.error("更新用户状态失败，用户ID: {}", userId);
            return false;
        }

        log.info("用户状态更新成功，用户ID: {}, 新状态: {}", userId, status);
        return true;
    }

    @Override
    public IPage<SysUser> findUserPage(Page<SysUser> page, String username, String realName,
                                     UserStatus status, Long departmentId) {
        return sysUserMapper.selectUserPage(page, username, realName,
                                          status != null ? status.getCode() : null, departmentId);
    }

    @Override
    @Transactional
    public int batchUpdateStatus(List<Long> userIds, UserStatus status, Long updateBy) {
        log.info("开始批量更新用户状态，用户数量: {}, 新状态: {}, 操作人: {}", userIds.size(), status, updateBy);

        if (userIds == null || userIds.isEmpty()) {
            return 0;
        }

        int result = sysUserMapper.updateStatusBatch(userIds, status.getCode(), updateBy);
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

        // 2. 逻辑删除用户
        SysUser deleteUser = new SysUser();
        deleteUser.setId(userId);
        deleteUser.setDeleted(1); // 标记为已删除
        deleteUser.setUpdateTime(LocalDateTime.now());

        int result = sysUserMapper.updateById(deleteUser);
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
            if (deleteUser(userId, deleteBy)) {
                count++;
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
        Optional<SysUser> userOpt = sysUserRepository.findByEmailAndDeleted(email, 0);
        if (!userOpt.isPresent()) {
            return false;
        }
        SysUser user = userOpt.get();
        return excludeUserId == null || !user.getId().equals(excludeUserId);
    }

    @Override
    public boolean existsByPhone(String phone, Long excludeUserId) {
        Optional<SysUser> userOpt = sysUserRepository.findByPhoneAndDeleted(phone, 0);
        if (!userOpt.isPresent()) {
            return false;
        }
        SysUser user = userOpt.get();
        return excludeUserId == null || !user.getId().equals(excludeUserId);
    }

    @Override
    public void recordLoginLog(Long userId, String loginIp, boolean success, String failReason) {
        try {
            SysUserLoginLog loginLog = new SysUserLoginLog();
            loginLog.setUserId(userId);
            if (userId != null) {
                SysUser user = findById(userId);
                if (user != null) {
                    loginLog.setUsername(user.getUsername());
                }
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
}