package com.haocai.management.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 耗材分页响应VO
 * 
 * 遵循development-standards.md中的VO设计规范：
 * - VO命名规范：使用业务名称+PageVO后缀
 * - 文档注解：使用Swagger注解描述字段
 * - 序列化：实现Serializable接口
 * - 包含分页信息：总记录数、当前页、每页大小、数据列表
 * 
 * @author haocai
 * @since 2026-01-09
 */
@Data
@Schema(description = "耗材分页响应")
public class MaterialPageVO {
    
    @Schema(description = "总记录数", example = "100")
    private Long total;
    
    @Schema(description = "当前页", example = "1")
    private Long current;
    
    @Schema(description = "每页大小", example = "10")
    private Long size;
    
    @Schema(description = "总页数", example = "10")
    private Long pages;
    
    @Schema(description = "耗材列表")
    private List<MaterialVO> records;
}
