package com.material.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.material.system.dto.UserCreateDTO;
import com.material.system.dto.UserLoginDTO;
import com.material.system.dto.UserUpdateDTO;
import com.material.system.entity.SysUser;
import com.material.system.vo.UserVO;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {
    
    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return JWT token
     */
    String login(UserLoginDTO loginDTO);
    
    /**
     * 创建用户
     * @param createDTO 用户创建信息
     * @return 用户ID
     */
    Long createUser(UserCreateDTO createDTO);
    
    /**
     * 更新用户信息
     * @param updateDTO 用户更新信息
     */
    void updateUser(UserUpdateDTO updateDTO);
    
    /**
     * 删除用户
     * @param userId 用户ID
     */
    void deleteUser(Long userId);
    
    /**
     * 根据ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserById(Long userId);
    
    /**
     * 分页查询用户列表
     * @param current 当前页
     * @param size 每页大小
     * @param username 用户名（模糊查询）
     * @param realName 真实姓名（模糊查询）
     * @param status 状态
     * @param departmentId 部门ID
     * @return 用户分页列表
     */
    Page<UserVO> getUserPage(Long current, Long size, String username, String realName, Integer status, Long departmentId);
    
    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     * @param userId 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long userId, String newPassword);
    
    /**
     * 启用/禁用用户
     * @param userId 用户ID
     * @param status 状态：1正常 0禁用
     */
    void updateUserStatus(Long userId, Integer status);
}
