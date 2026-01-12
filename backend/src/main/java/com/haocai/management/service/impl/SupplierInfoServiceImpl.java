package com.haocai.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haocai.management.dto.SupplierCreateDTO;
import com.haocai.management.dto.SupplierQueryDTO;
import com.haocai.management.dto.SupplierUpdateDTO;
import com.haocai.management.dto.SupplierVO;
import com.haocai.management.entity.Material;
import com.haocai.management.entity.SupplierInfo;
import com.haocai.management.enums.CooperationStatus;
import com.haocai.management.exception.SupplierException;
import com.haocai.management.mapper.MaterialMapper;
import com.haocai.management.mapper.SupplierInfoMapper;
import com.haocai.management.repository.SupplierInfoRepository;
import com.haocai.management.service.ISupplierInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 供应商业务逻辑实现类
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
public class SupplierInfoServiceImpl extends ServiceImpl<SupplierInfoMapper, SupplierInfo> 
        implements ISupplierInfoService {

    /**
     * 供应商数据访问层
     * 遵循：后端开发规范-第2.1条（数据访问层封装）
     */
    private final SupplierInfoRepository supplierInfoRepository;
    
    /**
     * 耗材Mapper（用于检查供应商关联的耗材）
     */
    private final MaterialMapper materialMapper;

    /**
     * 创建供应商
     * 
     * 业务规则：
     * 1. 供应商编码自动生成（如果未提供）
     * 2. 供应商编码不能重复
     * 3. 供应商名称不能重复
     * 4. 信用等级必须在1-10范围内
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSupplier(SupplierCreateDTO createDTO) {
        log.info("开始创建供应商，供应商名称：{}", createDTO.getSupplierName());

        // 生成供应商编码
        String supplierCode = createDTO.getSupplierCode();
        if (supplierCode == null || supplierCode.trim().isEmpty()) {
            supplierCode = generateSupplierCode();
            log.info("自动生成供应商编码：{}", supplierCode);
        }

        // 检查供应商编码是否已存在
        // 遵循：数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）
        if (supplierInfoRepository.existsBySupplierCode(supplierCode)) {
            log.error("供应商编码已存在：{}", supplierCode);
            throw SupplierException.codeDuplicate(supplierCode);
        }

        // 检查供应商名称是否已存在
        if (supplierInfoRepository.existsBySupplierName(createDTO.getSupplierName())) {
            log.error("供应商名称已存在：{}", createDTO.getSupplierName());
            throw SupplierException.nameDuplicate(createDTO.getSupplierName());
        }

        // 验证信用等级范围
        // 遵循：后端开发规范-第2.3条（异常处理：Service层捕获异常后，应抛出统一的BusinessException）
        Integer creditRating = createDTO.getCreditRating();
        if (creditRating != null && (creditRating < 1 || creditRating > 10)) {
            log.error("信用等级超出范围：{}", creditRating);
            throw SupplierException.creditRatingOutOfRange(creditRating, 1, 10);
        }

        // 创建供应商实体
        SupplierInfo supplier = new SupplierInfo();
        supplier.setSupplierCode(supplierCode);
        supplier.setSupplierName(createDTO.getSupplierName());
        supplier.setContactPerson(createDTO.getContactPerson());
        supplier.setPhone(createDTO.getPhone());
        supplier.setEmail(createDTO.getEmail());
        supplier.setAddress(createDTO.getAddress());
        supplier.setBusinessLicense(createDTO.getBusinessLicense());
        supplier.setTaxNumber(createDTO.getTaxNumber());
        supplier.setBankAccount(createDTO.getBankAccount());
        supplier.setBankName(createDTO.getBankName());
        supplier.setCreditRating(creditRating != null ? creditRating : 5); // 默认信用等级5
        
        // 设置合作状态
        if (createDTO.getCooperationStatus() != null) {
            supplier.setCooperationStatus(CooperationStatus.fromValue(createDTO.getCooperationStatus()));
        } else {
            supplier.setCooperationStatus(CooperationStatus.COOPERATING); // 默认合作中
        }
        
        // 设置状态（默认启用）
        supplier.setStatus(createDTO.getStatus() != null ? createDTO.getStatus() : 1);
        supplier.setDescription(createDTO.getDescription());

        // 保存供应商
        save(supplier);

        log.info("供应商创建成功，供应商ID：{}，供应商编码：{}，供应商名称：{}", 
                supplier.getId(), supplierCode, supplier.getSupplierName());
        return supplier.getId();
    }

    /**
     * 更新供应商
     * 
     * 业务规则：
     * 1. 供应商必须存在
     * 2. 供应商编码不能与其他供应商重复
     * 3. 供应商名称不能与其他供应商重复
     * 4. 信用等级必须在1-10范围内
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSupplier(Long id, SupplierUpdateDTO updateDTO) {
        log.info("开始更新供应商，供应商ID：{}", id);

        // 检查供应商是否存在
        SupplierInfo supplier = getById(id);
        if (supplier == null) {
            log.error("供应商不存在，供应商ID：{}", id);
            throw SupplierException.notFound(id);
        }

        // 检查供应商名称是否与其他供应商重复
        if (updateDTO.getSupplierName() != null && 
            !updateDTO.getSupplierName().equals(supplier.getSupplierName())) {
            if (supplierInfoRepository.existsBySupplierName(updateDTO.getSupplierName())) {
                log.error("供应商名称已存在：{}", updateDTO.getSupplierName());
                throw SupplierException.nameDuplicate(updateDTO.getSupplierName());
            }
            supplier.setSupplierName(updateDTO.getSupplierName());
        }

        // 验证信用等级范围
        Integer creditRating = updateDTO.getCreditRating();
        if (creditRating != null && (creditRating < 1 || creditRating > 10)) {
            log.error("信用等级超出范围：{}", creditRating);
            throw SupplierException.creditRatingOutOfRange(creditRating, 1, 10);
        }

        // 更新供应商信息
        if (creditRating != null) {
            supplier.setCreditRating(creditRating);
        }
        if (updateDTO.getContactPerson() != null) {
            supplier.setContactPerson(updateDTO.getContactPerson());
        }
        if (updateDTO.getPhone() != null) {
            supplier.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getEmail() != null) {
            supplier.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getAddress() != null) {
            supplier.setAddress(updateDTO.getAddress());
        }
        if (updateDTO.getBusinessLicense() != null) {
            supplier.setBusinessLicense(updateDTO.getBusinessLicense());
        }
        if (updateDTO.getTaxNumber() != null) {
            supplier.setTaxNumber(updateDTO.getTaxNumber());
        }
        if (updateDTO.getBankAccount() != null) {
            supplier.setBankAccount(updateDTO.getBankAccount());
        }
        if (updateDTO.getBankName() != null) {
            supplier.setBankName(updateDTO.getBankName());
        }
        if (updateDTO.getCooperationStatus() != null) {
            supplier.setCooperationStatus(CooperationStatus.fromValue(updateDTO.getCooperationStatus()));
        }
        if (updateDTO.getStatus() != null) {
            supplier.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getDescription() != null) {
            supplier.setDescription(updateDTO.getDescription());
        }

        // 保存更新
        boolean result = updateById(supplier);

        log.info("供应商更新成功，供应商ID：{}，供应商名称：{}", id, supplier.getSupplierName());
        return result;
    }

    /**
     * 删除供应商（逻辑删除）
     * 
     * 业务规则：
     * 1. 供应商必须存在
     * 2. 检查是否有关联的耗材
     * 3. 检查是否有关联的入库记录
     * 4. 有关联则拒绝删除
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSupplier(Long id) {
        log.info("开始删除供应商，供应商ID：{}", id);

        // 检查供应商是否存在
        SupplierInfo supplier = getById(id);
        if (supplier == null) {
            log.error("供应商不存在，供应商ID：{}", id);
            throw SupplierException.notFound(id);
        }

        // 检查是否有关联的耗材
        int materialCount = countSupplierMaterials(id);
        if (materialCount > 0) {
            log.error("供应商有关联耗材，无法删除，供应商ID：{}，关联耗材数：{}", id, materialCount);
            throw SupplierException.hasRelatedMaterials(id, materialCount);
        }

        // 检查是否有关联的入库记录
        int inboundOrderCount = countSupplierInboundOrders(id);
        if (inboundOrderCount > 0) {
            log.error("供应商有关联入库记录，无法删除，供应商ID：{}，关联入库单数：{}", id, inboundOrderCount);
            throw SupplierException.hasRelatedInboundOrders(id, inboundOrderCount);
        }

        // 逻辑删除供应商
        // 遵循：数据库设计规范-第1.3条（审计字段规范：使用逻辑删除）
        boolean result = removeById(id);

        log.info("供应商删除成功，供应商ID：{}，供应商名称：{}", id, supplier.getSupplierName());
        return result;
    }

    /**
     * 批量删除供应商（逻辑删除）
     * 
     * 遵循：后端开发规范-第2.2条（批量操作：禁止直接批量更新不存在的ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteSuppliers(List<Long> ids) {
        log.info("开始批量删除供应商，供应商ID列表：{}", ids);

        if (ids == null || ids.isEmpty()) {
            log.warn("批量删除供应商列表为空");
            return true;
        }

        // 检查所有供应商是否存在
        List<SupplierInfo> suppliers = listByIds(ids);
        if (suppliers.size() != ids.size()) {
            log.error("部分供应商不存在");
            throw SupplierException.notFound(ids.stream().findFirst().orElse(0L));
        }

        // 检查每个供应商是否有关联数据
        for (SupplierInfo supplier : suppliers) {
            int materialCount = countSupplierMaterials(supplier.getId());
            if (materialCount > 0) {
                log.error("供应商有关联耗材，无法删除，供应商ID：{}，关联耗材数：{}", 
                        supplier.getId(), materialCount);
                throw SupplierException.hasRelatedMaterials(supplier.getId(), materialCount);
            }

            int inboundOrderCount = countSupplierInboundOrders(supplier.getId());
            if (inboundOrderCount > 0) {
                log.error("供应商有关联入库记录，无法删除，供应商ID：{}，关联入库单数：{}", 
                        supplier.getId(), inboundOrderCount);
                throw SupplierException.hasRelatedInboundOrders(supplier.getId(), inboundOrderCount);
            }
        }

        // 批量逻辑删除供应商
        boolean result = removeByIds(ids);

        log.info("批量删除供应商成功，删除数量：{}", ids.size());
        return result;
    }

    /**
     * 根据ID查询供应商
     */
    @Override
    public SupplierVO getSupplierById(Long id) {
        log.info("查询供应商，供应商ID：{}", id);

        SupplierInfo supplier = getById(id);
        if (supplier == null) {
            log.error("供应商不存在，供应商ID：{}", id);
            throw SupplierException.notFound(id);
        }

        SupplierVO vo = convertToVO(supplier);

        log.info("查询成功，供应商名称：{}", vo.getSupplierName());
        return vo;
    }

    /**
     * 分页查询供应商列表
     */
    @Override
    public IPage<SupplierVO> getSupplierPage(SupplierQueryDTO queryDTO) {
        log.info("分页查询供应商列表，查询条件：{}", queryDTO);

        // 创建分页对象
        Page<SupplierInfo> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        // 构建查询条件
        LambdaQueryWrapper<SupplierInfo> queryWrapper = new LambdaQueryWrapper<>();

        // 供应商名称模糊查询
        if (queryDTO.getSupplierName() != null && !queryDTO.getSupplierName().trim().isEmpty()) {
            queryWrapper.like(SupplierInfo::getSupplierName, queryDTO.getSupplierName());
        }

        // 供应商编码精确查询
        if (queryDTO.getSupplierCode() != null && !queryDTO.getSupplierCode().trim().isEmpty()) {
            queryWrapper.eq(SupplierInfo::getSupplierCode, queryDTO.getSupplierCode());
        }

        // 联系人模糊查询
        if (queryDTO.getContactPerson() != null && !queryDTO.getContactPerson().trim().isEmpty()) {
            queryWrapper.like(SupplierInfo::getContactPerson, queryDTO.getContactPerson());
        }

        // 联系电话精确查询
        if (queryDTO.getPhone() != null && !queryDTO.getPhone().trim().isEmpty()) {
            queryWrapper.eq(SupplierInfo::getPhone, queryDTO.getPhone());
        }

        // 信用等级范围查询
        if (queryDTO.getCreditRatingMin() != null) {
            queryWrapper.ge(SupplierInfo::getCreditRating, queryDTO.getCreditRatingMin());
        }
        if (queryDTO.getCreditRatingMax() != null) {
            queryWrapper.le(SupplierInfo::getCreditRating, queryDTO.getCreditRatingMax());
        }

        // 合作状态查询
        if (queryDTO.getCooperationStatus() != null) {
            queryWrapper.eq(SupplierInfo::getCooperationStatus, 
                    CooperationStatus.fromValue(queryDTO.getCooperationStatus()));
        }

        // 状态查询
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(SupplierInfo::getStatus, queryDTO.getStatus());
        }

        // 逻辑删除标识
        queryWrapper.eq(SupplierInfo::getDeleted, 0);

        // 排序
        String orderBy = queryDTO.getOrderBy();
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "createTime";
        }
        if ("asc".equalsIgnoreCase(queryDTO.getOrderDirection())) {
            queryWrapper.orderByAsc(SupplierInfo::getCreateTime);
        } else {
            queryWrapper.orderByDesc(SupplierInfo::getCreateTime);
        }

        // 执行分页查询
        IPage<SupplierInfo> supplierPage = page(page, queryWrapper);

        // 转换为VO
        Page<SupplierVO> voPage = new Page<>(supplierPage.getCurrent(), supplierPage.getSize());
        voPage.setTotal(supplierPage.getTotal());
        voPage.setPages(supplierPage.getPages());

        List<SupplierVO> records = supplierPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(records);

        log.info("分页查询供应商列表成功，总记录数：{}", voPage.getTotal());
        return voPage;
    }

    /**
     * 查询供应商列表（不分页）
     */
    @Override
    public List<SupplierVO> getSupplierList(SupplierQueryDTO queryDTO) {
        log.info("查询供应商列表（不分页），查询条件：{}", queryDTO);

        LambdaQueryWrapper<SupplierInfo> queryWrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getSupplierName() != null && !queryDTO.getSupplierName().trim().isEmpty()) {
            queryWrapper.like(SupplierInfo::getSupplierName, queryDTO.getSupplierName());
        }
        if (queryDTO.getCooperationStatus() != null) {
            queryWrapper.eq(SupplierInfo::getCooperationStatus, 
                    CooperationStatus.fromValue(queryDTO.getCooperationStatus()));
        }
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(SupplierInfo::getStatus, queryDTO.getStatus());
        }
        queryWrapper.eq(SupplierInfo::getDeleted, 0);
        queryWrapper.orderByDesc(SupplierInfo::getCreateTime);

        List<SupplierInfo> suppliers = list(queryWrapper);

        List<SupplierVO> vos = suppliers.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        log.info("查询供应商列表成功，数量：{}", vos.size());
        return vos;
    }

    /**
     * 切换供应商状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleStatus(Long id) {
        log.info("开始切换供应商状态，供应商ID：{}", id);

        SupplierInfo supplier = getById(id);
        if (supplier == null) {
            log.error("供应商不存在，供应商ID：{}", id);
            throw SupplierException.notFound(id);
        }

        // 切换状态：0-禁用，1-启用
        supplier.setStatus(supplier.getStatus() == 1 ? 0 : 1);
        boolean result = updateById(supplier);

        log.info("切换状态成功，供应商ID：{}，新状态：{}", id, supplier.getStatus());
        return result;
    }

    /**
     * 更新供应商状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        log.info("开始更新供应商状态，供应商ID：{}，新状态：{}", id, status);

        SupplierInfo supplier = getById(id);
        if (supplier == null) {
            log.error("供应商不存在，供应商ID：{}", id);
            throw SupplierException.notFound(id);
        }

        if (status != null && (status == 0 || status == 1)) {
            supplier.setStatus(status);
            boolean result = updateById(supplier);
            log.info("更新状态成功，供应商ID：{}，新状态：{}", id, status);
            return result;
        }

        log.error("无效的状态值：{}", status);
        throw SupplierException.statusNotAllowed(id, supplier.getStatus(), "状态更新");
    }

    /**
     * 批量更新供应商状态
     * 
     * 遵循：后端开发规范-第2.2条（批量操作：禁止直接批量更新不存在的ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateStatus(List<Long> ids, Integer status) {
        log.info("开始批量更新供应商状态，供应商ID列表：{}，新状态：{}", ids, status);

        if (ids == null || ids.isEmpty()) {
            log.warn("批量更新供应商列表为空");
            return 0;
        }

        if (status == null || (status != 0 && status != 1)) {
            log.error("无效的状态值：{}", status);
            return 0;
        }

        // 先过滤出有效的ID
        List<SupplierInfo> suppliers = listByIds(ids);
        if (suppliers.isEmpty()) {
            log.warn("没有有效的供应商ID");
            return 0;
        }

        // 批量更新状态
        int successCount = 0;
        for (SupplierInfo supplier : suppliers) {
            supplier.setStatus(status);
            if (updateById(supplier)) {
                successCount++;
            }
        }

        log.info("批量更新供应商状态成功，成功数量：{}，总数：{}", successCount, ids.size());
        return successCount;
    }

    /**
     * 生成供应商编码
     * 
     * 编码规则：SUP + 年月日 + 流水号
     * 示例：SUP20260112001
     * 
     * 遵循：数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）
     * 在生成编码时检查是否已存在，如果存在则递增流水号
     */
    @Override
    public String generateSupplierCode() {
        log.info("开始生成供应商编码");

        // 获取当前日期
        LocalDate today = LocalDate.now();
        String datePrefix = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 查询当天最大的供应商编码
        String pattern = "SUP" + datePrefix + "%";
        List<SupplierInfo> todaySuppliers = supplierInfoRepository.findBySupplierNameContainingIgnoreCase("");
        
        // 过滤出今天生成的供应商
        int maxSequence = 0;
        for (SupplierInfo supplier : todaySuppliers) {
            String code = supplier.getSupplierCode();
            if (code != null && code.startsWith("SUP" + datePrefix)) {
                try {
                    String sequenceStr = code.substring(("SUP" + datePrefix).length());
                    int sequence = Integer.parseInt(sequenceStr);
                    if (sequence > maxSequence) {
                        maxSequence = sequence;
                    }
                } catch (NumberFormatException e) {
                    // 忽略格式错误的编码
                }
            }
        }

        // 生成新的流水号
        int newSequence = maxSequence + 1;
        String supplierCode = String.format("SUP%s%03d", datePrefix, newSequence);

        log.info("生成供应商编码成功：{}", supplierCode);
        return supplierCode;
    }

    /**
     * 检查供应商编码是否存在
     */
    @Override
    public boolean existsBySupplierCode(String supplierCode) {
        return supplierInfoRepository.existsBySupplierCode(supplierCode);
    }

    /**
     * 检查供应商编码是否存在（排除指定ID）
     */
    @Override
    public boolean existsBySupplierCodeExcludeId(String supplierCode, Long excludeId) {
        return supplierInfoRepository.existsBySupplierCodeExcludeId(supplierCode, excludeId);
    }

    /**
     * 获取供应商关联的耗材数量
     */
    @Override
    public int countSupplierMaterials(Long supplierId) {
        LambdaQueryWrapper<Material> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Material::getSupplierId, supplierId);
        queryWrapper.eq(Material::getDeleted, 0);
        Long count = materialMapper.selectCount(queryWrapper);
        return count != null ? count.intValue() : 0;
    }

    /**
     * 获取供应商关联的入库单数量
     * 
     * 注意：这里需要查询inbound_order表
     * 由于当前没有InboundOrderMapper，暂时返回0
     * 实际项目中需要添加相应的Mapper
     */
    @Override
    public int countSupplierInboundOrders(Long supplierId) {
        // TODO: 实现入库单统计逻辑
        // 需要创建InboundOrderMapper并注入
        log.info("查询供应商关联的入库单数量，供应商ID：{}", supplierId);
        return 0;
    }

    /**
     * 根据合作状态查询供应商列表
     */
    @Override
    public List<SupplierVO> getSuppliersByCooperationStatus(Integer cooperationStatus) {
        log.info("根据合作状态查询供应商，合作状态：{}", cooperationStatus);

        List<SupplierInfo> suppliers = supplierInfoRepository.findByCooperationStatus(cooperationStatus);

        List<SupplierVO> vos = suppliers.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        log.info("查询成功，供应商数量：{}", vos.size());
        return vos;
    }

    /**
     * 根据信用等级范围查询供应商
     */
    @Override
    public List<SupplierVO> getSuppliersByCreditRatingRange(Integer minRating, Integer maxRating) {
        log.info("根据信用等级范围查询供应商，最小等级：{}，最大等级：{}", minRating, maxRating);

        List<SupplierInfo> suppliers = supplierInfoRepository.findByCreditRatingBetween(minRating, maxRating);

        List<SupplierVO> vos = suppliers.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        log.info("查询成功，供应商数量：{}", vos.size());
        return vos;
    }

    /**
     * 搜索供应商
     */
    @Override
    public List<SupplierVO> searchSuppliers(String keyword) {
        log.info("搜索供应商，关键词：{}", keyword);

        List<SupplierInfo> suppliers = supplierInfoRepository.findBySupplierNameContainingIgnoreCase(keyword);

        List<SupplierVO> vos = suppliers.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        log.info("搜索成功，供应商数量：{}", vos.size());
        return vos;
    }

    /**
     * 将SupplierInfo实体转换为SupplierVO
     * 
     * 遵循：后端开发规范-第2.1条（字段映射规范）
     */
    private SupplierVO convertToVO(SupplierInfo supplier) {
        SupplierVO vo = new SupplierVO();
        BeanUtils.copyProperties(supplier, vo);

        // 设置合作状态描述
        if (supplier.getCooperationStatus() != null) {
            vo.setCooperationStatus(supplier.getCooperationStatus().getValue());
            vo.setCooperationStatusDescription(supplier.getCooperationStatus().getDescription());
        }

        // 设置状态描述
        if (supplier.getStatus() != null) {
            vo.setStatusDescription(supplier.getStatus() == 1 ? "启用" : "禁用");
        }

        // 设置信用等级描述
        if (supplier.getCreditRating() != null) {
            vo.setCreditRatingDescription(getCreditRatingDescription(supplier.getCreditRating()));
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
