package com.haocai.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haocai.management.dto.MaterialCreateDTO;
import com.haocai.management.dto.MaterialUpdateDTO;
import com.haocai.management.entity.Material;
import com.haocai.management.vo.MaterialPageVO;
import com.haocai.management.vo.MaterialVO;

import java.util.List;

/**
 * 耗材Service接口
 *
 * 遵循development-standards.md中的服务层规范：
 * - Service命名规范：使用I+业务名称+Service后缀
 * - 继承IService：获得MyBatis-Plus提供的CRUD方法
 * - 方法命名规范：使用动词+名词，清晰表达业务含义
 * - 事务管理：在实现类中使用@Transactional注解
 *
 * @author haocai
 * @since 2026-01-09
 */
public interface IMaterialService extends IService<Material> {

    /**
     * 创建耗材
     *
     * @param createDTO 创建请求DTO
     * @return 创建的耗材ID
     */
    Long createMaterial(MaterialCreateDTO createDTO);

    /**
     * 更新耗材
     *
     * @param id 耗材ID
     * @param updateDTO 更新请求DTO
     * @return 是否成功
     */
    boolean updateMaterial(Long id, MaterialUpdateDTO updateDTO);

    /**
     * 删除耗材（逻辑删除）
     *
     * @param id 耗材ID
     * @return 是否成功
     */
    boolean deleteMaterial(Long id);

    /**
     * 批量删除耗材（逻辑删除）
     *
     * @param ids 耗材ID列表
     * @return 是否成功
     */
    boolean batchDeleteMaterials(List<Long> ids);

    /**
     * 根据ID查询耗材
     *
     * @param id 耗材ID
     * @return 耗材信息
     */
    MaterialVO getMaterialById(Long id);

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
    MaterialPageVO getMaterialPage(Long current, Long size, String materialName, String materialCode,
            Long categoryId, String brand, String manufacturer, Integer status, String startTime, String endTime);

    /**
     * 切换耗材状态
     *
     * @param id 耗材ID
     * @return 是否成功
     */
    boolean toggleMaterialStatus(Long id);

    /**
     * 检查耗材编码是否存在
     *
     * @param materialCode 耗材编码
     * @return 是否存在
     */
    boolean existsByMaterialCode(String materialCode);

    /**
     * 检查耗材编码是否存在（排除指定ID）
     *
     * @param materialCode 耗材编码
     * @param excludeId 排除的耗材ID
     * @return 是否存在
     */
    boolean existsByMaterialCodeExcludeId(String materialCode, Long excludeId);

    /**
     * 根据分类ID查询耗材列表
     *
     * @param categoryId 分类ID
     * @return 耗材列表
     */
    List<MaterialVO> getMaterialsByCategoryId(Long categoryId);

    /**
     * 搜索耗材
     *
     * @param keyword 搜索关键词
     * @return 耗材列表
     */
    List<MaterialVO> searchMaterials(String keyword);

    /**
     * 生成耗材编码
     *
     * @param categoryId 分类ID
     * @return 生成的耗材编码
     */
    String generateMaterialCode(Long categoryId);

    /**
     * 生成耗材条码
     *
     * @param id 耗材ID
     * @return 条码图片的Base64编码
     */
    String generateBarcode(Long id);

    /**
     * 生成耗材二维码
     *
     * @param id 耗材ID
     * @return 二维码图片的Base64编码
     */
    String generateQRCode(Long id);

    /**
     * 上传耗材图片
     *
     * @param id 耗材ID
     * @param imageFile 图片文件
     * @return 图片URL
     */
    String uploadImage(Long id, byte[] imageFile);

    /**
     * 删除耗材图片
     *
     * @param id 耗材ID
     * @param imageId 图片ID
     * @return 是否成功
     */
    boolean deleteImage(Long id, String imageId);
}
