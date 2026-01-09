package com.haocai.management.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Material实体类测试
 * 验证Day 6计划中新增字段的正确性
 */
public class MaterialTest {

    @Test
    public void testMaterialEntityFields() {
        // 创建Material实例
        Material material = new Material();

        // 设置Day 6新增字段
        material.setSupplierId(1L);
        material.setBarcode("6901234567890");
        material.setQrCode("https://example.com/qr/123");
        material.setUnitPrice(new BigDecimal("25.50"));
        material.setTechnicalParameters("过滤效率≥95%，呼吸阻力≤175Pa");
        material.setUsageInstructions("佩戴时确保口罩完全覆盖口鼻");
        material.setStorageRequirements("避光、干燥处保存");
        material.setImageUrl("https://example.com/images/material/123.jpg");

        // 设置原有字段
        material.setMaterialName("医用口罩");
        material.setMaterialCode("MAT001");
        material.setCategoryId(1L);
        material.setSpecification("N95");
        material.setUnit("个");
        material.setBrand("3M");
        material.setManufacturer("3M中国有限公司");
        material.setMinStock(100);
        material.setMaxStock(1000);
        material.setSafetyStock(200);
        material.setDescription("医用防护口罩");
        material.setStatus(1);

        // 验证Day 6新增字段
        assertEquals(1L, material.getSupplierId());
        assertEquals("6901234567890", material.getBarcode());
        assertEquals("https://example.com/qr/123", material.getQrCode());
        assertEquals(new BigDecimal("25.50"), material.getUnitPrice());
        assertEquals("过滤效率≥95%，呼吸阻力≤175Pa", material.getTechnicalParameters());
        assertEquals("佩戴时确保口罩完全覆盖口鼻", material.getUsageInstructions());
        assertEquals("避光、干燥处保存", material.getStorageRequirements());
        assertEquals("https://example.com/images/material/123.jpg", material.getImageUrl());

        // 验证原有字段
        assertEquals("医用口罩", material.getMaterialName());
        assertEquals("MAT001", material.getMaterialCode());
        assertEquals(1L, material.getCategoryId());
        assertEquals("N95", material.getSpecification());
        assertEquals("个", material.getUnit());
        assertEquals("3M", material.getBrand());
        assertEquals("3M中国有限公司", material.getManufacturer());
        assertEquals(100, material.getMinStock());
        assertEquals(1000, material.getMaxStock());
        assertEquals(200, material.getSafetyStock());
        assertEquals("医用防护口罩", material.getDescription());
        assertEquals(1, material.getStatus());
    }

    @Test
    public void testMaterialToString() {
        Material material = new Material();
        material.setId(1L);
        material.setMaterialName("医用口罩");
        material.setMaterialCode("MAT001");
        material.setSupplierId(1L);
        material.setBarcode("6901234567890");

        // 验证toString方法包含新字段
        String toString = material.toString();
        assertTrue(toString.contains("materialName=医用口罩"));
        assertTrue(toString.contains("materialCode=MAT001"));
        assertTrue(toString.contains("supplierId=1"));
        assertTrue(toString.contains("barcode=6901234567890"));
    }

    @Test
    public void testMaterialEqualsAndHashCode() {
        Material material1 = new Material();
        material1.setId(1L);
        material1.setMaterialName("医用口罩");
        material1.setSupplierId(1L);

        Material material2 = new Material();
        material2.setId(1L);
        material2.setMaterialName("医用口罩");
        material2.setSupplierId(1L);

        Material material3 = new Material();
        material3.setId(2L);
        material3.setMaterialName("医用口罩");
        material3.setSupplierId(1L);

        // 验证equals方法
        assertEquals(material1, material2);
        assertNotEquals(material1, material3);

        // 验证hashCode方法
        assertEquals(material1.hashCode(), material2.hashCode());
        assertNotEquals(material1.hashCode(), material3.hashCode());
    }
}