package com.haocai.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haocai.management.dto.SupplierEvaluationCreateDTO;
import com.haocai.management.dto.SupplierEvaluationVO;
import com.haocai.management.entity.SupplierEvaluation;
import com.haocai.management.entity.SupplierInfo;
import com.haocai.management.exception.SupplierException;
import com.haocai.management.mapper.SupplierEvaluationMapper;
import com.haocai.management.mapper.SupplierInfoMapper;
import com.haocai.management.repository.SupplierEvaluationRepository;
import com.haocai.management.service.ISupplierEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 供应商评价Service实现类
 * 
 * 遵循development-standards.md中的业务逻辑层规范：
 * - Service命名规范：使用业务名称+ServiceImpl后缀
 * - 继承ServiceImpl：继承MyBatis-Plus提供的基础实现
 * - 事务管理：使用@Transactional注解管理事务
 * - 日志记录：使用Slf4j进行日志记录
 * - 依赖注入：使用@RequiredArgsConstructor进行构造器注入
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierEvaluationServiceImpl extends ServiceImpl<SupplierEvaluationMapper, SupplierEvaluation> 
        implements ISupplierEvaluationService {

    private final SupplierEvaluationRepository supplierEvaluationRepository;
    private final SupplierInfoMapper supplierInfoMapper;

    /**
     * 创建供应商评价
     * 
     * 业务规则：
     * 1. 验证供应商是否存在
     * 2. 计算总分和平均分
     * 3. 计算信用等级
     * 4. 保存评价
     * 5. 更新供应商的信用等级
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEvaluation(SupplierEvaluationCreateDTO createDTO, Long evaluatorId, String evaluatorName) {
        log.info("开始创建供应商评价，供应商ID：{}，评价人ID：{}", createDTO.getSupplierId(), evaluatorId);

        // 验证供应商是否存在
        SupplierInfo supplier = supplierInfoMapper.selectById(createDTO.getSupplierId());
        if (supplier == null) {
            log.error("供应商不存在，供应商ID：{}", createDTO.getSupplierId());
            throw SupplierException.notFound(createDTO.getSupplierId());
        }

        // 验证评分范围
        validateScore(createDTO.getDeliveryScore(), "交付评分");
        validateScore(createDTO.getQualityScore(), "质量评分");
        validateScore(createDTO.getServiceScore(), "服务评分");
        validateScore(createDTO.getPriceScore(), "价格评分");

        // 计算总分
        BigDecimal totalScore = createDTO.getDeliveryScore()
                .add(createDTO.getQualityScore())
                .add(createDTO.getServiceScore())
                .add(createDTO.getPriceScore());

        // 计算平均分（4项评分平均）
        BigDecimal averageScore = totalScore.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);

        // 计算信用等级
        Integer creditRating = calculateCreditRatingFromScore(averageScore);

        // 创建评价实体
        SupplierEvaluation evaluation = new SupplierEvaluation();
        evaluation.setSupplierId(createDTO.getSupplierId());
        evaluation.setEvaluatorId(evaluatorId);
        evaluation.setEvaluatorName(evaluatorName);
        evaluation.setEvaluationDate(LocalDate.now());
        evaluation.setDeliveryScore(createDTO.getDeliveryScore());
        evaluation.setQualityScore(createDTO.getQualityScore());
        evaluation.setServiceScore(createDTO.getServiceScore());
        evaluation.setPriceScore(createDTO.getPriceScore());
        evaluation.setTotalScore(totalScore);
        evaluation.setAverageScore(averageScore);
        evaluation.setCreditRating(creditRating);
        evaluation.setRemark(createDTO.getRemark());

        // 保存评价
        supplierEvaluationRepository.save(evaluation);

        // 更新供应商的信用等级
        updateSupplierCreditRating(createDTO.getSupplierId());

        log.info("供应商评价创建成功，评价ID：{}，供应商ID：{}，信用等级：{}", 
                evaluation.getId(), createDTO.getSupplierId(), creditRating);
        return evaluation.getId();
    }

    /**
     * 根据供应商ID查询评价列表
     */
    @Override
    public List<SupplierEvaluationVO> getEvaluationsBySupplierId(Long supplierId) {
        log.info("查询供应商评价列表，供应商ID：{}", supplierId);

        // 验证供应商是否存在
        SupplierInfo supplier = supplierInfoMapper.selectById(supplierId);
        if (supplier == null) {
            log.error("供应商不存在，供应商ID：{}", supplierId);
            throw SupplierException.notFound(supplierId);
        }

        List<SupplierEvaluation> evaluations = supplierEvaluationRepository.findBySupplierId(supplierId);

        List<SupplierEvaluationVO> vos = evaluations.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        log.info("查询成功，评价数量：{}", vos.size());
        return vos;
    }

    /**
     * 根据评价人ID查询评价列表
     */
    @Override
    public List<SupplierEvaluationVO> getEvaluationsByEvaluatorId(Long evaluatorId) {
        log.info("查询评价人评价列表，评价人ID：{}", evaluatorId);

        List<SupplierEvaluation> evaluations = supplierEvaluationRepository.findByEvaluatorId(evaluatorId);

        List<SupplierEvaluationVO> vos = evaluations.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        log.info("查询成功，评价数量：{}", vos.size());
        return vos;
    }

    /**
     * 根据ID查询评价
     */
    @Override
    public SupplierEvaluationVO getEvaluationById(Long id) {
        log.info("查询评价详情，评价ID：{}", id);

        SupplierEvaluation evaluation = supplierEvaluationRepository.findById(id);
        if (evaluation == null) {
            log.error("评价不存在，评价ID：{}", id);
            throw new RuntimeException("评价不存在");
        }

        return convertToVO(evaluation);
    }

    /**
     * 删除评价（逻辑删除）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteEvaluation(Long id) {
        log.info("删除评价，评价ID：{}", id);

        SupplierEvaluation evaluation = supplierEvaluationRepository.findById(id);
        if (evaluation == null) {
            log.error("评价不存在，评价ID：{}", id);
            throw new RuntimeException("评价不存在");
        }

        boolean result = supplierEvaluationRepository.deleteById(id);

        // 更新供应商的信用等级
        updateSupplierCreditRating(evaluation.getSupplierId());

        log.info("评价删除成功，评价ID：{}", id);
        return result;
    }

    /**
     * 计算供应商的平均评分
     */
    @Override
    public BigDecimal calculateAverageScore(Long supplierId) {
        log.info("计算供应商平均评分，供应商ID：{}", supplierId);

        List<SupplierEvaluation> evaluations = supplierEvaluationRepository.findBySupplierId(supplierId);
        if (evaluations.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalAverage = BigDecimal.ZERO;
        for (SupplierEvaluation evaluation : evaluations) {
            if (evaluation.getAverageScore() != null) {
                totalAverage = totalAverage.add(evaluation.getAverageScore());
            }
        }

        return totalAverage.divide(BigDecimal.valueOf(evaluations.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * 计算供应商的信用等级
     */
    @Override
    public Integer calculateCreditRating(Long supplierId) {
        log.info("计算供应商信用等级，供应商ID：{}", supplierId);

        BigDecimal averageScore = calculateAverageScore(supplierId);
        return calculateCreditRatingFromScore(averageScore);
    }

    /**
     * 更新供应商的信用等级
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSupplierCreditRating(Long supplierId) {
        log.info("更新供应商信用等级，供应商ID：{}", supplierId);

        Integer creditRating = calculateCreditRating(supplierId);

        SupplierInfo supplier = new SupplierInfo();
        supplier.setId(supplierId);
        supplier.setCreditRating(creditRating);

        boolean result = supplierInfoMapper.updateById(supplier) > 0;

        log.info("供应商信用等级更新成功，供应商ID：{}，新信用等级：{}", supplierId, creditRating);
        return result;
    }

    /**
     * 验证评分范围
     */
    private void validateScore(BigDecimal score, String scoreName) {
        if (score == null || score.compareTo(BigDecimal.ONE) < 0 || score.compareTo(BigDecimal.TEN) > 0) {
            log.error("{}超出范围：{}", scoreName, score);
            throw new IllegalArgumentException(scoreName + "必须在1-10之间");
        }
    }

    /**
     * 根据平均分计算信用等级
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
     */
    private Integer calculateCreditRatingFromScore(BigDecimal score) {
        if (score == null || score.compareTo(BigDecimal.ZERO) <= 0) {
            return 1;
        }

        double scoreValue = score.doubleValue();
        if (scoreValue >= 9.0) {
            return 10;
        } else if (scoreValue >= 8.0) {
            return 9;
        } else if (scoreValue >= 7.0) {
            return 8;
        } else if (scoreValue >= 6.0) {
            return 7;
        } else if (scoreValue >= 5.0) {
            return 6;
        } else if (scoreValue >= 4.0) {
            return 5;
        } else if (scoreValue >= 3.0) {
            return 4;
        } else if (scoreValue >= 2.0) {
            return 3;
        } else if (scoreValue >= 1.0) {
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * 将SupplierEvaluation实体转换为SupplierEvaluationVO
     */
    private SupplierEvaluationVO convertToVO(SupplierEvaluation evaluation) {
        SupplierEvaluationVO vo = new SupplierEvaluationVO();
        BeanUtils.copyProperties(evaluation, vo);

        // 设置信用等级描述
        if (evaluation.getCreditRating() != null) {
            vo.setCreditRatingDescription(getCreditRatingDescription(evaluation.getCreditRating()));
        }

        return vo;
    }

    /**
     * 获取信用等级描述
     */
    private String getCreditRatingDescription(Integer rating) {
        if (rating == null) {
            return "未知";
        }
        return switch (rating) {
            case 10, 9 -> "优秀";
            case 8 -> "良好";
            case 7 -> "较好";
            case 6 -> "一般";
            case 5 -> "及格";
            case 4 -> "较差";
            case 3 -> "差";
            case 2 -> "很差";
            case 1 -> "极差";
            default -> "未知";
        };
    }
}
