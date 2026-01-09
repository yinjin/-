package com.haocai.management.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * 文件上传工具类
 *
 * 遵循development-standards.md中的工具类规范：
 * - 工具类命名规范：使用业务名称+Utils后缀
 * - 静态方法：提供静态方法供外部调用
 * - 异常处理：对可能的异常进行捕获和处理
 * - 日志记录：使用Slf4j记录操作日志
 *
 * @author haocai
 * @since 2026-01-09
 */
@Slf4j
public class FileUploadUtils {

    // 支持的图片类型
    private static final List<String> IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp");

    // 支持的文件类型
    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp",  // 图片
            "pdf", "doc", "docx", "xls", "xlsx", "txt"  // 文档
    );

    // 最大文件大小（10MB）
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @param uploadDir 上传目录
     * @return 文件访问路径
     */
    public static String uploadFile(MultipartFile file, String uploadDir) {
        log.info("开始上传文件，原始文件名：{}", file.getOriginalFilename());

        // 验证文件
        validateFile(file);

        // 生成唯一文件名
        String originalFileName = file.getOriginalFilename();
        String extension = getFileExtension(originalFileName);
        String newFileName = IdUtil.getSnowflakeNextIdStr() + "." + extension;

        // 构建完整路径
        String fullPath = Paths.get(uploadDir, newFileName).toString();

        try {
            // 保存文件
            FileUtil.writeBytes(file.getBytes(), fullPath);
            log.info("文件上传成功，保存路径：{}", fullPath);
            return "/uploads/" + newFileName; // 返回相对路径
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 验证文件
     *
     * @param file 上传的文件
     */
    private static void validateFile(MultipartFile file) {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("上传文件大小不能超过10MB");
        }

        // 检查文件类型
        String fileName = file.getOriginalFilename();
        String extension = getFileExtension(fileName);
        if (!ALLOWED_TYPES.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("不支持的文件类型：" + extension);
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    private static String getFileExtension(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    /**
     * 检查是否为图片文件
     *
     * @param fileName 文件名
     * @return 是否为图片
     */
    public static boolean isImageFile(String fileName) {
        String extension = getFileExtension(fileName);
        return IMAGE_TYPES.contains(extension.toLowerCase());
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        log.info("删除文件，路径：{}", filePath);
        try {
            String fullPath = System.getProperty("user.dir") + filePath.replace("/", "\\"); // Windows路径
            boolean deleted = FileUtil.del(fullPath);
            log.info("文件删除{}", deleted ? "成功" : "失败");
            return deleted;
        } catch (Exception e) {
            log.error("删除文件失败", e);
            return false;
        }
    }
}