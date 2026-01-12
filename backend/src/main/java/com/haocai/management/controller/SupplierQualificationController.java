package com.haocai.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.dto.*;
import com.haocai.management.entity.SupplierQualification;
import com.haocai.management.service.ISupplierQualificationService;
import com.haocai.management.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 供应商资质管理控制器
 * 
 * 遵循规范：
 * - CONTROLLER-01：统一响应格式（ApiResponse包装）
 * - CONTROLLER-02：参数校验（@Validated + JSR-303注解）
 * - CONTROLLER-03：权限控制（@PreAuthorize）
 */
@Tag(name = "供应商资质管理", description = "供应商资质信息的CRUD管理接口")
@RestController
@RequestMapping("/api/supplier-qualification")
@RequiredArgsConstructor
public class SupplierQualificationController {

    private final ISupplierQualificationService supplierQualificationService;

    /**
     * 创建供应商资质
     * 
     * 遵循：CONTROLLER-02（参数校验）、CONTROLLER-03（权限控制）
     */
    @Operation(summary = "创建供应商资质", description = "新增供应商资质信息")
    @PostMapping
    @PreAuthorize("hasAuthority('supplier:create')")
    public ApiResponse<Long> createQualification(
            @Valid @RequestBody SupplierQualificationCreateDTO createDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 遵循：SERVICE-01（事务管理）
        Long currentUserId = Long.parseLong(userDetails.getUsername());
        Long qualificationId = supplierQualificationService.createQualification(createDTO, currentUserId);
        return ApiResponse.success(qualificationId);
    }

    /**
     * 更新供应商资质
     * 
     * 遵循：CONTROLLER-02（参数校验）、CONTROLLER-03（权限控制）
     */
    @Operation(summary = "更新供应商资质", description = "根据ID更新供应商资质信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('supplier:edit')")
    public ApiResponse<Void> updateQualification(
            @Parameter(description = "资质ID") @PathVariable Long id,
            @Valid @RequestBody SupplierQualificationUpdateDTO updateDTO) {
        supplierQualificationService.updateQualification(id, updateDTO);
        return ApiResponse.success();
    }

    /**
     * 删除供应商资质
     * 
     * 遵循：CONTROLLER-03（权限控制）
     */
    @Operation(summary = "删除供应商资质", description = "根据ID删除供应商资质（逻辑删除）")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('supplier:delete')")
    public ApiResponse<Void> deleteQualification(
            @Parameter(description = "资质ID") @PathVariable Long id) {
        supplierQualificationService.deleteQualification(id);
        return ApiResponse.success();
    }

    /**
     * 批量删除供应商资质
     * 
     * 遵循：CONTROLLER-03（权限控制）
     */
    @Operation(summary = "批量删除供应商资质", description = "批量删除供应商资质（逻辑删除）")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('supplier:delete')")
    public ApiResponse<Integer> batchDeleteQualifications(
            @RequestBody List<Long> ids) {
        Integer deletedCount = supplierQualificationService.batchDeleteQualifications(ids);
        return ApiResponse.success(deletedCount);
    }

    /**
     * 获取供应商资质详情
     * 
     * 遵循：CONTROLLER-03（权限控制）
     */
    @Operation(summary = "获取供应商资质详情", description = "根据ID获取供应商资质详细信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<SupplierQualificationVO> getQualificationById(
            @Parameter(description = "资质ID") @PathVariable Long id) {
        SupplierQualificationVO qualification = supplierQualificationService.getQualificationById(id);
        return ApiResponse.success(qualification);
    }

    /**
     * 获取供应商的所有资质
     * 
     * 遵循：CONTROLLER-03（权限控制）
     */
    @Operation(summary = "获取供应商的所有资质", description = "根据供应商ID获取该供应商的所有资质信息")
    @GetMapping("/supplier/{supplierId}")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<SupplierQualificationVO>> getQualificationsBySupplierId(
            @Parameter(description = "供应商ID") @PathVariable Long supplierId) {
        List<SupplierQualificationVO> qualifications = supplierQualificationService.getQualificationsBySupplierId(supplierId);
        return ApiResponse.success(qualifications);
    }

    /**
     * 分页查询供应商资质
     * 
     * 遵循：CONTROLLER-02（参数校验）
     */
    @Operation(summary = "分页查询供应商资质", description = "分页查询供应商资质列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<IPage<SupplierQualificationVO>> getQualificationPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "供应商ID") @RequestParam(required = false) Long supplierId,
            @Parameter(description = "资质类型") @RequestParam(required = false) String qualificationType,
            @Parameter(description = "状态：1-有效，2-即将到期，3-已过期") @RequestParam(required = false) Integer status) {
        IPage<SupplierQualificationVO> qualificationPage = supplierQualificationService.getQualificationPage(current, size, supplierId, qualificationType, status);
        return ApiResponse.success(qualificationPage);
    }

    /**
     * 获取即将到期的供应商资质
     * 
     * 遵循：CONTROLLER-03（权限控制）
     */
    @Operation(summary = "获取即将到期的资质", description = "获取指定天数内即将到期的供应商资质")
    @GetMapping("/expiring")
    @PreAuthorize("hasAuthority('supplier:view')")
    public ApiResponse<List<SupplierQualificationVO>> getExpiringQualifications() {
        List<SupplierQualificationVO> qualifications = supplierQualificationService.getExpiringQualifications();
        return ApiResponse.success(qualifications);
    }

    /**
     * 更新过期资质状态
     * 
     * 遵循：SERVICE-01（事务管理）
     */
    @Operation(summary = "更新过期资质状态", description = "自动将已过期的资质状态更新为过期")
    @PutMapping("/update-expired-status")
    @PreAuthorize("hasAuthority('supplier:edit')")
    public ApiResponse<Integer> updateExpiredQualificationsStatus() {
        Integer updatedCount = supplierQualificationService.updateExpiredQualificationsStatus();
        return ApiResponse.success(updatedCount);
    }
}
