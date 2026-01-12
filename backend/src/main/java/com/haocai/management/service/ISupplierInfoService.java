package com.haocai.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haocai.management.dto.SupplierCreateDTO;
import com.haocai.management.dto.SupplierQueryDTO;
import com.haocai.management.dto.SupplierUpdateDTO;
import com.haocai.management.dto.SupplierVO;
import com.haocai.management.entity.SupplierInfo;

import java.util.List;

/**
 * 供应商信息Service接口
 * 
 * 遵循development-standards.md中的服务层规范：
 * - Service命名规范：使用I+业务名称+Service后缀
 * - 继承IService：获得MyBatis-Plus提供的CRUD方法
 * - 方法命名规范：使用动词+名词，清晰表达业务含义
 * - 事务管理：在实现类中使用@Transactional注解
 * 
 * @author haocai
 * @since 2026-01-12
 */
public interface ISupplierInfoService extends IService<SupplierInfo> {

    /**
     * 创建供应商
     * 
     * 遵循：后端开发规范-第2.2条（事务管理：涉及多表操作或数据一致性要求的业务方法，必须添加@Transactional）
     * 
     * @param createDTO 创建请求DTO
     * @return 创建的供应商ID
     */
    Long createSupplier(SupplierCreateDTO createDTO);

    /**
     * 更新供应商
     * 
     * @param id 供应商ID
     * @param updateDTO 更新请求DTO
     * @return 是否成功
     */
    boolean updateSupplier(Long id, SupplierUpdateDTO updateDTO);

    /**
     * 删除供应商（逻辑删除）
     * 
     * 遵循：后端开发规范-第2.2条（事务管理：删除操作需要检查关联数据）
     * 
     * @param id 供应商ID
     * @return 是否成功
     */
    boolean deleteSupplier(Long id);

    /**
     * 批量删除供应商（逻辑删除）
     * 
     * @param ids 供应商ID列表
     * @return 是否成功
     */
    boolean batchDeleteSuppliers(List<Long> ids);

    /**
     * 根据ID查询供应商
     * 
     * @param id 供应商ID
     * @return 供应商信息
     */
    SupplierVO getSupplierById(Long id);

    /**
     * 分页查询供应商列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<SupplierVO> getSupplierPage(SupplierQueryDTO queryDTO);

    /**
     * 查询供应商列表（不分页）
     * 
     * @param queryDTO 查询条件
     * @return 供应商列表
     */
    List<SupplierVO> getSupplierList(SupplierQueryDTO queryDTO);

    /**
     * 切换供应商状态
     * 
     * @param id 供应商ID
     * @return 是否成功
     */
    boolean toggleStatus(Long id);

    /**
     * 更新供应商状态
     * 
     * @param id 供应商ID
     * @param status 新状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 批量更新供应商状态
     * 
     * 遵循：后端开发规范-第2.2条（批量操作：禁止直接批量更新不存在的ID）
     * 
     * @param ids 供应商ID列表
     * @param status 新状态
     * @return 成功数量
     */
    int batchUpdateStatus(List<Long> ids, Integer status);

    /**
     * 生成供应商编码
     * 
     * 遵循：数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）
     * 编码格式：SUP + 年月日 + 流水号
     * 示例：SUP20260112001
     * 
     * @return 生成的供应商编码
     */
    String generateSupplierCode();

    /**
     * 检查供应商编码是否存在
     * 
     * @param supplierCode 供应商编码
     * @return 是否存在
     */
    boolean existsBySupplierCode(String supplierCode);

    /**
     * 检查供应商编码是否存在（排除指定ID）
     * 
     * @param supplierCode 供应商编码
     * @param excludeId 排除的供应商ID
     * @return 是否存在
     */
    boolean existsBySupplierCodeExcludeId(String supplierCode, Long excludeId);

    /**
     * 获取供应商关联的耗材数量
     * 
     * @param supplierId 供应商ID
     * @return 耗材数量
     */
    int countSupplierMaterials(Long supplierId);

    /**
     * 获取供应商关联的入库单数量
     * 
     * @param supplierId 供应商ID
     * @return 入库单数量
     */
    int countSupplierInboundOrders(Long supplierId);

    /**
     * 根据合作状态查询供应商列表
     * 
     * @param cooperationStatus 合作状态
     * @return 供应商列表
     */
    List<SupplierVO> getSuppliersByCooperationStatus(Integer cooperationStatus);

    /**
     * 根据信用等级范围查询供应商
     * 
     * @param minRating 最小信用等级
     * @param maxRating 最大信用等级
     * @return 供应商列表
     */
    List<SupplierVO> getSuppliersByCreditRatingRange(Integer minRating, Integer maxRating);

    /**
     * 搜索供应商
     * 
     * @param keyword 搜索关键词
     * @return 供应商列表
     */
    List<SupplierVO> searchSuppliers(String keyword);
}
