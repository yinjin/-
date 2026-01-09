package com.haocai.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haocai.management.dto.MaterialCreateDTO;
import com.haocai.management.dto.MaterialUpdateDTO;
import com.haocai.management.entity.Material;
import com.haocai.management.entity.MaterialCategory;
import com.haocai.management.exception.BusinessException;
import com.haocai.management.mapper.MaterialMapper;
import com.haocai.management.service.IMaterialCategoryService;
import com.haocai.management.service.IMaterialService;
import com.haocai.management.utils.BarcodeUtils;
import com.haocai.management.utils.FileUploadUtils;
import com.haocai.management.vo.MaterialPageVO;
import com.haocai.management.vo.MaterialVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 耗材业务逻辑层实现类
 * 
 * 遵循development-standards.md中的业务逻辑层规范：
 * - 使用@Service注解标记为服务类
 * - 使用@Transactional进行事务管理
 * - 使用@Slf4j进行日志记录
 * - 使用@RequiredArgsConstructor进行依赖注入
 * - 实现业务逻辑，处理数据转换和业务规则
 * 
 * @author haocai
 * @since 2026-01-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> 
        implements IMaterialService {

    private final MaterialMapper materialMapper;
    private final IMaterialCategoryService materialCategoryService;

    /**
     * 创建耗材
     * 
     * 业务规则：
     * 1. 耗材编码不能重复
     * 2. 分类必须存在
     * 3. 自动设置默认值
     * 
     * @param createDTO 创建请求DTO
     * @return 创建的耗材ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMaterial(MaterialCreateDTO createDTO) {
        log.info("开始创建耗材，耗材名称：{}", createDTO.getMaterialName());
        
        // 1. 检查耗材编码是否已存在
        if (existsByMaterialCode(createDTO.getMaterialCode())) {
            log.error("耗材编码已存在：{}", createDTO.getMaterialCode());
            throw BusinessException.paramError("耗材编码已存在：" + createDTO.getMaterialCode());
        }
        
        // 2. 检查分类是否存在
        MaterialCategory category = materialCategoryService.getById(createDTO.getCategoryId());
        if (category == null) {
            log.error("分类不存在，分类ID：{}", createDTO.getCategoryId());
            throw BusinessException.dataNotFound("分类不存在");
        }
        
        // 3. 创建耗材实体
        Material material = new Material();
        BeanUtils.copyProperties(createDTO, material);
        
        // 4. 设置默认值
        if (material.getStatus() == null) {
            material.setStatus(1); // 默认启用
        }
        
        // 5. 保存耗材
        save(material);
        
        log.info("耗材创建成功，耗材ID：{}，耗材名称：{}", material.getId(), material.getMaterialName());
        return material.getId();
    }

    /**
     * 更新耗材
     * 
     * 业务规则：
     * 1. 耗材编码不能与其他耗材重复
     * 2. 分类必须存在
     * 
     * @param id 耗材ID
     * @param updateDTO 更新请求DTO
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMaterial(Long id, MaterialUpdateDTO updateDTO) {
        log.info("开始更新耗材，耗材ID：{}", id);
        
        // 1. 检查耗材是否存在
        Material material = getById(id);
        if (material == null) {
            log.error("耗材不存在，耗材ID：{}", id);
            throw BusinessException.dataNotFound("耗材不存在");
        }
        
        // 2. 检查耗材编码是否与其他耗材重复
        if (!updateDTO.getMaterialCode().equals(material.getMaterialCode())) {
            if (existsByMaterialCodeExcludeId(updateDTO.getMaterialCode(), id)) {
                log.error("耗材编码已存在：{}", updateDTO.getMaterialCode());
                throw BusinessException.paramError("耗材编码已存在：" + updateDTO.getMaterialCode());
            }
        }
        
        // 3. 检查分类是否存在
        MaterialCategory category = materialCategoryService.getById(updateDTO.getCategoryId());
        if (category == null) {
            log.error("分类不存在，分类ID：{}", updateDTO.getCategoryId());
            throw BusinessException.dataNotFound("分类不存在");
        }
        
        // 4. 更新耗材信息
        BeanUtils.copyProperties(updateDTO, material, "id");
        
        // 5. 保存更新
        boolean result = updateById(material);
        
        log.info("耗材更新成功，耗材ID：{}，耗材名称：{}", id, material.getMaterialName());
        return result;
    }

    /**
     * 删除耗材（逻辑删除）
     * 
     * 业务规则：
     * 1. 使用逻辑删除，不物理删除数据
     * 
     * @param id 耗材ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMaterial(Long id) {
        log.info("开始删除耗材，耗材ID：{}", id);
        
        // 1. 检查耗材是否存在
        Material material = getById(id);
        if (material == null) {
            log.error("耗材不存在，耗材ID：{}", id);
            throw BusinessException.dataNotFound("耗材不存在");
        }
        
        // 2. 逻辑删除耗材
        boolean result = removeById(id);
        
        log.info("耗材删除成功，耗材ID：{}", id);
        return result;
    }

    /**
     * 批量删除耗材（逻辑删除）
     * 
     * 业务规则：
     * 1. 使用逻辑删除，不物理删除数据
     * 2. 使用批量操作提高性能
     * 
     * @param ids 耗材ID列表
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteMaterials(List<Long> ids) {
        log.info("开始批量删除耗材，耗材ID列表：{}", ids);
        
        // 1. 检查所有耗材是否存在
        List<Material> materials = listByIds(ids);
        if (materials.size() != ids.size()) {
            log.error("部分耗材不存在");
            throw BusinessException.dataNotFound("部分耗材不存在");
        }
        
        // 2. 批量逻辑删除耗材
        boolean result = removeByIds(ids);
        
        log.info("批量删除耗材成功，删除数量：{}", ids.size());
        return result;
    }

    /**
     * 根据ID查询耗材
     * 
     * @param id 耗材ID
     * @return 耗材信息VO
     */
    @Override
    public MaterialVO getMaterialById(Long id) {
        log.info("查询耗材，耗材ID：{}", id);
        
        Material material = getById(id);
        if (material == null) {
            log.error("耗材不存在，耗材ID：{}", id);
            throw BusinessException.dataNotFound("耗材不存在");
        }
        
        MaterialVO vo = convertToVO(material);
        
        log.info("查询耗材成功，耗材ID：{}", id);
        return vo;
    }

    /**
     * 分页查询耗材列表
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param materialName 耗材名称（可选）
     * @param materialCode 耗材编码（可选）
     * @param categoryId 分类ID（可选）
     * @param brand 品牌（可选）
     * @param manufacturer 制造商（可选）
     * @param status 状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页结果
     */
    @Override
    public MaterialPageVO getMaterialPage(Long current, Long size, String materialName, String materialCode, 
            Long categoryId, String brand, String manufacturer, Integer status, String startTime, String endTime) {
        log.info("分页查询耗材列表，当前页：{}，每页大小：{}，耗材名称：{}，耗材编码：{}，分类ID：{}，品牌：{}，制造商：{}，状态：{}，开始时间：{}，结束时间：{}", 
                current, size, materialName, materialCode, categoryId, brand, manufacturer, status, startTime, endTime);
        
        // 1. 创建分页对象
        Page<Material> page = new Page<>(current, size);
        
        // 2. 构建查询条件
        LambdaQueryWrapper<Material> queryWrapper = new LambdaQueryWrapper<>();
        
        // 耗材名称模糊查询
        if (materialName != null && !materialName.trim().isEmpty()) {
            queryWrapper.like(Material::getMaterialName, materialName);
        }
        
        // 耗材编码模糊查询
        if (materialCode != null && !materialCode.trim().isEmpty()) {
            queryWrapper.like(Material::getMaterialCode, materialCode);
        }
        
        // 分类ID精确查询
        if (categoryId != null) {
            queryWrapper.eq(Material::getCategoryId, categoryId);
        }
        
        // 品牌模糊查询
        if (brand != null && !brand.trim().isEmpty()) {
            queryWrapper.like(Material::getBrand, brand);
        }
        
        // 制造商模糊查询
        if (manufacturer != null && !manufacturer.trim().isEmpty()) {
            queryWrapper.like(Material::getManufacturer, manufacturer);
        }
        
        // 状态精确查询
        if (status != null) {
            queryWrapper.eq(Material::getStatus, status);
        }
        
        // 创建时间范围查询
        if (startTime != null && !startTime.trim().isEmpty()) {
            queryWrapper.ge(Material::getCreateTime, startTime);
        }
        if (endTime != null && !endTime.trim().isEmpty()) {
            queryWrapper.le(Material::getCreateTime, endTime);
        }
        
        // 按ID降序排序
        queryWrapper.orderByDesc(Material::getId);
        
        // 3. 执行分页查询
        IPage<Material> materialPage = page(page, queryWrapper);
        
        // 4. 转换为VO
        MaterialPageVO pageVO = new MaterialPageVO();
        pageVO.setTotal(materialPage.getTotal());
        pageVO.setCurrent(materialPage.getCurrent());
        pageVO.setSize(materialPage.getSize());
        pageVO.setPages(materialPage.getPages());
        
        List<MaterialVO> records = materialPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        pageVO.setRecords(records);
        
        log.info("分页查询耗材列表成功，总记录数：{}", pageVO.getTotal());
        return pageVO;
    }

    /**
     * 切换耗材状态
     * 
     * @param id 耗材ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleMaterialStatus(Long id) {
        log.info("开始切换耗材状态，耗材ID：{}", id);
        
        Material material = getById(id);
        if (material == null) {
            log.error("耗材不存在，耗材ID：{}", id);
            throw BusinessException.dataNotFound("耗材不存在");
        }
        
        // 切换状态：0-禁用，1-启用
        material.setStatus(material.getStatus() == 1 ? 0 : 1);
        boolean result = updateById(material);
        
        log.info("切换耗材状态成功，耗材ID：{}，新状态：{}", id, material.getStatus());
        return result;
    }

    /**
     * 检查耗材编码是否存在
     * 
     * @param materialCode 耗材编码
     * @return 是否存在
     */
    @Override
    public boolean existsByMaterialCode(String materialCode) {
        Material material = materialMapper.selectByMaterialCode(materialCode);
        return material != null;
    }

    /**
     * 检查耗材编码是否存在（排除指定ID）
     * 
     * @param materialCode 耗材编码
     * @param excludeId 排除的耗材ID
     * @return 是否存在
     */
    @Override
    public boolean existsByMaterialCodeExcludeId(String materialCode, Long excludeId) {
        int count = materialMapper.countByMaterialCodeExcludeId(materialCode, excludeId);
        return count > 0;
    }

    /**
     * 根据分类ID查询耗材列表
     * 
     * @param categoryId 分类ID
     * @return 耗材列表
     */
    @Override
    public List<MaterialVO> getMaterialsByCategoryId(Long categoryId) {
        log.info("根据分类ID查询耗材列表，分类ID：{}", categoryId);
        
        List<Material> materials = materialMapper.selectByCategoryId(categoryId);
        
        List<MaterialVO> vos = materials.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        log.info("根据分类ID查询耗材列表成功，耗材数量：{}", vos.size());
        return vos;
    }

    /**
     * 搜索耗材
     * 
     * @param keyword 搜索关键词
     * @return 耗材列表
     */
    @Override
    public List<MaterialVO> searchMaterials(String keyword) {
        log.info("搜索耗材，关键词：{}", keyword);
        
        List<Material> materials = materialMapper.searchMaterials(keyword);
        
        List<MaterialVO> vos = materials.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        log.info("搜索耗材成功，耗材数量：{}", vos.size());
        return vos;
    }

    /**
     * 将Material实体转换为MaterialVO
     *
     * @param material 耗材实体
     * @return 耗材VO
     */
    private MaterialVO convertToVO(Material material) {
        MaterialVO vo = new MaterialVO();
        BeanUtils.copyProperties(material, vo);

        // 查询分类名称
        if (material.getCategoryId() != null) {
            MaterialCategory category = materialCategoryService.getById(material.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getCategoryName());
            }
        }

        return vo;
    }

    /**
     * 生成耗材编码
     *
     * @param categoryId 分类ID
     * @return 生成的耗材编码
     */
    @Override
    public String generateMaterialCode(Long categoryId) {
        log.info("开始生成耗材编码，分类ID：{}", categoryId);

        // 获取分类信息
        MaterialCategory category = materialCategoryService.getById(categoryId);
        if (category == null) {
            log.error("分类不存在，分类ID：{}", categoryId);
            throw BusinessException.dataNotFound("分类不存在");
        }

        // 生成流水号（这里简化为当前时间戳，实际项目中应该使用更复杂的算法）
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
        String code = category.getCategoryCode() + "-" + timestamp;

        log.info("生成耗材编码成功：{}", code);
        return code;
    }

    /**
     * 生成耗材条码
     *
     * @param id 耗材ID
     * @return 条码图片的Base64编码
     */
    @Override
    public String generateBarcode(Long id) {
        log.info("开始生成耗材条码，耗材ID：{}", id);

        Material material = getById(id);
        if (material == null) {
            log.error("耗材不存在，耗材ID：{}", id);
            throw BusinessException.dataNotFound("耗材不存在");
        }

        // 使用耗材编码作为条码内容
        String barcodeContent = material.getMaterialCode();
        String barcodeImage = BarcodeUtils.generateBarcode(barcodeContent);

        // 更新耗材信息中的条码字段
        material.setBarcode(barcodeContent);
        updateById(material);

        log.info("生成耗材条码成功，耗材ID：{}", id);
        return barcodeImage;
    }

    /**
     * 生成耗材二维码
     *
     * @param id 耗材ID
     * @return 二维码图片的Base64编码
     */
    @Override
    public String generateQRCode(Long id) {
        log.info("开始生成耗材二维码，耗材ID：{}", id);

        Material material = getById(id);
        if (material == null) {
            log.error("耗材不存在，耗材ID：{}", id);
            throw BusinessException.dataNotFound("耗材不存在");
        }

        // 使用耗材ID作为二维码内容，实际项目中可以包含更多信息
        String qrContent = "material:" + id + ",code:" + material.getMaterialCode();
        String qrImage = BarcodeUtils.generateQRCode(qrContent);

        // 更新耗材信息中的二维码字段
        material.setQrCode(qrContent);
        updateById(material);

        log.info("生成耗材二维码成功，耗材ID：{}", id);
        return qrImage;
    }

    /**
     * 上传耗材图片
     *
     * @param id 耗材ID
     * @param imageFile 图片文件
     * @return 图片URL
     */
    @Override
    public String uploadImage(Long id, byte[] imageFile) {
        log.info("开始上传耗材图片，耗材ID：{}", id);

        Material material = getById(id);
        if (material == null) {
            log.error("耗材不存在，耗材ID：{}", id);
            throw BusinessException.dataNotFound("耗材不存在");
        }

        // 这里需要模拟文件上传，实际项目中需要接收MultipartFile类型的参数
        // 为了演示，我们假设已经有一个临时的文件路径
        // 实际实现中，需要在Controller中接收MultipartFile，然后传递给Service

        // 模拟上传过程
        String uploadDir = System.getProperty("user.dir") + "/uploads";
        // 创建上传目录
        java.io.File uploadDirFile = new java.io.File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        // 生成唯一的文件名
        String fileName = java.util.UUID.randomUUID().toString() + ".jpg";
        String fullPath = uploadDir + "/" + fileName;

        try {
            // 保存文件
            java.nio.file.Files.write(java.nio.file.Paths.get(fullPath), imageFile);

            // 更新耗材信息中的图片URL字段
            String imageUrl = "/uploads/" + fileName;
            material.setImageUrl(imageUrl);
            updateById(material);

            log.info("上传耗材图片成功，耗材ID：{}，图片URL：{}", id, imageUrl);
            return imageUrl;
        } catch (Exception e) {
            log.error("上传耗材图片失败", e);
            throw new RuntimeException("上传耗材图片失败", e);
        }
    }

    /**
     * 删除耗材图片
     *
     * @param id 耗材ID
     * @param imageId 图片ID
     * @return 是否成功
     */
    @Override
    public boolean deleteImage(Long id, String imageId) {
        log.info("开始删除耗材图片，耗材ID：{}，图片ID：{}", id, imageId);

        Material material = getById(id);
        if (material == null) {
            log.error("耗材不存在，耗材ID：{}", id);
            throw BusinessException.dataNotFound("耗材不存在");
        }

        // 这里简单地将图片URL设为null，实际项目中可能需要删除物理文件
        String oldImageUrl = material.getImageUrl();
        material.setImageUrl(null);
        boolean result = updateById(material);

        // 如果有旧图片，尝试删除物理文件
        if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
            FileUploadUtils.deleteFile(oldImageUrl);
        }

        log.info("删除耗材图片{}，耗材ID：{}，图片ID：{}", result ? "成功" : "失败", id, imageId);
        return result;
    }
}
