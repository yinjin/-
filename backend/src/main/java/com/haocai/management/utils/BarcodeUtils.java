package com.haocai.management.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 条码和二维码生成工具类
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
public class BarcodeUtils {

    /**
     * 生成条码（这里简化为返回文本，实际项目中需要集成ZXing库）
     *
     * @param content 条码内容
     * @return 条码图片的Base64编码
     */
    public static String generateBarcode(String content) {
        log.info("生成条码，内容：{}", content);
        
        try {
            // 在实际项目中，这里应该使用ZXing库生成条码
            // 为了演示，我们返回一个模拟的Base64字符串
            BufferedImage image = createMockBarcode(content);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            log.info("条码生成成功");
            return "data:image/png;base64," + base64Image;
        } catch (Exception e) {
            log.error("生成条码失败", e);
            throw new RuntimeException("生成条码失败", e);
        }
    }

    /**
     * 生成二维码
     *
     * @param content 二维码内容
     * @param width 宽度
     * @param height 高度
     * @return 二维码图片的Base64编码
     */
    public static String generateQRCode(String content, int width, int height) {
        log.info("生成二维码，内容：{}，尺寸：{}x{}", content, width, height);
        
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            log.info("二维码生成成功");
            return "data:image/png;base64," + base64Image;
        } catch (WriterException | IOException e) {
            log.error("生成二维码失败", e);
            throw new RuntimeException("生成二维码失败", e);
        }
    }

    /**
     * 生成二维码（默认尺寸）
     *
     * @param content 二维码内容
     * @return 二维码图片的Base64编码
     */
    public static String generateQRCode(String content) {
        return generateQRCode(content, 200, 200);
    }

    /**
     * 创建模拟条码图像（仅用于演示）
     *
     * @param content 条码内容
     * @return 模拟的条码图像
     */
    private static BufferedImage createMockBarcode(String content) {
        int width = 300;
        int height = 100;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = image.createGraphics();
        
        // 设置背景色
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, width, height);
        
        // 绘制简单的条码效果
        g.setColor(java.awt.Color.BLACK);
        int x = 10;
        for (char c : content.toCharArray()) {
            if ((int) c % 2 == 0) {
                g.fillRect(x, 20, 5, 60);
            } else {
                g.fillRect(x, 20, 3, 60);
            }
            x += 8;
        }
        
        // 添加文本
        g.drawString(content, 10, 90);
        g.dispose();
        
        return image;
    }
}