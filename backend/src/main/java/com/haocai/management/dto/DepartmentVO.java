package com.haocai.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门信息响应VO
 * 
 * 遵循规范：
 * - VO命名规范：使用业务场景名称+VO后缀
 * - 字段设计规范：只包含前端展示需要的字段
 * - 文档规范：使用@Schema注解描述字段
 * 
 * @author haocai
 * @date 2026-01-08
 */
@Data
@Schema(description = "部门信息响应")
public class DepartmentVO {

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", example = "1")
    private Long id;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称", example = "软件工程教研室")
    private String name;

    /**
     * 部门编码
     */
    @Schema(description = "部门编码", example = "SE_GROUP")
    private String code;

    /**
     * 父部门ID
     */
    @Schema(description = "父部门ID", example = "2")
    private Long parentId;

    /**
     * 父部门名称
     */
    @Schema(description = "父部门名称", example = "计算机科学系")
    private String parentName;

    /**
     * 部门层级
     */
    @Schema(description = "部门层级（顶级为1级）", example = "3")
    private Integer level;

    /**
     * 排序号
     */
    @Schema(description = "排序号（同级部门排序，数字越小越靠前）", example = "1")
    private Integer sortOrder;

    /**
     * 部门状态
     */
    @Schema(description = "状态：NORMAL-正常，DISABLED-禁用", example = "NORMAL")
    private String status;

    /**
     * 部门负责人ID
     */
    @Schema(description = "部门负责人ID", example = "1")
    private Long leaderId;

    /**
     * 部门负责人名称
     */
    @Schema(description = "部门负责人名称", example = "张三")
    private String leaderName;

    /**
     * 联系方式
     */
    @Schema(description = "联系方式", example = "010-88888888")
    private String contactInfo;

    /**
     * 部门描述
     */
    @Schema(description = "部门描述", example = "软件工程教研室负责软件工程专业教学")
    private String description;

    /**
     * 子部门数量
     */
    @Schema(description = "子部门数量", example = "0")
    private Integer childrenCount;

    /**
     * 用户数量
     */
    @Schema(description = "部门下用户数量", example = "10")
    private Integer usersCount;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2026-01-08 10:00:00")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2026-01-08 10:00:00")
    private LocalDateTime updateTime;
}
