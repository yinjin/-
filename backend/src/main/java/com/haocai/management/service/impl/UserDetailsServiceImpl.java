package com.haocai.management.service.impl;

import com.haocai.management.entity.SysUser;
import com.haocai.management.entity.UserStatus;
import com.haocai.management.exception.BusinessException;
import com.haocai.management.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Spring Security用户详情服务实现
 * <p>
 * 职责：
 * 1. 根据用户名从数据库加载用户信息
 * 2. 将用户信息转换为Spring Security的UserDetails对象
 * 3. 处理用户状态验证（激活/禁用）
 * <p>
 * 使用场景：
 * - JWT认证过滤器验证token后，需要加载用户详情
 * - 登录认证过程中验证用户凭证
 * <p>
 * 设计原则：
 * - 统一异常处理，遵循全局异常规范
 * - 用户状态检查，确保只有激活用户可以认证
 * - 详细日志记录，便于问题追踪
 * <p>
 * 遵循规范：
 * - 代码规范-第2条（参数校验）
 * - 代码规范-第3条（异常处理）
 * - 代码规范-第4条（日志记录）
 * - 安全规范-第7条（用户状态验证）
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    @Lazy
    private ISysUserService sysUserService;

    /**
     * 根据用户名加载用户详情
     * <p>
     * 执行流程：
     * 1. 从数据库查询用户信息
     * 2. 验证用户是否存在
     * 3. 验证用户状态（是否激活）
     * 4. 构建UserDetails对象
     * 5. 返回用户详情
     *
     * @param username 用户名
     * @return 用户详情对象
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 步骤1：从数据库查询用户信息
        // 遵循：代码规范-第2条（参数校验）
        SysUser sysUser = sysUserService.findByUsername(username);

        // 步骤2：验证用户是否存在
        if (sysUser == null) {
            String errorMsg = "用户不存在: " + username;
            log.warn(errorMsg);
            throw new UsernameNotFoundException(errorMsg);
        }

        // 步骤3：验证用户状态
        // 遵循：安全规范-第7条（用户状态验证）
        if (sysUser.getStatus() == UserStatus.DISABLED) {
            String errorMsg = "用户已被禁用: " + username;
            log.warn("用户已被禁用 - 用户ID: {}, 用户名: {}", sysUser.getId(), username);
            throw new BusinessException(1002, errorMsg);
        }

        // 步骤4：构建UserDetails对象
        // 注意：这里使用空权限列表，实际权限可根据需要从数据库加载
        // 密码字段可以为null，因为JWT认证时不需要密码验证
        UserDetails userDetails = User.builder()
                .username(sysUser.getUsername())
                .password(sysUser.getPassword())
                .authorities(Collections.emptyList())
                .accountExpired(false) // 账户未过期
                .accountLocked(false) // 账户未锁定
                .credentialsExpired(false) // 凭证未过期
                .disabled(sysUser.getStatus() == UserStatus.DISABLED) // 是否禁用
                .build();

        // 步骤5：记录日志
        log.info("用户详情加载成功 - 用户ID: {}, 用户名: {}",
                sysUser.getId(), sysUser.getUsername());

        return userDetails;
    }
}
