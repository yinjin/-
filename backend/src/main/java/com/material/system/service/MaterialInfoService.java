package com.material.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.system.dto.MaterialInfoCreateDTO;
import com.material.system.dto.MaterialInfoUpdateDTO;
import com.material.system.vo.MaterialInfoVO;

import java.util.List;

/**
 * 物料信息服务接口
 */
public interface MaterialInfoService {
    
    /**
     * 创建物料信息
     */
    MaterialInfoVO create(MaterialInfoCreateDTO dto);
    
    /**
     * 更新物料信息
     */
    MaterialInfoVO update(MaterialInfoUpdateDTO dto);
    
    /**
     * 删除物料信息
     */
    void delete(Long id);
    
    /**
     * 根据ID查询物料信息
     */
    MaterialInfoVO getById(Long id);
    
    /**
     * 分页查询物料信息
     */
    Page<MaterialInfoVO> page(Integer current, Integer size, String name, String code, Long categoryId, Integer status);
    
    /**
     * 根据分类ID查询物料信息
     */
    List<MaterialInfoVO> getByCategoryId(Long categoryId);
    
    /**
     * 检查物料编码是否已存在
     */
    boolean checkCodeExists(String code, Long excludeId);
}
