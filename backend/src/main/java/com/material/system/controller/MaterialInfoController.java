package com.material.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.system.common.Result;
import com.material.system.dto.MaterialInfoCreateDTO;
import com.material.system.dto.MaterialInfoUpdateDTO;
import com.material.system.service.MaterialInfoService;
import com.material.system.vo.MaterialInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物料信息控制器
 */
@Tag(name = "物料信息管理", description = "物料信息相关接口")
@RestController
@RequestMapping("/api/material-info")
@RequiredArgsConstructor
public class MaterialInfoController {
    
    private final MaterialInfoService materialInfoService;
    
    @Operation(summary = "创建物料信息")
    @PostMapping
    @PreAuthorize("hasAuthority('material:info:create')")
    public Result<MaterialInfoVO> create(@Validated @RequestBody MaterialInfoCreateDTO dto) {
        MaterialInfoVO vo = materialInfoService.create(dto);
        return Result.success(vo);
    }
    
    @Operation(summary = "更新物料信息")
    @PutMapping
    @PreAuthorize("hasAuthority('material:info:update')")
    public Result<MaterialInfoVO> update(@Validated @RequestBody MaterialInfoUpdateDTO dto) {
        MaterialInfoVO vo = materialInfoService.update(dto);
        return Result.success(vo);
    }
    
    @Operation(summary = "删除物料信息")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('material:info:delete')")
    public Result<Void> delete(@Parameter(description = "物料ID") @PathVariable Long id) {
        materialInfoService.delete(id);
        return Result.success();
    }
    
    @Operation(summary = "根据ID查询物料信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('material:info:query')")
    public Result<MaterialInfoVO> getById(@Parameter(description = "物料ID") @PathVariable Long id) {
        MaterialInfoVO vo = materialInfoService.getById(id);
        return Result.success(vo);
    }
    
    @Operation(summary = "分页查询物料信息")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('material:info:query')")
    public Result<Page<MaterialInfoVO>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "物料名称") @RequestParam(required = false) String name,
            @Parameter(description = "物料编码") @RequestParam(required = false) String code,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        Page<MaterialInfoVO> page = materialInfoService.page(current, size, name, code, categoryId, status);
        return Result.success(page);
    }
    
    @Operation(summary = "根据分类ID查询物料信息")
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAuthority('material:info:query')")
    public Result<List<MaterialInfoVO>> getByCategoryId(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        List<MaterialInfoVO> materials = materialInfoService.getByCategoryId(categoryId);
        return Result.success(materials);
    }
}
