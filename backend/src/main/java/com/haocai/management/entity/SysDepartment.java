package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.haocai.management.enums.DepartmentStatus;
import com.haocai.management.enums.DepartmentStatusConverter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门实体类
 * 
 * <p>用于存储组织架构部门信息，支持树形结构</p>
 * 
 * <p>字段设计：</p>
 * <ul>
 *   <li>id：部门ID（主键）</li>
 *   <li>name：部门名称</li>
 *   <li>code：部门编码（唯一）</li>
 *   <li>parentId：父部门ID（支持树形结构）</li>
 *   <li>level：部门层级</li>
 *   <li>sortOrder：排序</li>
 *   <li>status：部门状态</li>
 *   <li>leaderId：部门负责人ID</li>
 *   <li>contactInfo：联系方式</li>
 *   <li>description：部门描述</li>
 *   <li>审计字段：createTime、updateTime、createBy、updateBy、deleted</li>
 * </ul>
 * 
 * <p>遵循规范：</p>
 * <ul>
 *   <li>实体类设计规范-第2.1条：字段映射规范</li>
 *   <li>实体类设计规范-第2.3条：字段自动填充规范</li>
 *   <li>数据库设计规范-第1.1条：字段命名规范</li>
 * </ul>
 * 
 * @author 开发团队
 * @since 2026-01-08
 * @version 1.0
 */
@Data
@TableName("sys_department")
public class SysDepartment {

    /**
     * 部门ID（主键）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部门名称
     * 长度：100字符
     * 必填
     */
    private String name;

    /**
     * 部门编码
     * 长度：50字符
     * 唯一
     */
    private String code;

    /**
     * 父部门ID
     * 支持树形结构，顶级部门为null或0
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 部门层级
     * 顶级部门为1级
     * 默认值：1
     */
    private Integer level;

    /**
     * 排序
     * 默认值：0
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 部门状态
     * 使用枚举类型映射
     * 默认值：NORMAL（正常）
     *
     * 遵循：实体类设计规范-第2.2条（类型转换器规范）
     * 使用DepartmentStatusConverter进行枚举与VARCHAR的转换
     */
    @TableField(value = "status", typeHandler = DepartmentStatusConverter.class)
    private DepartmentStatus status;

    /**
     * 部门负责人ID
     * 关联sys_user表
     */
    @TableField("leader_id")
    private Long leaderId;

    /**
     * 联系方式
     */
    @TableField("contact_info")
    private String contactInfo;

    /**
     * 部门描述
     */
    private String description;

    /**
     * 创建时间
     * 自动填充（INSERT时）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 自动填充（INSERT和UPDATE时）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     * 自动填充（INSERT时）
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人ID
     * 自动填充（INSERT和UPDATE时）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 逻辑删除
     * 默认值：0（未删除）
     */
    @TableLogic
    private Integer deleted;

    // ==================== 非数据库字段（用于树形结构展示） ====================

    /**
     * 父部门名称（非数据库字段）
     */
    @TableField(exist = false)
    private String parentName;

    /**
     * 负责人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String leaderName;

    /**
     * 子部门列表（非数据库字段，用于树形结构）
     */
    @TableField(exist = false)
    private java.util.List<SysDepartment> children;

    /**
     * 部门下用户数量（非数据库字段）
     */
    @TableField(exist = false)
    private Integer userCount;
}
