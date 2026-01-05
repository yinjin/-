package com.haocai.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.dto.UserLoginDTO;
import com.haocai.management.dto.UserRegisterDTO;
import com.haocai.management.dto.UserUpdateDTO;
import com.haocai.management.entity.SysUser;
import com.haocai.management.entity.UserStatus;

import java.util.List;

/**
 * 用户业务逻辑接口
 * 定义用户管理相关的所有业务方法
 */
public interface ISysUserService {

    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册成功的用户信息
     */
    SysUser register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return JWT token
     */
    String login(UserLoginDTO loginDTO);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    SysUser findByUsername(String username);

    /**
     * 根据ID查找用户
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUser findById(Long userId);

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param updateDTO 更新信息
     * @return 更新后的用户信息
     */
    SysUser updateUser(Long userId, UserUpdateDTO updateDTO);

    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 新状态
     * @param updateBy 操作人ID
     * @return 是否成功
     */
    boolean updateUserStatus(Long userId, UserStatus status, Long updateBy);

    /**
     * 分页查询用户列表
     * @param page 分页参数
     * @param username 用户名关键词（可选）
     * @param realName 真实姓名关键词（可选）
     * @param status 用户状态（可选）
     * @param departmentId 部门ID（可选）
     * @return 分页结果
     */
    IPage<SysUser> findUserPage(Page<SysUser> page, String username, String realName,
                               UserStatus status, Long departmentId);

    /**
     * 批量更新用户状态
     * @param userIds 用户ID列表
     * @param status 新状态
     * @param updateBy 操作人ID
     * @return 更新的用户数量
     */
    int batchUpdateStatus(List<Long> userIds, UserStatus status, Long updateBy);

    /**
     * 删除用户（逻辑删除）
     * @param userId 用户ID
     * @param deleteBy 删除人ID
     * @return 是否成功
     */
    boolean deleteUser(Long userId, Long deleteBy);

    /**
     * 批量删除用户（逻辑删除）
     * @param userIds 用户ID列表
     * @param deleteBy 删除人ID
     * @return 删除的用户数量
     */
    int batchDeleteUsers(List<Long> userIds, Long deleteBy);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @param excludeUserId 排除的用户ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsByEmail(String email, Long excludeUserId);

    /**
     * 检查手机号是否存在
     * @param phone 手机号
     * @param excludeUserId 排除的用户ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsByPhone(String phone, Long excludeUserId);

    /**
     * 记录用户登录日志
     * @param userId 用户ID
     * @param loginIp 登录IP
     * @param success 是否登录成功
     * @param failReason 失败原因（成功时为null）
     */
    void recordLoginLog(Long userId, String loginIp, boolean success, String failReason);

    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     */
    void updateLastLoginTime(Long userId);
}