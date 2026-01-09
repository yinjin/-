package com.haocai.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haocai.management.dto.MaterialCategoryCreateDTO;
import com.haocai.management.dto.MaterialCategoryUpdateDTO;
import com.haocai.management.entity.MaterialCategory;
import com.haocai.management.exception.BusinessException;
import com.haocai.management.mapper.MaterialCategoryMapper;
import com.haocai.management.service.IMaterialCategoryService;
import com.haocai.management.vo.MaterialCategoryTreeVO;
import com.haocai.management.vo.MaterialCategoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 耗材分类业务逻辑层实现类
 * 
 * 遵循development-standards.md中的业务逻辑层规范：
 * - 使用@Service注解标记为服务类
 * - 使用@Transactional进行事务管理
 * - 使用@Slf4j进行日志记录
 * - 使用@RequiredArgsConstructor进行依赖注入
 * - 实现业务逻辑，处理数据转换和业务规则
 * 
 * @author haocai
 * @since 2026-01-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialCategoryServiceImpl extends ServiceImpl<MaterialCategoryMapper, MaterialCategory> 
        implements IMaterialCategoryService {

    private final MaterialCategoryMapper materialCategoryMapper;

    /**
     * 创建耗材分类
     * 
     * 业务规则：
     * 1. 分类编码不能重复
     * 2. 父分类必须存在
     * 3. 分类层级不能超过3级
     * 4. 自动生成分类编码（如果未提供）
     * 5. 自动计算分类层级
     * 
     * @param createDTO 创建请求DTO
     * @return 创建的分类ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(MaterialCategoryCreateDTO createDTO) {
        log.info("开始创建耗材分类，分类名称：{}", createDTO.getCategoryName());
        
        // 1. 检查分类编码是否已存在
        if (createDTO.getCategoryCode() != null && !createDTO.getCategoryCode().isEmpty()) {
            if (existsByCategoryCode(createDTO.getCategoryCode())) {
                log.error("分类编码已存在：{}", createDTO.getCategoryCode());
                throw BusinessException.paramError("分类编码已存在：" + createDTO.getCategoryCode());
            }
        }
        
        // 2. 检查父分类是否存在
        Long parentId = createDTO.getParentId();
        if (parentId != null && parentId != 0) {
            MaterialCategory parentCategory = getById(parentId);
            if (parentCategory == null) {
                log.error("父分类不存在，父分类ID：{}", parentId);
                throw BusinessException.dataNotFound("父分类不存在");
            }
            
            // 3. 检查分类层级是否超过3级
            if (parentCategory.getLevel() >= 3) {
                log.error("分类层级不能超过3级，父分类层级：{}", parentCategory.getLevel());
                throw BusinessException.operationFailed("分类层级不能超过3级");
            }
        }
        
        // 4. 创建分类实体
        MaterialCategory category = new MaterialCategory();
        BeanUtils.copyProperties(createDTO, category);
        
        // 5. 自动生成分类编码（如果未提供）
        if (category.getCategoryCode() == null || category.getCategoryCode().isEmpty()) {
            category.setCategoryCode(generateCategoryCode(parentId));
        }
        
        // 6. 自动计算分类层级
        category.setLevel(calculateLevel(parentId));
        
        // 7. 设置默认值
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1); // 默认启用
        }
        
        // 8. 保存分类
        save(category);
        
        log.info("耗材分类创建成功，分类ID：{}，分类名称：{}", category.getId(), category.getCategoryName());
        return category.getId();
    }

    /**
     * 更新耗材分类
     * 
     * 业务规则：
     * 1. 分类编码不能与其他分类重复
     * 2. 不能将分类移动到自己的子分类下
     * 
     * @param id 分类ID
     * @param updateDTO 更新请求DTO
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategory(Long id, MaterialCategoryUpdateDTO updateDTO) {
        log.info("开始更新耗材分类，分类ID：{}", id);
        
        // 1. 检查分类是否存在
        MaterialCategory category = getById(id);
        if (category == null) {
            log.error("耗材分类不存在，分类ID：{}", id);
            throw BusinessException.dataNotFound("耗材分类不存在");
        }
        
        // 2. 检查分类编码是否与其他分类重复
        if (updateDTO.getCategoryCode() != null && !updateDTO.getCategoryCode().isEmpty()) {
            if (!updateDTO.getCategoryCode().equals(category.getCategoryCode())) {
                if (existsByCategoryCodeExcludeId(updateDTO.getCategoryCode(), id)) {
                    log.error("分类编码已存在：{}", updateDTO.getCategoryCode());
                    throw BusinessException.paramError("分类编码已存在：" + updateDTO.getCategoryCode());
                }
            }
        }
        
        // 3. 检查是否将分类移动到自己的子分类下（循环引用）
        if (updateDTO.getParentId() != null && !updateDTO.getParentId().equals(category.getParentId())) {
            if (isDescendant(id, updateDTO.getParentId())) {
                log.error("不能将分类移动到自己的子分类下，分类ID：{}，目标父分类ID：{}", id, updateDTO.getParentId());
                throw BusinessException.operationFailed("不能将分类移动到自己的子分类下");
            }
            
            // 检查父分类是否存在
            if (updateDTO.getParentId() != 0) {
                MaterialCategory parentCategory = getById(updateDTO.getParentId());
                if (parentCategory == null) {
                    log.error("父分类不存在，父分类ID：{}", updateDTO.getParentId());
                    throw BusinessException.dataNotFound("父分类不存在");
                }
                
                // 检查分类层级是否超过3级
                if (parentCategory.getLevel() >= 3) {
                    log.error("分类层级不能超过3级，父分类层级：{}", parentCategory.getLevel());
                    throw BusinessException.operationFailed("分类层级不能超过3级");
                }
                
                // 更新分类层级
                category.setLevel(parentCategory.getLevel() + 1);
            } else {
                // 移动到顶级
                category.setLevel(1);
            }
            
            // 更新父分类ID
            category.setParentId(updateDTO.getParentId());
        }
        
        // 4. 更新其他分类信息
        BeanUtils.copyProperties(updateDTO, category, "id", "parentId", "level");
        
        // 5. 保存更新
        boolean result = updateById(category);
        
        log.info("耗材分类更新成功，分类ID：{}，分类名称：{}", id, category.getCategoryName());
        return result;
    }

    /**
     * 删除耗材分类（逻辑删除）
     * 
     * 业务规则：
     * 1. 如果分类下有子分类，不能删除
     * 2. 使用逻辑删除，不物理删除数据
     * 
     * @param id 分类ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long id) {
        log.info("开始删除耗材分类，分类ID：{}", id);
        
        // 1. 检查分类是否存在
        MaterialCategory category = getById(id);
        if (category == null) {
            log.error("耗材分类不存在，分类ID：{}", id);
            throw BusinessException.dataNotFound("耗材分类不存在");
        }
        
        // 2. 检查分类下是否有子分类
        if (hasChildren(id)) {
            log.error("分类下存在子分类，不能删除，分类ID：{}", id);
            throw BusinessException.operationFailed("分类下存在子分类，不能删除");
        }
        
        // 3. 逻辑删除分类
        boolean result = removeById(id);
        
        log.info("耗材分类删除成功，分类ID：{}", id);
        return result;
    }

    /**
     * 批量删除耗材分类（逻辑删除）
     * 
     * 业务规则：
     * 1. 如果分类下有子分类，不能删除
     * 2. 使用逻辑删除，不物理删除数据
     * 3. 使用批量操作提高性能
     * 
     * @param ids 分类ID列表
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteCategories(List<Long> ids) {
        log.info("开始批量删除耗材分类，分类ID列表：{}", ids);
        
        // 1. 检查所有分类是否存在
        List<MaterialCategory> categories = listByIds(ids);
        if (categories.size() != ids.size()) {
            log.error("部分耗材分类不存在");
            throw BusinessException.dataNotFound("部分耗材分类不存在");
        }
        
        // 2. 检查分类下是否有子分类
        for (Long id : ids) {
            if (hasChildren(id)) {
                log.error("分类下存在子分类，不能删除，分类ID：{}", id);
                throw BusinessException.operationFailed("分类ID：" + id + " 下存在子分类，不能删除");
            }
        }
        
        // 3. 批量逻辑删除分类
        boolean result = removeByIds(ids);
        
        log.info("批量删除耗材分类成功，删除数量：{}", ids.size());
        return result;
    }

    /**
     * 根据ID查询耗材分类
     * 
     * @param id 分类ID
     * @return 分类信息VO
     */
    @Override
    public MaterialCategoryVO getCategoryById(Long id) {
        log.info("查询耗材分类，分类ID：{}", id);
        
        MaterialCategory category = getById(id);
        if (category == null) {
            log.error("耗材分类不存在，分类ID：{}", id);
            throw BusinessException.dataNotFound("耗材分类不存在");
        }
        
        MaterialCategoryVO vo = new MaterialCategoryVO();
        BeanUtils.copyProperties(category, vo);
        
        log.info("查询耗材分类成功，分类ID：{}", id);
        return vo;
    }

    /**
     * 查询耗材分类树形结构
     * 
     * 业务规则：
     * 1. 从顶级分类开始构建树形结构
     * 2. 递归加载子分类
     * 3. 按排序号排序
     * 
     * @return 分类树形结构
     */
    @Override
    public List<MaterialCategoryTreeVO> getCategoryTree() {
        log.info("开始查询耗材分类树形结构");
        
        // 1. 查询所有分类
        List<MaterialCategory> allCategories = list();
        
        // 2. 转换为VO列表
        List<MaterialCategoryTreeVO> categoryVOs = allCategories.stream()
                .map(category -> {
                    MaterialCategoryTreeVO vo = new MaterialCategoryTreeVO();
                    BeanUtils.copyProperties(category, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        
        // 3. 构建树形结构
        List<MaterialCategoryTreeVO> tree = buildTree(categoryVOs, 0L);
        
        log.info("查询耗材分类树形结构成功，顶级分类数量：{}", tree.size());
        return tree;
    }

    /**
     * 根据父分类ID查询子分类列表
     * 
     * @param parentId 父分类ID（0表示顶级分类）
     * @return 子分类列表
     */
    @Override
    public List<MaterialCategoryVO> getChildrenByParentId(Long parentId) {
        log.info("查询子分类列表，父分类ID：{}", parentId);
        
        List<MaterialCategory> children = materialCategoryMapper.selectByParentId(parentId);
        
        List<MaterialCategoryVO> vos = children.stream()
                .map(category -> {
                    MaterialCategoryVO vo = new MaterialCategoryVO();
                    BeanUtils.copyProperties(category, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        
        log.info("查询子分类列表成功，子分类数量：{}", vos.size());
        return vos;
    }

    /**
     * 查询所有顶级分类
     * 
     * @return 顶级分类列表
     */
    @Override
    public List<MaterialCategoryVO> getTopLevelCategories() {
        log.info("开始查询所有顶级分类");
        
        List<MaterialCategory> topCategories = materialCategoryMapper.selectTopLevelCategories();
        
        List<MaterialCategoryVO> vos = topCategories.stream()
                .map(category -> {
                    MaterialCategoryVO vo = new MaterialCategoryVO();
                    BeanUtils.copyProperties(category, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        
        log.info("查询顶级分类成功，顶级分类数量：{}", vos.size());
        return vos;
    }

    /**
     * 切换分类状态
     * 
     * @param id 分类ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleCategoryStatus(Long id) {
        log.info("开始切换分类状态，分类ID：{}", id);
        
        MaterialCategory category = getById(id);
        if (category == null) {
            log.error("耗材分类不存在，分类ID：{}", id);
            throw BusinessException.dataNotFound("耗材分类不存在");
        }
        
        // 切换状态：0-禁用，1-启用
        category.setStatus(category.getStatus() == 1 ? 0 : 1);
        boolean result = updateById(category);
        
        log.info("切换分类状态成功，分类ID：{}，新状态：{}", id, category.getStatus());
        return result;
    }

    /**
     * 检查分类编码是否存在
     * 
     * @param categoryCode 分类编码
     * @return 是否存在
     */
    @Override
    public boolean existsByCategoryCode(String categoryCode) {
        MaterialCategory category = materialCategoryMapper.selectByCategoryCode(categoryCode);
        return category != null;
    }

    /**
     * 检查分类编码是否存在（排除指定ID）
     * 
     * @param categoryCode 分类编码
     * @param excludeId 排除的分类ID
     * @return 是否存在
     */
    @Override
    public boolean existsByCategoryCodeExcludeId(String categoryCode, Long excludeId) {
        int count = materialCategoryMapper.countByCategoryCodeExcludeId(categoryCode, excludeId);
        return count > 0;
    }

    /**
     * 检查分类下是否有子分类
     * 
     * @param parentId 父分类ID
     * @return 是否有子分类
     */
    @Override
    public boolean hasChildren(Long parentId) {
        int count = materialCategoryMapper.countChildrenByParentId(parentId);
        return count > 0;
    }

    /**
     * 生成分类编码
     * 
     * 规则：
     * - 一级分类：A01, A02, ..., A99
     * - 二级分类：A01-01, A01-02, ..., A01-99
     * - 三级分类：A01-01-01, A01-01-02, ..., A01-01-99
     * 
     * @param parentId 父分类ID
     * @return 分类编码
     */
    private String generateCategoryCode(Long parentId) {
        if (parentId == null || parentId == 0) {
            // 一级分类：查询当前最大编码
            LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MaterialCategory::getParentId, 0L);
            wrapper.orderByDesc(MaterialCategory::getCategoryCode);
            wrapper.last("LIMIT 1");
            
            MaterialCategory lastCategory = getOne(wrapper);
            if (lastCategory == null) {
                return "A01";
            }
            
            String lastCode = lastCategory.getCategoryCode();
            int number = Integer.parseInt(lastCode.substring(1));
            number++;
            return String.format("A%02d", number);
        } else {
            // 二级或三级分类
            MaterialCategory parentCategory = getById(parentId);
            String parentCode = parentCategory.getCategoryCode();
            
            // 查询当前父分类下的最大子分类编码
            LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MaterialCategory::getParentId, parentId);
            wrapper.orderByDesc(MaterialCategory::getCategoryCode);
            wrapper.last("LIMIT 1");
            
            MaterialCategory lastChild = getOne(wrapper);
            if (lastChild == null) {
                return parentCode + "-01";
            }
            
            String lastCode = lastChild.getCategoryCode();
            String[] parts = lastCode.split("-");
            int number = Integer.parseInt(parts[parts.length - 1]);
            number++;
            return parentCode + "-" + String.format("%02d", number);
        }
    }

    /**
     * 计算分类层级
     * 
     * @param parentId 父分类ID
     * @return 分类层级
     */
    private Integer calculateLevel(Long parentId) {
        if (parentId == null || parentId == 0) {
            return 1;
        }
        
        MaterialCategory parentCategory = getById(parentId);
        return parentCategory.getLevel() + 1;
    }

    /**
     * 构建树形结构
     * 
     * @param categories 所有分类列表
     * @param parentId 父分类ID
     * @return 树形结构
     */
    private List<MaterialCategoryTreeVO> buildTree(List<MaterialCategoryTreeVO> categories, Long parentId) {
        List<MaterialCategoryTreeVO> tree = new ArrayList<>();
        
        for (MaterialCategoryTreeVO category : categories) {
            if (category.getParentId().equals(parentId)) {
                // 递归构建子分类
                List<MaterialCategoryTreeVO> children = buildTree(categories, category.getId());
                if (children != null && !children.isEmpty()) {
                    category.setChildren(children);
                }
                tree.add(category);
            }
        }
        
        // 按排序号排序
        tree.sort((a, b) -> {
            if (a.getSortOrder() == null) a.setSortOrder(0);
            if (b.getSortOrder() == null) b.setSortOrder(0);
            return a.getSortOrder().compareTo(b.getSortOrder());
        });
        
        return tree;
    }
    
    /**
     * 检查目标分类是否是当前分类的子孙分类
     * 
     * @param categoryId 当前分类ID
     * @param targetId 目标分类ID
     * @return 是否是子孙分类
     */
    private boolean isDescendant(Long categoryId, Long targetId) {
        // 递归检查目标分类是否是当前分类的子孙
        List<MaterialCategory> children = materialCategoryMapper.selectByParentId(targetId);
        if (children == null || children.isEmpty()) {
            return false;
        }
        
        for (MaterialCategory child : children) {
            if (child.getId().equals(categoryId)) {
                return true;
            }
            if (isDescendant(categoryId, child.getId())) {
                return true;
            }
        }
        
        return false;
    }
}
