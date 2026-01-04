package com.material.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.system.common.Result;
import com.material.system.dto.MaterialCategoryCreateDTO;
import com.material.system.dto.MaterialCategoryUpdateDTO;
import com.material.system.service.MaterialCategoryService;
import com.material.system.vo.MaterialCategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物料分类控制器
 */
@Tag(name = "物料分类管理", description = "物料分类相关接口")
@RestController
@RequestMapping("/api/material-category")
@RequiredArgsConstructor
public class MaterialCategoryController {
    
    private final MaterialCategoryService materialCategoryService;
    
    @Operation(summary = "创建物料分类")
    @PostMapping
    @PreAuthorize("hasAuthority('material:category:create')")
    public Result<MaterialCategoryVO> create(@Validated @RequestBody MaterialCategoryCreateDTO dto) {
        MaterialCategoryVO vo = materialCategoryService.create(dto);
        return Result.success(vo);
    }
    
    @Operation(summary = "更新物料分类")
    @PutMapping
    @PreAuthorize("hasAuthority('material:category:update')")
    public Result<MaterialCategoryVO> update(@Validated @RequestBody MaterialCategoryUpdateDTO dto) {
        MaterialCategoryVO vo = materialCategoryService.update(dto);
        return Result.success(vo);
    }
    
    @Operation(summary = "删除物料分类")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('material:category:delete')")
    public Result<Void> delete(@Parameter(description = "分类ID") @PathVariable Long id) {
        materialCategoryService.delete(id);
        return Result.success();
    }
    
    @Operation(summary = "根据ID查询物料分类")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('material:category:query')")
    public Result<MaterialCategoryVO> getById(@Parameter(description = "分类ID") @PathVariable Long id) {
        MaterialCategoryVO vo = materialCategoryService.getById(id);
        return Result.success(vo);
    }
    
    @Operation(summary = "分页查询物料分类")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('material:category:query')")
    public Result<Page<MaterialCategoryVO>> page(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "分类名称") @RequestParam(required = false) String name,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        Page<MaterialCategoryVO> page = materialCategoryService.page(current, size, name, status);
        return Result.success(page);
    }
    
    @Operation(summary = "获取物料分类树")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('material:category:query')")
    public Result<List<MaterialCategoryVO>> tree() {
        List<MaterialCategoryVO> tree = materialCategoryService.tree();
        return Result.success(tree);
    }
    
    @Operation(summary = "根据父分类ID查询子分类")
    @GetMapping("/children/{parentId}")
    @PreAuthorize("hasAuthority('material:category:query')")
    public Result<List<MaterialCategoryVO>> getByParentId(
            @Parameter(description = "父分类ID") @PathVariable Long parentId) {
        List<MaterialCategoryVO> children = materialCategoryService.getByParentId(parentId);
        return Result.success(children);
    }
}
