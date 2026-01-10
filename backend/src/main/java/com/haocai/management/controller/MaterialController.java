package com.haocai.management.controller;

import com.haocai.management.common.ApiResponse;

import java.time.LocalDateTime;
import com.haocai.management.dto.MaterialCreateDTO;
import com.haocai.management.dto.MaterialUpdateDTO;
import com.haocai.management.service.IMaterialService;
import com.haocai.management.vo.MaterialPageVO;
import com.haocai.management.vo.MaterialVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 耗材管理Controller
 *
 * 遵循development-standards.md中的控制层规范：
 * - Controller命名规范：使用业务名称+Controller后缀
 * - 方法命名规范：使用动词+名词，清晰表达业务含义
 * - 权限控制：使用@PreAuthorize注解控制方法级权限
 * - 接口文档：使用Swagger注解描述接口
 * - 日志记录：使用Slf4j进行日志记录
 * - 依赖注入：使用@RequiredArgsConstructor进行构造器注入
 *
 * @author haocai
 * @since 2026-01-09
 */
@Slf4j
@RestController
@RequestMapping("/api/material")
@io.swagger.v3.oas.annotations.tags.Tag(name = "耗材管理接口")
@RequiredArgsConstructor
public class MaterialController {

    private final IMaterialService materialService;

    // ==================== 基础CRUD接口 ====================

    /**
     * 创建耗材
     * 权限：耗材管理-创建
     */
    @PostMapping
    @Operation(summary = "创建耗材")
    @PreAuthorize("hasAuthority('material:create')")
    public ApiResponse<Long> createMaterial(
            @Parameter(description = "耗材创建请求") @RequestBody MaterialCreateDTO dto) {
        log.info("========== 创建耗材 ==========");
        log.info("请求参数: {}", dto);
        log.info("权限检查: material:create");

        Long materialId = materialService.createMaterial(dto);

        log.info("创建成功，耗材ID: {}", materialId);
        log.info("========== 创建耗材结束 ==========");

        return ApiResponse.success(materialId);
    }

    /**
     * 更新耗材
     * 权限：耗材管理-编辑
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新耗材")
    @PreAuthorize("hasAuthority('material:edit')")
    public ApiResponse<Boolean> updateMaterial(
            @Parameter(description = "耗材ID") @PathVariable Long id,
            @Parameter(description = "耗材更新请求") @RequestBody MaterialUpdateDTO dto) {
        log.info("========== 更新耗材 ==========");
        log.info("耗材ID: {}, 请求参数: {}", id, dto);
        log.info("权限检查: material:edit");

        boolean result = materialService.updateMaterial(id, dto);

        log.info("更新结果: {}", result);
        log.info("========== 更新耗材结束 ==========");

        return ApiResponse.success(result);
    }

    /**
     * 删除耗材
     * 权限：耗材管理-删除
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除耗材")
    @PreAuthorize("hasAuthority('material:delete')")
    public ApiResponse<Boolean> deleteMaterial(
            @Parameter(description = "耗材ID") @PathVariable Long id) {
        log.info("========== 删除耗材 ==========");
        log.info("耗材ID: {}", id);
        log.info("权限检查: material:delete");

        boolean result = materialService.deleteMaterial(id);

        log.info("删除结果: {}", result);
        log.info("========== 删除耗材结束 ==========");

        return ApiResponse.success(result);
    }

    /**
     * 获取耗材详情
     * 权限：耗材管理-查看
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取耗材详情")
    @PreAuthorize("hasAuthority('material:view')")
    public ApiResponse<MaterialVO> getMaterial(
            @Parameter(description = "耗材ID") @PathVariable Long id) {
        log.info("========== 获取耗材详情 ==========");
        log.info("耗材ID: {}", id);
        log.info("权限检查: material:view");

        MaterialVO material = materialService.getMaterialById(id);

        log.info("查询成功，耗材名称: {}", material.getMaterialName());
        log.info("========== 获取耗材详情结束 ==========");

        return ApiResponse.success(material);
    }

    // ==================== 分页查询接口 ====================

    /**
     * 分页查询耗材列表
     * 权限：耗材管理-查看
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询耗材列表")
    @PreAuthorize("hasAuthority('material:view')")
    public ApiResponse<MaterialPageVO> getMaterialPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "耗材名称") @RequestParam(required = false) String materialName,
            @Parameter(description = "耗材编码") @RequestParam(required = false) String materialCode,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "品牌") @RequestParam(required = false) String brand,
            @Parameter(description = "制造商") @RequestParam(required = false) String manufacturer,
            @Parameter(description = "状态（0-禁用，1-启用）") @RequestParam(required = false) Integer status,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime) {
        log.info("========== 分页查询耗材列表 ==========");
        log.info("当前页: {}, 每页大小: {}, 耗材名称: {}, 耗材编码: {}, 分类ID: {}, 品牌: {}, 制造商: {}, 状态: {}, 开始时间: {}, 结束时间: {}",
                current, size, materialName, materialCode, categoryId, brand, manufacturer, status, startTime, endTime);
        log.info("权限检查: material:view");

        MaterialPageVO pageVO = materialService.getMaterialPage(current, size, materialName, materialCode,
                categoryId, brand, manufacturer, status, startTime, endTime);

        log.info("查询成功，总记录数: {}", pageVO.getTotal());
        log.info("========== 分页查询耗材列表结束 ==========");

        return ApiResponse.success(pageVO);
    }

    // ==================== 批量操作接口 ====================

    /**
     * 批量删除耗材
     * 权限：耗材管理-删除
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除耗材")
    @PreAuthorize("hasAuthority('material:delete')")
    public ApiResponse<Boolean> batchDeleteMaterials(
            @Parameter(description = "耗材ID列表") @RequestBody List<Long> ids) {
        log.info("========== 批量删除耗材 ==========");
        log.info("耗材ID列表: {}", ids);
        log.info("权限检查: material:delete");

        boolean result = materialService.batchDeleteMaterials(ids);

        log.info("批量删除结果: {}", result);
        log.info("========== 批量删除耗材结束 ==========");

        return ApiResponse.success(result);
    }

    // ==================== 状态管理接口 ====================

    /**
     * 切换耗材状态
     * 权限：耗材管理-编辑
     */
    @PutMapping("/{id}/toggle-status")
    @Operation(summary = "切换耗材状态")
    @PreAuthorize("hasAuthority('material:edit')")
    public ApiResponse<Boolean> toggleMaterialStatus(
            @Parameter(description = "耗材ID") @PathVariable Long id) {
        log.info("========== 切换耗材状态 ==========");
        log.info("耗材ID: {}", id);
        log.info("权限检查: material:edit");

        boolean result = materialService.toggleMaterialStatus(id);

        log.info("切换状态结果: {}", result);
        log.info("========== 切换耗材状态结束 ==========");

        return ApiResponse.success(result);
    }

    // ==================== 验证接口 ====================

    /**
     * 检查耗材编码是否存在
     * 权限：无需登录（用于表单验证）
     */
    @GetMapping("/check/code")
    @Operation(summary = "检查耗材编码是否存在")
    public ApiResponse<Boolean> checkMaterialCode(
            @Parameter(description = "耗材编码") @RequestParam String materialCode,
            @Parameter(description = "排除的耗材ID（更新时使用）") @RequestParam(required = false) Long excludeId) {
        log.info("========== 检查耗材编码是否存在 ==========");
        log.info("耗材编码: {}, 排除ID: {}", materialCode, excludeId);

        boolean exists;
        if (excludeId != null) {
            exists = materialService.existsByMaterialCodeExcludeId(materialCode, excludeId);
        } else {
            exists = materialService.existsByMaterialCode(materialCode);
        }

        log.info("检查结果: {}", exists);
        log.info("========== 检查耗材编码是否存在结束 ==========");

        return ApiResponse.success(exists);
    }

    // ==================== 查询接口 ====================

    /**
     * 根据分类ID查询耗材列表
     * 权限：耗材管理-查看
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类ID查询耗材列表")
    @PreAuthorize("hasAuthority('material:view')")
    public ApiResponse<List<MaterialVO>> getMaterialsByCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        log.info("========== 根据分类ID查询耗材列表 ==========");
        log.info("分类ID: {}", categoryId);
        log.info("权限检查: material:view");

        List<MaterialVO> materials = materialService.getMaterialsByCategoryId(categoryId);

        log.info("查询成功，耗材数量: {}", materials.size());
        log.info("========== 根据分类ID查询耗材列表结束 ==========");

        return ApiResponse.success(materials);
    }

    /**
     * 搜索耗材
     * 权限：耗材管理-查看
     */
    @GetMapping("/search")
    @Operation(summary = "搜索耗材")
    @PreAuthorize("hasAuthority('material:view')")
    public ApiResponse<List<MaterialVO>> searchMaterials(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        log.info("========== 搜索耗材 ==========");
        log.info("关键词: {}", keyword);
        log.info("权限检查: material:view");

        List<MaterialVO> materials = materialService.searchMaterials(keyword);

        log.info("搜索成功，耗材数量: {}", materials.size());
        log.info("========== 搜索耗材结束 ==========");

        return ApiResponse.success(materials);
    }

    // ==================== 图片管理接口 ====================

    /**
     * 上传耗材图片
     * 权限：耗材管理-编辑
     */
    @PostMapping("/{id}/upload-image")
    @Operation(summary = "上传耗材图片")
    @PreAuthorize("hasAuthority('material:edit')")
    public ApiResponse<String> uploadMaterialImage(
            @Parameter(description = "耗材ID") @PathVariable Long id,
            @Parameter(description = "图片文件") @RequestParam("file") MultipartFile file) {
        log.info("========== 上传耗材图片 ==========");
        log.info("耗材ID: {}, 文件名: {}, 文件大小: {}", id, file.getOriginalFilename(), file.getSize());
        log.info("权限检查: material:edit");

        // 检查文件是否为空
        if (file.isEmpty()) {
            log.error("上传文件为空");
            return ApiResponse.<String>error("上传文件不能为空");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            log.error("文件类型不支持: {}", contentType);
            return ApiResponse.<String>error("只支持图片文件上传");
        }

        try {
            // 将MultipartFile转换为byte数组
            byte[] imageBytes = file.getBytes();

            String imageUrl = materialService.uploadImage(id, imageBytes);

            log.info("上传成功，耗材ID: {}, 图片URL: {}", id, imageUrl);
            log.info("========== 上传耗材图片结束 ==========");

            return new ApiResponse<>(200, "success", imageUrl, LocalDateTime.now(), null);
        } catch (Exception e) {
            log.error("上传耗材图片失败", e);
            return ApiResponse.<String>error("上传耗材图片失败: " + e.getMessage());
        }
    }

    /**
     * 删除耗材图片
     * 权限：耗材管理-编辑
     */
    @DeleteMapping("/{id}/image/{imageId}")
    @Operation(summary = "删除耗材图片")
    @PreAuthorize("hasAuthority('material:edit')")
    public ApiResponse<Boolean> deleteMaterialImage(
            @Parameter(description = "耗材ID") @PathVariable Long id,
            @Parameter(description = "图片ID") @PathVariable String imageId) {
        log.info("========== 删除耗材图片 ==========");
        log.info("耗材ID: {}, 图片ID: {}", id, imageId);
        log.info("权限检查: material:edit");

        boolean result = materialService.deleteImage(id, imageId);

        log.info("删除结果: {}", result);
        log.info("========== 删除耗材图片结束 ==========");

        return ApiResponse.success(result);
    }

    // ==================== 编码生成接口 ====================

    /**
     * 生成耗材编码
     * 权限：耗材管理-查看（用于表单辅助功能）
     */
    @GetMapping("/generate-code")
    @Operation(summary = "生成耗材编码")
    public ApiResponse<String> generateMaterialCode(
            @Parameter(description = "分类ID") @RequestParam Long categoryId) {
        log.info("========== 生成耗材编码 ==========");
        log.info("分类ID: {}", categoryId);

        String code = materialService.generateMaterialCode(categoryId);

        log.info("生成编码成功: {}", code);
        log.info("========== 生成耗材编码结束 ==========");

        return new ApiResponse<>(200, "success", code, LocalDateTime.now(), null);
    }

    /**
     * 生成耗材条码
     * 权限：耗材管理-查看
     */
    @GetMapping("/{id}/barcode")
    @Operation(summary = "生成耗材条码")
    @PreAuthorize("hasAuthority('material:view')")
    public ApiResponse<String> generateBarcode(
            @Parameter(description = "耗材ID") @PathVariable Long id) {
        log.info("========== 生成耗材条码 ==========");
        log.info("耗材ID: {}", id);
        log.info("权限检查: material:view");

        String barcode = materialService.generateBarcode(id);

        log.info("条码生成成功");
        log.info("========== 生成耗材条码结束 ==========");

        return new ApiResponse<>(200, "success", barcode, LocalDateTime.now(), null);
    }

    /**
     * 生成耗材二维码
     * 权限：耗材管理-查看
     */
    @GetMapping("/{id}/qr-code")
    @Operation(summary = "生成耗材二维码")
    @PreAuthorize("hasAuthority('material:view')")
    public ApiResponse<String> generateQRCode(
            @Parameter(description = "耗材ID") @PathVariable Long id) {
        log.info("========== 生成耗材二维码 ==========");
        log.info("耗材ID: {}", id);
        log.info("权限检查: material:view");

        String qrCode = materialService.generateQRCode(id);

        log.info("二维码生成成功");
        log.info("========== 生成耗材二维码结束 ==========");

        return new ApiResponse<>(200, "success", qrCode, LocalDateTime.now(), null);
    }

}
