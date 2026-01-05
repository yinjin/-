package com.haocai.management.mapper;

import com.haocai.management.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户JPA数据访问接口
 * 基于Spring Data JPA，提供声明式数据访问
 * 结合JpaSpecificationExecutor支持动态查询
 *
 * @author 系统开发团队
 * @since 2026-01-07
 */
@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long>, JpaSpecificationExecutor<SysUser> {

    /**
     * 根据用户名查找用户
     * Spring Data JPA方法命名规则自动生成查询
     *
     * @param username 用户名
     * @return 用户对象Optional包装
     */
    Optional<SysUser> findByUsername(String username);

    /**
     * 根据用户名查找未删除的用户
     * 结合逻辑删除条件
     *
     * @param username 用户名
     * @return 用户对象Optional包装
     */
    Optional<SysUser> findByUsernameAndDeleted(String username, Integer deleted);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱地址
     * @return 用户对象Optional包装
     */
    Optional<SysUser> findByEmail(String email);

    /**
     * 根据邮箱查找未删除的用户
     *
     * @param email 邮箱地址
     * @param deleted 删除标志
     * @return 用户对象Optional包装
     */
    Optional<SysUser> findByEmailAndDeleted(String email, Integer deleted);

    /**
     * 根据手机号查找用户
     *
     * @param phone 手机号
     * @return 用户对象Optional包装
     */
    Optional<SysUser> findByPhone(String phone);

    /**
     * 根据手机号查找未删除的用户
     *
     * @param phone 手机号
     * @param deleted 删除标志
     * @return 用户对象Optional包装
     */
    Optional<SysUser> findByPhoneAndDeleted(String phone, Integer deleted);

    /**
     * 根据用户名或邮箱或手机号查找用户
     * 用于支持多种登录方式
     *
     * @param username 用户名
     * @param email 邮箱
     * @param phone 手机号
     * @param deleted 删除标志
     * @return 用户对象Optional包装
     */
    @Query("SELECT u FROM SysUser u WHERE u.deleted = :deleted AND " +
           "(u.username = :username OR u.email = :email OR u.phone = :phone)")
    Optional<SysUser> findByUsernameOrEmailOrPhoneAndDeleted(
            @Param("username") String username,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("deleted") Integer deleted);

    /**
     * 根据部门ID查找用户列表
     *
     * @param departmentId 部门ID
     * @param deleted 删除标志
     * @return 用户列表
     */
    List<SysUser> findByDepartmentIdAndDeletedOrderByCreateTimeDesc(Long departmentId, Integer deleted);

    /**
     * 根据用户状态查找用户列表
     *
     * @param status 用户状态
     * @param deleted 删除标志
     * @return 用户列表
     */
    List<SysUser> findByStatusAndDeleted(Integer status, Integer deleted);

    /**
     * 统计各部门用户数量
     * 使用JPQL自定义查询
     *
     * @return 部门用户统计结果
     */
    @Query("SELECT u.departmentId as departmentId, COUNT(u) as userCount " +
           "FROM SysUser u WHERE u.deleted = 0 AND u.departmentId IS NOT NULL " +
           "GROUP BY u.departmentId")
    List<DepartmentUserCount> findUserCountByDepartment();

    /**
     * 统计用户状态分布
     *
     * @return 用户状态统计结果
     */
    @Query("SELECT u.status as status, COUNT(u) as count " +
           "FROM SysUser u WHERE u.deleted = 0 GROUP BY u.status")
    List<UserStatusCount> findUserCountByStatus();

    /**
     * 检查用户名是否存在（排除指定用户）
     * 用于更新时的唯一性验证
     *
     * @param username 用户名
     * @param excludeUserId 排除的用户ID
     * @param deleted 删除标志
     * @return 是否存在
     */
    boolean existsByUsernameAndIdNotAndDeleted(String username, Long excludeUserId, Integer deleted);

    /**
     * 检查邮箱是否存在（排除指定用户）
     *
     * @param email 邮箱
     * @param excludeUserId 排除的用户ID
     * @param deleted 删除标志
     * @return 是否存在
     */
    boolean existsByEmailAndIdNotAndDeleted(String email, Long excludeUserId, Integer deleted);

    /**
     * 检查手机号是否存在（排除指定用户）
     *
     * @param phone 手机号
     * @param excludeUserId 排除的用户ID
     * @param deleted 删除标志
     * @return 是否存在
     */
    boolean existsByPhoneAndIdNotAndDeleted(String phone, Long excludeUserId, Integer deleted);

    /**
     * 查找指定时间之后创建的用户
     * 用于数据同步和统计
     *
     * @param createTime 创建时间起点
     * @param deleted 删除标志
     * @return 用户列表
     */
    List<SysUser> findByCreateTimeAfterAndDeletedOrderByCreateTimeDesc(LocalDateTime createTime, Integer deleted);

    /**
     * 查找指定时间范围内登录的用户
     * 用于活跃用户统计
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param deleted 删除标志
     * @return 用户列表
     */
    List<SysUser> findByLastLoginTimeBetweenAndDeleted(
            LocalDateTime startTime, LocalDateTime endTime, Integer deleted);

    /**
     * 批量更新用户状态
     * 使用@Modifying和@Query注解执行更新操作
     *
     * @param userIds 用户ID列表
     * @param status 目标状态
     * @param updateBy 操作者ID
     * @param updateTime 更新时间
     * @return 影响行数
     */
    @Modifying
    @Query("UPDATE SysUser u SET u.status = :status, u.updateBy = :updateBy, u.updateTime = :updateTime " +
           "WHERE u.id IN :userIds AND u.deleted = 0")
    int updateStatusBatch(@Param("userIds") List<Long> userIds,
                         @Param("status") Integer status,
                         @Param("updateBy") Long updateBy,
                         @Param("updateTime") LocalDateTime updateTime);

    /**
     * 更新用户最后登录时间
     *
     * @param userId 用户ID
     * @param lastLoginTime 最后登录时间
     * @return 影响行数
     */
    @Modifying
    @Query("UPDATE SysUser u SET u.lastLoginTime = :lastLoginTime WHERE u.id = :userId AND u.deleted = 0")
    int updateLastLoginTime(@Param("userId") Long userId, @Param("lastLoginTime") LocalDateTime lastLoginTime);

    /**
     * 部门用户统计结果接口
     */
    interface DepartmentUserCount {
        Long getDepartmentId();
        Long getUserCount();
    }

    /**
     * 用户状态统计结果接口
     */
    interface UserStatusCount {
        Integer getStatus();
        Long getCount();
    }
}