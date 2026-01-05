package com.material.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.material.system.common.ResultCode;
import com.material.system.dto.UserCreateDTO;
import com.material.system.dto.UserLoginDTO;
import com.material.system.dto.UserUpdateDTO;
import com.material.system.entity.SysRole;
import com.material.system.entity.SysUser;
import com.material.system.entity.SysUserRole;
import com.material.system.exception.BusinessException;
import com.material.system.mapper.SysUserMapper;
import com.material.system.service.SysRoleService;
import com.material.system.service.SysUserService;
import com.material.system.util.JwtUtil;
import com.material.system.util.PasswordUtil;
import com.material.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    private final JwtUtil jwtUtil;
    private final com.material.system.mapper.SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleService sysRoleService;
    
    @Override
    public String login(UserLoginDTO loginDTO) {
        // 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginDTO.getUsername());
        SysUser user = getOne(wrapper);
        
        // 验证用户是否存在
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        
        // 验证密码
        if (!PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        
        // 验证用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        
        // 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        user.setLoginCount(user.getLoginCount() + 1);
        updateById(user);
        
        // 生成JWT token
        return jwtUtil.generateToken(user.getUsername(), user.getId());
    }
    
    @Override
    public Long createUser(UserCreateDTO createDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, createDTO.getUsername());
        if (getOne(wrapper) != null) {
            throw new BusinessException(ResultCode.USERNAME_ALREADY_EXIST);
        }
        
        // 创建用户
        SysUser user = new SysUser();
        BeanUtils.copyProperties(createDTO, user);
        
        // 加密密码
        user.setPassword(PasswordUtil.encode(createDTO.getPassword()));
        
        // 设置默认值
        user.setStatus(1);
        user.setLoginCount(0);
        user.setCreateTime(LocalDateTime.now());
        
        // 保存用户
        save(user);
        
        return user.getId();
    }
    
    @Override
    public void updateUser(UserUpdateDTO updateDTO) {
        // 检查用户是否存在
        SysUser user = getById(updateDTO.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        
        // 检查用户名是否被其他用户使用
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, updateDTO.getUsername());
        wrapper.ne(SysUser::getId, updateDTO.getId());
        if (getOne(wrapper) != null) {
            throw new BusinessException(ResultCode.USERNAME_ALREADY_EXIST);
        }
        
        // 更新用户信息
        BeanUtils.copyProperties(updateDTO, user);
        updateById(user);
    }
    
    @Override
    public void deleteUser(Long userId) {
        // 检查用户是否存在
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        
        // 删除用户
        removeById(userId);
    }
    
    @Override
    public UserVO getUserById(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        
        // 查询角色名称
        if (user.getRoleId() != null) {
            SysRole role = sysRoleService.getById(user.getRoleId());
            if (role != null) {
                userVO.setRoleName(role.getRoleName());
            }
        }
        
        // TODO: 查询部门名称
        userVO.setDepartmentName("默认部门");
        
        return userVO;
    }
    
    @Override
    public Page<UserVO> getUserPage(Long current, Long size, String username, String realName, Integer status, Long departmentId) {
        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        
        if (StringUtils.hasText(realName)) {
            wrapper.like(SysUser::getRealName, realName);
        }
        
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        
        if (departmentId != null) {
            wrapper.eq(SysUser::getDepartmentId, departmentId);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(SysUser::getCreateTime);
        
        // 分页查询
        Page<SysUser> page = page(new Page<>(current, size), wrapper);
        
        // 转换为VO
        Page<UserVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            
            // 查询角色名称
            if (user.getRoleId() != null) {
                SysRole role = sysRoleService.getById(user.getRoleId());
                if (role != null) {
                    userVO.setRoleName(role.getRoleName());
                }
            }
            
            // TODO: 查询部门名称
            userVO.setDepartmentName("默认部门");
            
            return userVO;
        }).toList());
        
        return voPage;
    }
    
    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        // 检查用户是否存在
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        
        // 验证旧密码
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        
        // 更新密码
        user.setPassword(PasswordUtil.encode(newPassword));
        updateById(user);
    }
    
    @Override
    public void resetPassword(Long userId, String newPassword) {
        // 检查用户是否存在
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        
        // 重置密码
        user.setPassword(PasswordUtil.encode(newPassword));
        updateById(user);
    }
    
    @Override
    public void updateUserStatus(Long userId, Integer status) {
        // 检查用户是否存在
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        
        // 更新状态
        user.setStatus(status);
        updateById(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRole(Long userId, Long roleId) {
        // 检查用户是否存在
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        
        // 检查角色是否存在
        SysRole role = sysRoleService.getById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_EXIST);
        }
        
        // 删除用户原有的角色关联
        LambdaQueryWrapper<SysUserRole> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SysUserRole::getUserId, userId);
        sysUserRoleMapper.delete(deleteWrapper);
        
        // 创建新的角色关联
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        sysUserRoleMapper.insert(userRole);
        
        // 更新用户的角色ID
        user.setRoleId(roleId);
        updateById(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRole(Long userId) {
        // 检查用户是否存在
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        
        // 删除用户角色关联
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        sysUserRoleMapper.delete(wrapper);
        
        // 清空用户的角色ID
        user.setRoleId(null);
        updateById(user);
    }
    
    @Override
    public List<Long> getUserRoleIds(Long userId) {
        // 检查用户是否存在
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        
        // 查询用户的角色ID列表
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(wrapper);
        
        return userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
    }
}
