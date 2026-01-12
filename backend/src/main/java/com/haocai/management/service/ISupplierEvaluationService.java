package com.haocai.management.service;

import com.haocai.management.dto.SupplierEvaluationCreateDTO;
import com.haocai.management.dto.SupplierEvaluationVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 供应商评价Service接口
 * 
 * 遵循development-standards.md中的服务层规范：
 * - Service命名规范：使用I+业务名称+Service后缀
 * - 继承IService：获得MyBatis-Plus提供的CRUD方法
 * - 方法命名规范：使用动词+名词，清晰表达业务含义
 * 
 * @author haocai
 * @since 2026-01-12
 */
public interface ISupplierEvaluationService {

    /**
     * 创建供应商评价
     * 
     * 遵循：后端开发规范-第2.2条（事务管理）
     * 
     * @param createDTO 创建请求DTO
     * @param evaluatorId 评价人ID
     * @param evaluatorName 评价人名称
     * @return 创建的评价ID
     */
    Long createEvaluation(SupplierEvaluationCreateDTO createDTO, Long evaluatorId, String evaluatorName);

    /**
     * 根据供应商ID查询评价列表
     * 
     * @param supplierId 供应商ID
     * @return 评价列表
     */
    List<SupplierEvaluationVO> getEvaluationsBySupplierId(Long supplierId);

    /**
     * 根据评价人ID查询评价列表
     * 
     * @param evaluatorId 评价人ID
     * @return 评价列表
     */
    List<SupplierEvaluationVO> getEvaluationsByEvaluatorId(Long evaluatorId);

    /**
     * 根据ID查询评价
     * 
     * @param id 评价ID
     * @return 评价信息
     */
    SupplierEvaluationVO getEvaluationById(Long id);

    /**
     * 删除评价（逻辑删除）
     * 
     * @param id 评价ID
     * @return 是否成功
     */
    boolean deleteEvaluation(Long id);

    /**
     * 计算供应商的平均评分
     * 
     * @param supplierId 供应商ID
     * @return 平均分
     */
    BigDecimal calculateAverageScore(Long supplierId);

    /**
     * 计算供应商的信用等级
     * 
     * 信用等级计算规则：
     * - 9.0-10.0：优秀（10）
     * - 8.0-8.9：良好（9）
     * - 7.0-7.9：较好（8）
     * - 6.0-6.9：一般（7）
     * - 5.0-5.9：及格（6）
     * - 4.0-4.9：较差（5）
     * - 3.0-3.9：差（4）
     * - 2.0-2.9：很差（3）
     * - 1.0-1.9：极差（2）
     * - 0.0-0.9：不合格（1）
     * 
     * @param supplierId 供应商ID
     * @return 信用等级
     */
    Integer calculateCreditRating(Long supplierId);

    /**
     * 更新供应商的信用等级
     * 
     * @param supplierId 供应商ID
     * @return 是否成功
     */
    boolean updateSupplierCreditRating(Long supplierId);
}
