package com.haocai.management.controller;

import com.haocai.management.common.ApiResponse;
import com.haocai.management.utils.FileUploadUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 文件访问控制器
 * 提供静态文件（如上传的图片）的访问功能
 *
 * @author haocai
 * @since 2026-01-09
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
@Tag(name = "文件访问接口")
@RequiredArgsConstructor
public class FileController {

    private static final String UPLOAD_DIR = "backend/uploads";

    /**
     * 访问上传的图片文件
     *
     * @param filename 文件名
     * @return 图片资源
     */
    @GetMapping("/uploads/{filename:.+}")
    @Operation(summary = "访问上传的图片")
    public ResponseEntity<Resource> getUploadedFile(
            @Parameter(description = "文件名") @PathVariable String filename) {
        log.info("访问上传文件: {}", filename);

        try {
            // 获取项目根目录
            String projectRoot = System.getProperty("user.dir");
            String filePath = projectRoot + "/" + UPLOAD_DIR + "/" + filename;

            File file = new File(filePath);

            if (!file.exists()) {
                log.error("文件不存在: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);

            // 根据文件扩展名确定Content-Type
            String contentType = getContentType(filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("访问文件失败: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据文件名获取Content-Type
     *
     * @param filename 文件名
     * @return Content-Type
     */
    private String getContentType(String filename) {
        if (filename == null) {
            return "application/octet-stream";
        }

        String lowerFilename = filename.toLowerCase();
        if (lowerFilename.endsWith(".png")) {
            return "image/png";
        } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerFilename.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerFilename.endsWith(".webp")) {
            return "image/webp";
        } else if (lowerFilename.endsWith(".svg")) {
            return "image/svg+xml";
        } else {
            return "application/octet-stream";
        }
    }

    /**
     * 文件上传接口
     * 
     * @param file 上传的文件
     * @return 上传结果
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "文件上传", description = "上传文件到服务器")
    public ApiResponse<String> uploadFile(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file) {
        log.info("文件上传请求，文件名：{}", file.getOriginalFilename());
        
        try {
            // 获取项目根目录
            String projectRoot = System.getProperty("user.dir");
            String uploadDir = projectRoot + "/" + UPLOAD_DIR;
            
            // 确保上传目录存在
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 上传文件
            String filePath = FileUploadUtils.uploadFile(file, uploadDir);
            
            log.info("文件上传成功，返回路径：{}", filePath);
            return ApiResponse.success(filePath, "文件上传成功");
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return ApiResponse.error("文件上传失败：" + e.getMessage());
        }
    }
}
