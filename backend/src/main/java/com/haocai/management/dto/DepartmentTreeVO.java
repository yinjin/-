package com.haocai.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门树形结构响应VO
 * 
 * 遵循规范：
 * - VO命名规范：使用业务场景名称+VO后缀
 * - 树形结构规范：使用children字段存储子部门列表
 * - 懒加载支持：支持懒加载模式，只在展开时加载子部门
 * 
 * @author haocai
 * @date 2026-01-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "部门树形结构响应")
public class DepartmentTreeVO extends DepartmentVO {

    /**
     * 子部门列表
     * 遵循：树形结构规范-第1条（使用children字段存储子节点）
     */
    @Schema(description = "子部门列表")
    private List<DepartmentTreeVO> children = new ArrayList<>();

    /**
     * 是否展开（用于前端控制）
     */
    @Schema(description = "是否展开（前端使用）")
    private Boolean expanded = false;

    /**
     * 是否叶子节点
     * 遵循：树形结构规范-第2条（叶子节点标识）
     */
    @Schema(description = "是否叶子节点")
    private Boolean leaf = false;

    /**
     * 懒加载模式下，子节点数据是否已加载
     */
    @Schema(description = "懒加载模式下子节点是否已加载")
    private Boolean loaded = false;
}
