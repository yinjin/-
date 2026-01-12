package com.haocai.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haocai.management.dto.SupplierQualificationCreateDTO;
import com.haocai.management.dto.SupplierQualificationUpdateDTO;
import com.haocai.management.dto.SupplierQualificationVO;

import java.util.List;

/**
 * 供应商资质Service接口
 * 
 * 遵循规范：
 * - 后端开发规范-第2.1条（Service接口定义）
 * 
 * @author haocai
 * @since 2026-01-12
 */
public interface ISupplierQualificationService {

    /**
     * 创建供应商资质
     * 
     * @param createDTO 创建请求
     * @param currentUserId 当前用户ID
     * @return 创建的资质ID
     */
    Long createQualification(SupplierQualificationCreateDTO createDTO, Long currentUserId);

    /**
     * 更新供应商资质
     * 
     * @param id 资质ID
     * @param updateDTO 更新请求
     * @return 是否成功
     */
    boolean updateQualification(Long id, SupplierQualificationUpdateDTO updateDTO);

    /**
     * 删除供应商资质
     * 
     * @param id 资质ID
     * @return 是否成功
     */
    boolean deleteQualification(Long id);

    /**
     * 批量删除供应商资质
     * 
     * @param ids 资质ID列表
     * @return 删除数量
     */
    int batchDeleteQualifications(List<Long> ids);

    /**
     * 获取供应商资质详情
     * 
     * @param id 资质ID
     * @return 资质信息
     */
    SupplierQualificationVO getQualificationById(Long id);

    /**
     * 获取供应商所有资质
     * 
     * @param supplierId 供应商ID
     * @return 资质列表
     */
    List<SupplierQualificationVO> getQualificationsBySupplierId(Long supplierId);

    /**
     * 分页查询供应商资质
     * 
     * @param page 页码
     * @param size 每页大小
     * @param supplierId 供应商ID（可选）
     * @param qualificationType 资质类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<SupplierQualificationVO> getQualificationPage(int page, int size, Long supplierId, String qualificationType, Integer status);

    /**
     * 检查供应商资质类型是否已存在
     * 
     * @param supplierId 供应商ID
     * @param qualificationType 资质类型
     * @param excludeId 排除的资质ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsBySupplierIdAndType(Long supplierId, String qualificationType, Long excludeId);

    /**
     * 获取即将到期的资质（30天内）
     * 
     * @return 资质列表
     */
    List<SupplierQualificationVO> getExpiringQualifications();

    /**
     * 更新过期资质状态
     * 
     * @return 更新数量
     */
    int updateExpiredQualificationsStatus();
}
